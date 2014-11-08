package org.mygreen.xlsmapper;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.mygreen.xlsmapper.Utils;


public class UtilsTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testCapitalize() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testUncapitalize() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testIsEmpty() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testIsNotEmpty() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testParseCellAddress() {
        Point p1 = Utils.parseCellAddress("A1");
        assertEquals(0, p1.x);
        assertEquals(0, p1.y);
        
        Point p2 = Utils.parseCellAddress("AX232");
        assertEquals(49, p2.x);
        assertEquals(231, p2.y);
        
        Point p3 = Utils.parseCellAddress("a32A132");
        assertNull(p3);
    }
}
