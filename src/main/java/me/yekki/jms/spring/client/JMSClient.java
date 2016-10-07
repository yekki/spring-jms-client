package me.yekki.jms.spring.client;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jms.spring.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.logging.Logger;

@Service
@Scope("prototype")
@Lazy(true)
public class JMSClient implements AutoCloseable, Constants {

    protected static Logger logger = Logger.getLogger(JMSClient.class.getName());

    protected ConnectionFactory connectionFactory;
    protected Destination destination;
    protected JMSContext context;
    protected JMSConsumer consumer;
    protected JMSProducer producer;

    public JMSClient() {
        try {
            Environment env = ApplicationContextProvider.getApplicationContext().getEnvironment();
            InitialContext ctx = ApplicationContextProvider.getApplicationContext().getBean(InitialContext.class);
            Role role = ApplicationContextProvider.getApplicationContext().getBean(Role.class);

            connectionFactory = (ConnectionFactory) ctx.lookup(env.getProperty(CONNECTON_FACTORY_KEY));
            destination = (Destination) ctx.lookup(env.getProperty(DESTINATION_KEY));
            context = connectionFactory.createContext();

            switch (role) {
                case Sender:
                    producer = context.createProducer();
                    producer.setDeliveryMode(getDeliveryMode(env.getProperty(DELIVERY_MODE_KEY)));
                    break;
                case Receiver:
                    consumer = context.createConsumer(destination);
            }
        }
        catch (NamingException ne) {
            logger.info("Failed to initial JMSClient:" + ne.getMessage());
        }
    }

    //NON_PERSISTENT=1, PERSISTENT=2
    protected int getDeliveryMode(String mode) {

        if (mode == null) return 1;

        try {
            int ret = Integer.parseInt(mode);

            if (ret>0 && ret < 3)
                return ret;
            else
                return 1;
        }
        catch (NumberFormatException nfe) {
            return 1;
        }
    }

    public JMSContext getJMSContext() {

        return context;
    }

    public ConnectionFactory getConnectionFactory() {

        return connectionFactory;
    }

    public JMSConsumer getConsumer() {

        return consumer;
    }

    public JMSProducer getProducer() {

        return producer;
    }

    public void send(Serializable msg) {

        if (producer != null) {
            producer.send(destination, msg);
        }
    }

    @Override
    public void close(){

        if (consumer != null) consumer.close();
        if (context != null) context.close();
    }
}
