package me.yekki.jms.spring.cmd;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jmx.utils.JMXWrapperRemote;
import org.springframework.core.env.Environment;

import java.util.logging.Logger;

public abstract class JMXCommand implements Command {

    protected static final Logger logger = Logger.getLogger(JMXCommand.class.getName());

    protected JMXWrapperRemote jmxWrapper;

    protected String username;

    protected String password;

    protected String url;

    protected boolean isEdit;

    protected boolean isDomainRuntime;

    public JMXCommand() {

        isEdit = false;
        isDomainRuntime = true;
    }

    public void init(boolean isEdit, boolean isDomainRuntime) {

        this.isEdit = isEdit;
        this.isDomainRuntime = isDomainRuntime;

        Environment env = ApplicationContextProvider.getApplicationContext().getEnvironment();

        url = env.getProperty(PROVIDER_URL_KEY);
        username = env.getProperty(SECURITY_PRINCIPAL_KEY);
        password = env.getProperty(SECURITY_CREDENTIALS_KEY);
        jmxWrapper = new JMXWrapperRemote();
    }

    public void connect(boolean isEdit, boolean isDomainRuntime) {

        try {
            jmxWrapper.connectToAdminServer(isEdit, isDomainRuntime, username, password, url);
        }
        catch (Exception e) {
            logger.info("Failed to connect admin server:" + e.getMessage());
        }
    }

    public void disconnect() {

        try {
            jmxWrapper.disconnectFromAdminServer(true);
        }
        catch (Exception e) {
            logger.info("Failed to disconnect from admin server:" + e.getMessage());
        }
    }

    @Override
    public void execute() {

        connect(isEdit, isDomainRuntime);
        exec();
        disconnect();
    };

    public abstract void exec();
}
