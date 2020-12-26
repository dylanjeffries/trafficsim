import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Button {

    private boolean enabled;
    private boolean active;
    private boolean hovered;
    private boolean clicked;
    private boolean switchable;

    private Vector2 pos;
    private Vector2 topright;
    private BoundingBox boundingBox;

    private Texture inactiveTexture;
    private Texture activeTexture;
    private Texture hoverTexture;
    private Texture disabledTexture;

    public Button(float x, float y, float width, float height, boolean switchable, Texture texture) {
        this(x, y, width, height, switchable, texture, texture, texture, texture);
    }

    public Button(float x, float y, float width, float height, boolean switchable, Texture inactiveTexture, Texture activeTexture) {
        this(x, y, width, height, switchable, inactiveTexture, activeTexture, inactiveTexture, inactiveTexture);
    }

    public Button(float x, float y, float width, float height, boolean switchable, Texture inactiveTexture, Texture activeTexture, Texture hoverTexture) {
        this(x, y, width, height, switchable, inactiveTexture, activeTexture, hoverTexture, inactiveTexture);
    }

    public Button(float x, float y, float width, float height, boolean switchable, Texture inactiveTexture, Texture activeTexture, Texture hoverTexture, Texture disabledTexture) {
        enabled = true;
        active = false;
        clicked = false;
        this.switchable = switchable;
        pos = new Vector2(x, y);
        topright = new Vector2(x + width, y + height);
        boundingBox = new BoundingBox(new Vector3(pos, 0), new Vector3(topright, 0));
        this.inactiveTexture = inactiveTexture;
        this.activeTexture = activeTexture;
        this.hoverTexture = hoverTexture;
        this.disabledTexture = disabledTexture;
    }


    public void draw(SpriteBatch spriteBatch) {
        if (!enabled) { //Button Disabled
            spriteBatch.draw(disabledTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
        } else if (active) { //Button Active
            spriteBatch.draw(activeTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
        } else {
            if (hovered) { //Button Hovered
                spriteBatch.draw(hoverTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
            } else { //Button Inactive
                spriteBatch.draw(inactiveTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
            }
        }
    }

    public void mouseMoved(Vector3 cursorPos) {
        hovered = boundingBox.contains(cursorPos);
    }

    public boolean leftClick() {
        if (hovered) {
            if (enabled) {
                clicked = true;
                if (switchable) { active = !active; }
                else { active = true; }
            }
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isClicked() {
        boolean temp = clicked;
        clicked = false;
        return temp;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}
