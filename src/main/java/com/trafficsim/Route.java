package com.trafficsim;

import com.trafficsim.simobjects.SimObject;

import java.util.ArrayList;

public class Route {

    private ArrayList<String> ids;
    private int length;

    public Route(SimObject simObject) {
        ids = new ArrayList<String>();
        ids.add(simObject.getId());
        length = simObject.getCellLength();
    }

    public Route(Route other) {
        this.ids = new ArrayList<String>(other.ids);
        this.length = other.length;
    }

    public void addSimObject(SimObject simObject) {
        ids.add(simObject.getId());
        this.length += simObject.getCellLength();
    }

    public boolean contains(String id) {
        return ids.contains(id);
    }

    public boolean isEndTunnel(String id) {
        return ids.get(ids.size() - 1).equals(id);
    }

    public String toString() {
        return ids.toString() + " Length: " + length;
    }
}
