package com.gh.mygreen.xlsmapper.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.util.PropertyPathTokenizer;
import com.gh.mygreen.xlsmapper.util.PropertyPath.Token;

/**
 * {@link PropertyPathTokenizer}のテスタ
 * 
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyPathTokenizerTest {
    
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
    public void testParse_single_path() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("name").getPathAsToken();
        assertThat(tokens, is(hasSize(1)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("name"));
        
        
    }
    
    @Test
    public void testParse_nested_path() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("person.name").getPathAsToken();
        assertThat(tokens, is(hasSize(3)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("person"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Separator.class)));
        assertThat(token.getToken(), is("."));
        
        token = tokens.get(2);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("name"));
        
    }
    
    @Test
    public void testParse_array() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("array[0]").getPathAsToken();
        assertThat(tokens, is(hasSize(2)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("array"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[0]"));
        assertThat(((Token.Key)token).getKey(), is("0"));
        
    }
    
    @Test
    public void testParse_map() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("map[abc]").getPathAsToken();
        assertThat(tokens, is(hasSize(2)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("map"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[abc]"));
        assertThat(((Token.Key)token).getKey(), is("abc"));
        
    }
    
    @Test
    public void testParse_map_escape() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("map[\\]abc]").getPathAsToken();
        assertThat(tokens, is(hasSize(2)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("map"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[\\]abc]"));
        assertThat(((Token.Key)token).getKey(), is("]abc"));
        
    }
    
    @Test
    public void testParse_map_escape2() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("map[\\\\]abc]").getPathAsToken();
        assertThat(tokens, is(hasSize(2)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("map"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[\\\\]abc]"));
        assertThat(((Token.Key)token).getKey(), is("\\]abc"));
        
    }
    
    @Test
    public void testParse_nested_array() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("data[0][abc]").getPathAsToken();
        assertThat(tokens, is(hasSize(3)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("data"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[0]"));
        assertThat(((Token.Key)token).getKey(), is("0"));
        
        token = tokens.get(2);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[abc]"));
        assertThat(((Token.Key)token).getKey(), is("abc"));
        
    }
    
    @Test
    public void testParse_nested_array2() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse("data[0].card[abc]").getPathAsToken();
        assertThat(tokens, is(hasSize(5)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("data"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[0]"));
        assertThat(((Token.Key)token).getKey(), is("0"));
        
        token = tokens.get(2);
        assertThat(token, is(instanceOf(Token.Separator.class)));
        assertThat(token.getToken(), is("."));
        
        token = tokens.get(3);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("card"));
        
        token = tokens.get(4);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[abc]"));
        assertThat(((Token.Key)token).getKey(), is("abc"));
        
    }
    
    @Test
    public void testParse_space() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse(" name ").getPathAsToken();
        assertThat(tokens, is(hasSize(1)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("name"));
        
        
    }
    
    @Test
    public void testParse_space_nested() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse(" person . name ").getPathAsToken();
        assertThat(tokens, is(hasSize(3)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("person"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Separator.class)));
        assertThat(token.getToken(), is("."));
        
        token = tokens.get(2);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("name"));
        
        
    }
    
    @Test
    public void testParse_space_array() {
        
        PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();
        
        List<Token> tokens = tokenizer.parse(" array [ 0 ] ").getPathAsToken();
        assertThat(tokens, is(hasSize(2)));
        
        Token token;
        
        token = tokens.get(0);
        assertThat(token, is(instanceOf(Token.Name.class)));
        assertThat(token.getToken(), is("array"));
        
        token = tokens.get(1);
        assertThat(token, is(instanceOf(Token.Key.class)));
        assertThat(token.getToken(), is("[ 0 ]"));
        assertThat(((Token.Key)token).getKey(), is(" 0 "));
        
    }
}
