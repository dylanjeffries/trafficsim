package com.trafficsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.trafficsim.enums.BuildingMode;
import com.trafficsim.enums.SimulationMode;

public class Toolbar {

    private Vector2 pos;
    private float height;
    private Texture texture;

    private float buttonSize;
    private float buttonY;

    // Building Mode
    private BuildingMode buildingMode;
    private Button selectButton;
    private Button roadButton;
    private Button tunnelButton;
    private Button intersectionButton;
    private Button bulldozeButton;

    // Simulation Mode
    private SimulationMode simulationMode;
    private SimulationMode previousSimulationMode;
    private Button pauseButton;
    private Button playButton;
    private Button stopButton;

    public Toolbar(float x, float y, Textures textures) {
        pos = new Vector2(x, y);
        height = Config.getInteger("toolbar_height");
        texture = textures.get("toolbar");

        buttonSize = height * 0.8f;
        buttonY = pos.y + (height * 0.1f);

        // Building Buttons and Mode
        selectButton = new Button(100, buttonY, buttonSize, buttonSize, false,
                textures.get("select_inactive"), textures.get("select_active"), textures.get("select_hover"), textures.get("select_disabled"));

        roadButton = new Button(100 + height, buttonY, buttonSize, buttonSize, false,
                textures.get("road_inactive"), textures.get("road_active"), textures.get("road_hover"), textures.get("road_disabled"));

        tunnelButton = new Button(100 + (height * 2), buttonY, buttonSize, buttonSize, false,
                textures.get("tunnel_inactive"), textures.get("tunnel_active"), textures.get("tunnel_hover"), textures.get("tunnel_disabled"));

        intersectionButton = new Button(100 + (height * 3), buttonY, buttonSize, buttonSize, false,
                textures.get("intersection_inactive"), textures.get("intersection_active"), textures.get("intersection_hover"), textures.get("intersection_disabled"));

        bulldozeButton = new Button(100 + (height * 4), buttonY, buttonSize, buttonSize, false,
                textures.get("bulldoze_inactive"), textures.get("bulldoze_active"), textures.get("bulldoze_hover"), textures.get("bulldoze_disabled"));

        buildingMode = BuildingMode.SELECT;
        selectButton.activate();

        // Simulation Buttons and Mode
        float centerX = (Gdx.graphics.getWidth() / 2f) - (buttonSize / 2);
        pauseButton = new Button(centerX - height, buttonY, buttonSize, buttonSize, false,
                textures.get("pause_inactive"), textures.get("pause_active"), textures.get("pause_hover"), textures.get("pause_disabled"));

        playButton = new Button(centerX, buttonY, buttonSize, buttonSize, false,
                textures.get("play_inactive"), textures.get("play_active"), textures.get("play_hover"), textures.get("play_disabled"));

        stopButton = new Button(centerX + height, buttonY, buttonSize, buttonSize, false,
                textures.get("stop_inactive"), textures.get("stop_active"), textures.get("stop_hover"), textures.get("stop_disabled"));

        simulationMode = SimulationMode.STOPPED;
        previousSimulationMode = SimulationMode.STOPPED;
        pauseButton.disable();
        stopButton.activate();
    }

    public void update() {
        // Set Previous Simulation Mode
        previousSimulationMode = simulationMode;

        // Building Buttons Logic
        if (selectButton.isClicked()) {
            buildingButtonClicked(selectButton, BuildingMode.SELECT);
        } else if (roadButton.isClicked()) {
            buildingButtonClicked(roadButton, BuildingMode.ROAD);
        } else if (tunnelButton.isClicked()) {
            buildingButtonClicked(tunnelButton, BuildingMode.TUNNEL);
        }  else if (intersectionButton.isClicked()) {
            buildingButtonClicked(intersectionButton, BuildingMode.INTERSECTION);
        } else if (bulldozeButton.isClicked()) {
            buildingButtonClicked(bulldozeButton, BuildingMode.BULLDOZE);
        }

        // Simulation Buttons Logic
        if (pauseButton.isClicked()) {
            simulationButtonClicked(pauseButton, SimulationMode.PAUSED);
        } else if (playButton.isClicked()) {
            simulationButtonClicked(playButton, SimulationMode.RUNNING);
            pauseButton.enable();
        } else if (stopButton.isClicked()) {
            simulationButtonClicked(stopButton, SimulationMode.STOPPED);
            pauseButton.disable();
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        // Toolbar
        spriteBatch.draw(texture, pos.x, pos.y, Config.getInteger("v_width"), height);

        // Building Buttons
        selectButton.draw(spriteBatch);
        roadButton.draw(spriteBatch);
        tunnelButton.draw(spriteBatch);
        intersectionButton.draw(spriteBatch);
        bulldozeButton.draw(spriteBatch);

        // Simulation Buttons
        pauseButton.draw(spriteBatch);
        playButton.draw(spriteBatch);
        stopButton.draw(spriteBatch);
    }

    private void buildingButtonClicked(Button button, BuildingMode newBuildingMode) {
        // Switch Building Mode
        buildingMode = newBuildingMode;
        // Deactivate all building buttons
        selectButton.deactivate();
        roadButton.deactivate();
        tunnelButton.deactivate();
        intersectionButton.deactivate();
        bulldozeButton.deactivate();
        // Activate clicked button
        button.activate();
    }

    private void simulationButtonClicked(Button button, SimulationMode newSimulationMode) {
        // Switch Simulation Mode
        simulationMode = newSimulationMode;
        // Deactivate all simulation buttons
        pauseButton.deactivate();
        playButton.deactivate();
        stopButton.deactivate();
        // Activate clicked button
        button.activate();
    }

    public void mouseMoved(Vector2 cursorPos) {
        // Building Buttons
        selectButton.mouseMoved(cursorPos);
        roadButton.mouseMoved(cursorPos);
        tunnelButton.mouseMoved(cursorPos);
        intersectionButton.mouseMoved(cursorPos);
        bulldozeButton.mouseMoved(cursorPos);

        // Simulation Buttons
        pauseButton.mouseMoved(cursorPos);
        playButton.mouseMoved(cursorPos);
        stopButton.mouseMoved(cursorPos);
    }

    public boolean leftClick() {
        return selectButton.leftClick() ||
                roadButton.leftClick() ||
                tunnelButton.leftClick() ||
                intersectionButton.leftClick() ||
                bulldozeButton.leftClick() ||
                pauseButton.leftClick() ||
                playButton.leftClick() ||
                stopButton.leftClick();
    }

    public BuildingMode getBuildingMode() {
        return buildingMode;
    }
    public SimulationMode getSimulationMode() {
        return simulationMode;
    }
    public SimulationMode getPreviousSimulationMode() {
        return previousSimulationMode;
    }
}
