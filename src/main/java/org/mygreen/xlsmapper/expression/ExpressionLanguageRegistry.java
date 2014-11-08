package org.mygreen.xlsmapper.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mygreen.xlsmapper.ArgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 式言語の実装を管理するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageRegistry.class);
    
    private final Map<String, ExpressionLanguage> elCache = new ConcurrentHashMap<String, ExpressionLanguage>();
    
    protected ExpressionLanguage initializeDefaultEl(final String languageId) {
        
        if("el".equals(languageId) && isClassPresent("javax.el.ValueExpression")) {
            return registerExpressionLanguage("el", new ExpressionLanguageELImpl());
            
        } else if("ognl".equals(languageId) && isClassPresent("ognl.Ognl")) {
            return registerExpressionLanguage("ognl", new ExpressionLanguageOGNLImpl());
            
        } else if("mvel".equals(languageId) && isClassPresent("org.mvel2.MVEL")) {
            return registerExpressionLanguage("mvel", new ExpressionLanguageMVELImpl());
            
        } else if("spel".equals(languageId) && isClassPresent("org.springframework.expression.Expression") ) {
            return registerExpressionLanguage("spel", new ExpressionLanguageSpELImpl());
            
        }
        
        return null;
    }
    
    /**
     * 指定した式言語IDに対する実装を取得する。
     * @param languageId 式言語のID。
     * @return 式言語の実装。
     */
    public synchronized ExpressionLanguage getExpressionLanguage(final String languageId) {
        
        ArgUtils.notEmpty(languageId, "languageId");
        
        ExpressionLanguage el = elCache.get(languageId);
        
        if(el == null) {
            el = initializeDefaultEl(languageId);
        }
        
        if(el == null) {
            throw new IllegalArgumentException(String.format("not availavled Expression Language '%s'.", languageId));
        }
        return el;
    }
    
    /**
     * 式言語の実装を登録する。
     * @param languageId 言語ID。
     * @param impl 式言語の実装
     * @return 登録した式言語の実装。
     * @throws IllegalArgumentException languageId is empty.
     * @throws IllegalArgumentException impl is null.
     */
    public ExpressionLanguage registerExpressionLanguage(final String languageId, final ExpressionLanguage impl) {
        
        ArgUtils.notEmpty(languageId, "languageId");
        ArgUtils.notNull("impl", "impl");
        
        if(logger.isInfoEnabled()) {
            logger.info("Expression language '{}' registerd: {}", languageId, impl);
        }
        
        elCache.put(languageId, impl);
        return impl;
        
    }
    
    /**
     * 指定したクラス名が、クラスパス上または読み込まれているかチェックする。
     * @param className
     * @return
     */
    private boolean isClassPresent(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e){
            return false;
        }
    }
}
