/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This file incorporates work covered by the following license notice:
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements. See the NOTICE file distributed
 *   with this work for additional information regarding copyright
 *   ownership. The ASF licenses this file to you under the Apache
 *   License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of
 *   the License at http://www.apache.org/licenses/LICENSE-2.0 .
 */
package com.sun.star.lib.connections.pipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnectionBroadcaster;
import com.sun.star.io.XStreamListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The PipeConnectionWrapper implements the <code>XConnection</code> interface and is
 * uses by the <code>PipeConnector</code> and the <code>PipeAcceptor</code>.
 * This class is not part of the provided <code>api</code>.
 *
 * @see com.sun.star.lib.connections.pipe.pipeAcceptor
 * @see com.sun.star.lib.connections.pipe.pipeConnector
 * @see com.sun.star.connection.XConnection
 * @since UDK1.0
 */
public class PipeConnectionWrapper implements XConnection, XConnectionBroadcaster {

    /**
     * When set to true, enables various debugging output.
     */
    public static final boolean DEBUG = false;

//    static {
//        // load shared library for JNI code
//        NativeLibraryLoader.loadLibrary(PipeConnectionWrapper.class.getClassLoader(), "jpipe");
//    }
    private final Object jniObject;
    private Method createJNI;
    private Method closeJNI;
    private Method flushJNI;
    private Method readJNI;
    private Method writeJNI;
    protected ArrayList<XStreamListener> _aListeners;
    protected boolean _bFirstRead;

    /**
     * Constructs a new <code>PipeConnection</code>.
     *
     * @param description the description of the connection.
     */
    public PipeConnectionWrapper(Object jniObj, String description)
            throws IOException {
        this.jniObject = jniObj;
        if (DEBUG) {
            System.err.println("##### " + getClass().getName() + " - instantiated " + description);
        }

        _aListeners = new ArrayList<XStreamListener>();
        _bFirstRead = true;

        // get pipe name from pipe descriptor
        String aPipeName;
        StringTokenizer aTokenizer = new StringTokenizer(description, ",");
        if (aTokenizer.hasMoreTokens()) {
            String aConnType = aTokenizer.nextToken();
            if (!aConnType.equals("pipe")) {
                throw new RuntimeException("invalid pipe descriptor: does not start with 'pipe,'");
            }

            String aPipeNameParam = aTokenizer.nextToken();
            if (!aPipeNameParam.substring(0, 5).equals("name=")) {
                throw new RuntimeException("invalid pipe descriptor: no 'name=' parameter found");
            }
            aPipeName = aPipeNameParam.substring(5);
        } else {
            throw new RuntimeException("invalid or empty pipe descriptor");
        }

        // create the pipe
        try {
            createJNI(aPipeName);
        } catch (java.lang.Exception ex1) {
            IOException ex2 = new IOException();
            ex2.initCause(ex1);
            throw ex2;
        }
    }

    public void addStreamListener(XStreamListener aListener) throws com.sun.star.uno.RuntimeException {
        _aListeners.add(aListener);
    }

    public void removeStreamListener(XStreamListener aListener) throws com.sun.star.uno.RuntimeException {
        _aListeners.remove(aListener);
    }

    private void notifyListeners_open() {
        for (XStreamListener xStreamListener : _aListeners) {
            xStreamListener.started();
        }
    }

    private void notifyListeners_close() {
        for (XStreamListener xStreamListener : _aListeners) {
            xStreamListener.closed();
        }
    }

    private void notifyListeners_error(com.sun.star.uno.Exception exception) {
        for (XStreamListener xStreamListener : _aListeners) {
            xStreamListener.error(exception);
        }
    }

    // JNI implementation to create the pipe
    private int createJNI(String name)
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            if (createJNI == null) {
                createJNI = jniObject.getClass().getDeclaredMethod("createJNI", String.class);
            }
            createJNI.setAccessible(true);
            return (int) createJNI.invoke(jniObject, name);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new com.sun.star.io.IOException(ex);
        }
    }

    // JNI implementation to read from the pipe
    private int readJNI(/*OUT*/byte[][] bytes, int nBytesToRead)
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            if (readJNI == null) {
                readJNI = jniObject.getClass().getDeclaredMethod("readJNI", byte[][].class, int.class);
            }
            readJNI.setAccessible(true);
            return (int) readJNI.invoke(jniObject, bytes, nBytesToRead);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new com.sun.star.io.IOException(ex);
        }
    }

    // JNI implementation to write to the pipe
    private void writeJNI(byte aData[])
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            if (writeJNI == null) {
                writeJNI = jniObject.getClass().getDeclaredMethod("writeJNI", byte[].class);
            }
            writeJNI.setAccessible(true);
            writeJNI.invoke(jniObject, aData);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new com.sun.star.io.IOException(ex);
        }
    }

    // JNI implementation to flush the pipe
    private void flushJNI()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            if (flushJNI == null) {
                flushJNI = jniObject.getClass().getDeclaredMethod("flushJNI");
            }
            flushJNI.setAccessible(true);
            flushJNI.invoke(jniObject);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new com.sun.star.io.IOException(ex);
        }
    }

    // JNI implementation to close the pipe
    private void closeJNI()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            if (closeJNI == null) {
                closeJNI = jniObject.getClass().getDeclaredMethod("closeJNI");
            }
            closeJNI.setAccessible(true);
            closeJNI.invoke(jniObject);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new com.sun.star.io.IOException(ex);
        }
    }

    /**
     * Read the required number of bytes.
     *
     * @param bytes the outparameter, where the bytes have to be placed.
     * @param nBytesToRead the number of bytes to read.
     * @return the number of bytes read.
     *
     * @see com.sun.star.connection.XConnection#read
     */
    public int read(/*OUT*/byte[][] bytes, int nBytesToRead)
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        if (_bFirstRead) {
            _bFirstRead = false;

            notifyListeners_open();
        }

        return readJNI(bytes, nBytesToRead);
    }

    /**
     * Write bytes.
     *
     * @param aData the bytes to write.
     * @see com.sun.star.connection.XConnection#write
     */
    public void write(byte aData[])
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        writeJNI(aData);
    }

    /**
     * Flushes the buffer.
     *
     * @see com.sun.star.connection.XConnection#flush
     */
    public void flush()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        flushJNI();
    }

    /**
     * Closes the connection.
     *
     * @see com.sun.star.connection.XConnection#close
     */
    public void close()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        if (DEBUG) {
            System.out.print("PipeConnection::close() ");
        }
        closeJNI();
        notifyListeners_close();
        if (DEBUG) {
            System.out.println("done");
        }
    }

    /**
     * Gives a description of the connection.
     *
     * @return the description.
     * @see com.sun.star.connection.XConnection#getDescription
     */
    public String getDescription() throws com.sun.star.uno.RuntimeException {
        try {
            Field field = jniObject.getClass().getDeclaredField("_aDescription");
            field.setAccessible(true);
            return (String) field.get(jniObject);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new com.sun.star.uno.RuntimeException(ex);
        }
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
