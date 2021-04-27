package com.trafficsim;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public final class Config {

    private static Properties properties;
    private static String outputFilename;
    private static SimpleDateFormat dateFormat;

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
        outputFilename = Gdx.files.getLocalStoragePath() + "/output_" + System.currentTimeMillis() + ".txt";
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public static int getInteger(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

    public static String getOutputFilename() { return outputFilename; }

    public  static String getDateTime() { return dateFormat.format(new Date()); }

}
