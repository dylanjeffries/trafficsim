import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Direction;
import enums.Orientation;
import enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Tunnel extends SimObject {

    private Cell cell;

    private Direction direction;
    private HashMap<Direction, Float> anchors;

    //Drawing
    private Texture texture;
    private int cellSize;

    public Tunnel(String id, Cell cell, Direction direction, Texture texture) {
        super(id, SimObjectType.TUNNEL);
        this.cell = cell;
        this.direction = direction;
        this.texture = texture;
        cellSize = Config.getInteger("cell_size");
        calculateAnchors();
    }

    public Tunnel(Tunnel tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.direction, tunnel.texture);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, cell.getX(), cell.getY(), cellSize/2f, cellSize/2f, cellSize, cellSize,
                1, 1, GeoCalc.directionToDegrees(direction), 0, 0,
                texture.getWidth(), texture.getHeight(), false, false);
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

    public Direction getDirection() {
        return direction;
    }

    public float getAnchor(Direction direction) { return anchors.get(direction); }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
