package me.yekki.jms.spring.utils;

import me.yekki.jms.spring.config.ApplicationContextProvider;
import org.springframework.core.env.Environment;

import static me.yekki.jms.spring.utils.Constants.DELIVERY_MODE_KEY;

public class Utils {

    public static int getProperty(Environment env, String key, int defaultValue) {

        String value = env.getProperty(key);

        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) {

            return defaultValue;
        }
    }

    public static String getProperty(Environment env, String key, String defaultValue) {

        String value = env.getProperty(key);

        if (value == null || "".equals(value)) return defaultValue;

        return value;
    }

    public static String getDeliveryModeDesc(Environment env) {

        int mode = getProperty(env, env.getProperty(DELIVERY_MODE_KEY), 1);

        switch (mode) {
            case 1:
                return "Non Persistent";
            case 2:
                return "Persistent";
            default:
                throw new IllegalArgumentException("Illegal message delivery mode:" + mode);
        }
    }
}
