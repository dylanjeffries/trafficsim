package com.trafficsim.simobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.trafficsim.*;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.SimObjectType;

import java.util.Arrays;

public class Car extends SimObject {

    // Position
    private Vector2 position;
    private Vector2 frontPosition;
    private Vector2 backPosition;

    // Dimensions
    private float width;
    private float length;

    // Attributes
    private float maxSpeed;
    private float acceleratingRate;
    private float brakingRate;

    // Physics
    private Vector2 delta;
    private float acceleration;
    private float speed;
    private Direction direction;
    private Polygon collisionBox;
    private Polygon forwardAwarenessBox;

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

    private int counter;

    public Car(String id, Vector2 position, Direction direction, Route route, Environment environment, Textures textures) {
        super(id, SimObjectType.CAR, 0);
        this.position = position.cpy();
        this.environment = environment;

        width = Config.getInteger("car_width");
        length = Config.getInteger("car_length");

        maxSpeed = 2.241f; // 30 mph
        acceleratingRate = 0.0092f;
        brakingRate = -0.0112f;

        delta = new Vector2(0, 0);
        speed = 0;
        acceleration = 0;
        this.direction = direction;
        collisionBox = new Polygon(calculateCollisionVertices());
        collisionBox.setOrigin(position.x, position.y);
        collisionBox.setRotation(Calculator.directionToDegrees(direction));
        forwardAwarenessBox = new Polygon(calculateForwardAwarenessVertices());
        forwardAwarenessBox.setOrigin(position.x, position.y);
        forwardAwarenessBox.setRotation(Calculator.directionToDegrees(direction));

        calculateFrontAndBackPositions();
        currentCell = environment.getCellAtPosition(frontPosition);
        this.route = route;

        turnRequired = false;
        turnDirection = direction;
        turnAnchor = 0;

        texture = textures.get("car_pink");
        marker = textures.get("marker");

        counter = 0;
    }

    public void update() {
        // acceleration = 33.5f / 600f; // 60 Iterations per second
        // velocity += acceleration;
        // Reset delta
        delta.set(0, 0);

        counter++;

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

        if (environment.isCarInArea(id, forwardAwarenessBox)) {
            acceleration = Calculator.capFloat(brakingRate, -1*speed, brakingRate);;
        } else if (speed < maxSpeed) {
            acceleration = Calculator.capFloat(acceleratingRate, 0, maxSpeed - speed);
        } else {
            acceleration = 0;
        }
        speed += acceleration;
        delta.x += speed * (float)Math.sin(Calculator.directionToRadians(direction));
        delta.y -= speed * (float)Math.cos(Calculator.directionToRadians(direction));
        
        //Update Position
        position.add(delta);
        collisionBox.translate(delta.x, delta.y);
        calculateFrontAndBackPositions();

        // Update Forward Awareness Box
        forwardAwarenessBox.translate(delta.x, delta.y);
        forwardAwarenessBox.setVertices(updateForwardAwareness());
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x - (width / 2f), position.y - (length / 2f), width / 2f, length / 2f, width, length, 1, 1, Calculator.directionToDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);

//        spriteBatch.draw(marker, forwardAwarenessBox.getTransformedVertices()[0], forwardAwarenessBox.getTransformedVertices()[1], 8, 8);
//        spriteBatch.draw(marker, forwardAwarenessBox.getTransformedVertices()[2], forwardAwarenessBox.getTransformedVertices()[3], 4, 4);
//        spriteBatch.draw(marker, forwardAwarenessBox.getTransformedVertices()[4], forwardAwarenessBox.getTransformedVertices()[5], 4, 4);
//        spriteBatch.draw(marker, forwardAwarenessBox.getTransformedVertices()[6], forwardAwarenessBox.getTransformedVertices()[7], 4, 4);
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
                    direction = turnDirection;
                    turnRequired = false;
                }
                break;
            case SOUTH:
                if (turnAnchor - position.y >= 0) {
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

        // Rotate collision and forward awareness boxes
        collisionBox.setRotation(Calculator.directionToDegrees(direction));
        forwardAwarenessBox.setRotation(Calculator.directionToDegrees(direction));
    }

    private float[] calculateCollisionVertices() {
        float[] vertices = new float[8];
        // Initially assumes car is facing South
        // Bottom Left
        vertices[0] = position.x - (width / 2f);
        vertices[1] = position.y - (length / 2f);
        // Top Left
        vertices[2] = position.x - (width / 2f);
        vertices[3] = position.y + (length / 2f);
        // Top Right
        vertices[4] = position.x + (width / 2f);
        vertices[5] = position.y + (length / 2f);
        // Bottom Right
        vertices[6] = position.x + (width / 2f);
        vertices[7] = position.y - (length / 2f);

        return vertices;
    }

    private float[] calculateForwardAwarenessVertices() {
        float[] vertices = new float[8];
        // Initially assumes car is facing South
        // Bottom Left
        vertices[0] = position.x - (width / 2f);
        vertices[1] = position.y - (length / 2f) - 2;
        // Top Left
        vertices[2] = position.x - (width / 2f);
        vertices[3] = position.y - (length / 2f) - 1;
        // Top Right
        vertices[4] = position.x + (width / 2f);
        vertices[5] = position.y - (length / 2f) - 1;
        // Bottom Right
        vertices[6] = position.x + (width / 2f);
        vertices[7] = position.y - (length / 2f) - 2;

        return vertices;
    }

    private float[] updateForwardAwareness() {
        float[] vertices = forwardAwarenessBox.getVertices();
        // Braking Distance Calculation
        float brakingDistance = (speed * (speed / brakingRate)) / 2f;
        // New original y including forward awareness
        // Space for braking to zero and half a car length
        float newY = vertices[3] - Math.abs(brakingDistance) - (length / 2f);
        // Two second rule
        // float newY = vertices[3] - (speed * 120);
        // Bottom Left
        vertices[1] = newY;
        // Bottom Right
        vertices[7] = newY;

        return vertices;
    }

    private void calculateFrontAndBackPositions() {
        float radiusSine = (length / 2f) * (float)Math.sin(Calculator.directionToRadians(direction));
        float radiusCosine = (length / 2f) * (float)Math.cos(Calculator.directionToRadians(direction));
        frontPosition = new Vector2(position.x + radiusSine, position.y - radiusCosine);
        backPosition = new Vector2(position.x - radiusSine, position.y + radiusCosine);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public Polygon getCollisionBox() {
        return collisionBox;
    }
}
