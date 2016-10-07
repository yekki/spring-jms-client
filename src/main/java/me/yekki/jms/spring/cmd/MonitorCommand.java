package me.yekki.jms.spring.cmd;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jmx.utils.JMXWrapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

@Service("Monitor")
@Lazy(true)
public class MonitorCommand extends JMXCommand {

    @Resource(name="monitors")
    private List<String> monitors;

    public MonitorCommand() {
        super.init(false, true);
    }

    @Override
    public void exec() {

        monitors.forEach((m)->{
            invoke(jmxWrapper, m);
        });
    }

    public void invoke(JMXWrapper jmxWrapper, String monitor) {

        try {
            String[] meta = monitor.split(":");

            if (null != meta && meta.length >= 1) {

                String cz = meta[0];
                String methodStr = cz.substring(cz.lastIndexOf(".") + 1);
                String clazzStr = cz.substring(0, cz.lastIndexOf("."));

                Class clazz = Class.forName(clazzStr);

                Constructor constructor = clazz.getConstructor(JMXWrapper.class, PrintStream.class);
                Object inst = constructor.newInstance(jmxWrapper, System.out);

                String[] args = (meta.length > 1) ? meta[1].split("_") : null;

                Class[] argsClass = new Class[args.length];

                for (int i = 0; i < argsClass.length; i++) {
                    argsClass[i] = String.class;
                }

                Method method = clazz.getMethod(methodStr, argsClass);

                method.invoke(inst, args);
            }
        }
        catch (Exception e) {

            logger.info("Failed to execute monitor command:" + e.getMessage());
        }
    }

}
