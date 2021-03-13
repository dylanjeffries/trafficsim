import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RoadBuilder {

    //Graphics
    private Texture validTexture;
    private Texture invalidTexture;
    private Texture roadTexture;
    private Texture roadTPTexture;
    private float cellSize;

    //Environment
    private Environment environment;

    //Building
    private int counter;
    private Road newRoad;
    private boolean newRoadReady;
    private boolean buildValid;
    private boolean proxValid;
    private boolean inlineValid;

    public RoadBuilder(Textures textures, Environment environment) {
        validTexture = textures.get("build_valid");
        invalidTexture = textures.get("build_invalid");
        roadTexture = textures.get("road");
        roadTPTexture = textures.get("road_tp");
        cellSize = environment.getGridCellSize();

        this.environment = environment;

        counter = 1;
        newRoadReady = false;
        buildValid = false;
        proxValid = false;
        inlineValid = false;
    }

    public void update() {
        buildValid = proxValid && inlineValid;
    }

    public void draw(SpriteBatch spriteBatch, Vector2 buildValidPos) {
        if (buildValid) {
            spriteBatch.draw(validTexture, buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        } else {
            spriteBatch.draw(invalidTexture, buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        }

        if (newRoad != null) {
            newRoad.draw(spriteBatch);
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (buildValid) {
            if (newRoad == null) { //Starting a new road
                newRoad = new Road('R' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        environment.getCell(cursorIndex),
                        roadTPTexture);
            } else { //Finishing a new road
                newRoadReady = true;
            }
        }
        return true;
    }

    public boolean rightClick() {
        newRoad = null;
        return true;
    }

    public void mouseMoved(Vector2 cursorIndex) {
        if (newRoad == null) { //Looking to start a new road
            inlineValid = true;
            proxValid = !roadInProximity(cursorIndex);
        } else { //Building a new road
            inlineValid = newRoad.isIndexInline(cursorIndex);
            if (inlineValid) {
                newRoad.setEndCell(environment.getCell(cursorIndex));
            } else {
                newRoad.setEndCell(newRoad.getStartCell());
            }
            newRoad.recalculate();

            proxValid = true;
            for (Vector2 index : newRoad.getCellIndices()) {
                if (roadInProximity(index)) {
                    proxValid = false;
                }
            }
        }
    }

    public boolean isNewRoadReady() {
        return newRoadReady;
    }

    public Road getNewRoad() {
        Road newRoadCopy = new Road(newRoad); //Copy the new road
        newRoad = null;
        newRoadReady = false;
        counter++;
        newRoadCopy.setTexture(roadTexture); //Set road copy texture to full
        return newRoadCopy;
    }

    private boolean roadInProximity(Vector2 index) {
        return environment.cellHasRoad(index) ||
                environment.cellHasRoad(index.cpy().add(0, 1)) ||
                environment.cellHasRoad(index.cpy().add(1, 0)) ||
                environment.cellHasRoad(index.cpy().add(0, -1)) ||
                environment.cellHasRoad(index.cpy().add(-1, 0));
    }
}
