package de.mb.fork;


import java.io.File;
import java.util.Optional;

public class JvmFinder {

    public static String getJvmExecutable() {
        try {
            Optional<String> cmd = java.lang.ProcessHandle.current().info().command();
            if (cmd.isPresent()) {
                return cmd.get();
            }
        } catch (NoClassDefFoundError ignored) {
        }

        String jvmLocation;
		if (System.getProperty("os.name").startsWith("Win")) {
			jvmLocation = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
		} else {
			jvmLocation = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		}
		if (new File(jvmLocation).exists())
			return jvmLocation;

        return "java";
    }
}
