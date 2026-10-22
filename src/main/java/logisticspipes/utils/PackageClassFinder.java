package logisticspipes.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

/**
 * Finds the top level classes of one of our packages by reading only the jar or folder our classes come from. Scanning
 * the whole classpath with Guava means opening every mod jar in the pack.
 */
public final class PackageClassFinder {

    private PackageClassFinder() {}

    public static List<Class<?>> getTopLevelClassesRecursive(Class<?> anchor, String packageName)
            throws IOException, ClassNotFoundException {
        ClassLoader loader = anchor.getClassLoader();
        String anchorPath = anchor.getName().replace('.', '/') + ".class";
        String prefix = packageName.replace('.', '/') + "/";
        URL url = loader.getResource(anchorPath);

        List<String> names = new ArrayList<>();
        try {
            if (url != null && "jar".equals(url.getProtocol())) {
                URL jarUrl = ((JarURLConnection) url.openConnection()).getJarFileURL();
                try (JarFile jar = new JarFile(new File(jarUrl.toURI()))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        addIfTopLevelClass(entries.nextElement().getName(), prefix, names);
                    }
                }
            } else if (url != null && "file".equals(url.getProtocol())) {
                File root = new File(url.toURI());
                for (int i = anchorPath.split("/").length; i > 0; i--) {
                    root = root.getParentFile();
                }
                collectFromFolder(new File(root, prefix), prefix, names);
            } else {
                for (ClassInfo info : ClassPath.from(loader).getTopLevelClassesRecursive(packageName)) {
                    names.add(info.getName());
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        List<Class<?>> classes = new ArrayList<>(names.size());
        for (String name : names) {
            classes.add(loader.loadClass(name));
        }
        return classes;
    }

    private static void collectFromFolder(File folder, String path, List<String> names) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                collectFromFolder(file, path + file.getName() + "/", names);
            } else {
                addIfTopLevelClass(path + file.getName(), path, names);
            }
        }
    }

    private static void addIfTopLevelClass(String entry, String prefix, List<String> names) {
        if (entry.startsWith(prefix) && entry.endsWith(".class") && entry.indexOf('$') == -1) {
            names.add(entry.substring(0, entry.length() - ".class".length()).replace('/', '.'));
        }
    }
}
