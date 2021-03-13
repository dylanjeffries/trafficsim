import enums.SimObjectType;

import java.util.ArrayList;

public class SimObject {

    protected String id;
    protected SimObjectType simObjectType;
    protected ArrayList<SimObject> nextSimObjects;

    public SimObject(String id, SimObjectType simObjectType) {
        this.id = id;
        this.simObjectType = simObjectType;
        nextSimObjects = new ArrayList<SimObject>();
    }

    public boolean connect() { return true; }

    public String getId() {
        return id;
    }

    public SimObjectType getSimObjectType() {
        return simObjectType;
    }

    public ArrayList<SimObject> getNextSimObjects() {
        return nextSimObjects;
    }

    public void addNextSimObject(SimObject nextSimObject) {
        nextSimObjects.add(nextSimObject);
    }
}
