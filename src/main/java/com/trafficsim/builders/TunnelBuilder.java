package com.trafficsim.builders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.Calculator;
import com.trafficsim.Environment;
import com.trafficsim.Textures;
import com.trafficsim.simobjects.Tunnel;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;

public class TunnelBuilder {

    // Environment
    private Environment environment;

    // Graphics
    private Textures textures;

    // Building
    private int counter;
    private Direction direction;
    private Tunnel newTunnel;
    private boolean valid;

    public TunnelBuilder(Environment environment, Textures textures) {
        this.environment = environment;
        this.textures = textures;

        counter = 1;
        direction = Direction.SOUTH;
        valid = false;
    }

    public void update() {
        // Rotate keyboard input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            direction = rotateDirection();
        }
    }

    public void draw(SpriteBatch spriteBatch, Vector2 buildValidPos) {
        // Draw transparent tunnel
        spriteBatch.draw(textures.get("tunnel_tp"), buildValidPos.x, buildValidPos.y,
                environment.getGridCellSize()/2f, environment.getGridCellSize()/2f,
                environment.getGridCellSize(), environment.getGridCellSize(), 1, 1,
                Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel_tp").getWidth(), textures.get("tunnel_tp").getHeight(), false, false);

        // Draw valid or invalid build square
        if (valid) {
            spriteBatch.draw(textures.get("build_valid"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        } else {
            spriteBatch.draw(textures.get("build_invalid"), buildValidPos.x, buildValidPos.y,
                    environment.getGridCellSize(), environment.getGridCellSize());
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (valid) {
            newTunnel = new Tunnel('T' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        direction,
                        environment,
                        textures);
        }
        return true;
    }

    public void mouseMoved(Vector2 cursorIndex) {
        valid = environment.getCellSimObjectType(cursorIndex) == SimObjectType.NONE;
    }

    private Direction rotateDirection() {
        switch (direction) {
            case NORTH:
                return Direction.EAST;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default: // East
                return  Direction.SOUTH;
        }
    }

    public boolean isNewTunnelReady() {
        return newTunnel != null;
    }

    public Tunnel getNewTunnel() {
        Tunnel newTunnelCopy = new Tunnel(newTunnel); //Copy the new tunnel
        newTunnel = null;
        counter++;
        return newTunnelCopy;
    }
}
