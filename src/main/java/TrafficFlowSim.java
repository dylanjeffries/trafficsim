import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

public class TrafficFlowSim extends ApplicationAdapter {

    final float VIRTUAL_WIDTH = 1920;
    final float VIRTUAL_HEIGHT = 1080;

    //Graphics
    private Textures textures;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    private BitmapFont debugFont;
    private ColorData backgroundColor;

    private Environment environment;

    private Toolbar toolbar;

    @Override
    public void create() {
        super.create();

        //Config
        Config.init();

        //Textures
        textures = new Textures("textures.properties");

        //Graphics
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();

        debugFont = generateFont("arial.ttf", 24);
        debugFont.setColor(Color.BLACK);
        backgroundColor = new ColorData(255, 255, 255, 1);

        //Input
        setInputProcessor();

        //Environment
        environment = new Environment(textures, camera);

        //Toolbar
        toolbar = new Toolbar(0, Gdx.graphics.getHeight() - Config.getInteger("toolbar_height"), textures);
    }

    private void update() {
        environment.update();

        //Resolution Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.graphics.setWindowedMode(1280, 720);
        }
    }

    private void draw() {
        //Clear Buffer
        Gdx.gl.glClearColor(backgroundColor.getDecimalR(), backgroundColor.getDecimalG(), backgroundColor.getDecimalB(), backgroundColor.getA());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Environment
        environment.draw();

        spriteBatch.begin();

        //Toolbar
        toolbar.draw(spriteBatch);

        //Debug
        debugFont.draw(spriteBatch, "Mode: " + environment.getBuildingMode(), 10, 60);
        debugFont.draw(spriteBatch, Gdx.graphics.getFramesPerSecond() + " FPS", 10, 30);

        spriteBatch.end();
    }

    @Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        environment.dispose();
        textures.dispose();
    }

    private BitmapFont generateFont(String fontFileName, int fontSize) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontFileName));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = fontSize;
        return fontGenerator.generateFont(fontParameter);
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) {
                    toolbar.touchDown();
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                //Vector2 cursorIndex = new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize));

                toolbar.mouseMoved(cursorPos);

                return super.mouseMoved(screenX, screenY);
            }
        });
    }
}
