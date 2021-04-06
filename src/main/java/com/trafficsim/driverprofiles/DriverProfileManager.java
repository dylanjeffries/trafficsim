package com.trafficsim.driverprofiles;

import com.trafficsim.Textures;

import java.util.Random;

public class DriverProfileManager {

    // Profiles
    private DriverProfile slowProfile;
    private DriverProfile normalProfile;
    private DriverProfile fastProfile;

    // Percentages
    private int slowPct;
    private int normalPct;
    private int fastPct;

    // Random
    private Random random;

    public DriverProfileManager(Textures textures) {
        // Slow: maxSpeed = 20 mph, safeSpeed = 10 mph, acceleratingRate = 2.68 m/s^2, brakingRate = -4 m/s^2
        slowProfile = new DriverProfile(textures.get("car_blue"), 1.494f, 0.747f, 0.0075f, -0.0112f);
        // Normal: maxSpeed = 30 mph, safeSpeed = 10 mph, acceleratingRate = 3.35 m/s^2, brakingRate = -4 m/s^2
        normalProfile = new DriverProfile(textures.get("car_pink"), 2.241f, 0.747f, 0.0092f, -0.0112f);
        // Fast: maxSpeed = 60 mph, safeSpeed = 10 mph, acceleratingRate = 5.36 m/s^2, brakingRate = -4 m/s^2
        fastProfile = new DriverProfile(textures.get("car_green"), 4.482f, 0.747f, 0.015f, -0.0112f);

        slowPct = 0;
        normalPct = 100;
        fastPct = 0;

        random = new Random();
    }

    public void setPercentages(int slowPct, int normalPct, int fastPct) {
        this.slowPct = slowPct;
        this.normalPct = normalPct;
        this.fastPct = fastPct;
    }

    public DriverProfile getRandomProfile() {
        int randomInt = random.nextInt(101);
        // Slow
        if (randomInt <= slowPct) { return slowProfile; }
        // Normal
        if (randomInt <= slowPct + normalPct) { return normalProfile; }
        // Fast
        return fastProfile;
    }
}
