package net.yageek.lambert;

import static junit.framework.Assert.assertNotNull;
import static net.yageek.lambert.LambertZone.*;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@RunWith(JUnit4.class)
public class LambertTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After

    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void ResultTest() {
        LambertPoint pt = Lambert.convertToWGS84Deg(994272.661, 113467.422, LambertZone.LambertI);
        System.out.println("Point latitude:" + pt.getY() + " longitude:" + pt.getX());


    }

    @Test
    public void Lambert93BugTest() {
        LambertPoint pt = Lambert.convertToWGS84Deg(668832.5384, 6950138.7285, LambertZone.Lambert93);
        assertEquals(2.56865, pt.getX(), 0.0001);
        assertEquals(49.64961, pt.getY(), 0.0001);
    }

    @Test
    public void LambertIIExtendedToWgs84Test() {
        LambertPoint pt = Lambert.convertToWGS84Deg(618115, 2430676, LambertZone.LambertIIExtended);
        assertEquals(2.58331732871, pt.getX(), 0.0001);  // Longitude 2.5832231178521186
        assertEquals(48.8741427818, pt.getY(), 0.0001);  // Latitude 48.87412734248018
    }


    @Test
    public void LambertAlg0001Test() {
        double lat = Lambert.latitudeISOFromLat(0.87266462600, 0.08199188998);
        assertNotNull(lat);
        assertEquals(1.00552653649, lat, 0.0001);

        double lat2 = Lambert.latitudeISOFromLat(-0.30000000000, 0.08199188998);
        assertNotNull(lat2);
        assertEquals(-0.30261690063, lat2, 0.0001);
    }

    /*
    Avec les données de tests pour valider l'algorythme
    http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf
    ALG0003
     */
    @Test
    public void LambertAlg0003Test() {
        double latitude = 0.87266462600;
        double longitude = 0.14551209900;

        LambertPoint lambertPoint = Lambert.geographicToLambertAlg003(latitude, longitude, LambertZone.LambertI, LambertZone.LON_MERID_GREENWICH, LambertZone.E_CLARK_IGN);

        assertEquals(1029705.0818, lambertPoint.getX(), 0.0001);
        assertEquals(272723.84730, lambertPoint.getY(), 0.0001);
    }

    /*
    Calcul par Algorythme ALG003
     */
    @Test
    public void ConvertWGS84ToLambertByAlg0003Test() {
        double latitude = 48.87412734248018; // 48.8741427818;
        double latitude2 = latitude * 400 / 360; // Grad
        double radLat = Math.toRadians(latitude);
        double longitude = 2.5832231178521186; //2.58331732871;
        double longitude2 = longitude * 400 / 360; // Grad
        double radLong = Math.toRadians(longitude);

        LambertPoint lambertPoint = Lambert.convertToLambertByAlg003(radLat, radLong, LambertZone.LambertIIExtended);

        assertEquals(618115, lambertPoint.getX(), 1);
        assertEquals(2430676, lambertPoint.getY(), 1);
    }


    /*
        Methode provenant de
        http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
        3.4 attaquée en direct.
        avec les valeurs calculées précedemment sans la translation +towgs84=-168,-60,320,0,0,0,0
    */
    @Test
    public void LambertGeographicToLambertTest() {
        double latitude = 48.8741427818;
        double radLat = Math.toRadians(latitude);
        double longitude = 2.58331732871;
        double radLong = Math.toRadians(longitude);

        LambertPoint lambertPoint = Lambert.geographicToLambert(radLat, radLong, LambertZone.LambertIIExtended, LambertZone.LON_MERID_GREENWICH, LambertZone.E_CLARK_IGN);

        assertEquals(618062, lambertPoint.getX(), 1);
        assertEquals(2430668, lambertPoint.getY(), 1);
    }

    /*
        Methode provenant de
        http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
        3.4 attaquée en direct
        avec les données de test
    */
    @Test
    public void LambertConvertNTFToLambertTest() {
        double latitude = 51.8072313; // Grad
        double radLat = Math.toRadians(latitude * 360d / 400d); // Deg before Rad
        double longitude = 0.4721669; //Grad
        double radLong = Math.toRadians(longitude * 360d / 400d); // Deg before Rad

        LambertPoint lambertPoint = Lambert.geographicToLambert(radLat, radLong, LambertZone.LambertII, LON_MERID_PARIS, E_CLARK_IGN);

        assertEquals(632542.058, lambertPoint.getX(), 0.001);
        assertEquals(180804.145, lambertPoint.getY(), 0.01);
    }

    /*
        Validation de la méthode
        http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf
        3.3 attaquée en direct
     */
    @Test
    public void LamberConvertLambertToNTFTest() {
        double X = 1029705.083;
        double Y = 272723.849;

        LambertPoint lambertPoint = Lambert.lambertToGeographic(new LambertPoint(X, Y, 0), LambertZone.LambertI, LON_MERID_PARIS, E_CLARK_IGN, DEFAULT_EPS);

        assertEquals(0.145512099, lambertPoint.getX(), 10); // Longitude en rad
        assertEquals(0.872664626, lambertPoint.getY(), 10); // Latitude en trad
    }

    /*
        Test avec translation avant conversion
        .translate(168,60,-320);
    */
    @Test
    public void LambertConvertToLambertTest() {
        //double latitude = 48.8741427818;
        double latitude = 48.87412734248018; // 48.8741427818;
        double radLat = Math.toRadians(latitude);
        //double longitude = 2.58331732871;
        double longitude = 2.5832231178521186; //2.58331732871;
        double radLong = Math.toRadians(longitude);

        LambertPoint lambertPoint = Lambert.convertToLambert(radLat, radLong, LambertZone.LambertIIExtended);

        assertEquals(618115, lambertPoint.getX(), 1);
        assertEquals(2430676, lambertPoint.getY(), 1);
    }

}
