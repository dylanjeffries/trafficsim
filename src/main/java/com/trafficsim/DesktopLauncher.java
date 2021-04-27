package com.trafficsim;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1920;
        config.height = 1080;
        config.resizable = false;
        config.title = "SEATS";
        config.addIcon("icon/icon128.png", Files.FileType.Internal);
        config.addIcon("icon/icon32.png", Files.FileType.Internal);
        config.addIcon("icon/icon16.png", Files.FileType.Internal);
        config.forceExit = true;
        new LwjglApplication(new TrafficFlowSim(), config);
    }
}