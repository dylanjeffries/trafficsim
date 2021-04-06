package com.trafficsim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.trafficsim.builders.IntersectionBuilder;
import com.trafficsim.builders.RoadBuilder;
import com.trafficsim.builders.TunnelBuilder;
import com.trafficsim.enums.BuildingMode;
import com.trafficsim.enums.SimObjectType;
import com.trafficsim.enums.SimulationMode;

public class TrafficFlowSim extends ApplicationAdapter {

    private Environment environment;

    // Graphics
    private Textures textures;
    private SpriteBatch spriteBatch;
    private BoundedCamera camera;
    private Matrix4 staticMatrix;
    private ColorData backgroundColor;

    // Mouse and Cursor
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean middlePressed;
    private Vector2 cursorScreenPos;
    private Vector2 cursorEnvPos;
    private Vector2 cursorIndex;

    // Modes
    private BuildingMode buildingMode;
    private SimulationMode simulationMode;

    // Builders
    private RoadBuilder roadBuilder;
    private TunnelBuilder tunnelBuilder;
    private IntersectionBuilder intersectionBuilder;

    // UI
    private InputMultiplexer inputMultiplexer;
    private Toolbar toolbar;
    private Sidebar sidebar;

    @Override
    public void create() {
        super.create();

        // Inits
        Config.init();
        textures = new Textures("textures.properties");
        UIStyling.init(textures);

        // Graphics
        spriteBatch = new SpriteBatch();
        camera = new BoundedCamera(0, (Config.getInteger("grid_width") * Config.getInteger("cell_size")),
                (Config.getInteger("grid_height") * Config.getInteger("cell_size")), 0);
        staticMatrix = spriteBatch.getProjectionMatrix().cpy();

        backgroundColor = new ColorData(255, 255, 255, 1);

        // Environment
        environment = new Environment(textures);

        // Builders
        roadBuilder = new RoadBuilder(environment, textures);
        tunnelBuilder = new TunnelBuilder(environment, textures);
        intersectionBuilder = new IntersectionBuilder(environment, textures);

        // Input and UI
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(createMainInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);

        toolbar = new Toolbar(0, Gdx.graphics.getHeight() - Config.getInteger("toolbar_height"), textures);
        sidebar = new Sidebar(textures);
    }

    private void update() {
        // Resolution Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.graphics.setWindowedMode(1280, 720);
        }

        // Sidebar
        sidebar.update();

        // Toolbar
        toolbar.update();
        buildingMode = toolbar.getBuildingMode();
        simulationMode = toolbar.getSimulationMode();
        if (toolbar.isGlobalClicked()) {
            sidebar.setSimObject(environment.getGlobalSettings());
            sidebar.setSelectedCell(null);
            sidebar.show();
        }

        // Simulation Mode Switch
        switch (toolbar.getSimulationMode()) {
            case RUNNING:
                if (toolbar.getPreviousSimulationMode() == SimulationMode.STOPPED)
                {
                    environment.compile();
                } else {
                    environment.update();
                }
                break;

            case STOPPED:
                if (toolbar.getPreviousSimulationMode() != SimulationMode.STOPPED)
                {
                    environment.reset();
                } else {
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

                        case INTERSECTION:
                            intersectionBuilder.update();
                            if (intersectionBuilder.isNewIntersectionReady()) {
                                environment.addIntersection(intersectionBuilder.getNewIntersection());
                            }
                            break;
                    }
                }
                break;
        }
    }

    private void draw() {
        // Clear Buffer
        Gdx.gl.glClearColor(backgroundColor.getDecimalR(), backgroundColor.getDecimalG(), backgroundColor.getDecimalB(), backgroundColor.getA());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // World Draws
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw Environment Ground (Level 0)
        environment.draw(spriteBatch);

        // Draw Select or Builders (Level 1)
        if (toolbar.getSimulationMode() == SimulationMode.STOPPED) {
            // Building Mode Switch
            switch (toolbar.getBuildingMode()) {
                case SELECT:
                    // Select Highlight
                    spriteBatch.draw(textures.get("yellow_highlight"),
                            environment.getCellPosition(cursorIndex).x,
                            environment.getCellPosition(cursorIndex).y,
                            environment.getGridCellSize(),
                            environment.getGridCellSize());
                    // Sidebar Selected Cell Highlight
                    if (sidebar.getSelectedCell() != null) {
                        if (sidebar.getAnimationState()) {
                            spriteBatch.draw(textures.get("yellow_dot_1_highlight"),
                                    sidebar.getSelectedCell().getX(),
                                    sidebar.getSelectedCell().getY(),
                                    environment.getGridCellSize(),
                                    environment.getGridCellSize());
                        } else {
                            spriteBatch.draw(textures.get("yellow_dot_2_highlight"),
                                    sidebar.getSelectedCell().getX(),
                                    sidebar.getSelectedCell().getY(),
                                    environment.getGridCellSize(),
                                    environment.getGridCellSize());
                        }
                    }
                    break;

                case ROAD:
                    roadBuilder.draw(spriteBatch, environment.getCellPosition(cursorIndex));
                    break;

                case TUNNEL:
                    tunnelBuilder.draw(spriteBatch, environment.getCellPosition(cursorIndex));
                    break;

                case INTERSECTION:
                    intersectionBuilder.draw(spriteBatch, environment.getCellPosition(cursorIndex));
                    break;

                case BULLDOZE:
                    spriteBatch.draw(textures.get("black_yellow_highlight"),
                            environment.getCellPosition(cursorIndex).x,
                            environment.getCellPosition(cursorIndex).y,
                            environment.getGridCellSize(),
                            environment.getGridCellSize());
                    break;
            }
        }

        // UI Draws (Static to the camera)
        spriteBatch.setProjectionMatrix(staticMatrix);

        // Draw Sidebar
        if (toolbar.getSimulationMode() == SimulationMode.STOPPED && toolbar.getBuildingMode() == BuildingMode.SELECT) {
            sidebar.draw(spriteBatch);
        }
        // Draw Toolbar
        toolbar.draw(spriteBatch);

        UIStyling.BODY_FONT.draw(spriteBatch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 100, 100);

        spriteBatch.end();
    }

    @Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, Config.getInteger("v_width"), Config.getInteger("v_height"));
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        environment.dispose();
        spriteBatch.dispose();
        textures.dispose();
    }

    private InputAdapter createMainInputProcessor() {
         return new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) { //Left Pressed
                    leftPressed = true;
                    boolean toolbarClicked = toolbar.leftClick();

                    // Get cell at click position
                    Cell cursorCell = environment.getCell(cursorIndex);

                    // If a toolbar button was not clicked and the simulation is stopped
                    if (!toolbarClicked && toolbar.getSimulationMode() == SimulationMode.STOPPED) {
                        switch (toolbar.getBuildingMode()) {
                            case SELECT:
                                if (cursorCell.getSimObjectType() != SimObjectType.NONE) {
                                    sidebar.setSimObject(cursorCell.getSimObject());
                                    sidebar.setSelectedCell(cursorCell);
                                    sidebar.show();
                                } else {
                                    sidebar.setSimObject(null);
                                    sidebar.setSelectedCell(null);
                                    sidebar.hide();
                                }
                            case ROAD:
                                roadBuilder.leftClick(cursorIndex);
                                break;

                            case TUNNEL:
                                tunnelBuilder.leftClick(cursorIndex);
                                break;

                            case INTERSECTION:
                                intersectionBuilder.leftClick(cursorIndex);
                                break;

                            case BULLDOZE:
                                if (cursorCell.getSimObjectType() != SimObjectType.NONE) {
                                    environment.deleteSimObject(cursorCell.getSimObject().getId());
                                }
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
                cursorScreenPos = new Vector2(screenX, Math.abs(screenY - Gdx.graphics.getHeight()));
                Vector3 cursorEnvPosVector3 = camera.unproject(new Vector3(screenX, screenY, 0));
                cursorEnvPos = new Vector2(cursorEnvPosVector3.x, cursorEnvPosVector3.y);
                cursorIndex = environment.getIndexAtPosition(cursorEnvPos);

                toolbar.mouseMoved(cursorScreenPos.scl(Config.getInteger("v_height") / (float)Gdx.graphics.getHeight()));

                switch(toolbar.getBuildingMode()) {
                    case ROAD:
                        roadBuilder.mouseMoved(cursorIndex);

                    case TUNNEL:
                        tunnelBuilder.mouseMoved(cursorIndex);

                    case INTERSECTION:
                        intersectionBuilder.mouseMoved(cursorIndex);
                }

                return super.mouseMoved(screenX, screenY);
            }
        };
    }
}
