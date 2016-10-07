package me.yekki.jms.spring;

import me.yekki.jms.spring.cmd.Command;
import me.yekki.jms.spring.config.ApplicationConfig;
import me.yekki.jms.spring.utils.Constants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public class CommandLineApp implements Constants{

    public static void main(String... args) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ApplicationConfig.class);
        CommandLinePropertySource clps = new SimpleCommandLinePropertySource(args);
        ctx.getEnvironment().getPropertySources().addFirst(clps);
        ctx.refresh();

        Command cmd = ctx.getBean(ctx.getBean(Role.class).name(), Command.class);

        cmd.execute();
    }
}
