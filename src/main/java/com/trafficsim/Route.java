package com.trafficsim;

import com.trafficsim.enums.SimObjectType;
import com.trafficsim.simobjects.SimObject;

import java.util.ArrayList;

public class Route {

    // Route IDs and Length
    private ArrayList<String> ids;
    private int length;

    // Tracking
    private String currentId;
    private String nextId;

    public Route(SimObject simObject) {
        // Route IDs and Length
        ids = new ArrayList<String>();
        ids.add(simObject.getId());
        length = simObject.getCellLength();

        //Tracking
        currentId = "";
        nextId = "";
    }

    public Route(Route other) {
        // Route IDs and Length
        this.ids = new ArrayList<String>(other.ids);
        this.length = other.length;
    }

    public void addSimObject(SimObject simObject) {
        ids.add(simObject.getId());
        this.length += simObject.getCellLength();
    }

    public void startTracking() {
        //Tracking
        currentId = ids.get(0);
        nextId = ids.get(1);
    }

    public boolean contains(String id) {
        return ids.contains(id);
    }

    public boolean isEndTunnel(String id) {
        return ids.get(ids.size() - 1).equals(id);
    }

    public String getIdAfter(String id) {
        int index = ids.indexOf(id);
        if (index != ids.size() - 1) {
            return ids.get(index + 1);
        }
        return "";
    }

    public void updateTracking(String newCurrentId) {
        currentId = newCurrentId;
        nextId = getIdAfter(currentId);
    }

    public String getNextId() {
        return nextId;
    }

    public String toString() {
        return ids.toString() + " Length: " + length;
    }
}
