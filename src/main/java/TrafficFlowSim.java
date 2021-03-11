import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import enums.BuildingMode;
import enums.SimulationMode;

public class TrafficFlowSim extends ApplicationAdapter {

    final float VIRTUAL_WIDTH = 1920;
    final float VIRTUAL_HEIGHT = 1080;

    // Graphics
    private Textures textures;
    private SpriteBatch spriteBatch;
    private BoundedCamera camera;
    private Matrix4 staticMatrix;

    private BitmapFont debugFont;
    private ColorData backgroundColor;

    // Mouse and Cursor
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean middlePressed;
    private Vector3 cursorScreenPos;
    private Vector3 cursorEnvPos;
    private Vector2 cursorIndex;

    // Modes
    private BuildingMode buildingMode;
    private SimulationMode simulationMode;

    private Environment environment;

    private Toolbar toolbar;

    //Road Stuff
    private RoadStuff roadStuff;

    // Car Test
    private Car car;

    @Override
    public void create() {
        super.create();

        //Config
        Config.init();

        //Graphics
        textures = new Textures("textures.properties");
        spriteBatch = new SpriteBatch();
        camera = new BoundedCamera(0, (Config.getInteger("grid_width") * Config.getInteger("cell_size")),
                (Config.getInteger("grid_height") * Config.getInteger("cell_size")), 0);
        staticMatrix = spriteBatch.getProjectionMatrix().cpy();

        debugFont = generateFont("arial.ttf", 24);
        debugFont.setColor(Color.BLACK);
        backgroundColor = new ColorData(255, 255, 255, 1);

        //Input
        setInputProcessor();

        //Environment
        environment = new Environment(textures, camera);

        //Toolbar
        toolbar = new Toolbar(0, Gdx.graphics.getHeight() - Config.getInteger("toolbar_height"), textures);

        //Road Stuff
        roadStuff = new RoadStuff(textures, environment);

        // Test Road
        environment.addRoad(new Road("100",
                environment.getCell(new Vector2(6, 10)),
                environment.getCell(new Vector2(20, 10)),
                textures.get("road")));

        // Test Car
        car = new Car(environment.getCellPosition(new Vector2(10, 10)), environment, textures);
    }

    private void update() {
        //Resolution Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.graphics.setWindowedMode(1280, 720);
        }

        // First Updates
        toolbar.update();
        buildingMode = toolbar.getBuildingMode();
        simulationMode = toolbar.getSimulationMode();
        environment.update();

        // Simulation Mode Switch
        switch (toolbar.getSimulationMode()) {
            case RUNNING:
                car.update();
                break;

            case STOPPED:
                // Building Mode Switch
                switch (toolbar.getBuildingMode()) {
                    case ROAD:
                        roadStuff.update();
                        if (roadStuff.isNewRoadReady()) {
                            environment.addRoad(roadStuff.getNewRoad());
                        }
                        break;
                }
                break;
        }
    }

    private void draw() {
        //Clear Buffer
        Gdx.gl.glClearColor(backgroundColor.getDecimalR(), backgroundColor.getDecimalG(), backgroundColor.getDecimalB(), backgroundColor.getA());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Camera Draws
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        environment.draw(spriteBatch);

        if (toolbar.getBuildingMode() == BuildingMode.ROAD) {
            Vector2 buildValidPos = environment.getCellPosition(cursorIndex);
            roadStuff.draw(spriteBatch, buildValidPos);
        }

        car.draw(spriteBatch);

        //Static Draws
        spriteBatch.setProjectionMatrix(staticMatrix);

        toolbar.draw(spriteBatch);

        //Debug
        debugFont.draw(spriteBatch, "Mode: " + toolbar.getBuildingMode(), 10, 60);
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
        environment.dispose();
        spriteBatch.dispose();
        textures.dispose();
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) { //Left Pressed
                    leftPressed = true;
                    toolbar.leftClick();

                    switch (toolbar.getBuildingMode()) {
                        case ROAD:
                            roadStuff.leftClick(cursorIndex);
                            break;
                    }

                } else if (button == 1) { //Right Pressed
                    rightPressed = true;

                    switch (toolbar.getBuildingMode()) {
                        case ROAD:
                            roadStuff.rightClick();
                            break;
                    }

                }
                else if (button == 2) { //Middle Pressed
                    middlePressed = true;
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (middlePressed) {
                    camera.translate(-(float)Gdx.input.getDeltaX(), (float)Gdx.input.getDeltaY());
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == 0) { leftPressed = false; }
                else if (button == 1) { rightPressed = false; }
                else if (button == 2) { middlePressed = false; }
                return super.touchUp(screenX, screenY, pointer, button);
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                cursorScreenPos = new Vector3(screenX, Math.abs(screenY - VIRTUAL_HEIGHT), 0);
                cursorEnvPos = camera.unproject(new Vector3(screenX, screenY, 0));
                cursorIndex = environment.getIndexAtPosition(cursorEnvPos);

                toolbar.mouseMoved(cursorScreenPos);

                switch(toolbar.getBuildingMode()) {
                    case ROAD:
                        roadStuff.mouseMoved(cursorIndex);
                }

                return super.mouseMoved(screenX, screenY);
            }
        });
    }

    private BitmapFont generateFont(String fontFileName, int fontSize) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontFileName));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = fontSize;
        return fontGenerator.generateFont(fontParameter);
    }
}
