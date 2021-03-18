package com.trafficsim.simobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.*;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;

public class Car extends SimObject {

    // Position
    private Vector2 position;
    private Vector2 frontPosition;
    private Vector2 backPosition;

    // Dimensions
    private float width;
    private float length;

    // Physics
    private Vector2 delta;
    private float velocity;
    private float acceleration;
    private Direction direction;

    // Environment
    private Environment environment;
    private Cell currentCell;
    private Cell forwardCell;
    private Route route;

    // Drawing
    private Texture texture;
    private Texture marker;

    // Other
    private boolean reachedDestination;
    private float travelled;


    public Car(String id, Vector2 position, Direction direction, Route route, Environment environment, Textures textures) {
        super(id, SimObjectType.CAR, 0);
        this.position = position.cpy();
        this.environment = environment;
        delta = new Vector2(0, 0);

        width = Config.getInteger("car_width");
        length = Config.getInteger("car_length");

        velocity = 0;
        acceleration = 0;
        this.direction = direction;

        calculateFrontAndBackPositions();
        this.route = route;

        texture = textures.get("car_pink");
        marker = textures.get("marker");
    }

    public void update() {
        // acceleration = 33.5f / 600f; // 60 Iterations per second
        // velocity += acceleration;
        // Reset delta
        delta.set(0, 0);

        // Current and Forward com.trafficsim.Cell
        currentCell = environment.getCellAtPosition(frontPosition);
        forwardCell = environment.getCell(calculateForwardIndex(environment.getIndexAtPosition(frontPosition)));

        // Current com.trafficsim.Cell Actions
        switch (currentCell.getSimObjectType()) {
            case ROAD:
                roadActions();
                break;

            case TUNNEL:
                tunnelActions();
                break;
        }

        // Check for turn
        if (travelled >= 300) {
            //turnClockwise();
            travelled = 0;
        }

        velocity = 2f;
        delta.x += velocity * (float)Math.sin(Calculator.directionToRadians(direction));
        delta.y -= velocity * (float)Math.cos(Calculator.directionToRadians(direction));

        //Update Position
        position.add(delta);
        calculateFrontAndBackPositions();

        //Update travelled
        travelled += velocity;
    }

    private void roadActions() {
        // Get com.trafficsim.simobjects.Road from Current com.trafficsim.Cell
        Road road = (Road) currentCell.getSimObject();

        // Anchor
        float anchor = road.getAnchor(direction);
        if (direction == Direction.EAST || direction == Direction.WEST) {
            float diff = anchor - position.y;
            if (diff != 0) {
                delta.y += Calculator.capFloat(diff, -1, 1);
            }
        } else { // North and South
            float diff = anchor - position.x;
            if (diff != 0) {
                delta.x += Calculator.capFloat(diff, -1, 1);
            }
        }
    }

    private void tunnelActions() {
        // Get com.trafficsim.simobjects.Tunnel from Current com.trafficsim.Cell
        Tunnel tunnel = (Tunnel) currentCell.getSimObject();

        // Despawn com.trafficsim.simobjects.Car?
        // If the tunnel is the ending node for this car's route
        if (route.isEndTunnel(tunnel.getId())) {
            // If the back of the car is also in the tunnel
            if (environment.getCellAtPosition(backPosition).getSimObjectType() == SimObjectType.TUNNEL) {
                tunnel.setCarToDespawn(id);
            }
        }
    }

    private void calculateFrontAndBackPositions() {
        float radiusSine = (length / 2f) * (float)Math.sin(Calculator.directionToRadians(direction));
        float radiusCosine = (length / 2f) * (float)Math.cos(Calculator.directionToRadians(direction));
        frontPosition = new Vector2(position.x + radiusSine, position.y - radiusCosine);
        backPosition = new Vector2(position.x - radiusSine, position.y + radiusCosine);
    }

    private Vector2 calculateForwardIndex(Vector2 index) {
        switch (direction) {
            case NORTH:
                return index.cpy().add(0, 1);
            case EAST:
                return index.cpy().add(1, 0);
            case WEST:
                return index.cpy().add(-1, 0);
            default: // South
                return index.cpy().add(0, -1);
        }
    }

    private void turnClockwise() {
        switch (direction) {
            case NORTH:
                direction = Direction.EAST;
                break;
            case EAST:
                direction = Direction.SOUTH;
                break;
            case SOUTH:
                direction = Direction.WEST;
                break;
            case WEST:
                direction = Direction.NORTH;
                break;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x - (width / 2f), position.y - (length / 2f), width / 2f, length / 2f, width, length, 1, 1, Calculator.directionToDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
//        spriteBatch.draw(marker, position.x, position.y, 4, 4);
//        spriteBatch.draw(marker, frontPosition.x, frontPosition.y, 4, 4);
//        spriteBatch.draw(marker, backPosition.x, backPosition.y, 8, 8);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public boolean hasReachedDestination() {
        return reachedDestination;
    }
}
