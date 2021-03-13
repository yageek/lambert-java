package net.yageek.lambert;

import org.apfloat.Apfloat;

public class LambertPoint {

    private Apfloat x;
    private Apfloat y;
    private Apfloat z;


    LambertPoint(double x, double y, double z) {
        this.x = new Apfloat(Double.valueOf(x), LambertZone.PREC);
        this.y = new Apfloat(Double.valueOf(y), LambertZone.PREC);
        this.z = new Apfloat(Double.valueOf(z), LambertZone.PREC);
    }

    LambertPoint(Apfloat x, Apfloat y, Apfloat z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Apfloat getX() {
        return x;
    }

    public void setX(Apfloat x) {
        this.x = x;
    }

    public Apfloat getY() {
        return y;
    }

    public void setY(Apfloat y) {
        this.y = y;
    }

    public Apfloat getZ() {
        return z;
    }

    public void setZ(Apfloat z) {
        this.z = z;
    }

    public void translate(double x, double y, double z) {
        translate(new Apfloat(Double.toString(x)), new Apfloat(Double.toString(y)), new Apfloat(Double.toString(y)));
    }

    public void translate(Apfloat x, Apfloat y, Apfloat z) {

        this.x = this.x.add(x);
        this.y = this.y.add(y);
        this.z = this.z.add(z);
    }

    public LambertPoint toDegree() {

        Apfloat pi = new Apfloat(Math.PI);
        Apfloat factor = new Apfloat(Float.toString(180.0F)).divide(pi);
        this.x = this.x.multiply(factor);
        this.y = this.y.multiply(factor);
        this.z = this.z.multiply(factor);

        return this;
    }
}
