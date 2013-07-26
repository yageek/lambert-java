package net.yageek.lambert;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LambertTest {

    @Test
    public void ResultTest(){
        LambertPoint pt = Lambert.ConvertToWGS84Deg(994272.661,113467.422,LambertZone.LambertI);
        System.out.println("Point latitude:" + pt.getY() + " longitude:" + pt.getX());


    }


}
