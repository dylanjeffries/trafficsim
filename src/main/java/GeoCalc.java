public class GeoCalc {

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

    public static float cardinalToRadians(Cardinal cardinal) {
        switch (cardinal) {
            case NORTH:
                return (float)Math.toRadians(180);
            case EAST:
                return (float)Math.toRadians(90);
            case SOUTH:
                return (float)Math.toRadians(0);
            case WEST:
                return (float)Math.toRadians(270);
        }
        return 0;
    }
}
