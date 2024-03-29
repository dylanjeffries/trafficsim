package com.trafficsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.FloatArray;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;
import com.trafficsim.simobjects.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Environment {

    // Grid
    private final int gridWidth;
    private final int gridHeight;
    private final int gridCellSize;
    private HashMap<Vector2, Cell> grid;

    // SimObjects
    private GlobalSettings globalSettings;
    private HashMap<String, Car> cars;
    private HashMap<String, Road> roads;
    private HashMap<String, Tunnel> tunnels;
    private HashMap<String, Intersection> intersections;

    // Output
    private float timer;
    private int minutesPassed;
    private int minuteCarsDespawned;
    private float cumulativeEfficiency;
    private int carInflow;

    public Environment(Textures textures) {
        //Grid
        gridWidth = Config.getInteger("grid_width");;
        gridHeight = Config.getInteger("grid_height");;
        gridCellSize = Config.getInteger("cell_size");
        grid = new HashMap<Vector2, Cell>();
        for (int i = 0; i < gridHeight; i++) { //
            for (int j = 0; j < gridWidth; j++) {
                grid.put(
                        new Vector2(j, i),
                        new Cell(textures.get("cell"), j, i, gridCellSize)
                );
            }
        }

        globalSettings = new GlobalSettings();

        cars = new HashMap<String, Car>();
        roads = new HashMap<String, Road>();
        tunnels = new HashMap<String, Tunnel>();
        intersections = new HashMap<String, Intersection>();

        timer = 0;
        minutesPassed = 1;
        minuteCarsDespawned = 0;
        cumulativeEfficiency = 0;
        carInflow = 0;

        // Test Road
//        addRoad(new Road("100",
//                getCell(new Vector2(2, 2)),
//                getCell(new Vector2(8, 2)),
//                textures.get("road")));

        // Test Cars
        //cars.put("C1T3", new Car("C1T3", getCellPosition(new Vector2(2, 2)), Direction.EAST, this, textures));
        //cars.put("C2T3", new Car("C2T3", getCellPosition(new Vector2(20, 10)), Direction.WEST, this, textures));
    }

    public void update() {
        // Tick timer
        timer += Gdx.graphics.getDeltaTime();

        // Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.update();
            if (tunnel.getCarToSpawn() != null) {
                cars.put(tunnel.getCarToSpawn().getId(), tunnel.getCarToSpawn());
                tunnel.setCarToSpawn(null);
            }
            if (!tunnel.getCarToDespawn().equals("")) {
                cars.remove(tunnel.despawnCar());
            }

            // Tunnel Output
            if (timer >= 60f) {
                // Write to File
                writeToFile(String.format("\n%d, %s, %d", minutesPassed, tunnel.getId(), tunnel.getCarsDespawned()));
                minuteCarsDespawned += tunnel.getCarsDespawned();
                tunnel.setCarsDespawned(0);
            }
        }

        // Check output timer
        if (timer >= 60f) {
            float efficiency = ((float)minuteCarsDespawned / (float)carInflow)*100;
            writeToFile(String.format("\nMinute: %d, Cars Despawned: %d, Efficiency: %f",
                    minutesPassed, minuteCarsDespawned, efficiency));
            cumulativeEfficiency += efficiency;
            minutesPassed++;
            minuteCarsDespawned = 0;
            timer = 0;
        }

        // Intersections
        for (Intersection intersection : intersections.values()) {
            intersection.update();
        }

        // Cars
        for (Car car : cars.values()) {
            car.update();
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        // Grid
        for (Cell cell : grid.values()) {
            cell.draw(spriteBatch);
        }

        // Roads
        for (Road road : roads.values()) {
            road.draw(spriteBatch);
        }


        // Tunnel Roads
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.drawGround(spriteBatch);
        }

        // Intersection Roads
        for (Intersection intersection : intersections.values()) {
            intersection.drawGround(spriteBatch);
        }

        // Cars
        for (Car car : cars.values()) {
            //System.out.println(car.getId());
            car.draw(spriteBatch);
        }

        // Tunnel Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.drawAerial(spriteBatch);
        }

        // Intersection Lights
        for (Intersection intersection : intersections.values()) {
            intersection.drawAerial(spriteBatch);
        }
    }

    public void dispose() {

    }

    public void resize(int width, int height) {

    }

    public void compile() {
        // Write to File
        writeToFile("\n*** SIMULATION STARTED ***");

        // Determine connections
        // Roads
        for (Road road : roads.values()) {
            road.compile(this);
        }
        // Intersections
        for (Intersection intersection : intersections.values()) {
            intersection.compile();
        }
        // Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.compile(globalSettings);
            carInflow += tunnel.getRate();
        }

        // Write to File
        writeToFile("\n*** END OF CONFIGURATIONS - " + Config.getDateTime() + " ***");
    }

    public void reset() {
        writeToFile(String.format("\nOverall Efficiency: %f", cumulativeEfficiency / Math.max(1, minutesPassed-1)));

        // Write to File
        writeToFile("\n*** SIMULATION STOPPED " + Config.getDateTime() + " ***");

        // Reset output
        timer = 0;
        minutesPassed = 1;
        minuteCarsDespawned = 0;
        cumulativeEfficiency = 0;
        carInflow = 0;

        // Clear all cars
        cars.clear();

        // Reset tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.reset();
        }
    }

    private void writeToFile(String string) {
        try {
            // Write to File
            FileWriter writer = new FileWriter(Config.getOutputFilename(), true);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void addRoad(Road road) {
        roads.put(road.getId(), road);
        for (Vector2 v : road.getCellIndices()) {
            grid.get(v).setSimObject(road);
        }
    }

    public void deleteRoad(Road road) {
        for (Vector2 v : road.getCellIndices()) {
            grid.get(v).setSimObject(null);
        }
        roads.remove(road.getId());
    }

    public void addTunnel(Tunnel tunnel) {
        tunnels.put(tunnel.getId(), tunnel);
        grid.get(tunnel.getCell().getIndex()).setSimObject(tunnel);
    }

    public void deleteTunnel(Tunnel tunnel) {
        tunnel.getCell().setSimObject(null);
        tunnels.remove(tunnel.getId());
    }

    public void addIntersection(Intersection intersection) {
        intersections.put(intersection.getId(), intersection);
        grid.get(intersection.getCell().getIndex()).setSimObject(intersection);
    }

    public void deleteIntersection(Intersection intersection) {
        intersection.getCell().setSimObject(null);
        intersections.remove(intersection.getId());
    }

    public Cell getCell(Vector2 index) {
        return grid.containsKey(index) ? grid.get(index) : grid.get(new Vector2(1, 1));
    }

    public Vector2 getIndexAtPosition(Vector2 pos) {
        Vector2 index = new Vector2((int)(pos.x/gridCellSize), (int)(pos.y/gridCellSize));
        return grid.containsKey(index) ? index : new Vector2(1, 1);
    }

    public Cell getCellAtPosition(Vector2 pos) {
        return grid.get(getIndexAtPosition(pos));
    }

    public boolean isIndexInGrid(Vector2 index) {
        return grid.containsKey(index);
    }

    public Vector2 getCellPosition(Vector2 index) {
        return (index == null) ? new Vector2(0, 0) : grid.get(index).getPosition();
    }

    public SimObjectType getCellSimObjectType(Vector2 index) {
        if (grid.containsKey(index)) {
            SimObject simObject = grid.get(index).getSimObject();
            if (simObject != null) {
                return simObject.getSimObjectType();
            }
        }
        return SimObjectType.NONE;
    }

    public SimObject getSimObject(String id) {
        switch (id.charAt(0)) {
            case 'R': // Road
                return roads.containsKey(id) ? roads.get(id) : null;

            case 'T': // Tunnel
                return tunnels.containsKey(id) ? tunnels.get(id) : null;

            case 'I': // Intersection
                return intersections.containsKey(id) ? intersections.get(id) : null;
        }
        return null;
    }

    public void deleteSimObject(String id) {
        switch (id.charAt(0)) {
            case 'R': // Road
                if (roads.containsKey(id)) {
                    deleteRoad(roads.get(id));
                }

            case 'T': // Tunnel
                if (tunnels.containsKey(id)) {
                    deleteTunnel(tunnels.get(id));
                }

            case 'I': // Intersection
                if (intersections.containsKey(id)) {
                    deleteIntersection(intersections.get(id));
                }
        }
    }

    public int getGridCellSize() {
        return gridCellSize;
    }

    public boolean isCarInArea(String carId, Polygon area) {
        for (Car c : cars.values()) {
            if (!carId.equals(c.getId()) &&
                    Intersector.intersectPolygons(new FloatArray(area.getTransformedVertices()),
                    new FloatArray(c.getCollisionBox().getTransformedVertices()))) {
                return true;
            }
        }
        return false;
    }

    public boolean isIntersectionInArea(String intersectionId, Polygon area) {
        // If Intersection exists
        if (intersections.containsKey(intersectionId)) {
            // If Intersection Collision Box and Area intersect
            if (Intersector.intersectPolygons(new FloatArray(area.getTransformedVertices()),
                    new FloatArray(intersections.get(intersectionId).getCollisionBox().getTransformedVertices()))) {
                return true;
            }
        }
        return false;
    }

    public boolean getIntersectionLightState(String intersectionId, Direction direction) {
        // If Intersection exists
        if (intersections.containsKey(intersectionId)) {
            return intersections.get(intersectionId).getTrafficLightState(Calculator.flipDirection(direction));
        }
        return false;
    }

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }
}
