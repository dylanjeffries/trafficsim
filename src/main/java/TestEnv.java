import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;

public class TestEnv {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    //Textures
    private Texture road_texture;
    private Texture empty_texture;

    //Grid
    private final int gridWidth;
    private final int gridHeight;
    private final int gridCellSize;
    private HashMap<Vector2, Cell> grid;

    private ArrayList<Car> cars;
    private ArrayList<Car> carsToRemove;

    //SimObjects
    private int startpointCounter, roadCounter, endpointCounter;
    private HashMap<String, Startpoint> startpoints;
    private HashMap<String, Road> roads;
    private HashMap<String, Endpoint> endpoints;

    //Input
    private boolean leftPressed;
    private boolean rightPressed;

    //Building
    private boolean roadPaint;

    public TestEnv() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera(1920, 1080);
        camera.position.set(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0);
        camera.update();
        setInputProcessor();

        //Textures
        road_texture = new Texture("road.png");
        empty_texture = new Texture("empty.png");

        //Grid
        gridWidth = 100;
        gridHeight = 100;
        gridCellSize = 40;
        grid = new HashMap<Vector2, Cell>();
        for (int i = 0; i < gridHeight; i++) { //
            for (int j = 0; j < gridWidth; j++) {
                grid.put(
                        new Vector2(j, i),
                        new Cell(j, i, gridCellSize)
                );
            }
        }

        //SimObjects
        startpointCounter = 0;
        roadCounter = 0;
        endpointCounter = 0;

        startpoints = new HashMap<String, Startpoint>();
        roads = new HashMap<String, Road>();
        endpoints = new HashMap<String, Endpoint>();

        //SimObjects Test

        //Cars
        cars = new ArrayList<Car>();
        carsToRemove = new ArrayList<Car>();
//        Car car1 = new Car(road1);
//        car1.setVelocity(2f);
//        cars.add(car1);

        //Connect
        //connectSimObjects();

        //Input
        leftPressed = false;
        rightPressed = false;

        //Building
        roadPaint = true;

    }

    public void update() {
        //Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            roadPaint = !roadPaint;
        }

        //Startpoint Update
        //startpoint1.update();
//        if (startpoint1.isReadyToSpawn()) {
//            cars.add(new Car(startpoint1.getRoad(), startpoint1.getVelocity()));
//            startpoint1.setReadyToSpawn(false);
//        }

        //Cars Update
        for (Car car : cars) {
            car.update();
            if (car.hasReachedDestination()) {
                carsToRemove.add(car);
            }
        }

        //Car Disposal
        for (Car carToRemove : carsToRemove) {
            cars.remove(carToRemove);
        }
        carsToRemove.clear();

        //Test
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            connectSimObjects();
            tester(roads.get("R1"));
        }
    }

    private void tester(SimObject simObject) {
        System.out.println(simObject.getId());
        for (SimObject sO : simObject.getNextSimObjects()) {
            tester(sO);
        }
    }

    public void draw() {
        //ShapeRenderer Begin
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();

        //Grid
//        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(0.66f, 0.66f, 0.66f, 1);
//        for (Cell cell : grid.values()) {
//            cell.draw(shapeRenderer);
//        }

        //ShapeRenderer End
        shapeRenderer.end();

        //SpriteBatch Begin
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        //Grid
        for (Cell cell : grid.values()) {
            cell.draw(spriteBatch, empty_texture, road_texture);
        }

        //Startpoints
        //startpoint1.draw(spriteBatch);

        //Roads
        for (Road road : roads.values()) {
            road.draw(spriteBatch);
        }

        //Cars
        for (Car car : cars) {
            car.draw(spriteBatch);
        }

        //SpriteBatch End
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width/2f, height/2f, 0);
        camera.update();
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) {
                    leftPressed = true;
                    Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    grid.get(new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize))).setRoad(roadPaint);
                }
                else if (button == 1) { rightPressed = true; }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
//                dragging = true;
                if (leftPressed) {
                    Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    grid.get(new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize))).setRoad(roadPaint);
                } else if (rightPressed) {
                    camera.translate(-(float)Gdx.input.getDeltaX(), (float)Gdx.input.getDeltaY());
                    camera.update();
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == 0) { leftPressed = false; }
                else if (button == 1) { rightPressed = false; }
                return super.touchUp(screenX, screenY, pointer, button);
            }
        });
    }

    private void connectSimObjects() {
        //Startpoints
        for (Startpoint s : startpoints.values()) {
            s.connect();
        }

        //Roads
        for (Road r : roads.values()) {
            String nextSimObjectId = r.getEndCell().getSimObjectID();
            if (nextSimObjectId != null) {
                r.addNextSimObject(getSimObjectbyId(nextSimObjectId));
            }
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
        startCell.setSimObjectID(id);

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

        roads.put(id, new Road(id, cells, cardinal));
    }
}
