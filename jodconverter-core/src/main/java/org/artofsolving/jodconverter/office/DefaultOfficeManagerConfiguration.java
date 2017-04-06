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

import java.io.File;
import java.util.Properties;

import org.artofsolving.jodconverter.process.ProcessManager;

public class DefaultOfficeManagerConfiguration extends AbstractOfficeManagerConfiguration {

    private File templateProfileDir = null;

    private long taskQueueTimeout = 30000L; // 30 seconds

    private long taskExecutionTimeout = 120000L; // 2 minutes

    private int maxTasksPerProcess = 200;

    private boolean useGnuStyleLongOptions;

    public DefaultOfficeManagerConfiguration() {
        super();
    }

    @Override
    public DefaultOfficeManagerConfiguration load(Properties properties, String prefix) {
        return (DefaultOfficeManagerConfiguration) super.load(properties, prefix);
    }

    public DefaultOfficeManagerConfiguration setOfficeHome(String officeHome) {
        propertiesUtils.setOfficeHome(officeHome);
        return this;
    }

    public DefaultOfficeManagerConfiguration setOfficeHome(File officeHome) {
        propertiesUtils.setOfficeHome(officeHome);
        return this;
    }

    public DefaultOfficeManagerConfiguration setConnectionProtocol(OfficeConnectionProtocol connectionProtocol) {
        propertiesUtils.setProtocol(connectionProtocol);
        return this;
    }

    public DefaultOfficeManagerConfiguration setKillExistingProcess(boolean killExistingProcess) {
        propertiesUtils.setProcessPreKill(killExistingProcess);
        return this;
    }

    public DefaultOfficeManagerConfiguration setPortNumber(int portNumber) {
        propertiesUtils.setPort(portNumber);
        return this;
    }

    public DefaultOfficeManagerConfiguration setPortNumbers(int... portNumbers) {
        propertiesUtils.setPorts(portNumbers);
        return this;
    }

    public DefaultOfficeManagerConfiguration setPipeName(String pipeName) {
        propertiesUtils.setPipeName(pipeName);
        return this;
    }

    public DefaultOfficeManagerConfiguration setPipeNames(String... pipeNames) {
        propertiesUtils.setPipeNames(pipeNames);
        return this;
    }

    public DefaultOfficeManagerConfiguration setTemplateProfileDir(File templateProfileDir)
            throws IllegalArgumentException {
        if (templateProfileDir != null) {
            checkArgument("templateProfileDir", templateProfileDir.isDirectory(), "must exist and be a directory");
        }
        this.templateProfileDir = templateProfileDir;
        return this;
    }

    public DefaultOfficeManagerConfiguration setTaskQueueTimeout(long taskQueueTimeout) {
        this.taskQueueTimeout = taskQueueTimeout;
        return this;
    }

    public DefaultOfficeManagerConfiguration setTaskExecutionTimeout(long taskExecutionTimeout) {
        this.taskExecutionTimeout = taskExecutionTimeout;
        return this;
    }

    public DefaultOfficeManagerConfiguration setMaxTasksPerProcess(int maxTasksPerProcess) {
        this.maxTasksPerProcess = maxTasksPerProcess;
        return this;
    }

    public DefaultOfficeManagerConfiguration setProcessManager(ProcessManager processManager) {
        propertiesUtils.setProcessManager(processManager);
        return this;
    }

    public OfficeManager buildOfficeManager() throws IllegalStateException {
        File officeHome = propertiesUtils.getOfficeHome();
        OfficeConnectionProtocol protocol = propertiesUtils.getProtocol();
        int[] ports = propertiesUtils.getPorts();
        String[] pipeNames = propertiesUtils.getPipeNames();
        ProcessManager processManager = propertiesUtils.getProcessManager();
        if (!officeHome.isDirectory()) {
            throw new IllegalStateException("officeHome doesn't exist or is not a directory: " + officeHome);
        } else if (!OfficeUtils.getOfficeExecutable(officeHome).isFile()) {
            throw new IllegalStateException("invalid officeHome: couldn't find "
                    + OfficeUtils.getOfficeExecutable(officeHome));
        }
        if (templateProfileDir != null && !isValidProfileDir(templateProfileDir)) {
            throw new IllegalStateException("invalid templateProfileDir: " + templateProfileDir);
        }

        String forceOptionStyle = System.getProperty("jod.office.gnustyleoptions.force");
        if (forceOptionStyle != null) {
            useGnuStyleLongOptions = Boolean.parseBoolean(forceOptionStyle);
        }

        int numInstances = protocol == OfficeConnectionProtocol.PIPE ? pipeNames.length : ports.length;
        UnoUrl[] unoUrls = new UnoUrl[numInstances];
        for (int i = 0; i < numInstances; i++) {
            unoUrls[i] = (protocol == OfficeConnectionProtocol.PIPE) ? UnoUrl.pipe(pipeNames[i])
                    : UnoUrl.socket(ports[i]);
        }
        return new ProcessPoolOfficeManager(officeHome, unoUrls, templateProfileDir, taskQueueTimeout,
                taskExecutionTimeout, maxTasksPerProcess, processManager, useGnuStyleLongOptions, propertiesUtils.isProcessPreKill());
    }

    private void checkArgument(String argName, boolean condition, String message) throws IllegalArgumentException {
        if (!condition) {
            throw new IllegalArgumentException(argName + " " + message);
        }
    }

    private boolean isValidProfileDir(File profileDir) {
        File setupXcu = new File(profileDir, "user/registry/data/org/openoffice/Setup.xcu");
        return setupXcu.exists();
    }

    public void setUseGnuStyleLongOptions(boolean useGnuStyleLongOptions) {
        this.useGnuStyleLongOptions = useGnuStyleLongOptions;
    }

}
