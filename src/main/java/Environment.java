import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import enums.BuildingMode;
import enums.SimObjectType;

import java.util.HashMap;

public class Environment {

    //Grid
    private final int gridWidth;
    private final int gridHeight;
    private final int gridCellSize;
    private HashMap<Vector2, Cell> grid;

    //SimObjects
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

        roads = new HashMap<String, Road>();
        tunnels = new HashMap<String, Tunnel>();
    }

    public void update() {

    }

    public void draw(SpriteBatch spriteBatch) {
        //Grid
        for (Cell cell : grid.values()) {
            cell.draw(spriteBatch);
        }

        //Roads
        for (Road road : roads.values()) {
            road.draw(spriteBatch);
        }

        //Tunnels
        for (Tunnel tunnel : tunnels.values()) {
            tunnel.draw(spriteBatch);
        }
    }

    public void dispose() {

    }

    public void resize(int width, int height) {

    }

    public void addRoad(Road road) {
        roads.put(road.getId(), road);
        for (Vector2 v : road.getCellIndices()) {
            grid.get(v).setRoad(road);
        }
    }

    public void addTunnel(Tunnel tunnel) {
        tunnels.put(tunnel.getId(), tunnel);
        grid.get(tunnel.getCell().getIndex()).setSimObject(tunnel);
    }

    public Vector2 getIndexAtPositionSafe(Vector3 pos) {
        Vector2 index = new Vector2((int)(pos.x/gridCellSize), (int)(pos.y/gridCellSize));
        System.out.println(index);
        if (grid.containsKey(index)) {
            return index;
        }
        return new Vector2(1, 1);
    }

    public Vector2 getIndexAtPosition(Vector3 pos) {
        return new Vector2((int)(pos.x/gridCellSize), (int)(pos.y/gridCellSize));
    }

    public Cell getCell(Vector2 index) {
        return grid.get(index);
    }

    public Cell getCellAtPosition(Vector2 pos) {
        return grid.get(getIndexAtPosition(new Vector3(pos, 0)));
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
        return null;
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
