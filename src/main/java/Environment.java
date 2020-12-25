import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Environment {

    //Graphics
    private HashMap<String, Texture> textures;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    //Grid
    private final int gridWidth;
    private final int gridHeight;
    private final int gridCellSize;
    private HashMap<Vector2, Cell> grid;

    //SimObjects
    private int startpointCounter, roadCounter, endpointCounter;
    private HashMap<String, Startpoint> startpoints;
    private HashMap<String, Road> roads;
    private HashMap<String, Endpoint> endpoints;

    //Input
    private boolean leftPressed;
    private boolean middlePressed;
    private boolean rightPressed;

    //Building
    private BuildingMode buildingMode;
    private boolean buildValidity;

    private Road roadInProgress;
    private boolean roadProximityValidity;
    private boolean roadInlineValidity;

    public Environment(HashMap<String, Texture> textures, OrthographicCamera camera) {
        this.textures = textures;
        this.camera = camera;

        spriteBatch = new SpriteBatch();

        //setInputProcessor();

        //Grid
        gridWidth = Config.getInteger("grid_width");;
        gridHeight = Config.getInteger("grid_height");;
        gridCellSize = Config.getInteger("cell_size");
        grid = new HashMap<Vector2, Cell>();
        for (int i = 0; i < gridHeight; i++) { //
            for (int j = 0; j < gridWidth; j++) {
                grid.put(
                        new Vector2(j, i),
                        new Cell(textures.get("cell"), j, i, gridCellSize)
                );
            }
        }

        //SimObjects
        startpointCounter = 0;
        roadCounter = 1;
        endpointCounter = 0;

        startpoints = new HashMap<String, Startpoint>();
        roads = new HashMap<String, Road>();
        endpoints = new HashMap<String, Endpoint>();

        //Input
        leftPressed = false;
        rightPressed = false;

        //Building
        buildingMode = BuildingMode.NONE;
        buildValidity = false;

        roadInProgress = null;
        roadProximityValidity = true;
        roadInlineValidity = true;
    }

    public void update() {
        //Building Mode Inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            buildingMode = BuildingMode.NONE;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            buildingMode = BuildingMode.ROAD;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            buildingMode = BuildingMode.BULLDOZE;
        }

        //Build Validity
        if (buildingMode == BuildingMode.ROAD) {
            buildValidity = roadProximityValidity && roadInlineValidity;
        }
    }

    private void tester(SimObject simObject) {
        System.out.println(simObject.getId());
        for (SimObject sO : simObject.getNextSimObjects()) {
            tester(sO);
        }
    }

    public void draw() {
        //SpriteBatch Begin
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        //Grid
        for (Cell cell : grid.values()) {
            cell.draw(spriteBatch);
        }

        //Roads
        for (Road road : roads.values()) {
            road.draw(spriteBatch);
        }
        if (roadInProgress != null) { roadInProgress.draw(spriteBatch); }

        //Building
        if (buildingMode == BuildingMode.ROAD) {
            Vector3 cursorPos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (buildValidity) {
                spriteBatch.draw(textures.get("build_valid"),
                        (int)(cursorPos.x/gridCellSize)*gridCellSize,
                        (int)(cursorPos.y/gridCellSize)*gridCellSize,
                        gridCellSize/2f,
                        gridCellSize/2f,
                        gridCellSize,
                        gridCellSize,
                        1,
                        1,
                        0f,
                        0,
                        0,
                        textures.get("build_valid").getWidth(),
                        textures.get("build_valid").getHeight(),
                        false,
                        false);
            } else {
                spriteBatch.draw(textures.get("build_invalid"),
                        (int)(cursorPos.x/gridCellSize)*gridCellSize,
                        (int)(cursorPos.y/gridCellSize)*gridCellSize,
                        gridCellSize/2f,
                        gridCellSize/2f,
                        gridCellSize,
                        gridCellSize,
                        1,
                        1,
                        0f,
                        0,
                        0,
                        textures.get("build_valid").getWidth(),
                        textures.get("build_valid").getHeight(),
                        false,
                        false);
            }
        }

        //SpriteBatch End
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
    }

    public void resize(int width, int height) {

    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) {
                    leftPressed = true;
                    Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    Vector2 cursorIndex = new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize));
                    if (buildingMode == BuildingMode.ROAD) {
                        if (roadInProgress == null) {
                            roadInProgress = new Road("R" + roadCounter,
                                    grid.get(cursorIndex),
                                    grid.get(cursorIndex),
                                    textures.get("road_tp"));
                        } else {
                            if (buildValidity) {
                                roadInProgress.setTexture(textures.get("road"));
                                for (Vector2 v : roadInProgress.getCellIndices()) {
                                    grid.get(v).setRoad(roadInProgress);
                                }
                                roads.put("R" + roadCounter, roadInProgress);
                                roadCounter++;
                                roadInProgress = null;
                            }
                        }
                    }
                }
                else if (button == 1) { rightPressed = true; }
                else if (button == 2) { middlePressed = true; }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
//                dragging = true;
                if (leftPressed) {
                    Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    //grid.get(new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize))).setRoad(roadPaint);
                } else if (middlePressed) {

                    camera.translate(-(float)Gdx.input.getDeltaX(), (float)Gdx.input.getDeltaY());
                    camera.update();
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
                Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 cursorIndex = new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize));

                if (buildingMode == BuildingMode.ROAD) {
                    roadProximityValidity = grid.get(cursorIndex).getRoad() == null &&
                            grid.get(cursorIndex.cpy().add(0, 1)).getRoad() == null &&
                            grid.get(cursorIndex.cpy().add(1, 0)).getRoad() == null &&
                            grid.get(cursorIndex.cpy().add(0, -1)).getRoad() == null &&
                            grid.get(cursorIndex.cpy().add(-1, 0)).getRoad() == null;

                    System.out.println(roadProximityValidity);

                    if (roadInProgress != null) {
                        roadInlineValidity = roadInProgress.isIndexInline(cursorIndex);
                        if (roadInlineValidity) {
                            roadInProgress.setEndCell(grid.get(cursorIndex));
                        } else {
                            roadInProgress.setEndCell(roadInProgress.getStartCell());
                        }
                        roadInProgress.recalculate();
                    }
                }

                return super.mouseMoved(screenX, screenY);
            }
        });
    }

    private void connectSimObjects() {
        //Startpoints
        for (Startpoint s : startpoints.values()) {
            s.connect();
        }

        //Endpoints
        for (Endpoint e : endpoints.values()) {
            e.connect();
        }
    }

    private SimObject getSimObjectbyId(String id) {
        switch (id.charAt(0)) {
            case 'S':
                return startpoints.get(id);
            case 'R':
                return roads.get(id);
            case 'E':
                return endpoints.get(id);
        }
        return null;
    }

    private void addRoad(Cell startCell, Cell endCell) {
        roadCounter++;
        String id = "R" + roadCounter;

        ArrayList<Cell> cells = new ArrayList<Cell>();
        Cardinal cardinal = Cardinal.SOUTH;

        if (startCell.getIndex().x == endCell.getIndex().x && startCell.getIndex().y < endCell.getIndex().y) { // NORTH
            for (float i = startCell.getIndex().y; i <= endCell.getIndex().y; i++) {
                cells.add(grid.get(new Vector2(startCell.getIndex().x, i)));
            }
            cardinal = Cardinal.NORTH;
        } else if (startCell.getIndex().y == endCell.getIndex().y && startCell.getIndex().x < endCell.getIndex().x) { // EAST
            for (float i = startCell.getIndex().x; i <= endCell.getIndex().x; i++) {
                cells.add(grid.get(new Vector2(i, startCell.getIndex().y)));
            }
            cardinal = Cardinal.EAST;
        } else if (startCell.getIndex().x == endCell.getIndex().x && startCell.getIndex().y > endCell.getIndex().y) { // SOUTH
            for (float i = startCell.getIndex().y; i >= endCell.getIndex().y; i--) {
                cells.add(grid.get(new Vector2(startCell.getIndex().x, i)));
            }
        } else if (startCell.getIndex().y == endCell.getIndex().y && startCell.getIndex().x > endCell.getIndex().x) { // WEST
            for (float i = startCell.getIndex().x; i >= endCell.getIndex().x; i--) {
                cells.add(grid.get(new Vector2(i, startCell.getIndex().y)));
            }
            cardinal = Cardinal.WEST;
        }
    }

    public BuildingMode getBuildingMode() {
        return buildingMode;
    }
}
