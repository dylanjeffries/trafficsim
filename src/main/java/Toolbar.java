import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Toolbar {

    private Vector2 pos;
    private float height;
    private Texture texture;

    private float buttonHeight;
    private float buttonY;

    private Button selectButton;
    private Button roadButton;

    private BuildingMode buildingMode;

    public Toolbar(float x, float y, Textures textures) {
        pos = new Vector2(x, y);
        height = Config.getInteger("toolbar_height");
        texture = textures.get("toolbar");

        buttonHeight = height * 0.8f;
        buttonY = pos.y + (height * 0.1f);

        selectButton = new Button(100, buttonY, buttonHeight, buttonHeight, false,
                textures.get("select_inactive"), textures.get("select_active"), textures.get("select_hover"), textures.get("select_disabled"));

        roadButton = new Button(100 + height, buttonY, buttonHeight, buttonHeight, false,
                textures.get("road_inactive"), textures.get("road_active"), textures.get("road_hover"), textures.get("road_disabled"));

        //Default
        buildingMode = BuildingMode.SELECT;
        selectButton.activate();
    }

    public void update() {
        if (selectButton.isClicked()) {
            buildingMode = BuildingMode.SELECT;
            deactivateAll();
            selectButton.activate();
        } else if (roadButton.isClicked()) {
            buildingMode = BuildingMode.ROAD;
            deactivateAll();
            roadButton.activate();
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, pos.x, pos.y, Gdx.graphics.getDisplayMode().width, height);

        selectButton.draw(spriteBatch);
        roadButton.draw(spriteBatch);
    }

    public void mouseMoved(Vector3 cursorPos) {
        selectButton.mouseMoved(cursorPos);
        roadButton.mouseMoved(cursorPos);
    }

    public boolean leftClick() {
        return selectButton.leftClick() ||
                roadButton.leftClick();
    }

    private void deactivateAll() {
        selectButton.deactivate();
        roadButton.deactivate();
    }

    public BuildingMode getBuildingMode() {
        return buildingMode;
    }
}
