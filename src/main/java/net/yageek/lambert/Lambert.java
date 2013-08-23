package net.yageek.lambert;


import static java.lang.Math.*;
import static net.yageek.lambert.LambertZone.*;

public class Lambert {

 /*
 *   ALGO0002
 */
 private static double latitudeFromLatitudeISO(double latISo, double e, double eps)
    {

        double phi0 =  2* atan(exp(latISo)) - M_PI_2;
        double phiI = 2*atan(pow((1+e*sin(phi0))/(1-e*sin(phi0)),e/2.0)*exp(latISo)) - M_PI_2;
        double delta = abs(phiI - phi0);

        while(delta > eps)
        {
            phi0 = phiI;
            phiI = 2*atan(pow((1+e*sin(phi0))/(1-e*sin(phi0)),e/2.0)*exp(latISo)) - M_PI_2;
            delta = abs(phiI - phi0);
        }

        return phiI;

    }

/*
*	ALGO0004 - Lambert vers geographiques
*/

    private static LambertPoint lambertToGeographic(LambertPoint org, LambertZone zone, double lonMeridian, double e, double eps)
    {
        double n = zone.n();
        double C = zone.c();
        double xs = zone.xs();
        double ys = zone.ys();

        double x = org.getX();
        double y = org.getY();


        double lon, gamma, R, latIso;

        R = sqrt((x - xs) * (x - xs) + (y - ys) * (y - ys));

        gamma = atan((x-xs)/(ys-y));

        lon = lonMeridian + gamma/n;

        latIso = -1/n*log(abs(R/C));

        double lat = latitudeFromLatitudeISO(latIso, e, eps);

        LambertPoint dest = new LambertPoint(lon,lat,0);
        return dest;
    }

 /*
 * ALGO0021 - Calcul de la grande Normale
 *
*/

    private static double lambertNormal(double lat, double a, double e)
    {

        return a/sqrt(1-e*e*sin(lat)*sin(lat));
    }

    /*
     * ALGO0009 - Transformations geographiques -> cartésiennes
     *
     */

    private static LambertPoint geographicToCartesian(double lon, double lat, double he, double a, double e)
    {
        double N = lambertNormal(lat, a, e);

        LambertPoint pt = new LambertPoint(0,0,0);

        pt.setX((N+he)*cos(lat)*cos(lon));
        pt.setY((N+he)*cos(lat)*sin(lon));
        pt.setZ((N*(1-e*e)+he)*sin(lat));

        return pt;

    }

    /*
 * ALGO0012 - Passage des coordonnées cartésiennes aux coordonnées géographiques
 */

    private static LambertPoint cartesianToGeographic(LambertPoint org, double meridien, double a, double e, double eps)
    {
        double x = org.getX(), y = org.getY(), z = org.getZ();

        double lon = meridien + atan(y/x);

        double module = sqrt(x*x + y*y);

        double phi0 = atan(z/(module*(1-(a*e*e)/sqrt(x*x+y*y+z*z))));
        double phiI = atan(z/module/(1-a*e*e*cos(phi0)/(module * sqrt(1-e*e*sin(phi0)*sin(phi0)))));
        double delta= abs(phiI - phi0);
        while(delta > eps)
        {
            phi0 = phiI;
            phiI = atan(z/module/(1-a*e*e*cos(phi0)/(module * sqrt(1-e*e*sin(phi0)*sin(phi0)))));
            delta= abs(phiI - phi0);

        }

        double he = module/cos(phiI) - a/sqrt(1-e*e*sin(phiI)*sin(phiI));

        LambertPoint pt = new LambertPoint(lon,phiI,he);

        return pt;
    }
     /*
 * Convert Lambert -> WGS84
 * http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/transfo.pdf
 *
 */

    public static LambertPoint convertToWGS84(LambertPoint org, LambertZone zone){

        LambertPoint pt1 =  lambertToGeographic(org, zone, LON_MERID_PARIS, E_CLARK_IGN, DEFAULT_EPS);

        LambertPoint pt2 = geographicToCartesian(pt1.getX(), pt1.getY(), pt1.getZ(), A_CLARK_IGN, E_CLARK_IGN);

        pt2.translate(-168,-60,320);

        //WGS84 refers to greenwich
        pt2 = cartesianToGeographic(pt2, LON_MERID_GREENWICH, A_WGS84, E_WGS84, DEFAULT_EPS);

        return pt2;

    }

    public static LambertPoint convertToWGS84(double x, double y, LambertZone zone){

        LambertPoint pt = new LambertPoint(x,y,0);
        return convertToWGS84(pt, zone);
    }

    public static LambertPoint convertToWGS84Deg(double x, double y, LambertZone zone){

        LambertPoint pt = new LambertPoint(x,y,0);
        return convertToWGS84(pt, zone).toDegree();
    }

}


