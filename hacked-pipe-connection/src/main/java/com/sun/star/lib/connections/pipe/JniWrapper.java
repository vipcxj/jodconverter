/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.lib.connections.pipe;

import com.sun.star.io.IOException;

/**
 *
 * @author cxj
 */
public class JniWrapper {

    private final PipeConnection connection;

    public JniWrapper() {
        connection = new PipeConnection();
    }

    public int createJNI(String name) throws IOException {
        return connection.createJNI(name);
    }

    // JNI implementation to read from the pipe
    public int readJNI(/*OUT*/byte[][] bytes, int nBytesToRead) throws IOException {
        return connection.readJNI(bytes, nBytesToRead);
    }

    // JNI implementation to write to the pipe
    public void writeJNI(byte aData[]) throws IOException {
        connection.writeJNI(aData);
    }

    // JNI implementation to flush the pipe
    public void flushJNI() throws com.sun.star.io.IOException {
        connection.flushJNI();
    }

    // JNI implementation to close the pipe
    public void closeJNI() throws com.sun.star.io.IOException {
        connection.closeJNI();
    }
    
    public String getDescription() {
        return connection._aDescription;
    }
    
    public static boolean isNativeLibraryLoaded() {
        return PipeConnection.LOADED;
    }
}
