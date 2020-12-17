import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cell {

    private Vector2 index;
    private Vector2 position;
    private Vector2 centerPosition;

    private String simObjectID;
    private boolean isRoad;

    private float size;
    private float direction;

    public Cell(int indexX, int indexY, float size) {
        index = new Vector2(indexX, indexY);
        position = new Vector2(indexX * size, indexY * size);
        centerPosition = new Vector2(position.x + (size / 2f), position.y + (size / 2f));

        this.size = size;
        direction = GeoCalc.cardinalToRadians(Cardinal.SOUTH);
    }

    public void draw(SpriteBatch spriteBatch, Texture empty, Texture road) {
        if(isRoad) spriteBatch.draw(road, position.x, position.y, size/2f, size/2f, size, size, 1, 1, (float)Math.toDegrees(direction), 0, 0, road.getWidth(), road.getHeight(), false, false);
        else spriteBatch.draw(empty, position.x, position.y, size/2f, size/2f, size, size, 1, 1, (float)Math.toDegrees(direction), 0, 0, empty.getWidth(), empty.getHeight(), false, false);
        //shapeRenderer.rect(position.x, position.y, size, size);
    }

    public Vector2 getIndex() {
        return index;
    }

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

    public String getSimObjectID() {
        return simObjectID;
    }

    public void setSimObjectID(String simObjectID) {
        this.simObjectID = simObjectID;
    }

    public boolean isRoad() {
        return isRoad;
    }

    public void setRoad(boolean road) {
        isRoad = road;
    }
}
