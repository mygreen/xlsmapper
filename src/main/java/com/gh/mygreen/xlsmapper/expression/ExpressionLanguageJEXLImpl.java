package com.gh.mygreen.xlsmapper.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * 式言語「JEXL」の実装。
 * <p>利用する際には、JEXL v3.3以上のライブラリが必要です。
 * <p>JEXL v3.3から、ELインジェクション対策として、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a>によるEL式中で参照／実行可能なクラスを制限されます。
 * <p>独自のCellConverter / FiledProcessosrなどを実装しているが場合は、システムプロパティ {@literal xlsmapper.jexlPermissions} で指定することができます。
 *    複数指定する場合はカンマ区切りで指定します。
 * </p>
 *
 * @version 2.3
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageJEXLImpl.class);
    
    /**
     * 本ライブラリでJEXLからアクセス許可するパッケージ指定のパーミッション。
     */
    private static final String[] LIB_PERMISSIONS = 
        {"com.gh.mygreen.xlsmapper.*"};
    
    /**
     * 独自のJEXLのパーミッション。
     */
    private static final String[] USER_PERMISSIONS;
    static {
        String value = System.getProperty("xlsmapper.jexlPermissions");
        if(Utils.isNotEmpty(value)) {
            USER_PERMISSIONS = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(String::isEmpty)
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            
        } else {
            USER_PERMISSIONS = new String[] {};
        }
    }
    
    /**
     * JEXLのキャッシュサイズ。
     * <p>キャッシュする式の個数。
     */
    private static final int CACHE_SIZE = 256;
    
    private final JexlEngine jexlEngine;
    
    /**
     * JEXLのパーミッションを指定するコンストラクタ。
     * <p>関数として{@link CustomFunctions}が登録されており、接頭語 {@literal x:}で呼び出し可能です。
     * 
     * @param userPermissions JEXLのパーミッション。
     *        詳細は、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a> を参照。
     */
    public ExpressionLanguageJEXLImpl(final String... userPermissions) {
        this(Collections.emptyMap(), userPermissions);
        
    }
    
    /**
     * JEXLの独自のEL関数とパーミッションを指定するコンストラクタ。
     * <p>関数として{@link CustomFunctions}が登録されており、接頭語 {@literal f:}で呼び出し可能です。
     * 
     * @param userFunctions 独自のEL関数を指定します。keyは接頭語、valueはメソッドが定義されたクラス。
     * @param userPermissions JEXLのパーミッション。
     *        詳細は、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a> を参照。
     */
    public ExpressionLanguageJEXLImpl(final Map<String, Object> userFunctions, final String... userPermissions) {
        
        // EL式中で使用可能な関数の登録
        Map<String, Object> functions = new HashMap<>();
        functions.put("f", CustomFunctions.class);
        
        if (Utils.isNotEmpty(userFunctions)) {
            functions.putAll(userFunctions);
        }

        /*
         * EL式で本ライブラリのクラス／メソッドのアクセスを許可する。
         * ・CustomFunctions以外にも、CellConverter / FieldProcessorでも参照するため。
         * ・JEXLv3からサーバーサイド・テンプレート・インジェクション、コマンドインジェクション対策のために、
         *   許可されたクラスしか参照できなくなったため、本ライブラリをEL式から参照可能に許可する。
         */
        String[] concateedUserPermission = Utils.concat(USER_PERMISSIONS, userPermissions);
        JexlPermissions permissions = JexlPermissions.RESTRICTED
                .compose(Utils.concat(LIB_PERMISSIONS, concateedUserPermission));

        this.jexlEngine = new JexlBuilder()
                .namespaces(functions)
                .permissions(permissions)
                .silent(true)
                .strict(false)  // JEXLv2相当の文法にする。
                .cache(CACHE_SIZE)
                .create();
        
    }
    
    
    /**
     * {@link JexlEngine}を指定するコンストラクタ。
     * @param jexlEngine JEXLの処理エンジン。
     */
    public ExpressionLanguageJEXLImpl(final JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notNull(values, "values");
        
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating JEXL expression: {}", expression);
        }
        
        try {
            JexlExpression expr = jexlEngine.createExpression(expression);
            return expr.evaluate(new MapContext((Map<String, Object>) values));
            
        } catch(Exception ex) {
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with JEXL failed.", expression), ex);
        }
    }
    
    /**
     * {@link JexlEngine}を取得する。
     * @return
     */
    public JexlEngine getJexlEngine() {
        return jexlEngine;
    }
    
}
