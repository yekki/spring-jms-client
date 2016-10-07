package me.yekki.jms.spring.cmd;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jmx.creation_extension.JMSConfiguration;
import me.yekki.jmx.utils.WLSJMXException;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service("Uninstaller")
@Lazy(true)
public class UninstallCommand extends JMXCommand {

    public UninstallCommand() {

        Environment env = ApplicationContextProvider.getApplicationContext().getEnvironment();
        super.init(true, false);
    }

    @Override
    public void exec() {
        try {

            JMSConfiguration jc = new JMSConfiguration(jmxWrapper);
            jc.destroyJMSModule("DemoSystemModule");
            jc.destroyJMSServer("DemoJMSServer");
            jc.destroyFileStore("DemoFileStore");
        }
        catch (WLSJMXException e) {
            logger.info("Failed to execute uninstall command:" + e.getMessage());
        }
    }
}
