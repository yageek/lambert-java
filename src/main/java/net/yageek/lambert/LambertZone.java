package net.yageek.lambert;

import org.apfloat.Apfloat;

public enum LambertZone {

    LambertI(0),
    LambertII(1),
    LambertIII(2),
    LambertIV(3),
    LambertIIExtended(4),
    Lambert93(5);

    private final int lambertZone;

    public final static int PREC = 50;

    private final static Apfloat[] LAMBERT_N = {
            new Apfloat("0.7604059656", PREC),
            new Apfloat("0.7289686274", PREC),
            new Apfloat("0.6959127966", PREC),
            new Apfloat("0.6712679322", PREC),
            new Apfloat("0.7289686274", PREC),
            new Apfloat("0.7256077650", PREC)
    };

    private final static Apfloat[] LAMBERT_C = {
            new Apfloat("11603796.98", PREC),
            new Apfloat("11745793.39", PREC),
            new Apfloat("11947992.52", PREC),
            new Apfloat("12136281.99", PREC),
            new Apfloat("11745793.39", PREC),
            new Apfloat("11754255.426", PREC)
    };

    private final static Apfloat[] LAMBERT_XS = {
            new Apfloat("600000.0", PREC),
            new Apfloat("600000.0", PREC),
            new Apfloat("600000.0", PREC),
            new Apfloat("234.358", PREC),
            new Apfloat("600000.0", PREC),
            new Apfloat("700000.0", PREC)
    };

    private final static Apfloat[] LAMBERT_YS = {
            new Apfloat("5657616.674", PREC),
            new Apfloat("6199695.768", PREC),
            new Apfloat("6791905.085", PREC),
            new Apfloat("7239161.542", PREC),
            new Apfloat("8199695.768", PREC),
            new Apfloat("12655612.050", PREC)
    };

    public final static Apfloat M_PI = new Apfloat(Double.toString(Math.PI));
    public final static Apfloat M_PI_2 = M_PI.divide(new Apfloat("2.0", PREC));
    public final static Apfloat DEFAULT_EPS = new Apfloat(1e-10);
    public final static Apfloat E_CLARK_IGN = new Apfloat("0.08248325676", PREC);
    public final static Apfloat E_WGS84 = new Apfloat("0.08181919106", PREC);

    public final static Apfloat A_CLARK_IGN = new Apfloat("6378249.2", PREC);
    public final static Apfloat A_WGS84 = new Apfloat("6378137.0", PREC);
    public final static Apfloat LON_MERID_PARIS = Apfloat.ZERO;
    public final static Apfloat LON_MERID_GREENWICH = new Apfloat("0.04079234433", PREC);
    public final static Apfloat LON_MERID_IERS = new Apfloat("3.0", PREC).multiply(M_PI).divide(new Apfloat("180.0", PREC));


    private LambertZone(int value) {
        this.lambertZone = value;
    }

    public Apfloat n() {
        return this.LAMBERT_N[this.lambertZone];
    }

    public Apfloat c() {
        return this.LAMBERT_C[this.lambertZone];
    }

    public Apfloat xs() {
        return this.LAMBERT_XS[this.lambertZone];
    }

    public Apfloat ys() {
        return this.LAMBERT_YS[this.lambertZone];
    }

}