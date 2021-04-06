package com.trafficsim.driverprofiles;

import com.badlogic.gdx.graphics.Texture;

public class DriverProfile {

    // Texture
    private Texture texture;

    // Attributes
    private float maxSpeed;
    private float safeSpeed;
    private float acceleratingRate;
    private float brakingRate;

    public DriverProfile(Texture texture, float maxSpeed, float safeSpeed, float acceleratingRate, float brakingRate) {
        this.texture = texture;

        this.maxSpeed = maxSpeed;
        this.safeSpeed = safeSpeed;
        this.acceleratingRate = acceleratingRate;
        this.brakingRate = brakingRate;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getSafeSpeed() {
        return safeSpeed;
    }

    public float getAcceleratingRate() {
        return acceleratingRate;
    }

    public float getBrakingRate() {
        return brakingRate;
    }
}
