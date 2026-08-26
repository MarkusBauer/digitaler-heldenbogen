package de.mb.standalone;

import autoUpdater.gui.UpdaterFrame;
import autoUpdater.helper.Downloader;
import autoUpdater.helper.OnlineStatus;
import autoUpdater.helper.Util;
import autoUpdater.HeldenStarterPatched;
import de.mb.fork.JvmFinder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class StandalonePluginUpdater {

    protected String name;
    protected String updateUrl;
    protected String channelPw;
    protected File pluginJar;
    protected String mainJar;

    public StandalonePluginUpdater(String name, String updateUrl) {
        this(name, updateUrl, "", null);
    }

    public StandalonePluginUpdater(String name, String updateUrl, String channelPw, File pluginJar) {
        this.name = name;
        this.updateUrl = updateUrl;
        this.channelPw = channelPw;
        this.pluginJar = pluginJar == null ? getDefaultJarFile() : pluginJar;
        this.mainJar = System.getProperty("java.class.path");
    }

    public File getDefaultJarFile() {
        try {
            return new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getChannelPw() {
        return channelPw;
    }

    public void setChannelPw(String channelPw) {
        this.channelPw = channelPw;
    }

    public static StandalonePluginUpdater createFromArgs(String[] args) {
        StandalonePluginUpdater updater = new StandalonePluginUpdater(null, "", "", null);
        for (String arg : args) {
            try {
                if (arg.startsWith("-jPluginJar=")) {
                    updater.pluginJar = new File(URLDecoder.decode(arg.substring("-jPluginJar=".length()), "UTF-8"));
                } else if (arg.startsWith("-jMainJar=")) {
                    updater.mainJar = URLDecoder.decode(arg.substring("-jMainJar=".length()), "UTF-8");
                } else if (arg.startsWith("-jChannelPw=")) {
                    updater.channelPw = URLDecoder.decode(arg.substring("-jChannelPw=".length()), "UTF-8");
                } else if (arg.startsWith("-jUpdateUrl=")) {
                    updater.updateUrl = URLDecoder.decode(arg.substring("-jUpdateUrl=".length()), "UTF-8");
                } else if (arg.startsWith("-jName=")) {
                    updater.name = URLDecoder.decode(arg.substring("-jName=".length()), "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return updater;
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StandalonePluginUpdater updater = StandalonePluginUpdater.createFromArgs(args);
        updater.update();
        if (updater.mainJar != null)
            new HeldenStarterPatched().startHelden(new File(updater.mainJar));
    }

    public OnlineStatus checkForUpdates() {
        try {
            OnlineStatus oStatus = new OnlineStatus(updateUrl, channelPw, Util.getMd5(pluginJar));
            if (pluginJar.exists() && !Util.getMd5(pluginJar).equals(oStatus.getMd5Hash())) {
                return oStatus;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update() {
        boolean result = false;
        UpdaterFrame frame = new UpdaterFrame(name);
        frame.setVisible(true);

        try {
            frame.setStatusText("Lade Onlinestatus");
            OnlineStatus oStatus = new OnlineStatus(updateUrl, channelPw, Util.getMd5(pluginJar));
            if (pluginJar.exists()) {
                if (Util.getMd5(pluginJar).equals(oStatus.getMd5Hash())) {
                    frame.dispose();
                    JOptionPane.showMessageDialog(frame, "Kein Update notwendig!");
                } else {
                    Thread t = new Thread(new Downloader(oStatus, pluginJar, frame));
                    t.start();
                    t.join();
                    frame.dispose();
                    result = true;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Jar nicht gefunden!");
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Server nicht erreichbar!");
        }

        frame.dispose();
        return result;
    }

    public void updateInNewProcess() {
        try {
            File thisJar = getDefaultJarFile();
            File tempJar = new File(thisJar.getParentFile(), thisJar.getName() + ".tmp");
            Files.copy(thisJar.toPath(), tempJar.toPath(), StandardCopyOption.REPLACE_EXISTING);

            ArrayList<String> cmd = new ArrayList<>();
            cmd.add(JvmFinder.getJvmExecutable());
            cmd.add("-cp");
            cmd.add(tempJar.getAbsolutePath());
            cmd.add(getClass().getName());
            cmd.add("-jPluginJar=" + URLEncoder.encode(pluginJar.getAbsolutePath(), "UTF-8"));
            cmd.add("-jChannelPw=" + URLEncoder.encode(channelPw, "UTF-8"));
            cmd.add("-jUpdateUrl=" + URLEncoder.encode(updateUrl, "UTF-8"));
            cmd.add("-jName=" + URLEncoder.encode(name, "UTF-8"));
            if (mainJar.endsWith(".jar"))
                cmd.add("-jMainJar=" + URLEncoder.encode(mainJar, "UTF-8"));

            System.out.println("[PluginUpdater] Command: ");
            for (String s : cmd)
                System.out.print("\"" + s + "\" ");
            System.out.println("");
            Runtime.getRuntime().exec(cmd.toArray(new String[0]));
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeTempJar() {
        File thisJar = getDefaultJarFile();
        File tempJar = new File(thisJar.getParentFile(), thisJar.getName() + ".tmp");
        if (tempJar.exists())
            tempJar.delete();
    }
}
