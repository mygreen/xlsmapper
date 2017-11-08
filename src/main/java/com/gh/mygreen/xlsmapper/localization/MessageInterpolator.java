package com.gh.mygreen.xlsmapper.localization;

import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.expression.CustomFunctions;
import com.gh.mygreen.xlsmapper.expression.ExpressionEvaluationException;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguage;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageJEXLImpl;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.StackUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 名前付き変数のメッセージをフォーマットするクラス。
 * <p><code>{...}</code>の場合、変数を単純に置換する。</p>
 * <p><code>${...}</code>の場合、EL式を利用し処理する。</p>
 * <p>文字'$', '{', '}'は特殊文字のため、<code>\</code>でエスケープを行う。</p>
 * <p>ELのパーサは、{@link ExpressionLanguage}の実装クラスで切り替え可能。</p>
 * <p>{@link MessageResolver}を指定した場合、メッセージ中の変数<code>{...}</code>をメッセージ定義コードとして解決する。
 *   <br>ただし、メッセージ変数で指定されている変数が優先される。
 * </p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolator {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageInterpolator.class);
    
    private ExpressionLanguage expressionLanguage;
    
    public MessageInterpolator() {
        
        // EL式中で使用可能な関数の登録
        ExpressionLanguageJEXLImpl el = new ExpressionLanguageJEXLImpl();
        
        Map<String, Object> funcs = new HashMap<>(); 
        funcs.put("f", CustomFunctions.class);
        el.getJexlEngine().setFunctions(funcs);
        
        setExpressionLanguage(el);
        
    }
    
    /**
     * 式言語の実装を指定するコンストラクタ
     * @param expressionLanguage
     */
    public MessageInterpolator(final ExpressionLanguage expressionLanguage) {
        ArgUtils.notNull(expressionLanguage, "expressionLanguage");
        this.expressionLanguage = expressionLanguage;
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars) {
        return interpolate(message, vars, false);
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, boolean recursive) {
        return parse(message, vars, recursive, null);
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * <p>{@link MessageResolver}を指定した場合、メッセージ中の変数をメッセージコードとして解決します。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか
     * @param messageResolver メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, boolean recursive,
            final MessageResolver messageResolver) {
        return parse(message, vars, recursive, messageResolver);
    }
    
    /**
     * メッセージをパースし、変数に値を差し込み、EL式を評価する。
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param messageResolver メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    protected String parse(final String message, final Map<String, ?> vars, boolean recursive, final MessageResolver messageResolver) {
        
        // 評価したメッセージを格納するバッファ。
        final StringBuilder sb = new StringBuilder(message.length());
        
        /*
         * 変数とEL式を解析する際に使用する、スタック変数。
         * 式の開始が現れたらスタックに積み、式の終了が現れたらスタックから全てを取り出す。
         * スタックに積まれるのは、1つ文の変数またはEL式。
         */
        final LinkedList<String> stack = new LinkedList<String>();
        
        final int length = message.length();
        
        for(int i=0; i < length; i++) {
            final char c = message.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, "\\")) {
                // 直前の文字がエスケープ文字の場合、エスケープ文字として結合する。
                String escapedChar = StackUtils.popup(stack) + c;
                
                if(!stack.isEmpty()) {
                    // 取り出した後もスタックがある場合は、式の途中であるため、再度スタックに積む。
                    stack.push(escapedChar);
                    
                } else {
                    // 取り出した後にスタックがない場合は、エスケープを解除して通常の文字として積む。
                    sb.append(c);
                    
                }
                
            } else if(c == '\\') {
                // エスケープ文字の場合はスタックに積む。
                stack.push(String.valueOf(c));
                
            } else if(c == '$') {
                stack.push(String.valueOf(c));
                
            } else if(c == '{') {
                
                if(!stack.isEmpty() && !StackUtils.equalsAnyBottomElement(stack, new String[]{"$", "{"})) {
                    // スタックの先頭が式の開始形式でない場合
                    throw new MessageParseException(message, "expression not start with '{' or '$'");
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
                
            } else if(c == '}') {
                
                if(StackUtils.equalsAnyBottomElement(stack, new String[]{"{", "$"})) {
                    // 式の終わりの場合は、式を取り出し評価する。
                    String expression = StackUtils.popupAndConcat(stack) + c;
                    
                    // エスケープを解除する
                    expression = Utils.removeEscapeChar(expression, '\\');
                    
                    String result = evaluate(expression, vars, recursive, messageResolver);
                    sb.append(result);
                    
                } else {
                    sb.append(c);
                    
                }
                
            } else {
                
                if(stack.isEmpty()) {
                    sb.append(c);
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
            }
            
        }
        
        if(!stack.isEmpty()) {
            String val = StackUtils.popupAndConcat(stack);
            val = Utils.removeEscapeChar(val, '\\');
            sb.append(val);
        }
        
        return sb.toString();
    }
    
    private String evaluate(final String expression, final Map<String, ?> values, final boolean recursive,
            final MessageResolver messageResolver) {
        
        if(expression.startsWith("{")) {
            // 変数の置換の場合
            final String varName = expression.substring(1, expression.length()-1);
            
            if(values.containsKey(varName)) {
                // 該当するキーが存在する場合
                final Object value = values.get(varName);
                final String eval = (value == null) ? "" : value.toString();
                if(!eval.isEmpty() && recursive) {
                    return parse(eval, values, recursive, messageResolver);
                } else {
                    return eval;
                }
                
            } else if(messageResolver != null) {
                // メッセージコードをとして解決をする。
                final Optional<String> eval = messageResolver.getMessage(varName);
                if(!eval.isPresent()) {
                    // 該当するキーが存在しない場合は、値をそのまま返す。
                    return String.format("{%s}", varName);
                }
                
                if(recursive) {
                    return parse(eval.get(), values, recursive, messageResolver);
                } else {
                    return eval.get();
                }
                
            } else {
                // 該当するキーが存在しない場合は、値をそのまま返す。
                return expression.toString();
            }
            
        } else if(expression.startsWith("${")) {
            // EL式で処理する
            final String expr = expression.substring(2, expression.length()-1);
            final String eval = evaluateExpression(expr, values);
            if(recursive) {
                return parse(eval, values, recursive, messageResolver);
            } else {
                return eval;
            }
            
        }
        
        throw new MessageParseException(expression, "not support expression.");
        
    }
    
    /**
     * EL式を評価する。
     * @param expression EL式
     * @param values EL式中の変数。
     * @return 評価した式。
     * @throws ExpressionEvaluationException 
     */
    protected String evaluateExpression(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
        
        final Map<String, Object> context = new LinkedHashMap<String, Object>();
        context.putAll(values);
        
        // フォーマッターの追加
        context.computeIfAbsent("formatter", key -> new Formatter());
        
        /*
         * JEXLで存在しない変数名の場合、nullが帰ってくるため、null判定を行う。
         */
        Object eval = expressionLanguage.evaluate(expression, context);
        String value = eval == null ? "" : eval.toString();
        
        if(logger.isTraceEnabled()) {
            logger.trace("evaluate expression language: expression='{}' ===> value='{}'", expression, value);
        }
        
        return value;
    }
    
    /**
     * EL式を解析する実装クラスを取得する。
     * @return
     */
    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }
    
    /**
     * EL式を解析する実装クラスを設定する。
     * @param expressionLanguage EL式の解析するクラスの実装。
     */
    public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
    }
    
}
