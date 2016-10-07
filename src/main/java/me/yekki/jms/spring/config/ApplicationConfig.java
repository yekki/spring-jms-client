package me.yekki.jms.spring.config;

import me.yekki.jms.spring.utils.Constants;
import me.yekki.jms.spring.utils.MessageCounter;
import me.yekki.jms.spring.utils.SizableObject;
import me.yekki.jms.spring.utils.Utils;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Optional;
import java.util.logging.Logger;

import static me.yekki.jms.spring.utils.Constants.Role.*;

@Configuration
@PropertySources({
        @PropertySource("classpath:META-INF/config/app.properties"),
        @PropertySource("classpath:META-INF/config/${JMS_CONFIG_FILE}.properties")
})
@ImportResource("classpath:META-INF/spring/app-context.xml")
@EnableAspectJAutoProxy
public class ApplicationConfig implements Constants {

    private static final Logger logger = Logger.getLogger(ApplicationConfig.class.getName());

    @Inject
    private Environment env;

    @Bean
    public MessageCounter counter() {

        int messageCount = Utils.getProperty(env, MESSAGE_COUNT_KEY, 0);
        int threadCount = Utils.getProperty(env, SENDER_THREADS_KEY, 1);

        return new MessageCounter(messageCount, threadCount);
    }

    @Bean
    public Serializable message() {
        String messageType = env.getProperty(MESSAGE_TYPE_KEY);

        assert messageType != null;

        Serializable msg = null;

        switch (messageType) {

            case "text":
                msg = Optional.of(env.getProperty(MESSAGE_CONTENT_KEY)).get();
                break;
            case "binary":
                msg = SizableObject.buildObject(Integer.parseInt(Optional.of(env.getProperty(MESSAGE_SIZE_KEY)).get()));
                break;
            case "file":
                Path path = Paths.get(env.getProperty(MESSAGE_FILENAME_KEY));
                try {
                    if (Files.exists(path)) msg = new String(Files.readAllBytes(path));
                } catch (IOException io) {
                    logger.info("Failed to load message file:" + path);
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal message type:" + messageType);
        }

        assert msg != null;

        return msg;
    }

    @Bean
    public Role role() {

        String optArg = env.getProperty("role");

        if (optArg == null) return Helper;

        switch (optArg) {
            case "r":
                return Receiver;
            case "s":
                return Sender;
            case "c":
                return Cleaner;
            case "a":
                return StoreAdmin;
            case "u":
                return Uninstaller;
            case "i":
                return Installer;
            case "m":
                return Monitor;
            default:
                return Helper;
        }
    }

    @Bean
    public InitialContext ctx() {

        InitialContext ctx = null;
        Hashtable<String, String> e = new Hashtable<>();

        String providerUrl = env.getProperty(PROVIDER_URL_KEY);

        assert providerUrl != null;

        if (providerUrl.startsWith("file:"))
            e.put(Context.INITIAL_CONTEXT_FACTORY, SAF_INITIAL_CONTEXT_FACTORY);
        else
            e.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);

        e.put(Context.PROVIDER_URL, env.getProperty(PROVIDER_URL_KEY));
        e.put(Context.SECURITY_PRINCIPAL, env.getProperty(SECURITY_PRINCIPAL_KEY));
        e.put(Context.SECURITY_CREDENTIALS, env.getProperty(SECURITY_CREDENTIALS_KEY));
        e.put(CONNECTON_FACTORY_KEY, env.getProperty(CONNECTON_FACTORY_KEY));
        e.put(DESTINATION_KEY, env.getProperty(DESTINATION_KEY));
        try {
            ctx = new InitialContext(e);
        } catch (NamingException ne) {
            logger.info("Failed to new initial context:" + ne.getMessage());
            Runtime.getRuntime().exit(-1);
        }

        return ctx;
    }
}
