package net.yageek.lambert;

import org.apfloat.Apfloat;

public class LambertPoint {

    private Apfloat x;
    private Apfloat y;
    private Apfloat z;


    LambertPoint(double x, double y, double z){
        this.x = new Apfloat(x);
        this.y = new Apfloat(y);
        this.z = new Apfloat(z);
    }
    LambertPoint(Apfloat x, Apfloat y , Apfloat z){
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

    public void translate(double x , double y, double z){
        translate(new Apfloat(x), new Apfloat(y), new Apfloat(z));
    }
    public void translate(Apfloat x , Apfloat y, Apfloat z){

        this.x = this.x.add(x);
        this.y = this.y.add(y);
        this.z = this.z.add(z);
    }

    public LambertPoint toDegree(){

        Apfloat pi = new Apfloat(Math.PI);
        Apfloat factor = new Apfloat(180.0f).divide(pi);
        this.x = this.x.multiply(factor);
        this.y = this.y.multiply(factor);
        this.z = this.z.multiply(factor);

        return this;
    }
}
