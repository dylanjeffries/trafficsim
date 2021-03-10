import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import enums.Cardinal;
import enums.Direction;

public class Car {

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
    private Direction direction;

    //Drawing
    private Texture texture;
    private Texture marker;

    //Other
    private Environment environment;
    private boolean reachedDestination;
    private float travelled;


    public Car(Vector2 position, Environment environment, Textures textures) {
        this.position = position;
        this.environment = environment;
//        centerPosition = position.add()
        delta = new Vector2(0, 0);

        width = 24;
        height = 48;

        velocity = 0;
        acceleration = 0;
        direction = Direction.EAST;

        texture = textures.get("car_pink");
        marker = textures.get("marker");
    }

    public void update() {
        // Resets
        delta.set(0, 0);

//        acceleration = 33.5f / 600f; // 60 Iterations per second
//        velocity += acceleration;

        // Anchor
        float anchor = environment.getCellAtPosition(position).getRoad().getAnchor(direction);
        switch (direction) {
            case EAST:
            case WEST:
                float diff = anchor - position.y;
                System.out.println(diff);
                if (diff == 0) {
                    break;
                } else if (Math.abs(diff) <= 1) {
                    delta.y += anchor - position.y;
                } else if (diff > 1) {
                    delta.y += 1;
                } else if (diff < -1) {
                    delta.y -= 1;
                }
                break;
        }

        // Check for turn
        if (travelled >= 300) {
            //turnClockwise();
            travelled = 0;
        }
        velocity = 1f;
        delta.x += velocity * (float)Math.sin(GeoCalc.directionToRadians(direction));
        delta.y -= velocity * (float)Math.cos(GeoCalc.directionToRadians(direction));

        //Update Position
        position.x += delta.x;
        position.y += delta.y;
//        centerPosition.x = position.x + (width / 2f);
//        centerPosition.y = position.y + (height / 2f);

        //Update travelled
        travelled += velocity;
    }

    private void turnClockwise() {
        switch (direction) {
            case NORTH:
                direction = Direction.EAST;
                break;
            case EAST:
                direction = Direction.SOUTH;
                break;
            case SOUTH:
                direction = Direction.WEST;
                break;
            case WEST:
                direction = Direction.NORTH;
                break;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x - (width / 2f), position.y - (height / 2f), width / 2f, height / 2f, width, height, 1, 1, GeoCalc.directionToDegrees(direction), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        spriteBatch.draw(marker, position.x, position.y, 4, 4);
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public boolean hasReachedDestination() {
        return reachedDestination;
    }
}
