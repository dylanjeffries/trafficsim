package com.trafficsim.trafficlights;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.trafficsim.enums.Direction;

import java.util.ArrayList;

public class TrafficLightManager {

    // On/Off
    private boolean enabled;

    // Stages
    private int pointer;
    private ArrayList<TrafficLightStage> stages;

    // Timer
    private float timer;
    private float allRedDuration;

    public TrafficLightManager() {
        enabled = false;

        pointer = 0;
        stages = new ArrayList<>();

        timer = 0;
        allRedDuration = 1;
    }

    public void update() {
        if (enabled) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer >= stages.get(pointer).getDuration()) {
                progressPointer();
                timer = 0;
            }
        }
    }

    public void compile(boolean enabled, float stageOneDuration, float stageTwoDuration) {
        this.enabled = enabled;
        pointer = 0;
        stages.clear();
        timer = 0;

        // Stage One
        stages.add(new TrafficLightStage(stageOneDuration, true, false, true, false));
        // All Red Stage
        stages.add(new TrafficLightStage(allRedDuration, false, false, false, false));
        // Stage Two
        stages.add(new TrafficLightStage(stageTwoDuration, false, true, false, true));
        // All Red Stage
        stages.add(new TrafficLightStage(allRedDuration, false, false, false, false));
    }

    private void progressPointer() {
        if (pointer == stages.size() - 1) {
            pointer = 0;
        } else {
            pointer++;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean getState(Direction direction) {
        if (enabled) { return stages.get(pointer).getState(direction); }
        return true;
    }
}
