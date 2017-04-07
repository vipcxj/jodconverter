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

/**
 * The PipeConnection implements the <code>XConnection</code> interface and is
 * uses by the <code>PipeConnector</code> and the <code>PipeAcceptor</code>.
 * This class is not part of the provided <code>api</code>.
 *
 * @see com.sun.star.lib.connections.pipe.pipeAcceptor
 * @see com.sun.star.lib.connections.pipe.pipeConnector
 * @see com.sun.star.connection.XConnection
 * @since UDK1.0
 */
public class PipeConnection {

    protected String _aDescription;
    protected long _nPipeHandle;

    // JNI implementation to create the pipe
    private native int createJNI(String name)
            throws com.sun.star.io.IOException;

    // JNI implementation to read from the pipe
    private native int readJNI(/*OUT*/byte[][] bytes, int nBytesToRead)
            throws com.sun.star.io.IOException;

    // JNI implementation to write to the pipe
    private native void writeJNI(byte aData[])
            throws com.sun.star.io.IOException;

    // JNI implementation to flush the pipe
    private native void flushJNI()
            throws com.sun.star.io.IOException;

    // JNI implementation to close the pipe
    private native void closeJNI()
            throws com.sun.star.io.IOException;

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
