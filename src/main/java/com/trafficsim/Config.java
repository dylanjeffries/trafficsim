package com.trafficsim;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static Properties properties;

    public static void init() {
        properties = new Properties();
        String propertiesFileName = "app.properties";
        InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(propertiesFileName);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Properties file not found");
        }
    }

    public static int getInteger(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

}
