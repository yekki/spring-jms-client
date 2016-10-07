package me.yekki.jms.spring.cmd;

import me.yekki.jmx.administration.JMSAdministration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;

@Service("Cleaner")
@Lazy(true)
public class CleanCommand extends JMXCommand{


    public CleanCommand() {
        super.init(false, true);
    }

    @Override
    public void exec() {
        try {
            JMSAdministration jmsadmin = new JMSAdministration(jmxWrapper);
            ObjectName dest = jmsadmin.getJMSDestinationRuntime("AdminServer", "DemoJMSServer", "DemoSystemModule!DemoQueue");
            jmsadmin.deleteMessagesFromJmsDestination(dest, "");
        }
        catch (Exception e) {
            logger.info("Failed to execute clean command:" + e.getMessage());
        }
    }
}
