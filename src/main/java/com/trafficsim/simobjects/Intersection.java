package com.trafficsim.simobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.*;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Intersection extends SimObject {

    // Environment
    private Environment environment;
    private Cell cell;

    // Geometry
    private HashMap<Direction, Float> anchors;

    // Drawing
    private Textures textures;
    private int cellSize;

    public Intersection(String id, Cell cell, Environment environment, Textures textures) {
        super(id, SimObjectType.INTERSECTION, 1);
        this.environment = environment;
        this.textures = textures;
        this.cell = cell;

        cellSize = Config.getInteger("cell_size");
        calculateAnchors();
    }

    public Intersection(Intersection tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.environment, tunnel.textures);
    }

    public void update() {}

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("intersection"), cell.getX(), cell.getY(), cellSize, cellSize);
    }

    public void compile() {
        // Clear connections
        connections.clear();

        // Adjacent connections
        for (Vector2 v : calculateAdjacentIndices(cell.getIndex())) {
            Cell tempCell = environment.getCell(v);
            if (tempCell.getSimObjectType() != SimObjectType.NONE) {
                connections.add(tempCell.getSimObject().getId());
            }
        }
    }

    private void calculateAnchors() {
        anchors = new HashMap<Direction, Float>();
        float quarter = cellSize / 4f;
        anchors.put(Direction.NORTH, cell.getX() + quarter);
        anchors.put(Direction.EAST, cell.getY() + (quarter * 3));
        anchors.put(Direction.SOUTH, cell.getX() + (quarter * 3));
        anchors.put(Direction.WEST, cell.getY() + quarter);
    }

    private ArrayList<Vector2> calculateAdjacentIndices(Vector2 index) {
        ArrayList<Vector2> adjacentIndices = new ArrayList<Vector2>();
        // North
        if (environment.isIndexInGrid(index.cpy().add(0, 1))) {
            adjacentIndices.add(index.cpy().add(0, 1));
        }
        // East
        if (environment.isIndexInGrid(index.cpy().add(1, 0))) {
            adjacentIndices.add(index.cpy().add(1, 0));
        }
        // South
        if (environment.isIndexInGrid(index.cpy().add(0, -1))) {
            adjacentIndices.add(index.cpy().add(0, -1));
        }
        // West
        if (environment.isIndexInGrid(index.cpy().add(-1, 0))) {
            adjacentIndices.add(index.cpy().add(-1, 0));
        }
        return adjacentIndices;
    }

    public String getId() {
        return id;
    }

    public Cell getCell() {
        return cell;
    }

    public float getAnchor(Direction direction) { return anchors.get(direction); }
}
