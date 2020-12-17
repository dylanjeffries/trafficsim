public class ColorData {

    float r;
    float g;
    float b;
    float a;

    public ColorData(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    public float getDecimalR() {
        return r / 255f;
    }

    public float getDecimalG() {
        return g / 255f;
    }

    public float getDecimalB() {
        return b / 255f;
    }
}
