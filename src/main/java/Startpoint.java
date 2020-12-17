import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class Startpoint extends SimObject {

    private Cell cell;
    private Cell adjacentCell;
    private Cardinal cardinal;
    private float direction;
    private Road road;

    //Spawning
    private int clock;
    private int interval;
    private boolean readyToSpawn;
    private float carVelocity;

    //Drawing
    private float size;
    private Texture texture;

    public Startpoint(String id, Cell cell, Cardinal cardinal) {
        super(id);

        this.cell = cell;
        this.cardinal = cardinal;
        direction = GeoCalc.cardinalToRadians(cardinal);

        clock = 0;
        readyToSpawn = false;
        interval = 180;
        carVelocity = 2f;

        size = 40;
        texture = new Texture("startpoint.png");
    }

    public void update() {
        if (clock > interval) {
            readyToSpawn = true;
            clock = 0;
        }
        clock++;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, cell.getX(), cell.getY(), size/2f, size/2f, size, size, 1, 1, (float)Math.toDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public boolean compile(HashMap<Vector2, Cell> grid, HashMap<String, Road> roads) {
        //Adjacent Cell
        Vector2 adjacentIndex;
        switch (cardinal) {
            case NORTH:
                adjacentIndex = new Vector2(cell.getIndex().add(0, 1));
                break;

            case EAST:
                adjacentIndex = new Vector2(cell.getIndex().add(1, 0));
                break;

            case SOUTH:
            default:
                adjacentIndex = new Vector2(cell.getIndex().sub(0, 1));
                break;

            case WEST:
                adjacentIndex = new Vector2(cell.getIndex().sub(1, 0));
                break;
        }
        if (!grid.containsKey(adjacentIndex)) {
            return false;
        } else {
            adjacentCell = grid.get(adjacentIndex);
        }

        //Road
        //road = new Road(id + "R", cell, adjacentCell);

        //Next Road
        if (adjacentCell.getSimObjectID() != null) {
            //road.setNextRoad(roads.get(adjacentCell.getSimObjectID()));
        } else {
            return false;
        }

        return true;
    }

    public boolean isReadyToSpawn() {
        return readyToSpawn;
    }

    public float getCarVelocity() {
        return carVelocity;
    }

    public Road getRoad() {
        return road;
    }

    public void setReadyToSpawn(boolean readyToSpawn) {
        this.readyToSpawn = readyToSpawn;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setcarVelocity(float carVelocity) {
        this.carVelocity = carVelocity;
    }
}
