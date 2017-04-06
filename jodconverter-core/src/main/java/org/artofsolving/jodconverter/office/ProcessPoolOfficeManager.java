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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.artofsolving.jodconverter.process.ProcessManager;

class ProcessPoolOfficeManager implements OfficeManager {

    private final BlockingQueue<PooledOfficeManager> pool;

    private final PooledOfficeManager[] pooledManagers;

    private final long taskQueueTimeout;

    private volatile boolean running = false;

    private static final Logger LOGGER = Logger.getLogger(ProcessPoolOfficeManager.class.getName());

    public ProcessPoolOfficeManager(File officeHome, UnoUrl[] unoUrls, File templateProfileDir, long taskQueueTimeout,
            long taskExecutionTimeout, int maxTasksPerProcess, ProcessManager processManager) {
        this(officeHome, unoUrls, templateProfileDir, taskQueueTimeout, taskExecutionTimeout, maxTasksPerProcess,
                processManager, false, true);
    }

    public ProcessPoolOfficeManager(File officeHome, UnoUrl[] unoUrls, File templateProfileDir, long taskQueueTimeout,
            long taskExecutionTimeout, int maxTasksPerProcess, ProcessManager processManager,
            boolean useGnuStyleLongOptions, boolean killExistingProcess) {
        this.taskQueueTimeout = taskQueueTimeout;
        pool = new ArrayBlockingQueue<>(unoUrls.length);
        pooledManagers = new PooledOfficeManager[unoUrls.length];
        for (int i = 0; i < unoUrls.length; i++) {
            PooledOfficeManagerSettings settings = new PooledOfficeManagerSettings(unoUrls[i]);
            settings.setTemplateProfileDir(templateProfileDir);
            settings.setOfficeHome(officeHome);
            settings.setTaskExecutionTimeout(taskExecutionTimeout);
            settings.setMaxTasksPerProcess(maxTasksPerProcess);
            settings.setProcessManager(processManager);
            settings.setUseGnuStyleLongOptions(useGnuStyleLongOptions);
            settings.setKillExistingProcess(killExistingProcess);
            pooledManagers[i] = new PooledOfficeManager(settings);
        }
        LOGGER.log(Level.INFO, "ProcessManager implementation is {0}", processManager.getClass().getSimpleName());
    }

    @Override
    public synchronized void start() throws OfficeException {
        for (int i = 0; i < pooledManagers.length; i++) {
            pooledManagers[i].start();
            releaseManager(pooledManagers[i]);
        }
        running = true;
    }

    @Override
    public void execute(OfficeTask task) throws IllegalStateException, OfficeException {
        if (!running) {
            throw new IllegalStateException("this OfficeManager is currently stopped");
        }
        PooledOfficeManager manager = null;
        try {
            manager = acquireManager();
            if (manager == null) {
                throw new OfficeException("no office manager available");
            }
            manager.execute(task);
        } finally {
            if (manager != null) {
                releaseManager(manager);
            }
        }
    }

    @Override
    public synchronized void stop() throws OfficeException {
        running = false;
        LOGGER.info("stopping");
        pool.clear();
        for (int i = 0; i < pooledManagers.length; i++) {
            pooledManagers[i].stop();
        }
        LOGGER.info("stopped");
    }

    private PooledOfficeManager acquireManager() {
        try {
            return pool.poll(taskQueueTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            throw new OfficeException("interrupted", interruptedException);
        }
    }

    private void releaseManager(PooledOfficeManager manager) {
        try {
            pool.put(manager);
        } catch (InterruptedException interruptedException) {
            throw new OfficeException("interrupted", interruptedException);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Running :").append(running);
        sb.append("\n Managers : ").append(pooledManagers.length);
        for (int i = 0; i < pooledManagers.length; i++) {
            sb.append("\n   Manager ").append(i);
            sb.append("\n   ").append(pooledManagers[i].toString());
        }
        return sb.toString();
    }

    @Override
    public OfficeConnection[] getConnection() {
        OfficeConnection[] result = new OfficeConnection[pooledManagers.length];
        for (int i = 0; i < pooledManagers.length; i++) {
            result[i] = pooledManagers[i].getConnection()[0];
        }
        return result;
    }
}
