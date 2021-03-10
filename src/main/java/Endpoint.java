import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Cardinal;

import java.util.HashMap;

public class Endpoint extends SimObject {

    private Cell cell;
    private Cell adjacentCell;
    private Cardinal cardinal;
    private float direction;
    private Road road;

    //Drawing
    private float size;
    private Texture texture;

    public Endpoint(String id, Cell cell, Cardinal cardinal) {
        super(id);

        this.cell = cell;
        this.cardinal = cardinal;
        //direction = GeoCalc.cardinalToRadians(cardinal);

        size = 40;
        texture = new Texture("endpoint.png");
    }

    public void update() {

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
        //road = new Road(id + "R", adjacentCell, cell);

        return true;
    }

    public Road getRoad() {
        return road;
    }

}
