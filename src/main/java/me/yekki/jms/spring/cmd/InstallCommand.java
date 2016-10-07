package me.yekki.jms.spring.cmd;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jmx.creation_extension.JMSConfiguration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;

@Service("Installer")
@Lazy(true)
public class InstallCommand extends JMXCommand{

    private Environment env;

    public InstallCommand() {

        env = ApplicationContextProvider.getApplicationContext().getEnvironment();
        super.init(true, false);
    }

    @Override
    public void exec() {
        try {
            JMSConfiguration jc = new JMSConfiguration(jmxWrapper);
            ObjectName filestore = jc.createFileStore("DemoFileStore", "AdminServer", env.getProperty(FILE_STORE_PATH_KEY));
            jc.createAnewJMSServer("DemoJMSServer", filestore, "AdminServer");
            jc.createJMSModule("DemoSystemModule", "Server", "AdminServer");
            jc.createJmsConnectionFactory("DemoSystemModule", "DemoConnectionFactory", "democf");
            jc.createJMSSubDeployment("DemoSystemModule", "DemoSubDeployment", "DemoJMSServer");
            jc.createQueue("DemoSystemModule", "DemoQueue", "demoqueue", "DemoSubDeployment");
        }
        catch (Exception e) {

            logger.info("Failed to execute install command:" + e.getMessage());
        }
    }
}
