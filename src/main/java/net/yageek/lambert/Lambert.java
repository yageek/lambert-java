package net.yageek.lambert;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static java.lang.Math.*;
import static net.yageek.lambert.LambertZone.*;

/*
https://github.com/yageek/lambert-java
https://bintray.com/yageek/maven/lambert-java/view/files/net/yageek/lambert/lambert-java/1.1

Online samples :
http://geofree.fr/gf/coordinateConv.asp#listSys

--------------------------------------------------------------------------------------
Install cs2cs on Ubuntu :
http://www.sarasafavi.com/installing-gdalogr-on-ubuntu.html

--------------------------------------------------------------------------------------
http://cs2cs.mygeodata.eu/
Conversion From Lambert Zone II to WGS 84 :
$>cs2cs +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs +to +proj=longlat +datum=WGS84 +no_defs -f "%.11f" <<EOF
> 618062.6287556458 2430668.2797339745
> EOF

2.58260346086	48.87407486915 43.05556181818
--------------------------------------------------------------------------------------
Conversion From WGS 84 To Lambert Zone II:
$>cs2cs +proj=longlat +datum=WGS84 +no_defs +to +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs  -f "%.11f" <<EOF
2.58331732871 48.8741427818
EOF
618115.00035284588	2430676.00004872493 -43.05512374081
*/


/*
Documentations :
http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf
http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_80.pdf
http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
 */
public class Lambert {

    /*
    *   ALGO0001
    */
    public static double latitudeISOFromLat(double lat, double e) {
        double elt11 = Math.PI / 4d;
        double elt12 = lat / 2d;
        double elt1 = tan(elt11 + elt12);

        double elt21 = e * sin(lat);
        double elt2 = pow((1 - elt21) / (1 + elt21), e / 2d);

        return log(elt1 * elt2);
    }


    /*
    *   ALGO0002
    */
    private static double latitudeFromLatitudeISO(double latISo, double e, double eps) {

        double phi0 = 2 * atan(exp(latISo)) - M_PI_2;
        double phiI = 2 * atan(pow((1 + e * sin(phi0)) / (1 - e * sin(phi0)), e / 2d) * exp(latISo)) - M_PI_2;
        double delta = abs(phiI - phi0);

        while (delta > eps) {
            phi0 = phiI;
            phiI = 2 * atan(pow((1 + e * sin(phi0)) / (1 - e * sin(phi0)), e / 2d) * exp(latISo)) - M_PI_2;
            delta = abs(phiI - phi0);
        }

        return phiI;
    }


    /*
   *   ALGO0003
   */
    public static LambertPoint geographicToLambertAlg003(double latitude, double longitude, LambertZone zone, double lonMeridian, double e) {

        double n = zone.n();
        double C = zone.c();
        double xs = zone.xs();
        double ys = zone.ys();

        double latIso = latitudeISOFromLat(latitude, e);

        double eLatIso = exp(-n * latIso);

        double nLon = n * (longitude - lonMeridian);

        double x = xs + C * eLatIso * sin(nLon);
        double y = ys - C * eLatIso * cos(nLon);

        return new LambertPoint(x, y, 0);
    }

    /*
   *  http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
   *  3.4 Coordonnées géographiques Lambert
   */
    public static LambertPoint geographicToLambert(double latitude, double longitude, LambertZone zone, double lonMeridian, double e) {

        double n = zone.n();
        double C = zone.c();
        double xs = zone.xs();
        double ys = zone.ys();

        double sinLat = sin(latitude);
        double eSinLat = (e * sinLat);
        double elt1 = (1 + sinLat) / (1 - sinLat);
        double elt2 = (1 + eSinLat) / (1 - eSinLat);

        double latIso = (1 / 2d) * log(elt1) - (e / 2d) * log(elt2);

        double R = C * exp(-(n * latIso));

        double LAMBDA = n * (longitude - lonMeridian);

        double x = xs + (R * sin(LAMBDA));
        double y = ys - (R * cos(LAMBDA));

        return new LambertPoint(x, y, 0);
    }

/*
*	ALGO0004 - Lambert vers geographiques
*/

    public static LambertPoint lambertToGeographic(LambertPoint org, LambertZone zone, double lonMeridian, double e, double eps) {
        double n = zone.n();
        double C = zone.c();
        double xs = zone.xs();
        double ys = zone.ys();

        double x = org.getX();
        double y = org.getY();


        double lon, gamma, R, latIso;

        R = sqrt((x - xs) * (x - xs) + (y - ys) * (y - ys));

        gamma = atan((x - xs) / (ys - y));

        lon = lonMeridian + gamma / n;

        latIso = -1 / n * log(abs(R / C));

        double lat = latitudeFromLatitudeISO(latIso, e, eps);

        return new LambertPoint(lon, lat, 0);
    }

 /*
 * ALGO0021 - Calcul de la grande Normale
 *
*/

    private static double lambertNormal(double lat, double a, double e) {

        return a / sqrt(1 - e * e * sin(lat) * sin(lat));
    }

    /*
     * ALGO0009 - Transformations geographiques -> cartésiennes
     *
     */

    private static LambertPoint geographicToCartesian(double lon, double lat, double he, double a, double e) {
        double N = lambertNormal(lat, a, e);

        LambertPoint pt = new LambertPoint(0, 0, 0);

        pt.setX((N + he) * cos(lat) * cos(lon));
        pt.setY((N + he) * cos(lat) * sin(lon));
        pt.setZ((N * (1 - e * e) + he) * sin(lat));

        return pt;

    }

    /*
 * ALGO0012 - Passage des coordonnées cartésiennes aux coordonnées géographiques
 */

    private static LambertPoint cartesianToGeographic(LambertPoint org, double meridien, double a, double e, double eps) {
        double x = org.getX(), y = org.getY(), z = org.getZ();

        double lon = meridien + atan(y / x);

        double module = sqrt(x * x + y * y);

        double phi0 = atan(z / (module * (1 - (a * e * e) / sqrt(x * x + y * y + z * z))));
        double phiI = atan(z / module / (1 - a * e * e * cos(phi0) / (module * sqrt(1 - e * e * sin(phi0) * sin(phi0)))));
        double delta = abs(phiI - phi0);
        while (delta > eps) {
            phi0 = phiI;
            phiI = atan(z / module / (1 - a * e * e * cos(phi0) / (module * sqrt(1 - e * e * sin(phi0) * sin(phi0)))));
            delta = abs(phiI - phi0);

        }

        double he = module / cos(phiI) - a / sqrt(1 - e * e * sin(phiI) * sin(phiI));

        return new LambertPoint(lon, phiI, he);
    }

     /*
 * Convert Lambert -> WGS84
 * http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/transfo.pdf
 *
 */

    public static LambertPoint convertToWGS84(LambertPoint org, LambertZone zone) {

        if (zone == Lambert93) {
            return lambertToGeographic(org, Lambert93, LON_MERID_IERS, E_WGS84, DEFAULT_EPS);
        } else {
            LambertPoint pt1 = lambertToGeographic(org, zone, LON_MERID_PARIS, E_CLARK_IGN, DEFAULT_EPS);

            LambertPoint pt2 = geographicToCartesian(pt1.getX(), pt1.getY(), pt1.getZ(), A_CLARK_IGN, E_CLARK_IGN);

            pt2.translate(-168, -60, 320);

            //WGS84 refers to greenwich
            return cartesianToGeographic(pt2, LON_MERID_GREENWICH, A_WGS84, E_WGS84, DEFAULT_EPS);
        }
    }

     /*
 * Convert WGS84 -> Lambert
 * http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/transfo.pdf
 *
 */

    public static LambertPoint convertToLambert(double latitude, double longitude, LambertZone zone) throws NotImplementedException {

        if (zone == Lambert93) {
            throw new NotImplementedException();
        } else {
            LambertPoint pt1 = geographicToCartesian(longitude - LON_MERID_GREENWICH, latitude, 0, A_WGS84, E_WGS84);

            pt1.translate(168, 60, -320);

            LambertPoint pt2 = cartesianToGeographic(pt1, LON_MERID_PARIS, A_WGS84, E_WGS84, DEFAULT_EPS);

            return geographicToLambert(pt2.getY(), pt2.getX(), zone, LON_MERID_PARIS, E_WGS84);
        }
    }

    /*
        Method not really usefull, just to have two ways of doing the same conversion.
     */
    public static LambertPoint convertToLambertByAlg003(double latitude, double longitude, LambertZone zone) throws NotImplementedException {

        if (zone == Lambert93) {
            throw new NotImplementedException();
        } else {
            LambertPoint pt1 = geographicToCartesian(longitude - LON_MERID_GREENWICH, latitude, 0, A_WGS84, E_WGS84);

            pt1.translate(168, 60, -320);

            LambertPoint pt2 = cartesianToGeographic(pt1, LON_MERID_PARIS, A_WGS84, E_WGS84, DEFAULT_EPS);

            return geographicToLambertAlg003(pt2.getY(), pt2.getX(), zone, LON_MERID_PARIS, E_WGS84);
        }
    }

    public static LambertPoint convertToWGS84(double x, double y, LambertZone zone) {

        LambertPoint pt = new LambertPoint(x, y, 0);
        return convertToWGS84(pt, zone);
    }

    public static LambertPoint convertToWGS84Deg(double x, double y, LambertZone zone) {

        LambertPoint pt = new LambertPoint(x, y, 0);
        return convertToWGS84(pt, zone).toDegree();
    }

}


