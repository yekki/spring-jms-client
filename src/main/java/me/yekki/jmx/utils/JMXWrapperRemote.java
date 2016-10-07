/**
 * WebLogic Automation Book Source Code (JMX sources)
 * <p>
 * This file is part of the WLS-Automation book sourcecode software distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Martin Heinzl
 * Copyright (C) 2013 MH-EnterpriseConsulting, All rights reserved.
 */
package me.yekki.jmx.utils;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Predicate;


public class JMXWrapperRemote extends AbstractJMXWrapper {

    // connector
    private JMXConnector connector;

    // is wrapper initialized ?
    private boolean initialized = false;

    // protocol information: will be set during initialization
    private String protocol = null;
    private String hostname = null;
    private String portString = null;
    private String connectionuser = null;
    private String connectionpassword = null;

    private boolean isEdit = false;
    private boolean isDomainRuntime = false;

    private void initConnection(boolean editmode,
                                boolean domainRuntime,
                                String url,
                                String username,
                                String password) throws Exception {
        try {
            isEdit = editmode;
            isDomainRuntime = domainRuntime;
            connectionuser = username;
            connectionpassword = password;

            // get protocol from URL string
            protocol = url.substring(0, url.indexOf("://"));

            // get hostname from URL string
            hostname = url.substring(url.indexOf("//") + 2, url.indexOf(":", protocol.length() + 3));

            // get port from URL string
            portString = url.substring(url.indexOf(":", protocol.length() + 3) + 1, url.length());

            Integer portInteger = Integer.valueOf(portString);
            int port = portInteger.intValue();
            String jndiroot = "/jndi/";

            String mserver = null;

            if (isEdit)
                mserver = "weblogic.management.mbeanservers.edit";
            else if (isDomainRuntime)
                mserver = "weblogic.management.mbeanservers.domainruntime";
            else
                mserver = "weblogic.management.mbeanservers.runtime";

            JMXServiceURL serviceURL = new JMXServiceURL(protocol, hostname,
                    port, jndiroot + mserver);

            Hashtable<String, String> h = new Hashtable<String, String>();
            h.put(Context.SECURITY_PRINCIPAL, connectionuser);
            h.put(Context.SECURITY_CREDENTIALS, connectionpassword);
            h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
            connector = JMXConnectorFactory.connect(serviceURL, h);
            connection = connector.getMBeanServerConnection();

            // success
            initialized = true;
        } catch (Exception ex) {
            initialized = false;
            throw new Exception("PROBLEM with JMXWrapper:initConnection: " + ex.getMessage());
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    private ObjectName startEditSession() throws Exception {
        try {
            // do not set domain runtime
            domainRuntimeRoot = null;

            // Get the object name for ConfigurationManagerMBean.
            ObjectName cfgMgr = (ObjectName) getAttribute(service, "ConfigurationManager");
            // The startEdit operation returns a handle to DomainMBean, which is the root of the edit hierarchy.
            ObjectName domainConfigRoot = (ObjectName) invoke(cfgMgr,
                    "startEdit", new Object[]{new Integer(60000),
                            new Integer(120000)}, new String[]{"java.lang.Integer",
                            "java.lang.Integer"});
            if (domainConfigRoot == null) {
                // Couldn't get the lock
                throw new Exception("Somebody else is editing already");
            }
            return domainConfigRoot;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:startEditSession: " + ex.getMessage());
        }
    }

    private ObjectName startNONEditSession() throws Exception {
        try {
            ObjectName domainMBean = (ObjectName) getAttribute(service, "DomainConfiguration");

            if (isDomainRuntime)
                domainRuntimeRoot = (ObjectName) getAttribute(service, "DomainRuntime");

            return domainMBean;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:startNONEditSession: " + ex.getMessage());
        }
    }


    public void connectToAdminServer(boolean edit,
                                     boolean domainRuntime,
                                     String username,
                                     String password,
                                     String url
    ) throws Exception {
        try {

            initConnection(edit, domainRuntime, url, username, password);

            if (edit)
                service = new ObjectName("com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean");
            else if (domainRuntime)
                service = new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
            else
                service = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");

            if (edit)
                domainConfigRoot = startEditSession();
            else
                domainConfigRoot = startNONEditSession();
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:connectToAdminServer: " + ex.getMessage());
        }
    }

    public void disconnectFromAdminServer() throws Exception {
        try {
            // Close the connection with the MBean server.
            domainConfigRoot = null;
            try {
                connector.close();
            } catch (Exception ex) {
                // ignore
            }

            connector = null;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapperRemote:disconnectFromAdminServer: " + ex.getMessage());
        }
    }


    private ObjectName activate() throws Exception {

        return (ObjectName)invoke((ObjectName) getAttribute(service, "ConfigurationManager"), "activate", new Object[]{new Long(120000)}, new String[]{"java.lang.Long"});
    }

    private ObjectName cancelChanges() throws Exception {

        return (ObjectName)invoke((ObjectName) getAttribute(service, "ConfigurationManager"), "cancelEdit", new Object[]{}, new String[]{});
    }


    public void disconnectFromAdminServer(boolean activate) throws Exception {
        try {
            if (isEdit)
                if (activate)
                    activate();
                else
                    cancelChanges();

            // Close the connection with the MBean server.
            domainConfigRoot = null;
            try {
                connector.close();
            } catch (Exception ex) {
                // ignore
            }

            connector = null;
        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapper:disconnectFromAdminServer: " + ex.getMessage());
        }
    }


    public ObjectName getServerRuntime(String serverName) throws Exception {

        try {
            if (isDomainRuntime)
                return new ObjectName(String.format("com.bea:Name=%s,Location=%s,Type=ServerRuntime", serverName, serverName));
            else
                return new ObjectName(String.format("com.bea:Name=%s,Type=ServerRuntime", serverName));
        } catch (Exception ex) {
            return null;
        }
    }


    public JMXConnector getConnector() {
        return connector;
    }

    public String getRemoteProtocol() {
        return protocol;
    }

    public String getRemoteHostName() {
        return hostname;
    }

    public String getRemotePort() {
        return portString;
    }

    public String getRemoteUser() {
        return connectionuser;
    }

    public String getRemotePassword() {
        return connectionpassword;
    }

}