import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Direction;
import enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Tunnel extends SimObject {

    // Environment
    private Environment environment;
    private Cell cell;
    private ArrayList<String> connections;

    // Geometry
    private Direction direction;
    private HashMap<Direction, Float> anchors;
    private Vector2 spawnPosition;

    // Cars
    private int carCounter;
    private Car carToSpawn;
    private String carToDespawn;
    private float timer;
    private int interval;

    // Drawing
    private Textures textures;
    private int cellSize;

    public Tunnel(String id, Cell cell, Direction direction, Environment environment, Textures textures) {
        super(id, SimObjectType.TUNNEL);
        this.environment = environment;
        this.textures = textures;
        this.cell = cell;
        this.direction = direction;
        connections = new ArrayList<String>();
        carCounter = 1;
        carToSpawn = null;
        carToDespawn = "";
        cellSize = Config.getInteger("cell_size");
        calculateAnchors();
        spawnPosition = calculateSpawnPosition();

        timer = 0;
        interval = 5;
    }

    public Tunnel(Tunnel tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.direction, tunnel.environment, tunnel.textures);
    }

    public void update() {
        // Timer
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= interval) {
            System.out.println("spawn");
            // Set carToSpawn
            carToSpawn = new Car('C' + carCounter + id, spawnPosition, direction, environment, textures);
            // Reset Timer
            timer = 0;
        }
    }

    public void drawGround(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("tunnel_road"), cell.getX(), cell.getY(), cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel_road").getWidth(), textures.get("tunnel_road").getHeight(), false, false);
    }

    public void drawAerial(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("tunnel"), cell.getX(), cell.getY(), cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel").getWidth(), textures.get("tunnel").getHeight(), false, false);
    }

    public void compile() {
        // Clear connections
        connections.clear();

        // Get cell in the forwards index, if cell contains a road, add the road's id to connections
        Cell forwardCell = environment.getCell(calculateForwardIndex(cell.getIndex()));
        if (forwardCell.getSimObjectType() == SimObjectType.ROAD) {
            connections.add(forwardCell.getSimObject().getId());
        }

        System.out.println("For " + id);
        System.out.println(connections.toString());
    }

    private void calculateAnchors() {
        anchors = new HashMap<Direction, Float>();
        float quarter = cellSize / 4f;
        anchors.put(Direction.NORTH, cell.getX() + quarter);
        anchors.put(Direction.EAST, cell.getY() + (quarter * 3));
        anchors.put(Direction.SOUTH, cell.getX() + (quarter * 3));
        anchors.put(Direction.WEST, cell.getY() + quarter);
    }

    private Vector2 calculateSpawnPosition() {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return new Vector2(cell.getCenterX(), anchors.get(direction));
        }
        return new Vector2(anchors.get(direction), cell.getCenterY());
    }

    private Vector2 calculateForwardIndex(Vector2 index) {
        switch (direction) {
            case NORTH:
                return index.cpy().add(0, 1);
            case EAST:
                return index.cpy().add(1, 0);
            case WEST:
                return index.cpy().add(-1, 0);
            default: // South
                return index.cpy().add(0, -1);
        }
    }

    public String getId() {
        return id;
    }

    public Cell getCell() {
        return cell;
    }

    public Car getCarToSpawn() {
        return carToSpawn;
    }

    public void setCarToSpawn(Car carToSpawn) {
        this.carToSpawn = carToSpawn;
    }

    public String getCarToDespawn() {
        return carToDespawn;
    }

    public void setCarToDespawn(String carToDespawn) {
        this.carToDespawn = carToDespawn;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getAnchor(Direction direction) { return anchors.get(direction); }
}
