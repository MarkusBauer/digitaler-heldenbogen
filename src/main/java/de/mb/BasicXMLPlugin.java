package de.mb;

import helden.plugin.HeldenXMLDatenPlugin3;
import helden.plugin.datenxmlplugin.DatenAustausch3Interface;

import javax.swing.*;
import java.util.ArrayList;

public abstract class BasicXMLPlugin implements HeldenXMLDatenPlugin3 {
    protected DatenAustausch3Interface iface;
    protected JFrame jFrame;

    @Override
    public void init(DatenAustausch3Interface datenAustausch3Interface, JFrame jFrame) {
        this.iface = datenAustausch3Interface;
        this.jFrame = jFrame;
    }

    @Override
    public void doWork(JFrame jFrame) {
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getType() {
        return DATEN;
    }

    @Override
    public boolean hatTab() {
        return false;
    }

    @Override
    public JComponent getPanel() {
        return null;
    }

    @Override
    public void click() {
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    public ArrayList<JComponent> getUntermenus() {
        return new ArrayList<>();
    }
}
