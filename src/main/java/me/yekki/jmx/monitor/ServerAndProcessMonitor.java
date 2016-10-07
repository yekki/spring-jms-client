package me.yekki.jmx.monitor;

import me.yekki.jmx.administration.JMSAdministration;
import me.yekki.jmx.administration.ServerAdministration;
import me.yekki.jmx.monitor.impl.AbstractMonitor;
import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.ObjectName;
import java.io.PrintStream;
import java.util.HashMap;

public class ServerAndProcessMonitor extends AbstractMonitor{

    private ServerAdministration serverAdmin;

    public ServerAndProcessMonitor(JMXWrapper wrapper, PrintStream out) {

        super(wrapper, out);
        try {
            serverAdmin = new ServerAdministration(wrapper);
        }
        catch (WLSJMXException ae) {
            System.err.println(ae.getMessage());
        }
    }

    public void printServerSummaryInformation(String serverName) throws WLSJMXException {


        try {
            ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);

            if (serverRuntime != null) {
                out.println("Server Summary:");

                printer.print(new String[][]{
                        new String[]{"Name", "State", "HealthState"},
                        new String[]{getAttribute(serverRuntime, "Name"), getAttribute(serverRuntime, "State"), getHealthStateInformation((weblogic.health.HealthState) jmxWrapper.getAttribute(serverRuntime, "HealthState"))},

                });
            }

            ObjectName jvmRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "JVMRuntime");

            if (jvmRuntime != null) {
                out.println("JVM Summary:");
                printer.print(new String[][]{
                        new String[]{"HeapFreeCurrent"},
                        new String[]{getAttribute(jvmRuntime, "HeapFreeCurrent")},

                });
            }

            ObjectName jtaRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "JTARuntime");

            if (jtaRuntime != null) {
                out.println("JTA Summary:");
                printer.print(new String[][]{
                        new String[]{"HealthState", "TransactionTotalCount"},
                        new String[]{getHealthStateInformation((weblogic.health.HealthState) jmxWrapper.getAttribute(jtaRuntime, "HealthState")), getAttribute(jtaRuntime, "TransactionTotalCount")},

                });
            }

            ObjectName threadPoolRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "ThreadPoolRuntime");

            if (threadPoolRuntime != null) {
                out.println("ThreadPool Summary:");
                printer.print(new String[][]{
                        new String[]{"HealthState", "CompletedRequestCount"},
                        new String[]{getHealthStateInformation((weblogic.health.HealthState) jmxWrapper.getAttribute(threadPoolRuntime, "HealthState")), getAttribute(threadPoolRuntime, "CompletedRequestCount")},

                });
            }
        }
        catch(Exception e) {
            throw new WLSJMXException("Error in printServerSummaryInformation : " + e.getMessage());
        }
    }

    public void printServerJVMRuntimeInformation(String serverName) throws Exception {

        try {
            ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);

            if (serverRuntime != null) {
                ObjectName jvmRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "JVMRuntime");

                if (jvmRuntime != null) {

                    out.println(String.format("Server %s JVM Summary:", serverName));

                    printer.print(new String[][]{
                            new String[]{"JavaVendor", "JavaVersion", "HeapFreeCurrent", "HeapFreePercent", "HeapSizeCurrent", "Uptime"},
                            new String[]{getAttribute(jvmRuntime, "JavaVendor"), getAttribute(jvmRuntime, "JavaVersion"), getAttribute(jvmRuntime, "HeapFreeCurrent"), getAttribute(jvmRuntime, "HeapFreePercent"), getAttribute(jvmRuntime, "HeapSizeCurrent"), getAttribute(jvmRuntime, "Uptime")},
                    });
                }
            }
        }
        catch (Exception e) {
            throw new WLSJMXException("Error in printServerJVMRuntimeInformation : " + e.getMessage());
        }
    }

    public void printServerBasicInformation(String serverName) throws Exception {

        ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);

        if (serverRuntime != null) {

            out.println(String.format("Server %s Basic Summary:", serverName));

            printer.print(new String[][]{
                    new String[]{"Name", "State", "SocketsOpenedTotalCount", "OpenSocketsCurrentCount", "AdminServer", "HealthState"},
                    new String[]{serverName, getAttribute(serverRuntime, "State"), getAttribute(serverRuntime, "SocketsOpenedTotalCount"), getAttribute(serverRuntime, "OpenSocketsCurrentCount"), getAttribute(serverRuntime, "AdminServer"), getHealthStateInformation((weblogic.health.HealthState) jmxWrapper.getAttribute(serverRuntime, "HealthState"))},
            });
        }
    }

    public void printThreadDump(String serverName) throws WLSJMXException {
        try {
            // get the server runtime(!)
            ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);

            if (serverRuntime !=null) {
                ObjectName jvmRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "JVMRuntime");

                if (jvmRuntime != null) {
                    out.println(getAttribute(jvmRuntime, "ThreadStackDump"));
                }
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error while printThreadDump of server " + serverName + " : " + ex.getMessage());
        }
    }
}
