package me.yekki.jms.spring.utils;

import me.yekki.jms.spring.cmd.*;

public interface Constants {

    //const
    final String INITIAL_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    final String SAF_INITIAL_CONTEXT_FACTORY = "weblogic.jms.safclient.jndi.InitialContextFactoryImpl";
    //keys
    final String SECURITY_PRINCIPAL_KEY = "SECURITY_PRINCIPAL";
    final String SECURITY_CREDENTIALS_KEY = "SECURITY_CREDENTIALS";
    final String PROVIDER_URL_KEY = "PROVIDER_URL";
    final String CONNECTON_FACTORY_KEY = "CONNECTION_FACTORY";
    final String DESTINATION_KEY = "DESTINATION";
    final String MESSAGE_SIZE_KEY = "MESSAGE_SIZE";
    final String FILE_STORE_PATH_KEY = "FILE_STORE_PATH";
    final String MESSAGE_CONTENT_KEY = "MESSAGE_CONTENT";
    final String MESSAGE_TYPE_KEY = "MESSAGE_TYPE";
    final String MESSAGE_FILENAME_KEY = "MESSAGE_FILENAME";
    final String SENDER_CONFIG_FILE_KEY = "SENDER_CONFIG_FILE";
    final String RECEIVER_CONFIG_FILE_KEY = "RECEIVER_CONFIG_FILE";
    final String MONITOR_KEY="MONITOR";
    final String SENDER_THREADS_KEY = "SENDER_THREADS";
    final String DELIVERY_MODE_KEY = "DELIVERY_MODE";
    final String APP_CONFIG_FILE = "META-INF/config/app.properties";

    //cmd arguments
    final String MESSAGE_COUNT_KEY = "count";

    //config files
    static final String JMS_CONFIG_FILE = "jms.properties";

    public enum Role {

        Receiver(RECEIVER_CONFIG_FILE_KEY, null), Cleaner(RECEIVER_CONFIG_FILE_KEY, CleanCommand.class),
        Sender(Constants.SENDER_CONFIG_FILE_KEY, SendCommand.class), Monitor(RECEIVER_CONFIG_FILE_KEY,
                MonitorCommand.class), Helper(null, null), Installer(RECEIVER_CONFIG_FILE_KEY, InstallCommand.class),
        Uninstaller(RECEIVER_CONFIG_FILE_KEY, UninstallCommand.class), StoreAdmin(null, StoreAdminCommand.class);

        private String configFileKey;
        private Class commandClass;
        private String description;

        Role(String configFileKey, Class commandClass) {

            this.configFileKey = configFileKey;
            this.commandClass = commandClass;
            this.description = "";
        }

        public String getConfigFileKey() {

            return configFileKey;
        }

        public Class getCommandClass() {

            return commandClass;
        }

        public void setDescription(String description) {

            if (description != null)
                this.description = description;
            else
                this.description = "";

        }

        public String getDescription() {

            return description;
        }

        public String toString() {

            return super.toString() + description;
        }
    }

}