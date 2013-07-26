package net.yageek.lambert;


import static java.lang.Math.*;
import static net.yageek.lambert.LambertZone.*;

public class Lambert {

 /*
 *   ALGO0002
 */
 private static double lat_from_lat_iso(double lat_iso, double e,double eps)
    {

        double phi_0 =  2* atan(exp(lat_iso)) - M_PI_2;
        double phi_i = 2*atan(pow((1+e*sin(phi_0))/(1-e*sin(phi_0)),e/2.0)*exp(lat_iso)) - M_PI_2;
        double delta = abs(phi_i - phi_0);

        while(delta > eps)
        {
            phi_0 = phi_i;
            phi_i = 2*atan(pow((1+e*sin(phi_0))/(1-e*sin(phi_0)),e/2.0)*exp(lat_iso)) - M_PI_2;
            delta = abs(phi_i - phi_0);
        }

        return phi_i;

    }

/*
*	ALGO0004 - Lambert vers geographiques
*/

    private static LambertPoint lambert_to_geographic(LambertPoint  org, LambertZone zone, double lon_merid, double e, double eps)
    {
        double n = zone.n();
        double C = zone.c();
        double x_s = zone.xs();
        double y_s = zone.ys();

        double x = org.getX();
        double y = org.getY();


        double lon, gamma, R, lat_iso;

        R = sqrt((x-x_s)*(x-x_s)+(y-y_s)*(y-y_s));

        gamma = atan((x-x_s)/(y_s-y));

        lon = lon_merid + gamma/n;

        lat_iso = -1/n*log(abs(R/C));

        double lat = lat_from_lat_iso(lat_iso,e,eps);

        LambertPoint dest = new LambertPoint(lon,lat,0);
        return dest;
    }

 /*
 * ALGO0021 - Calcul de la grande Normale
 *
*/

    private static double lambert_normal(double lat, double a, double e)
    {

        return a/sqrt(1-e*e*sin(lat)*sin(lat));
    }

    /*
     * ALGO0009 - Transformations geographiques -> cartésiennes
     *
     */

    private static LambertPoint geographic_to_cartesian(double lon, double lat, double he, double a, double e)
    {
        double N = lambert_normal(lat,a,e);

        LambertPoint pt = new LambertPoint(0,0,0);

        pt.setX((N+he)*cos(lat)*cos(lon));
        pt.setY((N+he)*cos(lat)*sin(lon));
        pt.setZ((N*(1-e*e)+he)*sin(lat));

        return pt;

    }

    /*
 * ALGO0012 - Passage des coordonnées cartésiennes aux coordonnées géographiques
 */

    private static LambertPoint cartesian_to_geographic(LambertPoint org, double meridien, double a, double e , double eps)
    {
        double x = org.getX(), y = org.getY(), z = org.getZ();

        double lon = meridien + atan(y/x);

        double module = sqrt(x*x + y*y);

        double phi_0 = atan(z/(module*(1-(a*e*e)/sqrt(x*x+y*y+z*z))));
        double phi_i = atan(z/module/(1-a*e*e*cos(phi_0)/(module * sqrt(1-e*e*sin(phi_0)*sin(phi_0)))));
        double delta= abs(phi_i - phi_0);
        while(delta > eps)
        {
            phi_0 = phi_i;
            phi_i = atan(z/module/(1-a*e*e*cos(phi_0)/(module * sqrt(1-e*e*sin(phi_0)*sin(phi_0)))));
            delta= abs(phi_i - phi_0);

        }

        double he = module/cos(phi_i) - a/sqrt(1-e*e*sin(phi_i)*sin(phi_i));

        LambertPoint pt = new LambertPoint(lon,phi_i,he);

        return pt;
    }
     /*
 * Convert Lambert -> WGS84
 * http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/transfo.pdf
 *
 */

    public static LambertPoint ConvertToWGS84(LambertPoint org,LambertZone zone){

        LambertPoint pt1 =  lambert_to_geographic(org,zone,LON_MERID_PARIS,E_CLARK_IGN,DEFAULT_EPS);

        LambertPoint pt2 = geographic_to_cartesian(pt1.getX(),pt1.getY(),pt1.getZ(),A_CLARK_IGN,E_CLARK_IGN);

        pt2.translate(-168,-60,320);

        //WGS84 refers to greenwich
        pt2 = cartesian_to_geographic(pt2, LON_MERID_GREENWICH, A_WGS84,E_WGS84,DEFAULT_EPS);

        return pt2;

    }

    public static LambertPoint ConvertToWGS84(double x, double y, LambertZone zone){

        LambertPoint pt = new LambertPoint(x,y,0);
        return ConvertToWGS84(pt,zone);
    }

    public static LambertPoint ConvertToWGS84Deg(double x, double y, LambertZone zone){

        LambertPoint pt = new LambertPoint(x,y,0);
        return ConvertToWGS84(pt,zone).toDegree();
    }

}


