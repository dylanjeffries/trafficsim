package com.trafficsim.builders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.Environment;
import com.trafficsim.simobjects.Road;
import com.trafficsim.Textures;
import com.trafficsim.enums.SimObjectType;

public class RoadBuilder {

    // Environment
    private Environment environment;

    // Graphics
    private Textures textures;

    // Building
    private int counter;
    private Road newRoad;
    private boolean newRoadReady;
    private boolean valid;
    private boolean validProximity;
    private boolean validInline;

    public RoadBuilder(Environment environment, Textures textures) {
        this.environment = environment;
        this.textures = textures;

        counter = 1;
        newRoadReady = false;
        valid = false;
        validProximity = false;
        validInline = false;
    }

    public void update() {
        valid = validProximity && validInline;
    }

    public void draw(SpriteBatch spriteBatch, Vector2 buildValidPos) {
        if (valid) {
            spriteBatch.draw(textures.get("build_valid"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        } else {
            spriteBatch.draw(textures.get("build_invalid"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        }

        if (newRoad != null) {
            newRoad.draw(spriteBatch);
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (valid) {
            if (newRoad == null) { //Starting a new road
                newRoad = new Road('R' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        environment.getCell(cursorIndex),
                        textures.get("road_tp"));
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
            validInline = true;
            validProximity = !roadInProximity(cursorIndex);
        } else { //Building a new road
            validInline = newRoad.isIndexInline(cursorIndex);
            if (validInline) {
                newRoad.setEndCell(environment.getCell(cursorIndex));
            } else {
                newRoad.setEndCell(newRoad.getStartCell());
            }
            newRoad.recalculate();

            validProximity = true;
            for (Vector2 index : newRoad.getCellIndices()) {
                if (roadInProximity(index)) {
                    validProximity = false;
                }
            }
        }
    }

    private boolean roadInProximity(Vector2 index) {
        return environment.getCellSimObjectType(index) == SimObjectType.ROAD ||
                environment.getCellSimObjectType(index.cpy().add(0, 1)) == SimObjectType.ROAD ||
                environment.getCellSimObjectType(index.cpy().add(1, 0)) == SimObjectType.ROAD ||
                environment.getCellSimObjectType(index.cpy().add(0, -1)) == SimObjectType.ROAD ||
                environment.getCellSimObjectType(index.cpy().add(-1, 0)) == SimObjectType.ROAD;
    }

    public boolean isNewRoadReady() {
        return newRoadReady;
    }

    public Road getNewRoad() {
        Road newRoadCopy = new Road(newRoad); //Copy the new road
        newRoad = null;
        newRoadReady = false;
        counter++;
        newRoadCopy.setTexture(textures.get("road")); //Set road copy texture to full
        return newRoadCopy;
    }
}
