package com.trafficsim.builders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.Environment;
import com.trafficsim.Textures;
import com.trafficsim.enums.SimObjectType;
import com.trafficsim.simobjects.Intersection;

public class IntersectionBuilder {

    // Environment
    private Environment environment;

    // Graphics
    private Textures textures;

    // Building
    private int counter;
    private Intersection newIntersection;
    private boolean valid;

    public IntersectionBuilder(Environment environment, Textures textures) {
        this.environment = environment;
        this.textures = textures;

        counter = 1;
        valid = false;
    }

    public void update() {}

    public void draw(SpriteBatch spriteBatch, Vector2 buildValidPos) {
        // Draw transparent intersection
        spriteBatch.draw(textures.get("intersection_tp"), buildValidPos.x, buildValidPos.y,
                environment.getGridCellSize(), environment.getGridCellSize());

        // Draw valid or invalid build square
        if (valid) {
            spriteBatch.draw(textures.get("green_highlight"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        } else {
            spriteBatch.draw(textures.get("red_highlight"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (valid) {
            newIntersection = new Intersection('I' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        environment,
                        textures);
        }
        return true;
    }

    public void mouseMoved(Vector2 cursorIndex) {
        valid = environment.getCellSimObjectType(cursorIndex) == SimObjectType.NONE;
    }

    public boolean isNewIntersectionReady() {
        return newIntersection != null;
    }

    public Intersection getNewIntersection() {
        Intersection newIntersectionCopy = new Intersection(newIntersection); //Copy the new intersection
        newIntersection = null;
        counter++;
        return newIntersectionCopy;
    }
}
