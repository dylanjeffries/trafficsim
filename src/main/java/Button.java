import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Button {

    private boolean enabled;
    private boolean isOver;
    private boolean state;

    private Vector2 pos;
    private Vector2 topright;
    private BoundingBox boundingBox;

    private Texture inactiveTexture;
    private Texture activeTexture;
    private Texture hoverTexture;

    public Button(float x, float y, float width, float height, Texture texture) {
        this(x, y, width, height, texture, texture);
    }

    public Button(float x, float y, float width, float height, Texture inactiveTexture, Texture activeTexture) {
        enabled = true;
        isOver = false;
        state = false;
        pos = new Vector2(x, y);
        topright = new Vector2(x + width, y + height);
        boundingBox = new BoundingBox(new Vector3(pos, 0), new Vector3(topright, 0));
        this.inactiveTexture = inactiveTexture;
        this.activeTexture = activeTexture;
    }

    public Button(float x, float y, float width, float height, Texture inactiveTexture, Texture activeTexture, Texture hoverTexture) {
        enabled = true;
        isOver = false;
        pos = new Vector2(x, y);
        topright = new Vector2(x + width, y + height);
        boundingBox = new BoundingBox(new Vector3(pos, 0), new Vector3(topright, 0));
        this.inactiveTexture = inactiveTexture;
        this.activeTexture = activeTexture;
        this.hoverTexture = hoverTexture;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (state) {
            spriteBatch.draw(activeTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
        } else {
            if (hoverTexture == null || !isOver) { //No Hover Texture
                spriteBatch.draw(inactiveTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
            } else {
                spriteBatch.draw(hoverTexture, pos.x, pos.y, boundingBox.getWidth(), boundingBox.getHeight());
            }
        }
    }

    public void mouseMoved(Vector3 cursorPos) {
        isOver = boundingBox.contains(cursorPos);
    }

    public boolean touchDown() {
        if (isOver) {
            if (enabled) {
                state = !state;
            }
            return true;
        }
        return false;
    }

}
