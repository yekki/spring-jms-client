package me.yekki.jmx.monitor.impl;

import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.PrettyPrinter;

import javax.management.ObjectName;
import java.io.PrintStream;

public class AbstractMonitor {

    protected JMXWrapper jmxWrapper;
    protected PrettyPrinter printer;
    protected PrintStream out;

    public AbstractMonitor(JMXWrapper jmxWrapper, PrintStream out) {

        this.jmxWrapper = jmxWrapper;
        printer = new PrettyPrinter(out);
        this.out = out;
    }

    protected String getAttribute(ObjectName objectName, String attr) {

        try {
            return jmxWrapper.getAttribute(objectName, attr).toString();
        }
        catch (Exception e) {

            return "";
        }
    }

    public String getHealthStateInformation(weblogic.health.HealthState myState) {
        if (myState.getState() == weblogic.health.HealthState.HEALTH_OK)
            return "HEALTH_OK";
        else if (myState.getState() == weblogic.health.HealthState.HEALTH_WARN)
            return "HEALTH_WARN";
        else if (myState.getState() == weblogic.health.HealthState.HEALTH_CRITICAL)
            return "HEALTH_CRITICAL";
        else if (myState.getState() == weblogic.health.HealthState.HEALTH_FAILED)
            return "HEALTH_FAILED";
        else if (myState.getState() == weblogic.health.HealthState.HEALTH_OVERLOADED)
            return "HEALTH_OVERLOADED";
        else
            return "UNKNOWN STATE";
    }
}
