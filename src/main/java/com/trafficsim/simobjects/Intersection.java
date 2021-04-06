package com.trafficsim.simobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.trafficsim.*;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;
import com.trafficsim.trafficlights.TrafficLightManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Intersection extends SimObject {

    // Environment
    private Environment environment;
    private Cell cell;

    // Geometry
    private Polygon collisionBox;
    private HashMap<Direction, Float> anchors;
    private HashMap<String, Direction> adjacentDirections;

    // Traffic Lights
    private TrafficLightManager trafficLightManager;
    private int stageOneDuration;
    private int stageTwoDuration;

    // Drawing
    private Textures textures;
    private int cellSize;

    public Intersection(String id, Cell cell, Environment environment, Textures textures) {
        super(id, SimObjectType.INTERSECTION, 1);
        this.environment = environment;
        this.textures = textures;
        this.cell = cell;

        cellSize = Config.getInteger("cell_size");
        collisionBox = new Polygon(calculateCollisionVertices());
        calculateAnchors();
        adjacentDirections = new HashMap<String, Direction>();

        trafficLightManager = new TrafficLightManager();
        stageOneDuration = 5;
        stageTwoDuration = 5;
    }

    public Intersection(Intersection tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.environment, tunnel.textures);
    }

    public void update() {
        trafficLightManager.update();
    }

    public void drawGround(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("intersection"), cell.getX(), cell.getY(), cellSize, cellSize);
    }

    public void drawAerial(SpriteBatch spriteBatch) {
        if (trafficLightManager.isEnabled()) {
            for (Direction d : adjacentDirections.values()) {
                String lightTextureString = "red_light";
                if (trafficLightManager.getState(d)) {
                    lightTextureString = "green_light";
                }
                spriteBatch.draw(textures.get(lightTextureString), cell.getX(), cell.getY(), cellSize / 2f, cellSize / 2f,
                        cellSize, cellSize, 1, 1, Calculator.directionToDegrees(d), 0, 0,
                        textures.get(lightTextureString).getWidth(), textures.get(lightTextureString).getHeight(), false, false);
            }
        }
    }

    public void compile() {
        // Clear connections
        connections.clear();
        adjacentDirections.clear();

        // Adjacent connections
        processAdjacentConnection(Direction.NORTH);
        processAdjacentConnection(Direction.EAST);
        processAdjacentConnection(Direction.SOUTH);
        processAdjacentConnection(Direction.WEST);

        // Traffic Light Manager
        // If the intersection is a corner, disable traffic lights. Else enable them.
        trafficLightManager.compile(adjacentDirections.size() > 2, stageOneDuration, stageTwoDuration);
    }

    @Override
    public Table getSidebarTable() {
        Table table = new Table();

        Label nameLabel = new Label("Intersection " + id, UIStyling.TITLE_LABEL_STYLE);
        table.add(nameLabel).colspan(2).padTop(250).spaceBottom(40);
        table.row();

        Label stageOneLabel = new Label("Stage One\n(North/South)\nDuration (s): ", UIStyling.BODY_LABEL_STYLE);
        TextField stageOneTextfield = new TextField(String.valueOf(stageOneDuration), UIStyling.TEXTFIELD_STYLE);
        stageOneTextfield.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (textField.getText().equals("")) {
                    stageOneDuration = 0;
                } else {
                    stageOneDuration = Integer.parseInt(textField.getText());
                }
            }
        });
        stageOneTextfield.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        table.add(stageOneLabel).spaceBottom(40);
        table.add(stageOneTextfield).spaceBottom(40);
        table.row();

        Label stageTwoLabel = new Label("Stage Two\n(East/West)\nDuration (s): ", UIStyling.BODY_LABEL_STYLE);
        TextField stageTwoTextfield = new TextField(String.valueOf(stageTwoDuration), UIStyling.TEXTFIELD_STYLE);
        stageTwoTextfield.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (textField.getText().equals("")) {
                    stageTwoDuration = 0;
                } else {
                    stageTwoDuration = Integer.parseInt(textField.getText());
                }
            }
        });
        stageTwoTextfield.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        table.add(stageTwoLabel);
        table.add(stageTwoTextfield);

        return table;
    }

    private void processAdjacentConnection(Direction direction) {
        Vector2 index = Calculator.getIndexInDirection(cell.getIndex(), direction);
        if (environment.isIndexInGrid(index)) {
            Cell tempCell = environment.getCell(index);
            if (tempCell.getSimObjectType() != SimObjectType.NONE) {
                connections.add(tempCell.getSimObject().getId());
                adjacentDirections.put(tempCell.getSimObject().getId(), direction);
            }
        }
    }

    private float[] calculateCollisionVertices() {
        float[] vertices = new float[8];
        // Initially assumes car is facing South
        // Bottom Left
        vertices[0] = cell.getX();
        vertices[1] = cell.getY();
        // Top Left
        vertices[2] = cell.getX();
        vertices[3] = cell.getY() + cellSize;
        // Top Right
        vertices[4] = cell.getX() + cellSize;
        vertices[5] = cell.getY() + cellSize;
        // Bottom Right
        vertices[6] = cell.getX() + cellSize;
        vertices[7] = cell.getY();

        return vertices;
    }

    private void calculateAnchors() {
        anchors = new HashMap<Direction, Float>();
        float quarter = cellSize / 4f;
        anchors.put(Direction.NORTH, cell.getX() + quarter);
        anchors.put(Direction.EAST, cell.getY() + (quarter * 3));
        anchors.put(Direction.SOUTH, cell.getX() + (quarter * 3));
        anchors.put(Direction.WEST, cell.getY() + quarter);
    }

    public Direction getNextDirection(String nextId) {
        return adjacentDirections.get(nextId);
    }

    public String getId() {
        return id;
    }

    public Cell getCell() {
        return cell;
    }

    public Polygon getCollisionBox() {
        return collisionBox;
    }

    public float getAnchor(Direction direction) { return anchors.get(direction); }

    public boolean getTrafficLightState(Direction direction) {
        return trafficLightManager.getState(direction);
    }
}
