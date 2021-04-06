package com.trafficsim.simobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.trafficsim.*;
import com.trafficsim.driverprofiles.DriverProfileManager;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Tunnel extends SimObject {

    // Environment
    private Environment environment;
    private Cell cell;

    // Geometry
    private Direction direction;
    private HashMap<Direction, Float> anchors;
    private Vector2 spawnPosition;

    // Cars
    private int carCounter;
    private Car carToSpawn;
    private String carToDespawn;
    private ArrayList<Route> routes;
    private DriverProfileManager driverProfileManager;
    private int rate;
    private float timer;

    // Drawing
    private Textures textures;
    private int cellSize;

    public Tunnel(String id, Cell cell, Direction direction, Environment environment, Textures textures) {
        super(id, SimObjectType.TUNNEL, 1);
        this.environment = environment;
        this.textures = textures;
        this.cell = cell;
        this.direction = direction;
        carCounter = 1;
        carToSpawn = null;
        carToDespawn = "";
        routes = new ArrayList<Route>();
        driverProfileManager = new DriverProfileManager(textures);

        cellSize = Config.getInteger("cell_size");
        calculateAnchors();
        spawnPosition = calculateSpawnPosition();

        rate = 10;
        timer = 0;
    }

    public Tunnel(Tunnel tunnel) {
        this(tunnel.id, tunnel.cell, tunnel.direction, tunnel.environment, tunnel.textures);
    }

    public void update() {
        // Timer
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= (60f / rate) && routes.size() != 0) {
            // Set carToSpawn
            Random r = new Random();
            carToSpawn = new Car("C" + carCounter + id, spawnPosition, direction,
                    routes.get(r.nextInt(routes.size())), driverProfileManager.getRandomProfile(), environment);
            carCounter++;
            // Reset Timer
            timer = 0;
        }
    }

    public void drawGround(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("tunnel_road"), cell.getX(), cell.getY(), cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel_road").getWidth(), textures.get("tunnel_road").getHeight(), false, false);
    }

    public void drawAerial(SpriteBatch spriteBatch) {
        spriteBatch.draw(textures.get("tunnel"), cell.getX(), cell.getY(), cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel").getWidth(), textures.get("tunnel").getHeight(), false, false);
    }

    public void compile(GlobalSettings globalSettings) {
        // Clear routes
        routes.clear();

        // Set Driver Profile Manager percentages
        driverProfileManager.setPercentages(
                globalSettings.getSlowPct(),
                globalSettings.getNormalPct(),
                globalSettings.getFastPct());

        // Get cell in the forwards index
        Cell forwardCell = environment.getCell(Calculator.getIndexInDirection(cell.getIndex(), direction));

        // If forward cell contains a road, commence route finding
        if (forwardCell.getSimObjectType() == SimObjectType.ROAD) {
            // Find routes to other tunnels using forward SimObject's id and a base route containing self
            calculateRoutes(forwardCell.getSimObject().getId(), new Route(this));
        }
    }

    public void reset() {
        carCounter = 1;
        carToSpawn = null;
        carToDespawn = "";
    }

    @Override
    public Table getSidebarTable() {
        Table table = new Table();
        table.padTop(50);

        Label nameLabel = new Label("Tunnel " + id, UIStyling.TITLE_LABEL_STYLE);
        table.add(nameLabel).colspan(2).pad(30);
        table.row();

        Label label = new Label("Spawns\nper Minute: ", UIStyling.BODY_LABEL_STYLE);
        TextField textField = new TextField(String.valueOf(rate), UIStyling.TEXTFIELD_STYLE);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (textField.getText().equals("")) {
                    rate = 0;
                } else {
                    rate = Integer.parseInt(textField.getText());
                }
            }
        });
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        table.add(label);
        table.add(textField);

        return table;
    }

    private void calculateRoutes(String id, Route route) {
        // If id is not already in route
        if (!route.contains(id)) {
            // Copy route to newRoute, get SimObject related to id and add SimObject to newRoute
            Route newRoute = new Route(route);
            SimObject simObject = environment.getSimObject(id);
            newRoute.addSimObject(simObject);
            // If SimObject is a tunnel
            if (simObject.getSimObjectType() == SimObjectType.TUNNEL) {
                // Stop searching and add route to routes
                routes.add(newRoute);
            } else {
                // Investigate the current SimObject's connections
                for (String connection : simObject.getConnections()) {
                    calculateRoutes(connection, newRoute);
                }
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

    private Vector2 calculateSpawnPosition() {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return new Vector2(cell.getCenterX(), anchors.get(direction));
        }
        return new Vector2(anchors.get(direction), cell.getCenterY());
    }

    public String getId() {
        return id;
    }

    public Cell getCell() {
        return cell;
    }

    public Car getCarToSpawn() {
        return carToSpawn;
    }

    public void setCarToSpawn(Car carToSpawn) {
        this.carToSpawn = carToSpawn;
    }

    public String getCarToDespawn() {
        return carToDespawn;
    }

    public void setCarToDespawn(String carToDespawn) {
        this.carToDespawn = carToDespawn;
    }
}
