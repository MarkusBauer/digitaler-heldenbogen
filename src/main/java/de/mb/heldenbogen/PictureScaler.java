package de.mb.heldenbogen;

import helden.framework.Einstellungen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;

import static java.lang.Math.round;

public class PictureScaler {

    private final HashMap<String, byte[]> cache = new HashMap<>();

    public byte[] loadPicture(String path) throws IOException {
        if (path.isEmpty()) return null;

        byte[] pic = cache.get(path);
        if (pic != null)
            return pic;

        File f = resolve(path);
        if (!f.exists() || !f.isFile()) {
            System.err.println("File not found: " + path);
            return null;
        }

        if (f.length() > 1000000) {
            pic = rescale(f);
        } else {
            pic = Files.readAllBytes(f.toPath());
        }

        cache.put(path, pic);
        return pic;
    }

    public Image loadPictureWithDimensions(String pfadZumPortrait) throws IOException {
        byte[] data = loadPicture(pfadZumPortrait);
        if (data == null) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            BufferedImage img = ImageIO.read(bais);
            return new Image(data, "image/" + getExtension(data), img.getWidth(), img.getHeight());
        }
    }

    public static String getExtension(byte[] img) {
        if (img[0] == (byte) 0x89 && img[1] == 0x50 && img[2] == 0x4E && img[3] == 0x47) {
            return "png";
        } else if (img[0] == (byte) 0xFF && img[1] == (byte) 0xD8) {
            return "jpg";
        } else if (img[0] == (byte) 0x47 && img[1] == (byte) 0x49) {
            return "gif";
        } else if (img[0] == (byte) 0x42 && img[1] == (byte) 0x4d) {
            return "bmp";
        } else if ((img[0] == (byte) 0x49 && img[1] == (byte) 0x49) || (img[0] == (byte) 0x4d && img[1] == (byte) 0x4d)) {
            return "tif";
        } else {
            System.out.println("unknown file format");
            return "jpg";
        }
    }

    public File resolve(String path) throws IOException {
        File f = new File(path);
        if (f.exists() && f.isFile()) return f;

        File f2 = new File(path.replace("C:\\Users", "/mnt/ssd/Users").replace('\\', '/'));
        if (f2.exists() && f2.isFile()) return f2;

        File f3 = new File(Einstellungen.getInstance().getPfade().getPfad("heldBildPfad"), f.getName());
        if (f3.exists() && f3.isFile()) return f3;

        return f;
    }

    private byte[] rescale(File f) throws IOException {
        BufferedImage img = ImageIO.read(f);
        if (img == null)
            throw new RuntimeException("Not a valid image: " + f);

        if (img.getWidth() > 2100 || img.getHeight() > 1200) {
            double factor = Math.min(1920.0 / img.getWidth(), 1080.0 / img.getHeight());
            int width = (int) round(img.getWidth() * factor);
            int height = (int) round(img.getHeight() * factor);
            System.out.println("Scale factor = " + factor + " => " + width + " / " + height);
            BufferedImage output = new BufferedImage(width, height, needsTransparency(img) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = output.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            g2d.drawImage(img, 0, 0, width, height, null);
            img = output;
        }

        // export as jpeg if possible
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!ImageIO.write(img, img.getTransparency() == Transparency.OPAQUE ? "jpg" : "png", baos))
                throw new RuntimeException("Cannot re-encode image " + f);
            baos.flush();
            System.out.println("Converted image from " + f.length() + " to " + baos.size() + " bytes");
            return baos.toByteArray();
        }
    }

    private boolean needsTransparency(BufferedImage img) {
        if (img.getTransparency() == Transparency.OPAQUE)
            return false;

        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int pixel : pixels) {
            if ((pixel >>> 24) != 0xff)
                return true;
        }

        System.out.println("Found image that does not need transparency");
        return false;
    }

    public static class Image {
        public final byte[] data;
        public final String mime;
        public final int width;
        public final int height;

        public Image(byte[] data, String mime, int width, int height) {
            this.data = data;
            this.mime = mime;
            this.width = width;
            this.height = height;
        }

        public String base64() {
            return Base64.getEncoder().encodeToString(data);
        }
    }
}
