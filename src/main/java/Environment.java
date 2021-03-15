import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Direction;
import enums.SimObjectType;

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
        // Grid (Level 0)
        for (Cell cell : grid.values()) {
            cell.draw(spriteBatch);
        }

        // Roads (Level 1)
        for (Road road : roads.values()) {
            road.draw(spriteBatch);
        }

        // Tunnel Roads (Level 2)
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.drawGround(spriteBatch);
        }

        // Cars (Level 3)
        for (Car car : cars.values()) {
            car.draw(spriteBatch);
        }

        // Tunnel Tunnels (Level 4)
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
        // Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.compile();
        }
        // Roads
        for (Road road : roads.values()) {
            road.compile(this);
        }
    }

    public void addRoad(Road road) {
        roads.put(road.getId(), road);
        for (Vector2 v : road.getCellIndices()) {
            grid.get(v).setSimObject(road);
        }
    }

    public void addTunnel(Tunnel tunnel) {
        tunnels.put(tunnel.getId(), tunnel);
        grid.get(tunnel.getCell().getIndex()).setSimObject(tunnel);
    }

    public Vector2 getIndexAtPositionSafe(Vector2 pos) {
        Vector2 index = new Vector2((int)(pos.x/gridCellSize), (int)(pos.y/gridCellSize));
        if (grid.containsKey(index)) {
            return index;
        }
        return new Vector2(1, 1);
    }

    public Vector2 getIndexAtPosition(Vector2 pos) {
        return new Vector2((int)(pos.x/gridCellSize), (int)(pos.y/gridCellSize));
    }

    public Cell getCell(Vector2 index) {
        return grid.get(index);
    }

    public Cell getCellAtPosition(Vector2 pos) {
        return grid.get(getIndexAtPosition(pos));
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

    public int getGridCellSize() {
        return gridCellSize;
    }

    public boolean cellHasRoad(Vector2 index) {
        if (grid.containsKey(index)) {
            SimObject simObject = grid.get(index).getSimObject();
            if (simObject != null) {
                return simObject.getSimObjectType() == SimObjectType.ROAD;
            }
        }
        return false;
    }
}
