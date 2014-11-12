package org.mygreen.xlsmapper;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mygreen.xlsmapper.cellconvert.LinkType;


public class POIUtilsTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testJudgeLinkType() {
        
        assertEquals(LinkType.DOCUMENT, POIUtils.judgeLinkType("!A1"));
        assertEquals(LinkType.DOCUMENT, POIUtils.judgeLinkType("Sheet(a)!A1"));
        
        assertEquals(LinkType.EMAIL, POIUtils.judgeLinkType("sample@sample.co.jp"));
        assertEquals(LinkType.EMAIL, POIUtils.judgeLinkType("mailto:sample@sample.co.jp"));
        
        assertEquals(LinkType.URL, POIUtils.judgeLinkType("http://sample.co.jp/"));
        assertEquals(LinkType.URL, POIUtils.judgeLinkType("http://sample.co.jp/?name1=1&name2=2"));
        
        assertEquals(LinkType.FILE, POIUtils.judgeLinkType("sample.xls"));
        assertEquals(LinkType.FILE, POIUtils.judgeLinkType("../sample.xls"));
        
    }
}
