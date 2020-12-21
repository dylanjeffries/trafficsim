import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class TrafficFlowSim extends ApplicationAdapter {

    private HashMap<String, Texture> textures;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private BitmapFont debugFont;
    private ColorData backgroundColor;

    //private Environment env;
    private TestEnv testenv;

    @Override
    public void create() {
        super.create();

        //Config
        Config.init();

        //Textures
        textures = new HashMap<String, Texture>();
        textures.put("road", new Texture("road2.png"));
        textures.put("road_tp", new Texture("road_tp2.png"));
        textures.put("cell", new Texture("cell.png"));
        textures.put("build_valid", new Texture("build_valid2.png"));
        textures.put("build_invalid", new Texture("build_invalid2.png"));

        //Graphics
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        debugFont = generateFont("arial.ttf", 15);
        debugFont.setColor(Color.BLACK);

        backgroundColor = new ColorData(255, 255, 255, 1);

        testenv = new TestEnv(textures, debugFont);
    }

    private void update() {
        //env.update();
        testenv.update();
    }

    private void draw() {
        //Clear Buffer
        Gdx.gl.glClearColor(backgroundColor.getDecimalR(), backgroundColor.getDecimalG(), backgroundColor.getDecimalB(), backgroundColor.getA());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Environment
        //env.draw();
        testenv.draw();

        //Debug
        spriteBatch.begin();
        debugFont.draw(spriteBatch, "Mode: " + testenv.getBuildingMode(), 20, 50);
        debugFont.draw(spriteBatch, Gdx.graphics.getFramesPerSecond() + " FPS", 20, 20);
        spriteBatch.end();
    }

    @Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        //env.resize(width, height);
        testenv.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        //env.dispose();
        testenv.dispose();
    }

    private BitmapFont generateFont(String fontFileName, int fontSize) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontFileName));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = fontSize;
        return fontGenerator.generateFont(fontParameter);
    }
}
