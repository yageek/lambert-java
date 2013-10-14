package net.yageek.lambert;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
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
    public void ResultTest(){
        LambertPoint pt = Lambert.convertToWGS84Deg(994272.661, 113467.422, LambertZone.LambertI);
        System.out.println("Point latitude:" + pt.getY() + " longitude:" + pt.getX());


    }

    @Test
    public void Lamber93BugTest(){
        LambertPoint pt = Lambert.convertToWGS84Deg(668832.5384, 6950138.7285,LambertZone.Lambert93);
        assertEquals(2.56865,pt.getX(),0.0001);
        assertEquals(49.64961,pt.getY(),0.0001);
    }


}
