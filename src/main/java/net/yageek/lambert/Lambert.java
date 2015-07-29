package net.yageek.lambert;


import org.apfloat.Apfloat;

import org.apfloat.ApfloatMath;
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
> 618115 2430676
> EOF

2.58331732871	48.87414278182 43.05512374267

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
    public static Apfloat latitudeISOFromLat(Apfloat lat, Apfloat e) {

        Apfloat elt11 = new Apfloat(Math.PI).divide(new Apfloat(4d));
        Apfloat elt12 = lat.divide( new Apfloat(2d));
        Apfloat elt1 = ApfloatMath.tan(elt11.add(elt12));

        Apfloat elt21 = e.add(ApfloatMath.sin(lat));
        Apfloat elt2 = ApfloatMath.pow( Apfloat.ONE.subtract(elt11).divide(Apfloat.ONE.add(elt21)), e.divide(new Apfloat(2d)) );

        return ApfloatMath.log(elt1.multiply(elt2));
    }


    /*
    *   ALGO0002
    */
    private static Apfloat latitudeFromLatitudeISO(Apfloat latISo, Apfloat e, Apfloat eps) {

        Apfloat two = new Apfloat(2);
        Apfloat aM_PI_2 = new Apfloat(M_PI_2);
        Apfloat phi0 = two.multiply(ApfloatMath.atan(ApfloatMath.exp(latISo))).subtract(aM_PI_2);

        Apfloat eSinPhi0 = e.multiply(ApfloatMath.sin(phi0));
        Apfloat phiIPowA = Apfloat.ONE.add(eSinPhi0).divide(Apfloat.ONE.subtract(eSinPhi0));

        Apfloat phiI = two.multiply(ApfloatMath.atan(ApfloatMath.pow(phiIPowA, e.divide(new Apfloat(2d))))).multiply(latISo).subtract(aM_PI_2);
        //double phiI = 2 * atan(pow((1 + e * sin(phi0)) / (1 - e * sin(phi0)), e / 2d) * exp(latISo)) - M_PI_2;
        Apfloat delta = ApfloatMath.abs(phiI.subtract(phi0));

        while (delta.doubleValue()> eps.doubleValue()) {
            phi0 = phiI;

            eSinPhi0 = e.multiply(ApfloatMath.sin(phi0));
            phiIPowA = Apfloat.ONE.add(eSinPhi0).divide(Apfloat.ONE.subtract(eSinPhi0));

            phiI =  two.multiply(ApfloatMath.atan(ApfloatMath.pow(phiIPowA, e.divide(new Apfloat(2d))))).multiply(latISo).subtract(aM_PI_2);
            //phiI = 2 * atan(pow((1 + e * sin(phi0)) / (1 - e * sin(phi0)), e / 2d) * exp(latISo)) - M_PI_2;
            delta = ApfloatMath.abs(phiI.subtract(phi0));
        }

        return phiI;
    }


    /*
   *   ALGO0003
   */
    public static LambertPoint geographicToLambertAlg003(Apfloat latitude, Apfloat longitude, LambertZone zone, Apfloat lonMeridian, Apfloat e) {

        Apfloat n = new Apfloat(zone.n());
        Apfloat C = new Apfloat(zone.c());
        Apfloat xs = new Apfloat(zone.xs());
        Apfloat ys = new Apfloat(zone.ys());

        Apfloat latIso = latitudeISOFromLat(latitude, e);

        Apfloat eLatIso = ApfloatMath.exp(n.negate().multiply(latIso));

        Apfloat nLon = n.multiply(longitude.subtract(lonMeridian));

        Apfloat x = xs.add(C.multiply(eLatIso).multiply(ApfloatMath.sin(nLon)));
        ys.add(C.multiply(eLatIso).multiply(ApfloatMath.cos(nLon)));
        Apfloat y =  ys.add(C.multiply(eLatIso).multiply(ApfloatMath.cos(nLon)));

        return new LambertPoint(x, y, Apfloat.ZERO);
    }

    /*
   *  http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
   *  3.4 Coordonnées géographiques Lambert
   */
    public static LambertPoint geographicToLambert(Apfloat latitude, Apfloat longitude, LambertZone zone, Apfloat lonMeridian, Apfloat e) {

        Apfloat two = new Apfloat(2d);

        Apfloat n = new Apfloat(zone.n());
        Apfloat C = new Apfloat(zone.c());
        Apfloat xs = new Apfloat(zone.xs());
        Apfloat ys = new Apfloat(zone.ys());

        Apfloat sinLat = ApfloatMath.sin(latitude);
        Apfloat eSinLat = e.multiply(sinLat);
        Apfloat elt1 = Apfloat.ONE.add(sinLat).divide(Apfloat.ONE.subtract(sinLat)); //(1 + sinLat) / (1 - sinLat);
        Apfloat elt2 = Apfloat.ONE.add(eSinLat).divide(Apfloat.ONE.subtract(eSinLat));;//(1 + eSinLat) / (1 - eSinLat);

        Apfloat latIso = Apfloat.ONE.divide(two).multiply(ApfloatMath.log(elt1)).subtract(e.divide(two).multiply(ApfloatMath.log(elt2)));

        Apfloat R = C.multiply(ApfloatMath.exp(n.multiply(latIso).negate()));

        Apfloat LAMBDA = n.multiply(longitude.subtract(lonMeridian));

        Apfloat x = xs.add(R.multiply(ApfloatMath.sin(LAMBDA)));
        Apfloat y =ys.subtract(R.multiply(ApfloatMath.cos(LAMBDA)));

        return new LambertPoint(x, y, Apfloat.ZERO);
    }

/*
*	ALGO0004 - Lambert vers geographiques
*/

    public static LambertPoint lambertToGeographic(LambertPoint org, LambertZone zone, double lonMeridian, double e, double eps){
        return lambertToGeographic(org, zone, new Apfloat(lonMeridian), new Apfloat(e), new Apfloat(eps));
    }

    public static LambertPoint lambertToGeographic(LambertPoint org, LambertZone zone, Apfloat lonMeridian, Apfloat e, Apfloat eps) {



        Apfloat n = new Apfloat(zone.n());
        Apfloat C = new Apfloat(zone.c());
        Apfloat xs = new Apfloat(zone.xs());
        Apfloat ys = new Apfloat(zone.ys());

        Apfloat x = org.getX();
        Apfloat y = org.getY();


        Apfloat lon, gamma, R, latIso;

        Apfloat xN = x.subtract(xs);
        Apfloat yN = y.subtract(ys);

        R = ApfloatMath.sqrt(xN.multiply(xN).add(yN.multiply(yN)));

        gamma = ApfloatMath.atan(xN.divide(yN.negate()));

        lon = lonMeridian.add(gamma.divide(n));

        latIso = Apfloat.ONE.negate().divide(n).multiply(ApfloatMath.log(ApfloatMath.abs(R.divide(C))));

        Apfloat lat = latitudeFromLatitudeISO(latIso, e, eps);

        return new LambertPoint(lon, lat, Apfloat.ZERO);
    }

 /*
 * ALGO0021 - Calcul de la grande Normale
 *
*/

    private static Apfloat lambertNormal(Apfloat lat, Apfloat a, Apfloat e) {

        Apfloat sintLat = ApfloatMath.sin(lat);
        return a.divide(ApfloatMath.sqrt(Apfloat.ONE.subtract(e.multiply(e).multiply(sintLat).multiply(sintLat))));
        //return a / sqrt(1 - e * e * sin(lat) * sin(lat));
    }

    /*
     * ALGO0009 - Transformations geographiques -> cartésiennes
     *
     */

    private static LambertPoint geographicToCartesian(Apfloat lon, Apfloat lat, Apfloat he, double a, double e){
        return geographicToCartesian(lon, lat, he, new Apfloat(a), new Apfloat(e));
    }
    private static LambertPoint geographicToCartesian(Apfloat lon, Apfloat lat, Apfloat he, Apfloat a, Apfloat e) {
        Apfloat N = lambertNormal(lat, a, e);

        LambertPoint pt = new LambertPoint(0, 0, 0);

        Apfloat cosLat = ApfloatMath.cos(lat);
        Apfloat sinLat = ApfloatMath.sin(lat);

        Apfloat cosLon = ApfloatMath.cos(lon);
        Apfloat sinLon = ApfloatMath.sin(lon);

        pt.setX((N.add(he).multiply(cosLat).multiply(cosLon)));
        pt.setY((N.add(he).multiply(cosLat).multiply(sinLon)));

        pt.setZ(N.multiply(Apfloat.ONE.subtract(e.multiply(e))).add(he).multiply(sinLat));

        return pt;

    }

    /*
 * ALGO0012 - Passage des coordonnées cartésiennes aux coordonnées géographiques
 */
    private static LambertPoint cartesianToGeographic(LambertPoint org, double meridien, double a, double e, double eps){
        return cartesianToGeographic(org, new Apfloat(meridien), new Apfloat(a), new Apfloat(e), new Apfloat(eps));
    }

    private static LambertPoint cartesianToGeographic(LambertPoint org, Apfloat meridien, Apfloat a, Apfloat e, Apfloat eps) {
        Apfloat x = org.getX(), y = org.getY(), z = org.getZ();

        Apfloat lon = meridien.add(ApfloatMath.atan(y.divide(x)));

        Apfloat module = ApfloatMath.sqrt(x.multiply(x).add(y.multiply(y)));

        Apfloat x2 = x.multiply(x);
        Apfloat y2 = y.multiply(y);
        Apfloat z2 = z.multiply(z);
        Apfloat e2 = e.multiply(e);

        Apfloat phi0 = ApfloatMath.atan(z.divide(module.multiply(Apfloat.ONE.subtract(a.multiply(e2))).divide(ApfloatMath.sqrt(x2.add(y2).add(z2)))));

        Apfloat cosPhi0 =  ApfloatMath.cos(phi0);
        Apfloat sinPhi0 =  ApfloatMath.sin(phi0);

        //double phi0 = atan(z / (module * (1 - (a * e * e) / sqrt(x * x + y * y + z * z))));
        Apfloat phiI = ApfloatMath.atan(z.divide(module).divide(Apfloat.ONE.subtract(a.multiply(e2).multiply(cosPhi0))).divide(module.multiply(ApfloatMath.sqrt(Apfloat.ONE.subtract(e2.multiply(sinPhi0).multiply(sinPhi0))))));
        //double phiI = atan(z / module / (1 - a * e * e * cos(phi0) / (module * sqrt(1 - e * e * sin(phi0) * sin(phi0)))));
        Apfloat delta = ApfloatMath.abs(phiI.subtract(phi0));
        while (delta.doubleValue() > eps.doubleValue()) {
            phi0 = phiI;

            cosPhi0 =  ApfloatMath.cos(phi0);
            sinPhi0 =  ApfloatMath.sin(phi0);

            phiI = ApfloatMath.atan(z.divide(module).divide(Apfloat.ONE.subtract(a.multiply(e2).multiply(cosPhi0))).divide(module.multiply(ApfloatMath.sqrt(Apfloat.ONE.subtract(e2.multiply(sinPhi0).multiply(sinPhi0))))));
            delta = ApfloatMath.abs(phiI.subtract(phi0));

        }

        Apfloat sinPhiI = ApfloatMath.sin(phiI);
        Apfloat he = module.divide(ApfloatMath.cos(phiI)).subtract(a.divide(ApfloatMath.sqrt(Apfloat.ONE.subtract(e2)).multiply(sinPhiI).multiply(sinPhiI)));

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
            LambertPoint pt1 = geographicToCartesian(new Apfloat(longitude - LON_MERID_GREENWICH), new Apfloat(latitude), Apfloat.ZERO, A_WGS84, E_WGS84);

            pt1.translate(168, 60, -320);

            LambertPoint pt2 = cartesianToGeographic(pt1, LON_MERID_PARIS, A_WGS84, E_WGS84, DEFAULT_EPS);

            return geographicToLambert(pt2.getY(), pt2.getX(), zone, new Apfloat(LON_MERID_PARIS), new Apfloat(E_WGS84));
        }
    }

    /*
        Method not really usefull, just to have two ways of doing the same conversion.
     */
    public static LambertPoint convertToLambertByAlg003(double latitude, double longitude, LambertZone zone) throws NotImplementedException {

        if (zone == Lambert93) {
            throw new NotImplementedException();
        } else {
            LambertPoint pt1 = geographicToCartesian(new Apfloat(longitude - LON_MERID_GREENWICH), new Apfloat(latitude), Apfloat.ZERO, A_WGS84, E_WGS84);

            pt1.translate(168, 60, -320);

            LambertPoint pt2 = cartesianToGeographic(pt1, LON_MERID_PARIS, A_WGS84, E_WGS84, DEFAULT_EPS);

            return geographicToLambertAlg003(pt2.getY(), pt2.getX(), zone, new Apfloat(LON_MERID_PARIS), new Apfloat(E_WGS84));
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


