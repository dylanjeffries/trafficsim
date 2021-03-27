package com.trafficsim;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.enums.SimObjectType;
import com.trafficsim.simobjects.*;

import java.util.HashMap;

public class Environment {

    //Grid
    private final int gridWidth;
    private final int gridHeight;
    private final int gridCellSize;
    private HashMap<Vector2, Cell> grid;

    //SimObjects
    private HashMap<String, Car> cars;
    private HashMap<String, Road> roads;
    private HashMap<String, Tunnel> tunnels;
    private HashMap<String, Intersection> intersections;

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

        cars = new HashMap<String, Car>();
        roads = new HashMap<String, Road>();
        tunnels = new HashMap<String, Tunnel>();
        intersections = new HashMap<String, Intersection>();

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
        // Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.update();
            if (tunnel.getCarToSpawn() != null) {
                cars.put(tunnel.getCarToSpawn().getId(), tunnel.getCarToSpawn());
                tunnel.setCarToSpawn(null);
            }
            if (!tunnel.getCarToDespawn().equals("")) {
                cars.remove(tunnel.getCarToDespawn());
                tunnel.setCarToDespawn("");
            }
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
            intersection.draw(spriteBatch);
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
    }

    public void dispose() {

    }

    public void resize(int width, int height) {

    }

    public void compile() {
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
            tunnel.compile();
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
}
