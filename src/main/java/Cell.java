import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.SimObjectType;

public class Cell {

    private Vector2 index; //Place in the grid
    private Vector2 position; //Place in the world
    private Vector2 centerPosition; //Place of its center point in the world
    private float size;

    private SimObject simObject;
    private Road road;

    private Texture texture;

    public Cell(Texture texture, int indexX, int indexY, float size) {
        this.texture = texture;
        index = new Vector2(indexX, indexY);
        position = new Vector2(indexX * size, indexY * size);
        centerPosition = new Vector2(position.x + (size / 2f), position.y + (size / 2f));

        this.size = size;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y, size, size, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public Vector2 getIndex() {
        return index;
    }

    public Vector2 getPosition() { return new Vector2(position); }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getCenterX() {
        return centerPosition.x;
    }

    public float getCenterY() {
        return centerPosition.y;
    }

    public SimObject getSimObject() {
        return simObject;
    }

    public void setSimObject(SimObject simObject) {
        this.simObject = simObject;
    }

    public SimObjectType getSimObjectType() {
        if (simObject != null) {
            return simObject.getSimObjectType();
        }
        return SimObjectType.NONE;
    }

    public Road getRoad() {
        return road;
    }
}
