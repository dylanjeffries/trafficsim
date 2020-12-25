import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

public class Road extends SimObject {

    private Cell startCell;
    private Cell endCell;

    private Direction direction;
    private float degrees;
    private int length;

    //Drawing
    private Texture texture;
    private int cellSize;

    public Road(String id, Cell startCell, Cell endCell, Texture texture) {
        super(id);
        this.startCell = startCell;
        this.endCell = endCell;
        this.texture = texture;
        direction = calculateDirection();
        degrees = directionToDegrees();
        length = calculateLength();
        cellSize = Config.getInteger("cell_size");
    }

    public void draw(SpriteBatch spriteBatch) {
        float drawX = startCell.getX();
        float drawY = startCell.getY();

        for (int i = 0; i < length; i++) {

            spriteBatch.draw(texture, drawX, drawY, cellSize/2f, cellSize/2f, cellSize, cellSize, 1, 1, degrees, 0, 0, texture.getWidth(), texture.getHeight(), false, false);

            switch(direction) {
                case NORTH:
                    drawY += cellSize;
                    break;
                case EAST:
                    drawX += cellSize;
                    break;
                case SOUTH:
                    drawY -= cellSize;
                    break;
                case WEST:
                    drawX -= cellSize;
                    break;
            }
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

    private Direction calculateDirection() {
        int sX = (int)startCell.getIndex().x;
        int sY = (int)startCell.getIndex().y;
        int eX = (int)endCell.getIndex().x;
        int eY = (int)endCell.getIndex().y;

        if (sX == eX && sY <= eY) {
            return Direction.NORTH;
        } else if (sX < eX && sY == eY) {
            return Direction.EAST;
        } else if (sX > eX && sY == eY) {
            return Direction.WEST;
        }
        return Direction.SOUTH;
    }

    private float directionToDegrees() {
        switch(direction) {
            case NORTH:
                return 180f;
            case EAST:
                return 90f;
            case WEST:
                return 270f;
        }
        return 0f;
    }

    private int calculateLength() {
        int sX = (int)startCell.getIndex().x;
        int sY = (int)startCell.getIndex().y;
        int eX = (int)endCell.getIndex().x;
        int eY = (int)endCell.getIndex().y;

        switch(direction) {
            case NORTH:
                return eY - sY + 1;
            case EAST:
                return eX - sX + 1;
            case WEST:
                return sX - eX + 1;
        }
        return sY - eY + 1;
    }

    public void recalculate() {
        direction = calculateDirection();
        degrees = directionToDegrees();
        length = calculateLength();
    }

    public boolean isIndexInline(Vector2 index) {
        if (startCell.getIndex().x == index.x || startCell.getIndex().y == index.y) {
            return true;
        }
        return false;
    }

    public ArrayList<Vector2> getCellIndices() {
        ArrayList<Vector2> cellIndices = new ArrayList<Vector2>();
        int indexX = (int)startCell.getIndex().x;
        int indexY = (int)startCell.getIndex().y;
        for (int i = 0; i < length; i++) {

            cellIndices.add(new Vector2(indexX, indexY));

            switch(direction) {
                case NORTH:
                    indexY += 1;
                    break;
                case EAST:
                    indexX += 1;
                    break;
                case SOUTH:
                    indexY -= 1;
                    break;
                case WEST:
                    indexX -= 1;
                    break;
            }
        }
        return cellIndices;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setEndCell(Cell endCell) {
        this.endCell = endCell;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    //    public float getDirection(float x, float y) {
//        return GeoCalc.getStraightAngle(cells.get(0).getCenterX(),
//                cells.get(0).getCenterY(),
//                cells.get(cells.size()-1).getCenterX(),
//                cells.get(cells.size()-1).getCenterY());
//    }
//
//    public boolean intersectsEnd(float currentX, float currentY, float nextX, float nextY) {
//        return cells.get(cells.size()-1).getCenterX() >= Math.min(currentX, nextX)
//                && cells.get(cells.size()-1).getCenterX() <= Math.max(currentX, nextX)
//                && cells.get(cells.size()-1).getCenterY() <= Math.max(currentY, nextY)
//                && cells.get(cells.size()-1).getCenterY() >= Math.min(currentY, nextY);
//    }
//
//    public float getDeltaX(float currentX) {
//        return cells.get(cells.size()-1).getCenterX() - currentX;
//    }
//
//    public float getDeltaY(float currentY) {
//        return currentY - cells.get(cells.size()-1).getCenterY();
//    }

//    public Road getNextRoad() {
//        return nextRoad;
//    }
//
//    public void setNextRoad(Road nextRoad) {
//        this.nextRoad = nextRoad;
//    }

//    public Cell getCell() {
//        return cells.get(0);
//    }
//
//    public Cell getEndCell() {
//        return cells.get(cells.size()-1);
//    }

    public String getId() {
        return id;
    }
}
