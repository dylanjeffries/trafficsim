package com.trafficsim.simobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.Calculator;
import com.trafficsim.Cell;
import com.trafficsim.Config;
import com.trafficsim.Environment;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.Orientation;
import com.trafficsim.enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Road extends SimObject {

    // Environment
    private Cell startCell;
    private Cell endCell;

    // Geometry
    private Orientation orientation;
    private Direction direction;
    private HashMap<Direction, Float> anchors;

    // Drawing
    private Texture texture;
    private int cellSize;

    public Road(String id, Cell startCell, Cell endCell, Texture texture) {
        super(id, SimObjectType.ROAD, 1);
        this.startCell = startCell;
        this.endCell = endCell;
        this.texture = texture;
        cellSize = Config.getInteger("cell_size");
        orientation = calculateOrientation();
        direction = calculateDirection();
        cellLength = calculateLength();
        calculateAnchors();
    }

    public Road(Road road) {
        this(road.id, road.startCell, road.endCell, road.texture);
    }

    public void draw(SpriteBatch spriteBatch) {
        float drawX = startCell.getX();
        float drawY = startCell.getY();

        for (int i = 0; i < cellLength; i++) {

            spriteBatch.draw(texture, drawX, drawY, cellSize/2f, cellSize/2f, cellSize, cellSize,
                    1, 1, Calculator.orientationToDegrees(orientation), 0, 0,
                    texture.getWidth(), texture.getHeight(), false, false);

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

    public void compile(Environment environment) {
        // Clear connections
        connections.clear();

        // Start connection
        Cell tempCell = environment.getCell(calculateIndexInDirection(startCell.getIndex(), Calculator.flipDirection(direction)));
        if (tempCell.getSimObjectType() != SimObjectType.NONE) {
            connections.add(tempCell.getSimObject().getId());
        }

        // End connection
        tempCell = environment.getCell(calculateIndexInDirection(endCell.getIndex(), direction));
        if (tempCell.getSimObjectType() != SimObjectType.NONE) {
            connections.add(tempCell.getSimObject().getId());
        }

//        System.out.println("For " + id);
//        System.out.println(connections.toString());
    }

    private Orientation calculateOrientation() {
        if ((int)startCell.getIndex().x == (int)endCell.getIndex().x) {
            return Orientation.VERTICAL;
        }
        return Orientation.HORIZONTAL;
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

    private void calculateAnchors() {
        anchors = new HashMap<Direction, Float>();
        float quarter = cellSize / 4f;
        anchors.put(Direction.NORTH, startCell.getX() + quarter);
        anchors.put(Direction.EAST, startCell.getY() + (quarter * 3));
        anchors.put(Direction.SOUTH, startCell.getX() + (quarter * 3));
        anchors.put(Direction.WEST, startCell.getY() + quarter);
    }

    private Vector2 calculateIndexInDirection(Vector2 index, Direction direction) {
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

    public void recalculate() {
        orientation = calculateOrientation();
        direction = calculateDirection();
        cellLength = calculateLength();
        calculateAnchors();
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
        for (int i = 0; i < cellLength; i++) {

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

    public String getId() {
        return id;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public void setEndCell(Cell endCell) {
        this.endCell = endCell;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getAnchor(Direction direction) { return anchors.get(direction); }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
