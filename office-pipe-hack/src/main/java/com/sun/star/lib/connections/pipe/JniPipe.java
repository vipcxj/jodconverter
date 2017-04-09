/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.lib.connections.pipe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * @author cxj
 */
public class JniPipe {

    private static volatile Class JNI_CLASS;
    private static volatile Method createJNI;
    private static volatile Method closeJNI;
    private static volatile Method flushJNI;
    private static volatile Method readJNI;
    private static volatile Method writeJNI;
    private static volatile Method getDescription;

    private static final String helperClassName = "com.sun.star.lib.connections.pipe.PipeNativeLibraryHelper";
    private static final String className = "com.sun.star.lib.connections.pipe.PipeConnection";
    private static final HackedClassLoader LOADER
            = AccessController.doPrivileged(new PrivilegedAction<HackedClassLoader>() {

                @Override
                public HackedClassLoader run() {
                    return new HackedClassLoader(JniPipe.class.getClassLoader(), helperClassName, className);
                }
            });

    private Object jni;

    public synchronized static Class getJniClass() {
        if (JNI_CLASS == null) {
            try {
                JNI_CLASS = loadJniClass();
            } catch (IOException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            if (createJNI == null) {
                createJNI = JNI_CLASS.getDeclaredMethod("createJNI", String.class);
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        createJNI.setAccessible(true);
                        return null;
                    }
                });
            }
            if (closeJNI == null) {
                closeJNI = JNI_CLASS.getDeclaredMethod("closeJNI");
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        closeJNI.setAccessible(true);
                        return null;
                    }
                });
            }
            if (flushJNI == null) {
                flushJNI = JNI_CLASS.getDeclaredMethod("flushJNI");
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        flushJNI.setAccessible(true);
                        return null;
                    }
                });
            }
            if (readJNI == null) {
                readJNI = JNI_CLASS.getDeclaredMethod("readJNI", byte[][].class, int.class);
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        readJNI.setAccessible(true);
                        return null;
                    }
                });
            }
            if (writeJNI == null) {
                writeJNI = JNI_CLASS.getDeclaredMethod("writeJNI", byte[].class);
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        writeJNI.setAccessible(true);
                        return null;
                    }
                });
            }
            if (getDescription == null) {
                getDescription = JNI_CLASS.getDeclaredMethod("getDescription");
                AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        getDescription.setAccessible(true);
                        return null;
                    }
                });
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        return JNI_CLASS;
    }

    public JniPipe() {
        try {
            jni = getJniClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int createJNI(String name) {
        try {
            return (int) createJNI.invoke(jni, name);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int readJNI(/*OUT*/byte[][] bytes, int nBytesToRead) {
        try {
            return (int) readJNI.invoke(jni, bytes, nBytesToRead);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    // JNI implementation to write to the pipe
    @SuppressWarnings({"ConfusingArrayVararg", "PrimitiveArrayArgumentToVariableArgMethod"})
    public void writeJNI(byte aData[]) {
        try {
            writeJNI.invoke(jni, aData);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    // JNI implementation to flush the pipe
    public void flushJNI() {
        try {
            flushJNI.invoke(jni);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    // JNI implementation to close the pipe
    public void closeJNI() {
        try {
            closeJNI.invoke(jni);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getDescription() {
        try {
            return (String) getDescription.invoke(jni);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

//    /**
//     * Creates a new and empty directory in the default temp directory using the
//     * given prefix. This methods uses {@link File#createTempFile} to create a
//     * new tmp file, deletes it and creates a directory for it instead.
//     *
//     * @param prefix The prefix string to be used in generating the diretory's
//     *               name; must be at least three characters long.
//     * @return A newly-created empty directory.
//     * @throws IOException If no directory could be created.
//     */
//    private static File createTempDir(String prefix) throws IOException {
//        String tmpDirStr = System.getProperty("java.io.tmpdir");
//        if (tmpDirStr == null) {
//            throw new IOException(
//                    "System property 'java.io.tmpdir' does not specify a tmp dir");
//        }
//
//        File tmpDir = new File(tmpDirStr);
//        if (!tmpDir.exists()) {
//            boolean created = tmpDir.mkdirs();
//            if (!created) {
//                throw new IOException("Unable to create tmp dir " + tmpDir);
//            }
//        }
//
//        File resultDir = null;
//        int suffix = (int) System.currentTimeMillis();
//        int failureCount = 0;
//        do {
//            resultDir = new File(tmpDir, prefix + suffix % 10000);
//            suffix++;
//            failureCount++;
//        } while (resultDir.exists() && failureCount < 50);
//
//        if (resultDir.exists()) {
//            throw new IOException(failureCount
//                    + " attempts to generate a non-existent directory name failed, giving up");
//        }
//        boolean created = resultDir.mkdir();
//        if (!created) {
//            throw new IOException("Failed to create tmp directory");
//        }
//        resultDir.deleteOnExit();
//        return resultDir;
//    }
    private static String checkPipePath() {
        String path = PipeConfigure.getPipeLibraryPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("The jpipe file path is not set.");
        }
        return path;
    }

    private static Class loadJniClass() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        final String path = checkPipePath();
        Class pipeClass = LOADER.loadClass(className, new Runnable() {

            @Override
            public void run() {
                try {
                    Class<?> helperClass = LOADER.loadClass(helperClassName);
                    final Field pathField = helperClass.getDeclaredField("PIPE_LIBRARY_PATH");
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {

                        @Override
                        public Void run() {
                            pathField.setAccessible(true);
                            return null;
                        }
                    });
                    pathField.set(null, path);
                } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        final Method loadMethod = pipeClass.getDeclaredMethod("isNativeLibraryLoaded");
        AccessController.doPrivileged(new PrivilegedAction<Void>() {

            @Override
            public Void run() {
                loadMethod.setAccessible(true);
                return null;
            }
        });
        boolean loaded = (boolean) loadMethod.invoke(null);
        if (!loaded) {
            System.out.println("Library load in static block failed, try load again.");
            NativeUtils.loadNativeLibrary(pipeClass, path, true);
        }
        return pipeClass;

//        ClassLoader loader = ClassLoader.getSystemClassLoader();
//        final String helperClassName = "com.sun.star.lib.connections.pipe.PipeNativeLibraryHelper";
//        final String className = "com.sun.star.lib.connections.pipe.JniWrapper";
//        try {
//            return (Class) loader.loadClass(className);
//        } catch (ClassNotFoundException ignore) {
//        }
//        File tmpDir;
//        tmpDir = createTempDir("office-pipe-connection-jar");
//        File jarFile = new File(tmpDir, "connector.jar");
//        jarFile.deleteOnExit();
//        FileUtils.copyInputStreamToFile(NativeUtils.class.getResourceAsStream("/connector.jar"), jarFile);
//        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//        AccessController.doPrivileged(new PrivilegedAction<Void>() {
//
//            @Override
//            public Void run() {
//                method.setAccessible(true);
//                return null;
//            }
//        });
//        method.invoke(loader, jarFile.toURI().toURL());
//        Class<?> helperClass = loader.loadClass(helperClassName);
//        final Field pathField = helperClass.getDeclaredField("PIPE_LIBRARY_PATH");
//        AccessController.doPrivileged(new PrivilegedAction<Void>() {
//
//            @Override
//            public Void run() {
//                pathField.setAccessible(true);
//                return null;
//            }
//        });
//        String path = checkPipePath();
//        pathField.set(null, path);
//        Class pipeClass = loader.loadClass(className);
//        final Method loadMethod = pipeClass.getDeclaredMethod("isNativeLibraryLoaded");
//        AccessController.doPrivileged(new PrivilegedAction<Void>() {
//
//            @Override
//            public Void run() {
//                loadMethod.setAccessible(true);
//                return null;
//            }
//        });
//        boolean loaded = (boolean) loadMethod.invoke(null);
//        if (!loaded) {
//            System.out.println("Library load in static block failed, try load again.");
//            Runtime.getRuntime().load(path);
//        }
//        return pipeClass;
    }
}
