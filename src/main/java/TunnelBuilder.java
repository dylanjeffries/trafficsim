import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Direction;
import enums.SimObjectType;

public class TunnelBuilder {

    // Graphics
    private Textures textures;
    private float cellSize;

    // Environment
    private Environment environment;

    // Building
    private int counter;
    private Direction direction;
    private Tunnel newTunnel;
    private boolean newTunnelReady;
    private boolean buildValid;

    public TunnelBuilder(Textures textures, Environment environment) {
        this.textures = textures;
        this.environment = environment;
        cellSize = environment.getGridCellSize();

        counter = 1;
        direction = Direction.SOUTH;
        newTunnelReady = false;
        buildValid = false;
    }

    public void update() {
        // Rotate keyboard input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            direction = rotateDirection();
        }
    }

    public void draw(SpriteBatch spriteBatch, Vector2 buildValidPos) {
        // Draw transparent tunnel
        spriteBatch.draw(textures.get("tunnel_tp"), buildValidPos.x, buildValidPos.y, cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                textures.get("tunnel_tp").getWidth(), textures.get("tunnel_tp").getHeight(), false, false);

        // Draw valid or invalid build square
        if (buildValid) {
            spriteBatch.draw(textures.get("build_valid"), buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        } else {
            spriteBatch.draw(textures.get("build_invalid"), buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (buildValid) {
            newTunnel = new Tunnel('T' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        direction,
                        environment,
                        textures);
            newTunnelReady = true;
        }
        return true;
    }

    public void mouseMoved(Vector2 cursorIndex) {
        buildValid = environment.getCellSimObjectType(cursorIndex) == SimObjectType.NONE;
    }

    private Direction rotateDirection() {
        switch (direction) {
            case NORTH:
                return Direction.EAST;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default: // East
                return  Direction.SOUTH;
        }
    }

    public boolean isNewTunnelReady() {
        return newTunnelReady;
    }

    public Tunnel getNewTunnel() {
        Tunnel newTunnelCopy = new Tunnel(newTunnel); //Copy the new tunnel
        newTunnel = null;
        newTunnelReady = false;
        counter++;
        return newTunnelCopy;
    }
}
