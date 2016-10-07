package me.yekki.jmx.monitor;

import me.yekki.jmx.administration.JMSAdministration;
import me.yekki.jmx.monitor.impl.AbstractMonitor;
import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.ObjectName;
import java.io.PrintStream;
import java.util.Arrays;

public class JMSMonitor extends AbstractMonitor {

    protected JMSAdministration jmsadmin;

    public JMSMonitor(JMXWrapper wrapper, PrintStream out) {

        super(wrapper, out);
        try {
            jmsadmin = new JMSAdministration(wrapper);
        }
        catch (WLSJMXException ae) {
            System.err.println(ae.getMessage());
        }
    }

    public void printJMSRuntimeInformation(String serverName) throws WLSJMXException {
        try {
            ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);


            if (serverRuntime != null) {
                ObjectName jmsRuntime = (ObjectName)jmxWrapper.getAttribute(serverRuntime, "JMSRuntime");
                if (jmsRuntime != null) {

                    out.println("JMS Servers Summary:");
                    printer.print(new String[][] {
                            new String[] {"JMSServersCurrentCount", "JMSServersHighCount", "JMSServersTotalCount"},
                            new String[] {getAttribute(jmsRuntime, "JMSServersCurrentCount"), getAttribute(jmsRuntime, "JMSServersHighCount"), getAttribute(jmsRuntime, "JMSServersTotalCount")},

                    });

                    out.println("JMS Connection Summary:");
                    printer.print(new String[][] {
                            new String[] {"ConnectionsCurrentCount", "ConnectionsHighCount", "ConnectionsTotalCount"},
                            new String[] {getAttribute(jmsRuntime, "ConnectionsCurrentCount"), getAttribute(jmsRuntime, "ConnectionsHighCount"), getAttribute(jmsRuntime, "ConnectionsTotalCount")},

                    });
                }
            }

        } catch (Exception ex) {
            throw new WLSJMXException("Error in printJMSRuntimeInformation : " + ex.getMessage());
        }
    }

    public void printSessionPoolInformation(String serverName, String jmsServer) throws WLSJMXException {
        try {
            ObjectName jmsServerRuntime = jmsadmin.getJMSServerRuntime(jmsServer, serverName);

            if (jmsServerRuntime != null) {
                printer.print(new String[][]{
                        new String[]{"SessionPoolsCurrentCount", "SessionPoolsHighCount", "SessionPoolsTotalCount"},
                        new String[]{getAttribute(jmsServerRuntime, "SessionPoolsCurrentCount"), getAttribute(jmsServerRuntime, "SessionPoolsHighCount"), getAttribute(jmsServerRuntime, "SessionPoolsTotalCount")},

                });
            }

        } catch (Exception ex) {
            throw new WLSJMXException("Error in printSessionPoolInformation : " + ex.getMessage());
        }
    }

    public void printDestinationState(String serverName, String jmsServer, String destination) throws WLSJMXException {
        try {

            ObjectName dest = jmsadmin.getJMSDestinationRuntime(serverName, jmsServer, destination);

            if (dest != null) {
                printer.print(new String[][]{
                        new String[]{"InsertionPausedState", "ConsumptionPausedState", "ProductionPausedState"},
                        new String[]{getAttribute(dest, "InsertionPausedState"), getAttribute(dest, "ConsumptionPausedState"), getAttribute(dest, "ProductionPausedState")},

                });
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error in printDestinationState : " + ex.getMessage());
        }
    }

    public void printAmountOfMessagesInDestination(String serverName, String jmsServer, String destination) throws WLSJMXException {
        try {

            ObjectName dest = jmsadmin.getJMSDestinationRuntime(serverName, jmsServer, destination);

            if (dest != null) {

                printer.print(new String[][]{
                        new String[]{"MessagesCurrentCount", "MessagesPendingCount"},
                        new String[]{getAttribute(dest, "MessagesCurrentCount"), getAttribute(dest, "MessagesPendingCount")},
                });
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error in printJMSRuntimeState : " + ex.getMessage());
        }
    }

    public void printConnectedJMSClients(String serverName) throws WLSJMXException {
        try {

            ObjectName jmsRuntime = (ObjectName) jmxWrapper.getAttribute(jmxWrapper.getServerRuntime(serverName), "JMSRuntime");

            if (jmsRuntime != null) {
                ObjectName[] jmsConnections = (ObjectName[]) jmxWrapper.getAttribute(jmsRuntime, "Connections");

                if (jmsConnections != null) {
                    out.println("Actually the " + serverName + ".jms runtime has " + jmsConnections.length + " connections !");

                    for (ObjectName c:jmsConnections) {

                        printer.print(new String[][]{
                                new String[]{"HostAddress", "ClientID", "SessionsCurrentCount", "SessionsHighCount", "SessionsTotalCount"},
                                new String[]{getAttribute(c, "HostAddress"), getAttribute(c, "ClientID"), getAttribute(c, "SessionsCurrentCount"), getAttribute(c, "SessionsHighCount"), getAttribute(c, "SessionsTotalCount")},
                        });

                        ObjectName[] allConnectionJMSSessions = (ObjectName[]) jmxWrapper.getAttribute(c, "Sessions");

                        Arrays.stream(allConnectionJMSSessions).forEach(s -> {
                            printer.print(new String[][]{
                                    new String[]{"ProducersCurrentCount", "ConsumersCurrentCount", "ConsumersCurrentCount", "MessagesSentCount", "MessagesReceivedCount", "MessagesPendingCount"},
                                    new String[]{getAttribute(s, "ProducersCurrentCount"), getAttribute(s, "ConsumersCurrentCount"), getAttribute(s, "ConsumersCurrentCount"), getAttribute(s, "MessagesSentCount"), getAttribute(s, "MessagesReceivedCount"), getAttribute(s, "MessagesPendingCount")}
                            });
                        });
                    }
                }
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error in printJMSRuntimeState : " + ex.getMessage());
        }
    }

    public void printJMSRuntimeState(String serverName) throws WLSJMXException {
        try {
            ObjectName serverRuntime = jmxWrapper.getServerRuntime(serverName);

            if (serverRuntime != null) {
                ObjectName jmsRuntime = (ObjectName) jmxWrapper.getAttribute(serverRuntime, "JMSRuntime");

                if (jmsRuntime != null) {
                    String name = (String) jmxWrapper.getAttribute(jmsRuntime, "Name");
                    String health = getHealthStateInformation((weblogic.health.HealthState) jmxWrapper.getAttribute(jmsRuntime, "HealthState"));
                    printer.print(new String[][]{
                            new String[]{"JMSRuntime", "HealthState"},
                            new String[]{name, health},
                    });
                }
            }
        } catch (Exception ex) {
            throw new WLSJMXException("Error in printJMSRuntimeState : " + ex.getMessage());
        }
    }
}
