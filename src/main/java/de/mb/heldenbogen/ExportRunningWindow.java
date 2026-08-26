package de.mb.heldenbogen;

import de.mb.reflection.HeldReflector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ExportRunningWindow extends JDialog {

    private static ExportRunningWindow instance = null;
    private static int processes = 0;

    public static synchronized void start() {
        processes++;
        if (instance == null) {
            instance = new ExportRunningWindow(HeldReflector.getInstance().getMainWindow());
            instance.setVisible(true);
        }
    }

    public static synchronized void stop() {
        processes--;
        if (processes == 0 && instance != null) {
            instance.dispose();
            instance = null;
        }
    }


    private ExportRunningWindow(JFrame parent) {
        super(parent, "Export läuft ...");
        setResizable(false);
        setType(Type.UTILITY);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Heldenbogen wird erstellt, bitte warten ...");
        label.setBorder(new EmptyBorder(10, 15, 5, 15));
        add(label);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setBorder(new EmptyBorder(5, 15, 15, 15));
        add(bar);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                synchronized (ExportRunningWindow.class) {
                    instance = null;
                }
            }
        });
    }
}
