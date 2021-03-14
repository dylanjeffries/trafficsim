import enums.Cardinal;
import enums.Direction;
import enums.Orientation;

public class Calculator {

    public static float getStraightAngle(float x1, float y1, float x2, float y2) {
        float xDiff = x2 - x1;
        float yDiff = y2 - y1;

        if (xDiff >= 0 && yDiff < 0) {
            return (float)Math.atan(Math.abs(xDiff) / Math.abs(yDiff)) + (float)Math.toRadians(0.0);
        } else if (xDiff > 0 && yDiff >= 0) {
            return (float)Math.atan(Math.abs(yDiff) / Math.abs(xDiff)) + (float)Math.toRadians(90.0);
        } else if (xDiff <= 0 && yDiff > 0) {
            return (float)Math.atan(Math.abs(xDiff) / Math.abs(yDiff)) + (float)Math.toRadians(180.0);
        } else if (xDiff < 0 && yDiff <= 0) {
            return (float)Math.atan(Math.abs(yDiff) / Math.abs(xDiff)) + (float)Math.toRadians(270.0);
        }
        return 0f;
    }

    public static boolean checkIntersect(float currentX, float currentY, float nextX, float nextY, float checkX, float checkY) {
        return checkX >= Math.min(currentX, nextX)
                && checkX <= Math.max(currentX, nextX)
                && checkY <= Math.max(currentY, nextY)
                && checkY >= Math.min(currentY, nextY);
    }

    // Direction and Orientation Enum conversion to angles
    public static float orientationToDegrees(Orientation orientation) {
        switch(orientation) {
            case HORIZONTAL:
                return 90f;
            case VERTICAL:
                return 0f;
        }
        return 0f;
    }

    public static float directionToDegrees(Direction direction) {
        switch (direction) {
            case NORTH:
                return 180f;
            case EAST:
                return 90f;
            case SOUTH:
                return 0f;
            case WEST:
                return 270f;
        }
        return 0f;
    }

    public static float orientationToRadians(Orientation orientation) {
        return (float)Math.toRadians(orientationToDegrees(orientation));
    }

    public static float directionToRadians(Direction direction) {
        return (float)Math.toRadians(directionToDegrees(direction));
    }

    // Value Cap
    public static float capFloat(float value, float min, float max) {
        if (value < min) { return min; }
        else if (value > max) { return max; }
        return value;
    }
}
