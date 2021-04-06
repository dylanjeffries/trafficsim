package com.trafficsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.trafficsim.enums.Direction;
import com.trafficsim.simobjects.SimObject;

public class Sidebar {

    // Position
    private Vector2 topLeft;

    // Selection
    private SimObject simObject;
    private Cell selectedCell;
    private float timer;
    private float animationInterval;
    private boolean animationState;

    // Texture and Fonts
    private boolean visible;
    private Textures textures;

    // Stage and Widgets
    private Stage stage;
    private Table table;

    public Sidebar(Textures textures) {
        topLeft = new Vector2(Config.getInteger("v_width") - (Config.getInteger("sidebar_width") / 2f),
                Config.getInteger("v_height") - Config.getInteger("toolbar_height"));

        simObject = null;
        selectedCell = null;
        timer = 0;
        animationInterval = 0.3f;
        animationState = false;

        visible = false;
        this.textures = textures;

        stage = new Stage();
        table = new Table();

        stage.addActor(table);
    }

    public void update() {
        stage.act();

        // Selected Cell Animation
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= animationInterval) {
            animationState = !animationState;
            timer = 0;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (visible) {
            // Sidebar
            spriteBatch.draw(textures.get("sidebar"),
                    Config.getInteger("v_width") - Config.getInteger("sidebar_width"),
                    0,
                    Config.getInteger("sidebar_width"),
                    Config.getInteger("v_height"));

            // Stage
            stage.getRoot().draw(spriteBatch, 1);
        }
    }

    public void setSimObject(SimObject simObject) {
        this.simObject = simObject;

        if (simObject != null) {
            table = simObject.getSidebarTable();
            table.setPosition(topLeft.x, topLeft.y - 30);
            stage.clear();
            stage.addActor(table);
        } else {
            table.clear();
        }
    }

    public Cell getSelectedCell() {
        return selectedCell;
    }

    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    public boolean getAnimationState() {
        return animationState;
    }

    private void enableTouch() {
        InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        if (!inputMultiplexer.getProcessors().contains(stage, true)) {
            inputMultiplexer.addProcessor(0, stage);
        }
    }

    private void disableTouch() {
        InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        if (inputMultiplexer.getProcessors().contains(stage, true)) {
            inputMultiplexer.removeProcessor(stage);
        }
    }

    public void show() {
        visible = true;
        enableTouch();
    }

    public void hide() {
        visible = false;
        disableTouch();
    }
}
