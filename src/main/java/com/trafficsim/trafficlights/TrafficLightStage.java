package com.trafficsim.trafficlights;

import com.trafficsim.enums.Direction;

import java.util.HashMap;

public class TrafficLightStage {

    private HashMap<Direction, Boolean> states;
    private float duration;

    public TrafficLightStage(float duration, boolean northState, boolean eastState, boolean southState, boolean westState) {
        this.duration = duration;

        states = new HashMap<>();
        states.put(Direction.NORTH, northState);
        states.put(Direction.EAST, eastState);
        states.put(Direction.SOUTH, southState);
        states.put(Direction.WEST, westState);
    }

    public boolean getState(Direction direction) {
        return states.getOrDefault(direction, false);
    }

    public float getDuration() {
        return duration;
    }
}
