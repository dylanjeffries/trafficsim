import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Textures extends HashMap<String, Texture> {

    Properties textureProperties;

    public Textures(String propertiesFileName) {
        textureProperties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
        if (inputStream != null) {
            try {
                textureProperties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Texture Properties file not found");
        }

        for(String key : textureProperties.stringPropertyNames()) {
            Texture temp = new Texture(Gdx.files.internal(textureProperties.getProperty(key)), true);
            temp.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.MipMap);
            this.put(key, temp);
        }
    }

    public void dispose() {
        for(Texture t : values()) {
            t.dispose();
        }
    }
}
