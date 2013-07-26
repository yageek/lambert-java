package net.yageek.lambert;

public class LambertPoint {

    private double x;
    private double y;
    private double z;

    LambertPoint(double x, double y , double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

   public double getY() {
        return y;
    }

   public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void translate(double x , double y, double z){

        this.x+= x;
        this.y+= y;
        this.z+= z;
    }

    public LambertPoint toDegree(){
        this.x = this.x * 180/Math.PI;
        this.y = this.y * 180/Math.PI;
        this.z = this.z * 180/Math.PI;

        return this;
    }
}
