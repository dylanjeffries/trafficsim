import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import enums.Direction;
import enums.SimObjectType;

import java.util.HashMap;

public class Tunnel extends SimObject {

    private Cell cell;

    private Direction direction;
    private HashMap<Direction, Float> anchors;

    private String carToDespawn;

    //Drawing
    private Texture tunnelTexture;
    private Texture roadTexture;
    private int cellSize;

    public Tunnel(String id, Cell cell, Direction direction, Texture tunnelTexture, Texture roadTexture) {
        super(id, SimObjectType.TUNNEL);
        this.cell = cell;
        this.direction = direction;
        this.tunnelTexture = tunnelTexture;
        this.roadTexture = roadTexture;
        carToDespawn = "";
        cellSize = Config.getInteger("cell_size");
        calculateAnchors();
    }

    public Tunnel(Tunnel tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.direction, tunnel.tunnelTexture, tunnel.roadTexture);
    }

    public void drawGround(SpriteBatch spriteBatch) {
        spriteBatch.draw(roadTexture, cell.getX(), cell.getY(), cellSize/2f, cellSize/2f, cellSize, cellSize,
                1, 1, Calculator.directionToDegrees(direction), 0, 0,
                roadTexture.getWidth(), roadTexture.getHeight(), false, false);
    }

    public void drawAerial(SpriteBatch spriteBatch) {
        spriteBatch.draw(tunnelTexture, cell.getX(), cell.getY(), cellSize/2f, cellSize/2f, cellSize, cellSize,
                1, 1, Calculator.directionToDegrees(direction), 0, 0,
                tunnelTexture.getWidth(), tunnelTexture.getHeight(), false, false);
    }

    private void calculateAnchors() {
        anchors = new HashMap<Direction, Float>();
        float quarter = cellSize / 4f;
        anchors.put(Direction.NORTH, cell.getX() + quarter);
        anchors.put(Direction.EAST, cell.getY() + (quarter * 3));
        anchors.put(Direction.SOUTH, cell.getX() + (quarter * 3));
        anchors.put(Direction.WEST, cell.getY() + quarter);
    }

    public String getId() {
        return id;
    }

    public Cell getCell() {
        return cell;
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
