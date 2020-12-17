import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private final int gridWidth;
    private final int gridHeight;
    private final float gridCellSize;
    private HashMap<Vector2, Cell> grid;

    private Startpoint startpoint1;
    private ArrayList<Car> cars;
    private ArrayList<Car> carsToRemove;

    //Roads
    private int roadCounter;
    private HashMap<String, Road> roads;

    public Environment() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera(1920, 1080);
        camera.position.set(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0);
        camera.update();
        setInputProcessor();

        //Grid Creation
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

        //Entities
        startpoint1 = new Startpoint("S1", grid.get(new Vector2(3, 8)), Cardinal.EAST);

        //Roads
        roadCounter = 0;
        roads = new HashMap<String, Road>();

        addRoad(grid.get(new Vector2(4, 8)), grid.get(new Vector2(12, 4)));
        addRoad(grid.get(new Vector2(12, 4)), grid.get(new Vector2(18, 10)));
        addRoad(grid.get(new Vector2(18, 10)), grid.get(new Vector2(14, 17)));

        //Cars
        cars = new ArrayList<Car>();
        carsToRemove = new ArrayList<Car>();
//        Car car1 = new Car(road1);
//        car1.setVelocity(2f);
//        cars.add(car1);

        //Compile
        compileEnvironment();

    }

    public void update() {
        //Startpoint Update
        startpoint1.update();
        if (startpoint1.isReadyToSpawn()) {
            cars.add(new Car(startpoint1.getRoad(), startpoint1.getCarVelocity()));
            startpoint1.setReadyToSpawn(false);
        }

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
    }

    public void draw() {
        //ShapeRenderer Begin
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();

        //Grid
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.66f, 0.66f, 0.66f, 1);
//        for (Cell cell : grid.values()) {
//            cell.draw(shapeRenderer);
//        }

        //ShapeRenderer End
        shapeRenderer.end();

        //SpriteBatch Begin
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        //Startpoints
        startpoint1.draw(spriteBatch);

        //Cars
        for (Car car : cars) {
            car.draw(spriteBatch);
        }

        //SpriteBatch End
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
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
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                camera.translate(-(float)Gdx.input.getDeltaX(), (float)Gdx.input.getDeltaY());
                camera.update();
                return true;
            }
        });
    }

    private void addRoad(Cell startCell, Cell endCell) {
        roadCounter++;
        String id = "R" + roadCounter;
        startCell.setSimObjectID(id);
        //roads.put(id, new Road(id, startCell, endCell));
    }

    private void compileEnvironment() {
        //Startpoints
        startpoint1.compile(grid, roads);

        //Roads
        for (Road road : roads.values()) {
            //road.compile(grid, roads);
        }
    }
}
