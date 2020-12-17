import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

public class Road extends SimObject {

    private ArrayList<Cell> cells;
    private Cardinal cardinal;
    private float direction;

    //Drawing
    private float size;
    private Texture texture;

    public Road(String id, ArrayList<Cell> cells, Cardinal cardinal) {
        super(id);
        this.cells = cells;
        this.cardinal = cardinal;
        direction = GeoCalc.cardinalToRadians(cardinal);

        size = 40;
        texture = new Texture("road.png");
    }

    public void draw(SpriteBatch spriteBatch) {
        for (Cell cell : cells) {
            spriteBatch.draw(texture, cell.getX(), cell.getY(), size/2f, size/2f, size, size, 1, 1, (float)Math.toDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    public boolean compile(HashMap<Vector2, Cell> grid, HashMap<String, Road> roads) {
        //Next Road
//        if (cells..getSimObjectID() != null) {
//            if (endCell.getSimObjectID().charAt(0) == 'E') { //Endpoint
//                nextRoad = roads.get(endCell.getSimObjectID());
//            } else { //Road
//                nextRoad = roads.get(endCell.getSimObjectID());
//            }
//        } else {
//            return false;
//        }
//
//        return true;
        return true;
    }

    public float getDirection(float x, float y) {
        return GeoCalc.getStraightAngle(cells.get(0).getCenterX(),
                cells.get(0).getCenterY(),
                cells.get(cells.size()-1).getCenterX(),
                cells.get(cells.size()-1).getCenterY());
    }

    public boolean intersectsEnd(float currentX, float currentY, float nextX, float nextY) {
        return cells.get(cells.size()-1).getCenterX() >= Math.min(currentX, nextX)
                && cells.get(cells.size()-1).getCenterX() <= Math.max(currentX, nextX)
                && cells.get(cells.size()-1).getCenterY() <= Math.max(currentY, nextY)
                && cells.get(cells.size()-1).getCenterY() >= Math.min(currentY, nextY);
    }

    public float getDeltaX(float currentX) {
        return cells.get(cells.size()-1).getCenterX() - currentX;
    }

    public float getDeltaY(float currentY) {
        return currentY - cells.get(cells.size()-1).getCenterY();
    }

//    public Road getNextRoad() {
//        return nextRoad;
//    }
//
//    public void setNextRoad(Road nextRoad) {
//        this.nextRoad = nextRoad;
//    }

    public Cell getCell() {
        return cells.get(0);
    }

    public Cell getEndCell() {
        return cells.get(cells.size()-1);
    }

    public String getId() {
        return id;
    }
}
