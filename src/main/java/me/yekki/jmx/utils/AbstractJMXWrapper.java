package me.yekki.jmx.utils;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Predicate;

public abstract class AbstractJMXWrapper implements JMXWrapper {


    protected ObjectName service;

    // active (if any) mbean server connection
    protected MBeanServerConnection connection;

    // Root MBean for configurations
    protected ObjectName domainConfigRoot = null;

    // Root MBean for configurations
    protected ObjectName domainRuntimeRoot = null;

    @Override
    public Predicate<ObjectName> getNamePredicate(String name) {

        return o->{

            try {

                return getAttribute(o, "Name").equals(name);
            }
            catch(Exception e) {
                return false;
            }
        };
    }

    @Override
    public ArrayList<String> getManagedServerNames() throws Exception {
        try {
            ArrayList<String> result = new ArrayList<String>();

            ObjectName domainMBean = (ObjectName) getAttribute(service, "DomainConfiguration");
            String adminServerName = (String) getAttribute(domainMBean, "AdminServerName");

            ObjectName[] serverRuntimes = (ObjectName[]) getAttribute(domainMBean, "Servers");
            for (int i = 0; i < serverRuntimes.length; i++) {
                String tmp = serverRuntimes[i].getKeyProperty("Name");
                if (!tmp.equals(adminServerName))
                    result.add(tmp);
            }

            return result;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:getManagedServerNames: " + ex.getMessage());
        }
    }

    @Override
    public ArrayList<ObjectName> getManagedServerObjectNames() throws Exception {
        try {
            ArrayList<ObjectName> result = new ArrayList<ObjectName>();


            ObjectName domainMBean = (ObjectName) getAttribute(service, "DomainConfiguration");
            String adminServerName = (String) getAttribute(domainMBean, "AdminServerName");

            ObjectName[] serverRuntimes = (ObjectName[]) getAttribute(domainMBean, "Servers");
            for (int i = 0; i < serverRuntimes.length; i++) {
                String tmp = serverRuntimes[i].getKeyProperty("Name");
                if (!tmp.equals(adminServerName))
                    result.add(serverRuntimes[i]);
            }

            return result;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:getManagedServerObjectNames: " + ex.getMessage());
        }
    }

    @Override
    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws Exception {
        try {
            // todo: logging  - JMXWrapperRemote:invoke called for "+name+" : "+ operationName +" !");

            if (params == null)
                params = new Object[0];
            if (signature == null)
                signature = new String[0];

            // do INVOKE
            return getConnection().invoke(name, operationName, params, signature);

        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:invoke: " + ex.getMessage());
        }
    }

    @Override
    public void setAttribute(ObjectName name, Attribute myAttribute) throws Exception {
        try {
            // todo: logging  - JMXWrapperRemote:setAttribute called for "+name+" : "+ attributeName +" !");

            // do INVOKE
            getConnection().setAttribute(name, myAttribute);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("PROBLEM with JMXWrapper:getAttribute: " + ex.getMessage());
        }
    }

    @Override
    public Object getAttribute(ObjectName name, String attributeName) throws Exception {
        try {
            // todo: logging  - JMXWrapperLocal:getAttribute called for "+name+" : "+ attributeName +" !");

            // do INVOKE
            return getConnection().getAttribute(name, attributeName);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("PROBLEM with JMXWrapper:getAttribute: " + ex.getMessage());
        }
    }

    @Override
    public void setAttribute(ObjectName name, String attributeName, Object attributeValue) throws Exception {
        try {
            // todo: logging  - JMXWrapperRemote:setAttribute called for "+name+" : "+ attributeName +" !");

            // do INVOKE
            Attribute myAttribute = new Attribute(attributeName, attributeValue);
            getConnection().setAttribute(name, myAttribute);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("PROBLEM with JMXWrapper:getAttribute: " + ex.getMessage());
        }
    }

    public Hashtable<String, String> getMainServerDomainValues() throws Exception {
        try {
            Hashtable<String, String> result = new Hashtable<String, String>();
            ObjectName domainMBean = (ObjectName) getAttribute(service, "DomainConfiguration");

            String serverName = (String) getAttribute(service, "ServerName");

            String adminServerName = (String) getAttribute(domainMBean, "AdminServerName");
            String domainName = domainMBean.getKeyProperty("Name");
            String domainRoot = (String) getAttribute(domainMBean, "RootDirectory");

            result.put("serverName", serverName);
            result.put("adminServerName", adminServerName);
            result.put("domainName", domainName);
            result.put("domainRoot", domainRoot);
            result.put("domainBase", domainRoot.substring(0, domainRoot.length() - (domainName.length() + 1)));

            // check if server is adminserver
            try {
                ObjectName serverRuntime = (ObjectName) getAttribute(service, "ServerRuntime");
                result.put("connectedToAdminServer", getAttribute(serverRuntime, "AdminServer").toString());
            } catch (Exception ex) {
                System.out.println("Uppps: Attribute AdminServer not found on (" + service.toString() + ")!\n");
                result.put("connectedToAdminServer", "false");
            }

            return result;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:getMainServerDomainValues: " + ex.getMessage());
        }
    }

    public String getServerState(String serverName) throws Exception {
        try {

            //ObjectName serverRuntimeObjectName = new ObjectName("com.bea:Location=" +serverName + ",Name=" + serverName + ",Type=ServerRuntime");
            ObjectName serverRuntimeObjectName = new ObjectName("com.bea:Name=" + serverName + ",Type=ServerLifeCycleRuntime");

            return (String) getAttribute(serverRuntimeObjectName, "State");
        } catch (Exception ex) {
            return "_UNKNOWN_ERROR_";
        }
    }

    @Override
    public void disconnectFromAdminServer() throws Exception {
        // nothing to do in local wrapper !!
        return;
    }

    @Override
    public ObjectName getDomainConfigRoot() {
        return domainConfigRoot;
    }

    @Override
    public ObjectName getDomainRuntimeRoot() {
        return domainRuntimeRoot;
    }

    @Override
    public ObjectName getService() {
        return service;
    }

    @Override
    public MBeanServerConnection getConnection() {
        return connection;
    }

    @Override
    public JMXConnector getConnector() {
        throw new RuntimeException("Not supported in local wrapper");
    }

}
