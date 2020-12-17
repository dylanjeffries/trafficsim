import java.util.ArrayList;

public class SimObject {

    protected String id;
    protected ArrayList<SimObject> nextSimObjects;

    public SimObject(String id) {
        this.id = id;
        nextSimObjects = new ArrayList<SimObject>();
    }

    public boolean connect() { return true; }

    public String getId() {
        return id;
    }

    public ArrayList<SimObject> getNextSimObjects() {
        return nextSimObjects;
    }

    public void addNextSimObject(SimObject nextSimObject) {
        nextSimObjects.add(nextSimObject);
    }
}
