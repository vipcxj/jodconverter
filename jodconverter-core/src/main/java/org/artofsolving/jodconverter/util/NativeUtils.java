/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.artofsolving.jodconverter.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class NativeUtils {

    public static boolean loadNativeLibrary(Class fromClass, String path, boolean absolute) {
        System.out.println("Use " + fromClass + " to load library " + path);
        try {
            final Method loadLibraryMethod = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, boolean.class);
            AccessController.doPrivileged(new PrivilegedAction<Void>() {

                @Override
                public Void run() {
                    loadLibraryMethod.setAccessible(true);
                    return null;
                }
            });
            loadLibraryMethod.invoke(null, fromClass, path, absolute);
            return true;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(NativeUtils.class.getName()).log(Level.WARNING, null, ex);
            return false;
        }
    }

    public static boolean loadSystemSharedLibrary(String path, boolean absolute) {
        Class fromClass = ClassLoader.getSystemClassLoader().getClass();
        return loadNativeLibrary(fromClass, path, absolute);
    }
    
    public static boolean loadAppUserSharedLibrary(String path, boolean absolute) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        try {
            Class<?> monkClass = loader.loadClass(String.class.getName());
            return loadNativeLibrary(monkClass, path, absolute);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NativeUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean replaceLibraryPath(String prePath, String path, boolean system) {
        if (prePath == null && path == null) {
            return true;
        }
        String strPaths;
        if (system) {
            strPaths = System.getProperty("sun.boot.library.path");
        } else {
            strPaths = System.getProperty("java.library.path");
        }
        if (strPaths != null) {
            String[] arrPaths = strPaths.split(";");
            for (String arrPath : arrPaths) {
                if (new File(arrPath).equals(new File(path))) {
                    return true;
                }
            }
        }
        try {
            final Field usrPathsField;
            if (system) {
                usrPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            } else {
                usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            }
            AccessController.doPrivileged(new PrivilegedAction<Void>() {

                @Override
                public Void run() {
                    usrPathsField.setAccessible(true);
                    return null;
                }
            });
            final String[] paths = (String[]) usrPathsField.get(null);
            if (path == null) {
                int idx = -1;
                for (int i = 0; i < paths.length; i++) {
                    if (Objects.equals(prePath, paths[i])) {
                        idx = i;
                        break;
                    }
                }
                if (idx != -1) {
                    final String[] newPaths = new String[paths.length - 1];
                    System.arraycopy(paths, 0, newPaths, 0, idx);
                    if (idx < paths.length - 1) {
                        System.arraycopy(paths, idx + 1, newPaths, idx, paths.length - idx - 1);
                    }
                    usrPathsField.set(null, newPaths);
                }
            } else {
                int idx = -1;
                for (int i = 0; i < paths.length; i++) {
                    if (Objects.equals(prePath, paths[i])) {
                        idx = i;
                        break;
                    }
                }
                if (idx != -1) {
                    paths[idx] = path;
                } else {
                    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
                    newPaths[newPaths.length - 1] = path;
                    usrPathsField.set(null, newPaths);
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Logger.getLogger(NativeUtils.class.getName()).log(Level.WARNING, null, e);
            return false;
        }
        return true;
    }
}
