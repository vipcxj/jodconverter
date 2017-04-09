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

import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnectionBroadcaster;
import com.sun.star.io.XStreamListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The PipeConnectionWrapper implements the <code>XConnection</code> interface
 * and is uses by the <code>PipeConnector</code> and the
 * <code>PipeAcceptor</code>. This class is not part of the provided
 * <code>api</code>.
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
    private JniPipe jni;
    protected ArrayList<XStreamListener> _aListeners;
    protected boolean _bFirstRead;

    private String aPipeName;

    /**
     * Constructs a new <code>PipeConnection</code>.
     *
     * @param description the description of the connection.
     */
    public PipeConnectionWrapper(String description) {
        this.jni = new JniPipe();
        if (DEBUG) {
            System.err.println("##### " + getClass().getName() + " - instantiated " + description);
        }

        _aListeners = new ArrayList<>();
        _bFirstRead = true;

        // get pipe name from pipe descriptor
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

    }

    public String getPipeName() {
        return aPipeName;
    }

    public void createConnect() throws IOException {
        try {
            jni.createJNI(aPipeName);
        } catch (java.lang.Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void addStreamListener(XStreamListener aListener) throws com.sun.star.uno.RuntimeException {
        _aListeners.add(aListener);
    }

    @Override
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

    // JNI implementation to read from the pipe
    private int readJNI(/*OUT*/byte[][] bytes, int nBytesToRead)
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        return jni.readJNI(bytes, nBytesToRead);
    }

    // JNI implementation to write to the pipe
    private void writeJNI(byte aData[])
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        jni.writeJNI(aData);
    }

    // JNI implementation to flush the pipe
    private void flushJNI()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        jni.flushJNI();
    }

    // JNI implementation to close the pipe
    private void closeJNI()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        jni.closeJNI();
    }

    /**
     * Read the required number of bytes.
     *
     * @param bytes        the outparameter, where the bytes have to be placed.
     * @param nBytesToRead the number of bytes to read.
     * @return the number of bytes read.
     *
     * @see com.sun.star.connection.XConnection#read
     */
    @Override
    public int read(/*OUT*/byte[][] bytes, int nBytesToRead)
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        if (_bFirstRead) {
            _bFirstRead = false;

            notifyListeners_open();
        }

        try {
            return readJNI(bytes, nBytesToRead);
        } catch (com.sun.star.io.IOException e) {
            notifyListeners_error(e);
            throw e;
        }
    }

    /**
     * Write bytes.
     *
     * @param aData the bytes to write.
     * @see com.sun.star.connection.XConnection#write
     */
    @Override
    public void write(byte aData[])
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            writeJNI(aData);
        } catch (com.sun.star.io.IOException e) {
            notifyListeners_error(e);
            throw e;
        }
    }

    /**
     * Flushes the buffer.
     *
     * @see com.sun.star.connection.XConnection#flush
     */
    @Override
    public void flush()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        try {
            flushJNI();
        } catch (com.sun.star.io.IOException e) {
            notifyListeners_error(e);
            throw e;
        }
    }

    /**
     * Closes the connection.
     *
     * @see com.sun.star.connection.XConnection#close
     */
    @Override
    public void close()
            throws com.sun.star.io.IOException, com.sun.star.uno.RuntimeException {
        if (DEBUG) {
            System.out.print("PipeConnection::close() ");
        }
        try {
            closeJNI();
            notifyListeners_close();
            if (DEBUG) {
                System.out.println("done");
            }
        } catch (com.sun.star.io.IOException e) {
            notifyListeners_error(e);
            throw e;
        }
    }

    /**
     * Gives a description of the connection.
     *
     * @return the description.
     * @see com.sun.star.connection.XConnection#getDescription
     */
    @Override
    public String getDescription() throws com.sun.star.uno.RuntimeException {
        return jni.getDescription();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
