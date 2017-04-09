/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.lib.connections.pipe;

/**
 *
 * @author cxj
 */
public class PipeNativeLibraryHelper {

    private static volatile String PIPE_LIBRARY_PATH;

    public static synchronized void setPipeLibraryPath(String path) {
        PIPE_LIBRARY_PATH = path;
    }

    public static synchronized String getPipeLibraryPath() {
        return PIPE_LIBRARY_PATH;
    }
}
