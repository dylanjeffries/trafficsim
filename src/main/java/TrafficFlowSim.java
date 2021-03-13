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
import enums.Direction;
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

    // Builders
    private RoadBuilder roadBuilder;
    private TunnelBuilder tunnelBuilder;

    // Car Test
    private Car car;
    private Car car2;

    @Override
    public void create() {
        super.create();

        // Config
        Config.init();

        // Graphics
        textures = new Textures("textures.properties");
        spriteBatch = new SpriteBatch();
        camera = new BoundedCamera(0, (Config.getInteger("grid_width") * Config.getInteger("cell_size")),
                (Config.getInteger("grid_height") * Config.getInteger("cell_size")), 0);
        staticMatrix = spriteBatch.getProjectionMatrix().cpy();

        debugFont = generateFont("arial.ttf", 24);
        debugFont.setColor(Color.BLACK);
        backgroundColor = new ColorData(255, 255, 255, 1);

        // Input
        setInputProcessor();

        // Environment
        environment = new Environment(textures);

        // Toolbar
        toolbar = new Toolbar(0, Gdx.graphics.getHeight() - Config.getInteger("toolbar_height"), textures);

        // Builders
        roadBuilder = new RoadBuilder(textures, environment);
        tunnelBuilder = new TunnelBuilder(textures, environment);

        // Test Road
        environment.addRoad(new Road("100",
                environment.getCell(new Vector2(6, 10)),
                environment.getCell(new Vector2(20, 10)),
                textures.get("road")));

        // Test Car
        car = new Car(environment.getCellPosition(new Vector2(6, 10)), environment, textures);
        car2 = new Car(environment.getCellPosition(new Vector2(20, 10)), environment, textures);
        car2.setDirection(Direction.WEST);
    }

    private void update() {
        // Resolution Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.graphics.setWindowedMode(1280, 720);
        }

        // First Updates
        environment.update();
        toolbar.update();
        buildingMode = toolbar.getBuildingMode();
        simulationMode = toolbar.getSimulationMode();

        // Simulation Mode Switch
        switch (toolbar.getSimulationMode()) {
            case RUNNING:
                car.update();
                car2.update();
                break;

            case STOPPED:
                // Building Mode Switch
                switch (toolbar.getBuildingMode()) {
                    case ROAD:
                        roadBuilder.update();
                        if (roadBuilder.isNewRoadReady()) {
                            environment.addRoad(roadBuilder.getNewRoad());
                        }
                        break;

                    case TUNNEL:
                        tunnelBuilder.update();
                        if (tunnelBuilder.isNewTunnelReady()) {
                            environment.addTunnel(tunnelBuilder.getNewTunnel());
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

        // Simulation Mode Switch
        switch (toolbar.getSimulationMode()) {
            case STOPPED:
                // Building Mode Switch
                switch (toolbar.getBuildingMode()) {
                    case SELECT:
                        spriteBatch.draw(textures.get("select"),
                                environment.getCellPosition(cursorIndex).x,
                                environment.getCellPosition(cursorIndex).y,
                                environment.getGridCellSize(),
                                environment.getGridCellSize());
                        break;

                    case ROAD:
                        roadBuilder.draw(spriteBatch, environment.getCellPosition(cursorIndex));
                        break;

                    case TUNNEL:
                        tunnelBuilder.draw(spriteBatch, environment.getCellPosition(cursorIndex));
                        break;
                }
                break;
        }

        car.draw(spriteBatch);
        car2.draw(spriteBatch);

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
                    boolean toolbarClicked = toolbar.leftClick();

                    // If a toolbar button was not clicked and the simulation is stopped
                    if (!toolbarClicked && toolbar.getSimulationMode() == SimulationMode.STOPPED) {
                        switch (toolbar.getBuildingMode()) {
                            case ROAD:
                                roadBuilder.leftClick(cursorIndex);
                                break;

                            case TUNNEL:
                                tunnelBuilder.leftClick(cursorIndex);
                                break;
                        }
                    }

                } else if (button == 1) { //Right Pressed
                    rightPressed = true;

                    switch (toolbar.getBuildingMode()) {
                        case ROAD:
                            roadBuilder.rightClick();
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
                        roadBuilder.mouseMoved(cursorIndex);

                    case TUNNEL:
                        tunnelBuilder.mouseMoved(cursorIndex);
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
