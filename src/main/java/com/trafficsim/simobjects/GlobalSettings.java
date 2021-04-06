package com.trafficsim.simobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.trafficsim.*;
import com.trafficsim.enums.Direction;
import com.trafficsim.enums.Orientation;
import com.trafficsim.enums.SimObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalSettings extends SimObject {

    // Slow Drivers
    private int slowPct;
    private Label slowLabel;
    private Slider slowSlider;

    // Normal Drivers
    private int normalPct;
    private Label normalLabel;
    private Slider normalSlider;

    // Fast Drivers
    private int fastPct;
    private Label fastLabel;
    private Slider fastSlider;

    public GlobalSettings() {
        super("Global", SimObjectType.NONE, 0);

        // Slow Drivers
        slowLabel = new Label("Slow Drivers: - %", UIStyling.BODY_LABEL_STYLE);
        slowSlider = new Slider(0, 100, 1f, false, UIStyling.SLOW_SLIDER_STYLE);
        slowSlider.setValue(50);
        slowSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (slowSlider.isDragging()) {
                    calculatePercentages();
                }
            }
        });

        // Normal Drivers
        normalLabel = new Label("Normal Drivers: - %", UIStyling.BODY_LABEL_STYLE);
        normalSlider = new Slider(0, 100, 1f, false, UIStyling.NORMAL_SLIDER_STYLE);
        normalSlider.setValue(50);
        normalSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (normalSlider.isDragging()) {
                    calculatePercentages();
                }
            }
        });

        // Fast Drivers
        fastLabel = new Label("Fast Drivers: - %", UIStyling.BODY_LABEL_STYLE);
        fastSlider = new Slider(0, 100, 1f, false, UIStyling.FAST_SLIDER_STYLE);
        fastSlider.setValue(50);
        fastSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (fastSlider.isDragging()) {
                    calculatePercentages();
                }
            }
        });

        calculatePercentages();
    }

    @Override
    public Table getSidebarTable() {
        Table table = new Table();

        Label nameLabel = new Label("Global Settings", UIStyling.TITLE_LABEL_STYLE);
        table.add(nameLabel).colspan(2).padTop(350).spaceBottom(40);
        table.row();

        table.add(slowLabel).spaceBottom(10);
        table.row();
        table.add(slowSlider).spaceBottom(40).spaceLeft(10);
        table.row();

        table.add(normalLabel).spaceBottom(10);
        table.row();
        table.add(normalSlider).spaceBottom(40).spaceLeft(10);
        table.row();

        table.add(fastLabel).spaceBottom(10);
        table.row();
        table.add(fastSlider).spaceBottom(40).spaceLeft(10);
        table.row();

        return table;
    }

    private void calculatePercentages() {
        float total = (slowSlider.getPercent() + normalSlider.getPercent() + fastSlider.getPercent()) / 100f;

        // All zeros safe case
        if (total == 0) {
            slowPct = 0;
            fastPct = 0;
            normalPct = 100;
        } else {
            slowPct = (int)(slowSlider.getPercent() / total);
            fastPct = (int)(fastSlider.getPercent() / total);
            normalPct = 100 - slowPct - fastPct;
        }

        slowLabel.setText("Slow Drivers: " + String.valueOf(slowPct) + "%");
        normalLabel.setText("Normal Drivers: " + String.valueOf(normalPct) + "%");
        fastLabel.setText("Fast Drivers: " + String.valueOf(fastPct) + "%");
    }

    public int getSlowPct() {
        return slowPct;
    }

    public int getNormalPct() {
        return normalPct;
    }

    public int getFastPct() {
        return fastPct;
    }
}
