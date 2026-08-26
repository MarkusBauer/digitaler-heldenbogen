package de.mb;

import de.mb.reflection.HeldReflector;
import helden.plugin.HeldenDatenPlugin;
import helden.plugin.HeldenXMLDatenPlugin3;
import helden.plugin.datenxmlplugin.DatenAustausch3Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CombiXmlPlugin implements HeldenXMLDatenPlugin3 {
    protected String name;
    protected Object[] plugins;
    JFrame frame;

    public CombiXmlPlugin(String name, Object... plugins) {
        this.name = name;
        this.plugins = plugins;
    }

    @Override
    public void click() {
    }

    @Override
    public JComponent getPanel() {
        return null;
    }

    @Override
    public ArrayList<JComponent> getUntermenus() {
        ArrayList<JComponent> list = new ArrayList<>();
        for (Object plugin : plugins) {
            if (plugin instanceof HeldenDatenPlugin) {
                list.add(new JMenuItem(new DatenPluginAction((HeldenDatenPlugin) plugin)));
            }
            if (plugin instanceof HeldenXMLDatenPlugin3) {
                list.add(new JMenuItem(new XmlPluginAction((HeldenXMLDatenPlugin3) plugin)));
                list.addAll(((HeldenXMLDatenPlugin3) plugin).getUntermenus());
            }
        }
        return list;
    }

    @Override
    public boolean hatMenu() {
        return true;
    }

    @Override
    public boolean hatTab() {
        return false;
    }

    @Override
    public void init(DatenAustausch3Interface datenAustausch3Interface, JFrame jFrame) {
        this.frame = jFrame;
        for (Object plugin : plugins) {
            if (plugin instanceof HeldenXMLDatenPlugin3) {
                ((HeldenXMLDatenPlugin3) plugin).init(datenAustausch3Interface, jFrame);
            }
        }
    }

    @Override
    public void doWork(JFrame jFrame) {
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getMenuName() {
        return name;
    }

    @Override
    public String getToolTipText() {
        return "";
    }

    @Override
    public String getType() {
        return DATEN;
    }

    private static class XmlPluginAction extends AbstractAction {
        private final HeldenXMLDatenPlugin3 plugin;

        public XmlPluginAction(HeldenXMLDatenPlugin3 plugin) {
            super(plugin.getMenuName());
            this.plugin = plugin;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.plugin.click();
        }
    }


    private class DatenPluginAction extends AbstractAction {
        private final HeldenDatenPlugin plugin;

        public DatenPluginAction(HeldenDatenPlugin plugin) {
            super(plugin.getMenuName());
            this.plugin = plugin;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.plugin.doWork(frame, null, HeldReflector.getInstance().getCurrentHeldWerkzeug());
        }
    }
}
