package de.mb.standalone;

import de.mb.CombiXmlPlugin;
import de.mb.autoexport.AutoExportPlugin;
import de.mb.heldenbogen.HeldenbogenPlugin;

public class HeldenbogenPluginStandalone extends CombiXmlPlugin {
    public HeldenbogenPluginStandalone() {
        super("Digitale Heldenbögen", new HeldenbogenPlugin(), new AutoExportPlugin());
        new UpdaterThread(new HeldenbogenUpdater()).start();
    }

    public static class HeldenbogenUpdater extends StandalonePluginUpdater {
        public HeldenbogenUpdater() {
            super("Digitale Heldenbögen", "https://www.mk-bauer.de/helden-software/index.php", "digitalerheldenbogen", null);
        }
    }
}
