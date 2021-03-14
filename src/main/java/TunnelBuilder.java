import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Direction;
import enums.SimObjectType;

public class TunnelBuilder {

    // Graphics
    private Texture validTexture;
    private Texture invalidTexture;
    private Texture tunnelTexture;
    private Texture roadTexture;
    private Texture tunnelTPTexture;
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
        validTexture = textures.get("build_valid");
        invalidTexture = textures.get("build_invalid");
        tunnelTexture = textures.get("tunnel");
        roadTexture = textures.get("tunnel_road");
        tunnelTPTexture = textures.get("tunnel_tp");
        cellSize = environment.getGridCellSize();

        this.environment = environment;

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
        spriteBatch.draw(tunnelTPTexture, buildValidPos.x, buildValidPos.y, cellSize/2f, cellSize/2f,
                cellSize, cellSize, 1, 1, Calculator.directionToDegrees(direction), 0, 0,
                tunnelTPTexture.getWidth(), tunnelTPTexture.getHeight(), false, false);

        // Draw valid or invalid build square
        if (buildValid) {
            spriteBatch.draw(validTexture, buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        } else {
            spriteBatch.draw(invalidTexture, buildValidPos.x, buildValidPos.y, cellSize, cellSize);
        }
    }

    public boolean leftClick(Vector2 cursorIndex) {
        if (buildValid) {
            newTunnel = new Tunnel('T' + Integer.toString(counter),
                        environment.getCell(cursorIndex),
                        direction,
                        tunnelTexture,
                        roadTexture);
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
