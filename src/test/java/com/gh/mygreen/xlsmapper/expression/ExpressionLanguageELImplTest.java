package com.gh.mygreen.xlsmapper.expression;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;

/**
 * {@link ExpressionLanguageELImpl}のテスタ
 * 
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageELImplTest {
    
    private ExpressionLanguageELImpl el;
    
    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageELImpl();
    }
    
    /**
     * formatterのテスト
     */
    @Test
    public void testEvaluate_format() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("2015/04/15"));
        
    }
    
    /**
     * EL関数の呼び出しテスト - 委譲ができているか
     * 
     */
    @Test
    public void test_function() throws Exception {
        
        // EL関数の登録
        TldLoader loader = new TldLoader();
        Taglib taglib = loader.load(ExpressionLanguageELImplTest.class.getResourceAsStream("/com/gh/mygreen/xlsmapper/expression/xlsmapper.tld"));
        el.register(taglib);
        
        String expression = "x:colToAlpha(columnNumber)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 1);
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("A"));
        
    }

}
