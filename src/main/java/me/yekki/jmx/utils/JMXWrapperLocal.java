package me.yekki.jmx.utils;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

public class JMXWrapperLocal extends AbstractJMXWrapper {

    public void connectToAdminServer() throws Exception {
        try {
            InitialContext ctx = new InitialContext();
            connection = (MBeanServer) ctx.lookup("java:comp/jmx/runtime");

            // service = new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
            service = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");

            domainConfigRoot = (ObjectName) getAttribute(service, "DomainConfiguration");
            domainRuntimeRoot = (ObjectName) getAttribute(service, "DomainRuntime");

        } catch (Exception ex) {
            throw new Exception("PROBLEM with JMXWrapperLocal:initConnection: " + ex.getMessage());
        }
    }

    @Override
    public ObjectName getServerRuntime(String serverName) throws Exception {
        try {

            return new ObjectName(String.format("com.bea:Name=%s,Type=ServerRuntime", serverName));
        } catch (Exception ex) {
            return null;
        }
    }

}
