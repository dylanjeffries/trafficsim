import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

public class Toolbar {

    private Vector2 pos;
    private float height;
    private Texture texture;

    private float buttonHeight;
    private float buttonY;

    private Button selectButton;
    private Button roadButton;

    public Toolbar(float x, float y, Textures textures) {
        pos = new Vector2(x, y);
        height = Config.getInteger("toolbar_height");
        texture = textures.get("toolbar");

        buttonHeight = height * 0.8f;
        buttonY = pos.y + (height * 0.1f);

        selectButton = new Button(100, buttonY, buttonHeight, buttonHeight,
                textures.get("select_inactive"), textures.get("select_active"), textures.get("select_hover"));

        roadButton = new Button(100 + height, buttonY, buttonHeight, buttonHeight,
                textures.get("road_inactive"), textures.get("road_active"), textures.get("road_hover"));
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, pos.x, pos.y, Gdx.graphics.getWidth(), height);

        selectButton.draw(spriteBatch);
        roadButton.draw(spriteBatch);
    }

    public void mouseMoved(Vector3 cursorPos) {
        selectButton.mouseMoved(cursorPos);
        roadButton.mouseMoved(cursorPos);
    }

    public boolean touchDown() {
        return selectButton.touchDown() ||
                roadButton.touchDown();
    }
}
