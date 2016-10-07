package me.yekki.jms.spring.cmd;

import me.yekki.jms.spring.client.JMSClient;
import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jms.spring.utils.MessageCounter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service("Sender")
@Lazy(true)
public class SendCommand implements Command {

    @Inject
    private MessageCounter counter;

    @Inject
    private Serializable message;

    @Override
    public void execute() {
        ExecutorService es = Executors.newCachedThreadPool();
        for ( int i = 1; i <= counter.getThreadCount(); i++ ) {

            if ( counter.isLastBatch(i))
                es.submit(new Sender(counter.getLeftMessageCount()));
            else
                es.submit(new Sender(counter.getMessageCountPerThread()));
        }

        try {
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException ie) {
        }
    }

    class Sender extends Thread {

        private int count;

        public Sender(int count) {

            this.count = count;
        }

        @Override
        public void run() {

            JMSClient client = ApplicationContextProvider.getApplicationContext().getBean(JMSClient.class);
            assert client != null;
            for (int i=0; i < count; i++) {
                client.send(message);
            }
        }

    }
}