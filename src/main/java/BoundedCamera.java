import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class BoundedCamera extends OrthographicCamera {

    private BoundingBox left;
    private BoundingBox right;
    private BoundingBox top;
    private BoundingBox bottom;

    public BoundedCamera(float leftBound, float rightBound, float topBound, float bottomBound) {
        super();
        this.left = new BoundingBox(new Vector3(leftBound-10, bottomBound, 0), new Vector3(leftBound, topBound, 0));
        this.right = new BoundingBox(new Vector3(rightBound, bottomBound, 0), new Vector3(rightBound+10, topBound, 0));
        this.top = new BoundingBox(new Vector3(leftBound, topBound, 0), new Vector3(rightBound, topBound+10, 0));
        this.bottom = new BoundingBox(new Vector3(leftBound, bottomBound-10, 0), new Vector3(rightBound, bottomBound, 0));
    }

    @Override
    public void translate(float x, float y) {
        Vector3 prevPos = position.cpy(); //Save current position
        super.translate(new Vector3(x, y, 0)); //Perform translation
        update();

        //If camera is too far left or right, reverse x axis translation
        if (frustum.boundsInFrustum(left) || frustum.boundsInFrustum(right)) {
            position.set(new Vector3(prevPos.x, position.y, 0));
        }

        //If camera is too far up or down, reverse y axis translation
        if (frustum.boundsInFrustum(top) || frustum.boundsInFrustum(bottom)) {
            position.set(new Vector3(position.x, prevPos.y, 0));
        }

        update();
    }

}
