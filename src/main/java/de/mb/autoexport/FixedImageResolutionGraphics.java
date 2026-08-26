package de.mb.autoexport;

import org.apache.fop.pdf.PDFXObject;
import org.apache.fop.svg.PDFDocumentGraphics2D;
import org.apache.fop.svg.PDFGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FixedImageResolutionGraphics extends PDFDocumentGraphics2D {
    public FixedImageResolutionGraphics() {
        super(false);
    }

    public FixedImageResolutionGraphics(PDFDocumentGraphics2D g) {
        super(g);
    }

    public Graphics nop() {
        return new NopGraphics(this);
    }

    @Override
    public Graphics create() {
        preparePainting();
        return new FixedImageResolutionGraphics(this);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        // copied from parent
        preparePainting();

        String key = "TempImage:" + img.toString();
        PDFXObject xObject = pdfDoc.getXObject(key);
        if (xObject == null) {
            // this block here is patched (resolution fix)
            BufferedImage buf = buildBufferedImage(new Dimension(width * 300 / 72, height * 300 / 72));
            int imageWidth = buf.getWidth();
            int imageHeight = buf.getHeight();
            java.awt.Graphics2D g = buf.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);
            g.setBackground(new Color(1, 1, 1, 0));
            g.setPaint(new Color(1, 1, 1, 0));
            g.fillRect(0, 0, imageWidth, imageHeight);
            g.clip(new Rectangle(0, 0, imageWidth, imageHeight));
            g.setComposite(gc.getComposite());

            boolean drawn = g.drawImage(img, 0, 0, imageWidth, imageHeight, observer);
            if (!drawn) {
                return false;
            }
            g.dispose();

            xObject = addRenderedImage(key, buf);
        } else {
            resourceContext.addXObject(xObject);
        }

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        useXObject(xObject, at, width, height);
        return true;
    }

    private BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    }

    private PDFXObject addRenderedImage(String key, RenderedImage img) {
        try {
            Method m = PDFGraphics2D.class.getDeclaredMethod("addRenderedImage", String.class, RenderedImage.class);
            m.setAccessible(true);
            return (PDFXObject) m.invoke(this, key, img);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void useXObject(PDFXObject xObject, AffineTransform xform, float width, float height) {
        try {
            Method m = PDFGraphics2D.class.getDeclaredMethod("useXObject", PDFXObject.class, AffineTransform.class, float.class, float.class);
            m.setAccessible(true);
            m.invoke(this, xObject, xform, width, height);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
