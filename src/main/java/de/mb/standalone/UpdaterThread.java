package de.mb.standalone;

import autoUpdater.helper.OnlineStatus;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class UpdaterThread extends Thread {

    private final StandalonePluginUpdater updater;

    public UpdaterThread(StandalonePluginUpdater updater) {
        super("UpdaterThread " + updater.name);
        setDaemon(true);
        this.updater = updater;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        OnlineStatus update = updater.checkForUpdates();
        if (update != null) {
            try {
                // Ask the user if he really wants to install this update
                FutureTask<Boolean> askAboutUpdate = new FutureTask<Boolean>(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return JOptionPane.showConfirmDialog(
                            null,
                            "Ein Update für " + updater.name + " ist verfügbar. Jetzt installieren?",
                            "Updater",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        ) == JOptionPane.YES_OPTION;
                    }
                });
                SwingUtilities.invokeLater(askAboutUpdate);
                // if so, trigger update and close this application
                if (askAboutUpdate.get()) {
                    updater.updateInNewProcess();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        updater.removeTempJar();
    }

}
