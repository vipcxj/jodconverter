/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.lib.connections.pipe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author cxj
 */
public class HackedClassLoader extends URLClassLoader {

    private final String[] targetClassNames;
    private final Class[] targetClasses;
    private static volatile File jarFile;

    public HackedClassLoader(ClassLoader parent, String... targetClassNames) {
        super(new URL[]{}, parent);
        this.targetClassNames = targetClassNames;
        this.targetClasses = new Class[this.targetClassNames.length];
    }

    public int getTargetClassIndex(String name) {
        for (int i = 0; i < targetClassNames.length; i++) {
            if (Objects.equals(targetClassNames[i], name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Creates a new and empty directory in the default temp directory using the
     * given prefix. This methods uses {@link File#createTempFile} to create a
     * new tmp file, deletes it and creates a directory for it instead.
     *
     * @param prefix The prefix string to be used in generating the diretory's
     *               name; must be at least three characters long.
     * @return A newly-created empty directory.
     * @throws IOException If no directory could be created.
     */
    private static File createTempDir(String prefix) throws IOException {
        String tmpDirStr = System.getProperty("java.io.tmpdir");
        if (tmpDirStr == null) {
            throw new IOException(
                    "System property 'java.io.tmpdir' does not specify a tmp dir");
        }

        File tmpDir = new File(tmpDirStr);
        if (!tmpDir.exists()) {
            boolean created = tmpDir.mkdirs();
            if (!created) {
                throw new IOException("Unable to create tmp dir " + tmpDir);
            }
        }

        File resultDir = null;
        int suffix = (int) System.currentTimeMillis();
        int failureCount = 0;
        do {
            resultDir = new File(tmpDir, prefix + suffix % 10000);
            suffix++;
            failureCount++;
        } while (resultDir.exists() && failureCount < 50);

        if (resultDir.exists()) {
            throw new IOException(failureCount
                    + " attempts to generate a non-existent directory name failed, giving up");
        }
        boolean created = resultDir.mkdir();
        if (!created) {
            throw new IOException("Failed to create tmp directory");
        }
        resultDir.deleteOnExit();
        return resultDir;
    }

    private static synchronized File getJarFile() throws IOException {
        if (jarFile != null && jarFile.exists()) {
            return jarFile;
        }
        File tmpDir = createTempDir("office-pipe-connection-jar");
        jarFile = new File(tmpDir, "connector.jar");
        jarFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(NativeUtils.class.getResourceAsStream("/connector.jar"), jarFile);
        return jarFile;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, null);
    }

    public Class<?> loadClass(String name, Runnable preLoader) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            int targetClassIndex = getTargetClassIndex(name);
            if (targetClassIndex == -1) {
                return getParent().loadClass(name);
            }
            Class c = targetClasses[targetClassIndex];
            if (c != null) {
                return c;
            }

            try {
                File jar = getJarFile();
                ClassLoader loader = ClassLoader.getSystemClassLoader();
                try {
                    final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    AccessController.doPrivileged(new SetAccessable(method));
                    method.invoke(loader, jar.toURI().toURL());
                } catch (NoSuchMethodException | SecurityException | MalformedURLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new ClassNotFoundException(name, e);
                }
                if (preLoader != null) {
                    preLoader.run();
                }
                return loader.loadClass(name);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }

    static class SetAccessable implements PrivilegedAction<Void> {

        private final Method method;

        public SetAccessable(Method method) {
            this.method = method;
        }

        @Override
        public Void run() {
            method.setAccessible(true);
            return null;
        }

    }
}
