import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Car extends Actor {

    //Vectors
    private Vector2 position;
    private Vector2 centerPosition;
    private Vector2 delta;

    //Dimensions
    private float width;
    private float height;

    //Physics
    private float velocity;
    private float acceleration;
    private float direction;

    //Drawing
    private Texture texture;

    //Other
    private Road road;
    private boolean reachedDestination;

    public Car(Road road) {
        //position = new Vector2(road.getStartCell().getX(), road.getStartCell().getY());
        //centerPosition = new Vector2(road.getStartCell().getCenterX(), road.getStartCell().getCenterY());
        delta = new Vector2(0, 0);

        width = 40;
        height = 40;

        velocity = 0;
        acceleration = 0;
        direction = 0;

        this.road = road;
        reachedDestination = false;

        texture = new Texture("carcircle.png");
    }

    public Car(Road road, float velocity) {
        this(road);
        this.velocity = velocity;
    }

    public void update() {
        if (road != null) {
            direction = road.getDirection(centerPosition.x, centerPosition.y);
            velocity += acceleration;
            delta.x = velocity * (float)Math.sin(direction);
            delta.y = velocity * (float)Math.cos(direction);
            if (road.intersectsEnd(centerPosition.x, centerPosition.y, centerPosition.x + delta.x, centerPosition.y - delta.y)) {
                delta.x = road.getDeltaX(centerPosition.x);
                delta.y = road.getDeltaY(centerPosition.y);
                //road = road.getNextRoad();
            }

            //Update Position
            position.x += delta.x;
            position.y -= delta.y;
            centerPosition.x = position.x + (width / 2f);
            centerPosition.y = position.y + (height / 2f);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y, width/2f, height/2f, width, height, 1, 1, (float)Math.toDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setDirectionRad(float radians) {
        this.direction = radians;
    }

    public void setDirectionDeg(float degrees) {
        this.direction = (float)Math.toRadians(degrees);
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public boolean hasReachedDestination() {
        return reachedDestination;
    }
}
