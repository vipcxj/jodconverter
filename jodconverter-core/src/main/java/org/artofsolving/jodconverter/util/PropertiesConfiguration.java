/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.artofsolving.jodconverter.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.office.OfficeUtils;
import org.artofsolving.jodconverter.process.MacProcessManager;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.PureJavaProcessManager;
import org.artofsolving.jodconverter.process.UnixProcessManager;
import org.artofsolving.jodconverter.process.WindowsProcessManager;

/**
 *
 * @author Administrator
 */
public class PropertiesConfiguration {

    private static final Logger LOGGER = Logger.getLogger(PropertiesConfiguration.class.getName());

    private final static String CONF_OFFICE_HOME = "office.home";
    private final static String CONF_PROCESS_MANAGER = "process.manager";
    private final static String CONF_PROCESS_PREKILL = "process.prekill";
    private final static String CONF_CONNECT_PORT = "connect.port";
    private final static String CONF_CONNECT_PORTS = "connect.ports";
    private final static String CONF_CONNECT_PROTOCOL = "connect.protocol";
    private final static String CONF_CONNECT_PIPE_NAME = "connect.pipe.name";
    private final static String CONF_CONNECT_PIPE_NAMES = "connect.pipe.names";
    private final static String CONF_CONNECT_ONSTART = "connect.onstart";

    private ProcessManager processManager = null;
    private static volatile String pipePath = null;
    private static volatile String binPath = null;

    private final Properties properties;

    public PropertiesConfiguration() {
        Properties defaultProperties = new Properties();
        InputStream is = getClass().getResourceAsStream("/jodconverter.properties");
        if (is != null) {
            try {
                defaultProperties.load(is);
            } catch (IOException ex) {
                LOGGER.warning("Unable to load configure file jodconverter.properties");
            }
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
        properties = new Properties(defaultProperties);
    }

    public void load(Properties properties, String prefix) {
        Enumeration<?> names = properties.propertyNames();
        while (names.hasMoreElements()) {
            Object oKey = names.nextElement();
            String key = oKey.toString();
            if (prefix == null || key.startsWith(prefix)) {
                String newkey = prefix != null ? key.substring(prefix.length()) : key;
                this.properties.setProperty(newkey, properties.getProperty(key));
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public File getOfficeHome() {
        String officeHome = properties.getProperty(CONF_OFFICE_HOME);
        if (officeHome != null) {
            return new File(officeHome);
        } else {
            return OfficeUtils.getDefaultOfficeHome();
        }
    }

    public void setOfficeHome(File file) {
        setOfficeHome(file != null ? file.getAbsolutePath() : null);
    }

    public void setOfficeHome(String path) {
        if (path != null) {
            properties.setProperty(CONF_OFFICE_HOME, path);
        } else {
            properties.remove(CONF_OFFICE_HOME);
        }
    }

    private int _getPort(int defaultPort) {
        String pPort = properties.getProperty(CONF_CONNECT_PORT);
        Integer portNumber = null;
        if (pPort != null) {
            try {
                portNumber = Integer.parseInt(pPort);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid port number \"{0}\".", pPort);
                portNumber = defaultPort;
            }
        }
        if (portNumber == null) {
            portNumber = defaultPort;
        }
        return portNumber;
    }

    private int[] _getPorts(int[] defaultPorts) {
        int[] portNumbers = null;
        String pPorts = properties.getProperty(CONF_CONNECT_PORTS);
        if (pPorts != null) {
            String[] pArrPorts = pPorts.split(",");
            portNumbers = new int[pArrPorts.length];
            try {
                for (int i = 0; i < portNumbers.length; i++) {
                    portNumbers[i] = Integer.parseInt(pArrPorts[i]);
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid port numbers \"{0}\".", pPorts);
                portNumbers = defaultPorts;
            }
        }
        if (portNumbers == null) {
            portNumbers = defaultPorts;
        }
        return portNumbers;
    }

    public int getPort() {
        int portNumber = _getPort(-1);
        if (portNumber == -1) {
            int[] portNumbers = _getPorts(null);
            if (portNumbers != null && portNumbers.length > 0) {
                portNumber = portNumbers[0];
            } else {
                portNumber = 2002;
            }
        }
        return portNumber;
    }

    public void setPort(int port) {
        properties.setProperty(CONF_CONNECT_PORT, Integer.toString(port));
    }

    public int[] getPorts() {
        int[] portNumbers = _getPorts(null);
        if (portNumbers == null || portNumbers.length == 0) {
            portNumbers = new int[]{_getPort(2002)};
        }
        return portNumbers;
    }

    public void setPorts(int[] ports) {
        if (ports != null && ports.length > 0) {
            String[] strPorts = new String[ports.length];
            for (int i = 0; i < ports.length; i++) {
                strPorts[i] = Integer.toString(ports[i]);
            }
            properties.setProperty(CONF_CONNECT_PORTS, String.join(",", strPorts));
        } else {
            properties.remove(CONF_CONNECT_PORTS);
        }

    }

    private String _getPipeName(String defaultName) {
        return properties.getProperty(CONF_CONNECT_PIPE_NAME, defaultName);
    }

    private String[] _getPipeNames(String[] defaultNames) {
        String[] pipeNames = null;
        String pPipeNames = properties.getProperty(CONF_CONNECT_PIPE_NAMES);
        if (pPipeNames != null) {
            pipeNames = pPipeNames.split(",");
        }
        if (pipeNames == null) {
            pipeNames = defaultNames;
        }
        return pipeNames;
    }

    public String getPipeName() {
        String pipeName = _getPipeName(null);
        if (pipeName == null) {
            String[] pipeNames = _getPipeNames(null);
            if (pipeNames != null && pipeNames.length > 0) {
                pipeName = pipeNames[0];
            }
        }
        if (pipeName == null) {
            pipeName = "office";
        }
        return pipeName;
    }

    public void setPipeName(String pipeName) {
        if (pipeName != null) {
            properties.setProperty(CONF_CONNECT_PIPE_NAME, pipeName);
        } else {
            properties.remove(CONF_CONNECT_PIPE_NAME);
        }
    }

    public String[] getPipeNames() {
        String[] pipeNames = _getPipeNames(null);
        if (pipeNames == null || pipeNames.length == 0) {
            pipeNames = new String[]{_getPipeName("office")};
        }
        return pipeNames;
    }

    public void setPipeNames(String[] pipeNames) {
        if (pipeNames != null && pipeNames.length > 0) {
            properties.setProperty(CONF_CONNECT_PIPE_NAMES, String.join(",", pipeNames));
        } else {
            properties.remove(CONF_CONNECT_PIPE_NAMES);
        }
    }

    private static OfficeConnectionProtocol toProtocol(String strProtocol) {
        OfficeConnectionProtocol[] protocols = OfficeConnectionProtocol.values();
        for (OfficeConnectionProtocol protocol : protocols) {
            if (protocol.toString().equalsIgnoreCase(strProtocol)) {
                return protocol;
            }
        }
        return null;
    }

    public OfficeConnectionProtocol getProtocol() {
        OfficeConnectionProtocol protocol;
        String pProtocol = properties.getProperty(CONF_CONNECT_PROTOCOL);
        if (pProtocol != null) {
            protocol = toProtocol(pProtocol);
            if (protocol == null) {
                LOGGER.log(Level.WARNING, "Invalid protocol type \"{0}\", use socket instead.", pProtocol);
                protocol = OfficeConnectionProtocol.SOCKET;
            }
        } else {
            protocol = OfficeConnectionProtocol.SOCKET;
        }
        if (protocol == null) {
            protocol = OfficeConnectionProtocol.SOCKET;
        }
        if (protocol == OfficeConnectionProtocol.PIPE) {
            loadPipeLibrary(getOfficeHome());
        }
        return protocol;
    }

    private static void replaceLibraryPath(String prePath, String path) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (prePath == null && path == null) {
            return;
        }
        String strPaths = System.getProperty("java.library.path");
        if (strPaths != null) {
            String[] arrPaths = strPaths.split(";");
            for (String arrPath : arrPaths) {
                if (new File(arrPath).equals(new File(path))) {
                    return;
                }
            }
        }
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);
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
    }

    private static synchronized boolean loadPipeLibrary(File officeHome) {
        try {
            String pipePathNow = OfficeUtils.getJPipePath(officeHome);
            String binPathNow = OfficeUtils.getOfficeBinDir(officeHome).getAbsolutePath();
            if (!Objects.equals(pipePathNow, pipePath)) {
                Runtime.getRuntime().load(pipePathNow);
                pipePath = pipePathNow;
            }
            if (!Objects.equals(binPathNow, binPath)) {
                replaceLibraryPath(binPath, binPathNow);
                binPath = binPathNow;
            }
            return true;
        } catch (UnsatisfiedLinkError e) {
            LOGGER.log(Level.WARNING, e, new Supplier<String>() {

                @Override
                public String get() {
                    return "Pipe is not avaialbe.";
                }
            });
            return false;
        } catch (NoSuchFieldException e) {
            LOGGER.log(Level.WARNING, e, new Supplier<String>() {

                @Override
                public String get() {
                    return "Pipe is not avaialbe.";
                }
            });
            return false;
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e, new Supplier<String>() {

                @Override
                public String get() {
                    return "Pipe is not avaialbe.";
                }
            });
            return false;
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.WARNING, e, new Supplier<String>() {

                @Override
                public String get() {
                    return "Pipe is not avaialbe.";
                }
            });
            return false;
        }
    }

    public void setProtocol(OfficeConnectionProtocol protocol) {
        if (protocol != null) {
            properties.setProperty(CONF_CONNECT_PROTOCOL, protocol.toString());
        } else {
            properties.remove(CONF_CONNECT_PROTOCOL);
        }
    }

    private ProcessManager findBestProcessManager() {
        if (PlatformUtils.isWindows()) {
            WindowsProcessManager windowsProcessManager = new WindowsProcessManager();
            if (windowsProcessManager.isUsable()) {
                return windowsProcessManager;
            } else {
                return new PureJavaProcessManager();
            }
        } else if (PlatformUtils.isLinux()) {
            return new UnixProcessManager();
        } else if (PlatformUtils.isMac()) {
            return new MacProcessManager();
        } else {
            return new PureJavaProcessManager();
        }
    }

    private boolean isBestProcessManager(ProcessManager manager) {
        if (PlatformUtils.isWindows()) {
            if (manager instanceof WindowsProcessManager) {
                return ((WindowsProcessManager) manager).isUsable();
            } else {
                return false;
            }
        } else if (PlatformUtils.isLinux()) {
            return (manager instanceof UnixProcessManager);
        } else if (PlatformUtils.isMac()) {
            return (manager instanceof MacProcessManager);
        } else {
            return (manager instanceof PureJavaProcessManager);
        }
    }

    public ProcessManager getProcessManager() {
        if (processManager == null) {
            String pProcessManager = properties.getProperty(CONF_PROCESS_MANAGER);
            if (pProcessManager != null) {
                try {
                    Class<?> managerClass = Class.forName(pProcessManager.trim());
                    if (ProcessManager.class.isAssignableFrom(managerClass)) {
                        Object managerInstance = managerClass.newInstance();
                        processManager = (ProcessManager) managerInstance;
                    }
                } catch (ClassNotFoundException ex) {
                    LOGGER.log(Level.WARNING, "Invalid process manager impl \"{0}\".", pProcessManager);
                } catch (InstantiationException ex) {
                    LOGGER.log(Level.WARNING, "Unable to create the instance of process manager impl \"{0}\".", pProcessManager);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, "No access to create the instance of process manager impl \"{0}\".", pProcessManager);
                }
            }
        } else {
            String pProcessManager = properties.getProperty(CONF_PROCESS_MANAGER);
            if (pProcessManager == null && !isBestProcessManager(processManager)) {
                processManager = null;
            } else if (pProcessManager != null) {
                try {
                    Class<?> managerClass = Class.forName(pProcessManager.trim());
                    if (!managerClass.isAssignableFrom(processManager.getClass())) {
                        if (ProcessManager.class.isAssignableFrom(managerClass)) {
                            Object managerInstance = managerClass.newInstance();
                            processManager = (ProcessManager) managerInstance;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.WARNING, "Invalid process manager impl \"{0}\".", pProcessManager);
                } catch (InstantiationException e) {
                    LOGGER.log(Level.WARNING, "Unable to create the instance of process manager impl \"{0}\".", pProcessManager);
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, "No access to create the instance of process manager impl \"{0}\".", pProcessManager);
                }
            }
        }
        if (processManager == null) {
            processManager = findBestProcessManager();
        }
        return processManager;
    }

    public void setProcessManager(ProcessManager manager) {
        this.processManager = manager;
        if (manager != null) {
            properties.put(CONF_PROCESS_MANAGER, manager.getClass().getName());
        } else {
            properties.remove(CONF_PROCESS_MANAGER);
        }
    }

    private boolean toBoolean(String value) {
        value = value.trim();
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Unable to convert " + value + " to a boolean type.");
    }

    public boolean isProcessPreKill() {
        String pPreKill = properties.getProperty(CONF_PROCESS_PREKILL);
        boolean preKill = true;
        if (pPreKill != null) {
            try {
                preKill = toBoolean(pPreKill);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid value \"{0}\" of property {1}, use true instead.", new String[]{pPreKill, CONF_PROCESS_PREKILL});
                preKill = true;
            }
        }
        return preKill;
    }

    public void setProcessPreKill(boolean preKill) {
        properties.setProperty(CONF_PROCESS_PREKILL, Boolean.toString(preKill));
    }

    public boolean isConnectOnStart() {
        String pOnStart = properties.getProperty(CONF_CONNECT_ONSTART);
        boolean onStart = true;
        if (pOnStart != null) {
            try {
                onStart = toBoolean(pOnStart);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid value \"{0}\" of property {1}, use true instead.", new String[]{pOnStart, CONF_CONNECT_ONSTART});
                onStart = true;
            }
        }
        return onStart;
    }

    public void setConnectOnStart(boolean connectOnStart) {
        properties.setProperty(CONF_CONNECT_ONSTART, Boolean.toString(connectOnStart));
    }
}
