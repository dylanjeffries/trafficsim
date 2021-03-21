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
    private SimObjectType previousCellSimObjectType;
    private Route route;

    // Turning
    private boolean turnRequired;
    private Direction turnDirection;
    private float turnAnchor;

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

        width = Config.getInteger("car_width");
        length = Config.getInteger("car_length");

        delta = new Vector2(0, 0);
        velocity = 0;
        acceleration = 0;
        this.direction = direction;

        calculateFrontAndBackPositions();
        currentCell = environment.getCellAtPosition(frontPosition);
        this.route = route;

        turnRequired = false;
        turnDirection = direction;
        turnAnchor = 0;

        texture = textures.get("car_pink");
        marker = textures.get("marker");
    }

    public void update() {
        // acceleration = 33.5f / 600f; // 60 Iterations per second
        // velocity += acceleration;
        // Reset delta
        delta.set(0, 0);

        // Previous Cell Type, Current Cell and Forward Cell
        previousCellSimObjectType = currentCell.getSimObjectType();
        currentCell = environment.getCellAtPosition(frontPosition);
        forwardCell = environment.getCell(Calculator.getIndexInDirection(environment.getIndexAtPosition(frontPosition), direction));

        // Current Cell Actions
        switch (currentCell.getSimObjectType()) {
            case ROAD:
                roadActions();
                break;
            case TUNNEL:
                tunnelActions();
                break;
            case INTERSECTION:
                intersectionActions();
                break;
        }

        // Executing a queued turn
        if (turnRequired) { executeTurn(); }

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
        // Get Road from Current Cell
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
        // Get Tunnel from Current Cell
        Tunnel tunnel = (Tunnel) currentCell.getSimObject();

        // Despawn Car?
        // If the tunnel is the ending node for this car's route
        if (route.isEndTunnel(tunnel.getId())) {
            // If the back of the car is also in the tunnel
            if (environment.getCellAtPosition(backPosition).getSimObjectType() == SimObjectType.TUNNEL) {
                tunnel.setCarToDespawn(id);
            }
        }
    }

    private void intersectionActions() {
        // Get Intersection from Current Cell
        Intersection intersection = (Intersection) currentCell.getSimObject();

        // If first update in this intersection
        if (currentCell.getSimObjectType() != previousCellSimObjectType) {
            turnDirection = intersection.getNextDirection(route.getIdAfter(intersection.getId()));
            turnRequired = direction != turnDirection;
            turnAnchor = intersection.getAnchor(turnDirection);
        }
    }

    private void executeTurn() {
        switch (direction) {
            case NORTH:
                if (turnAnchor - position.y <= 0) {
                    direction = turnDirection;
                    turnRequired = false;
                }
                break;
            case EAST:
                if (turnAnchor - position.x <= 0) {
                    System.out.println(id);
                    direction = turnDirection;
                    turnRequired = false;
                }
                break;
            case SOUTH:
                if (turnAnchor - position.y >= 0) {
                    System.out.println(id);
                    direction = turnDirection;
                    turnRequired = false;
                }
                break;
            case WEST:
                if (turnAnchor - position.x >= 0) {
                    direction = turnDirection;
                    turnRequired = false;
                }
                break;
        }
    }

    private void calculateFrontAndBackPositions() {
        float radiusSine = (length / 2f) * (float)Math.sin(Calculator.directionToRadians(direction));
        float radiusCosine = (length / 2f) * (float)Math.cos(Calculator.directionToRadians(direction));
        frontPosition = new Vector2(position.x + radiusSine, position.y - radiusCosine);
        backPosition = new Vector2(position.x - radiusSine, position.y + radiusCosine);
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
