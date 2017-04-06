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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

class PooledOfficeManager implements OfficeManager {

    private final PooledOfficeManagerSettings settings;

    private final ManagedOfficeProcess managedOfficeProcess;

    private final SuspendableThreadPoolExecutor taskExecutor;

    private volatile boolean stopping = false;

    private int taskCount;

    private Future<?> currentTask;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private OfficeConnectionEventListener connectionEventListener = new OfficeConnectionEventListener() {
        @Override
        public void connected(OfficeConnectionEvent event) {
            taskCount = 0;
            taskExecutor.setAvailable(true);
        }

        @Override
        public void disconnected(OfficeConnectionEvent event) {
            taskExecutor.setAvailable(false);
            if (stopping) {
                // expected
                stopping = false;
            } else {
                logger.warning("connection lost unexpectedly; attempting restart");
                if (currentTask != null) {
                    currentTask.cancel(true);
                }
                managedOfficeProcess.restartDueToLostConnection();
            }
        }
    };

    public PooledOfficeManager(UnoUrl unoUrl) {
        this(new PooledOfficeManagerSettings(unoUrl));
    }

    public PooledOfficeManager(PooledOfficeManagerSettings settings) {
        this.settings = settings;
        managedOfficeProcess = new ManagedOfficeProcess(settings);
        managedOfficeProcess.getConnection().addConnectionEventListener(
                connectionEventListener);
        taskExecutor = new SuspendableThreadPoolExecutor(
                new NamedThreadFactory("OfficeTaskThread"));
    }

    @Override
    public void execute(final OfficeTask task) throws OfficeException {
        Future<?> futureTask = taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (settings.getMaxTasksPerProcess() > 0
                        && ++taskCount == settings.getMaxTasksPerProcess() + 1) {
                    logger.info(String.format(
                            "reached limit of %d maxTasksPerProcess: restarting",
                            settings.getMaxTasksPerProcess()));
                    taskExecutor.setAvailable(false);
                    stopping = true;
                    managedOfficeProcess.restartAndWait();
                    // FIXME taskCount will be 0 rather than 1 at this point
                }
                task.execute(managedOfficeProcess.getConnection());
            }
        });
        currentTask = futureTask;
        try {
            futureTask.get(settings.getTaskExecutionTimeout(),
                    TimeUnit.MILLISECONDS);
        } catch (TimeoutException timeoutException) {
            managedOfficeProcess.restartDueToTaskTimeout();
            throw new OfficeException("task did not complete within timeout",
                    timeoutException);
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof OfficeException) {
                throw (OfficeException) executionException.getCause();
            } else {
                throw new OfficeException("task failed",
                        executionException.getCause());
            }
        } catch (Exception exception) {
            throw new OfficeException("task failed", exception);
        }
    }

    @Override
    public void start() throws OfficeException {
        managedOfficeProcess.startAndWait();
    }

    @Override
    public void stop() throws OfficeException {
        taskExecutor.setAvailable(false);
        stopping = true;
        taskExecutor.shutdownNow();
        managedOfficeProcess.stopAndWait();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPooledOfficeManager Settings :");
        sb.append(settings.toString());
        sb.append("\nManaged Office Process :");
        sb.append(managedOfficeProcess.toString());
        return sb.toString();
    }

    @Override
    public OfficeConnection[] getConnection() {
        OfficeConnection[] result = {managedOfficeProcess.getConnection()};
        return result;
    }
}
