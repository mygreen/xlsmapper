package com.gh.mygreen.xlsmapper.localization;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link ResourceBundleMessageResolver}のテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ResourceBundleMessageResolverTest {
    
    
    private ResourceBundleMessageResolver messageResolver;
    
    @Before
    public void setUp() throws Exception {
        this.messageResolver = new ResourceBundleMessageResolver();
    }
    
    /**
     * デフォルトの場合
     */
    @Test
    public void test_default() {
        
        Optional<String> message = messageResolver.getMessage("org.hibernate.validator.constraints.Email.message");
        assertThat(message).hasValue("{cellContext}の値'${empty(fieldFormatter) ? validatedValue : fieldFormatter.format(validatedValue)}'は、E-mail形式で設定してください。");
        
    }
    
    /**
     * 他のプロパティを追加
     */
    @Test
    public void test_addResourceBundle() {
        
        messageResolver.addResourceBundle(
                ResourceBundle.getBundle("com.gh.mygreen.xlsmapper.validation.beanvalidation.OtherElMessages", new EncodingControl("UTF-8")));
        
        Optional<String> message = messageResolver.getMessage("org.hibernate.validator.constraints.Email.message");
        assertThat(message).hasValue("[{sheetName}]:${empty label ? '' : label} - セル({cellAddress})はメールアドレスの形式(例:hoge@sample.co.jp)で値を入力してください。");
        
    }
}
