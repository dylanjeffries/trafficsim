import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1920;
        config.height = 1080;
        config.resizable = false;
        config.title = "Traffic Flow Simulator";
        config.addIcon("icon/icon128.png", Files.FileType.Internal);
        config.addIcon("icon/icon32.png", Files.FileType.Internal);
        config.addIcon("icon/icon16.png", Files.FileType.Internal);
        config.forceExit = true;
        new LwjglApplication(new TrafficFlowSim(), config);
    }
}