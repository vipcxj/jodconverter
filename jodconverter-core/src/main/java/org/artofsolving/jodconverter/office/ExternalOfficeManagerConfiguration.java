//
// JODConverter - Java OpenDocument Converter
// Copyright 2009 Art of Solving Ltd
// Copyright 2004-2009 Mirko Nasato
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter.office;

import java.util.Properties;

public class ExternalOfficeManagerConfiguration extends AbstractOfficeManagerConfiguration {

    public ExternalOfficeManagerConfiguration() {
        super();
    }

    @Override
    public ExternalOfficeManagerConfiguration load(Properties properties, String prefix) {
        return (ExternalOfficeManagerConfiguration) super.load(properties, prefix);
    }

    public ExternalOfficeManagerConfiguration setConnectionProtocol(OfficeConnectionProtocol connectionProtocol) {
        propertiesUtils.setProtocol(connectionProtocol);
        return this;
    }

    public ExternalOfficeManagerConfiguration setPortNumber(int portNumber) {
        propertiesUtils.setPort(portNumber);
        return this;
    }

    public ExternalOfficeManagerConfiguration setPipeName(String pipeName) {
        propertiesUtils.setPipeName(pipeName);
        return this;
    }

    public ExternalOfficeManagerConfiguration setConnectOnStart(boolean connectOnStart) {
        propertiesUtils.setConnectOnStart(connectOnStart);
        return this;
    }

    public OfficeManager buildOfficeManager() {
        UnoUrl unoUrl = propertiesUtils.getProtocol() == OfficeConnectionProtocol.SOCKET ? UnoUrl.socket(propertiesUtils.getPort()) : UnoUrl.pipe(propertiesUtils.getPipeName());
        return new ExternalOfficeManager(unoUrl, propertiesUtils.isConnectOnStart());
    }

}
