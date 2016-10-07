package me.yekki.jms.spring.aop;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import me.yekki.jms.spring.utils.Constants;
import me.yekki.jms.spring.utils.Utils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class ProfilingAspect implements Constants {

    @Around("execution(* me.yekki.jms.spring.cmd.*Command.execute())")
    public void cmdAdvice(ProceedingJoinPoint joinPoint) throws Throwable{
        StopWatch sw = new StopWatch();
        sw.start(joinPoint.getSignature().getName());
        joinPoint.proceed();
        sw.stop();

        System.out.println("Command:" + joinPoint.getTarget().getClass().getName());
        System.out.println("Estimate:" + sw.getTotalTimeMillis() + " ms");
    }

    @Before("execution(* me.yekki.jms.spring.cmd.SendCommand.execute())")
    public void sendAdvice() {
        Environment env = ApplicationContextProvider.getApplicationContext().getEnvironment();
        String mode = Utils.getDeliveryModeDesc(env);
        String msgType = Utils.getProperty(env, MESSAGE_TYPE_KEY, "");
        int messageCount = Utils.getProperty(env, MESSAGE_COUNT_KEY, 0);
        int threadCount = Utils.getProperty(env, SENDER_THREADS_KEY, 1);
        String profile = String.format("(Type:%s, Mode:%s, Count:%d, Threads:%d)", msgType, mode, messageCount, threadCount);
        System.out.println(profile);
    }
}
