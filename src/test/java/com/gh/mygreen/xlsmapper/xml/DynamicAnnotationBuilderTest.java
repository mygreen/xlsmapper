package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;

/**
 * {@link DynamicAnnotationBuilder}のテスタ
 * 
 * @version 0.5
 * @since 0.1
 * @author T.TSUCHIE
 *
 */
public class DynamicAnnotationBuilderTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_buildAnnotation1() throws Exception {
        
        AnnotationInfo info = new AnnotationInfo();
        info.addAttribute("row", "10");
        info.addAttribute("column", "99");
        
        DynamicAnnotationBuilder builder = DynamicAnnotationBuilder.getInstance();
        Annotation ann = builder.buildAnnotation(XlsCell.class, info);
        
        assertThat(ann.annotationType(), is(typeCompatibleWith(XlsCell.class)));
        
        XlsCell cell = (XlsCell) ann;
        assertThat(cell.row(), is(10));
        assertThat(cell.column(), is(99));
        
    }
    
    @Test
    public void test_buildAnnotation2() throws Exception {
        
        DynamicAnnotationBuilder builder = DynamicAnnotationBuilder.getInstance();
        Annotation ann =  builder.buildAnnotation(XlsHorizontalRecords.class, new AnnotationInfo());
        
        assertThat(ann.annotationType(), is(typeCompatibleWith(XlsHorizontalRecords.class)));
        
        XlsHorizontalRecords records = (XlsHorizontalRecords) ann;
        
        assertThat(records.optional(), is(false));
        assertThat(records.tableLabel(), is(""));
        assertThat(records.terminateLabel(), is(""));
        assertThat(records.headerRow(), is(-1));
        assertThat(records.headerColumn(), is(-1));
        assertThat(records.recordClass(), is(typeCompatibleWith(Object.class)));
        assertThat(records.terminal(), is(RecordTerminal.Empty));
        assertThat(records.range(), is(1));
        assertThat(records.bottom(), is(1));
        
        assertThat(records.headerAddress(), is(""));
        
    }
    
    
}
