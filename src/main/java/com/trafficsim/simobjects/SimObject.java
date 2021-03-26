package com.trafficsim.simobjects;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.trafficsim.enums.SimObjectType;

import java.util.ArrayList;

public class SimObject {

    protected String id;
    protected int cellLength;
    protected SimObjectType simObjectType;
    protected ArrayList<String> connections;

    public SimObject(String id, SimObjectType simObjectType, int cellLength) {
        this.id = id;
        this.simObjectType = simObjectType;
        this.cellLength = cellLength;
        connections = new ArrayList<String>();
    }

    public Table getSidebarTable() {
        return new Table();
    }

    public String getId() {
        return id;
    }

    public int getCellLength() {
        return cellLength;
    }

    public void setCellLength(int cellLength) {
        this.cellLength = cellLength;
    }

    public SimObjectType getSimObjectType() {
        return simObjectType;
    }

    public ArrayList<String> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<String> connections) {
        this.connections = connections;
    }
}
