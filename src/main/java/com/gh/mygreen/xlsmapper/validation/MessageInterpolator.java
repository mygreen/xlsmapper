package com.gh.mygreen.xlsmapper.validation;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.expression.ExpressionEvaluationException;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguage;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageELImpl;


/**
 * 名前付き変数のメッセージをフォーマットするクラス。
 * <p><code>{...}</code>の場合、変数を単純に置換する。
 * <p><code>${...}</code>の場合、EL式を利用し処理する。
 * <p>文字'$', '{', '}'は特殊文字のため、<code>\</code>でエスケープを行う。
 * <p>ELのパーサは、{@link ExpressionLanguage}の実装クラスで切り替え可能。
 * <p>{@link MessageResolver}を指定した場合、メッセージ中の変数<code>{...}</code>をメッセージ定義コードとして解決する。
 *    ただし、メッセージ変数で指定されている変数が優先される。
 * 
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolator {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageInterpolator.class);
    
    private ExpressionLanguage expressionLanguage = new ExpressionLanguageELImpl();
    
    public MessageInterpolator() {
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
        final Stack<Character> stack = new Stack<Character>();
        
        int pos = 0;
        int end = message.length();
        
        while(pos < end ) {
            
            char c = message.charAt(pos);
            if(c == '$') {
                if(!stack.isEmpty()) {
                    pushConsiderEscape(stack, c);
                    
                } else if(stack.isEmpty() && notEqualsBufferLast(sb, '\\')) {
                    stack.push(c);
                    
                } else {
                    appendCondiderEscape(sb, c);
                }
                
            } else if(c == '{') {
                if(stack.isEmpty() && equalsBufferLast(sb, '\\')) {
                    appendCondiderEscape(sb, c);
                } else {
                    if(!validateExpressionFormatWithStart(stack, c)) {
                        throw new MessageParseException(message, "expression not start with '$'");
                    }
                    pushConsiderEscape(stack, c);
                }
                
            } else if(c == '}') {
                if(stack.isEmpty()) {
                    sb.append(c);
                } else {
                    final boolean endExp = !stack.peek().equals('\\');
                    pushConsiderEscape(stack, c);
                    
                    if(endExp) {
                        // 式が終わりの場合、式を取り出し評価する。
                        final String value = evaluate(stack, vars, recursive, messageResolver);
                        sb.append(value);
                        stack.clear();
                    }
                }
                
            } else {
                if(stack.isEmpty()) {
                    sb.append(c);
                } else {
                    stack.push(c);
                }
                
            }
            
            pos++;
        }
        
        if(!stack.isEmpty()) {
            throw new MessageParseException(message, "not found '}'");
        }
        
        return sb.toString();
    }
    
    /**
     * 評価中のメッセージの最後の文字が、引数で指定した値'value'かどうか検証する。
     * <p>メッセージが0文字の場合は、falseを返す。
     * @param sb
     * @param value
     * @return メッセージの最後の文字が引数'value'で指定した値と等しい場合。
     */
    private static boolean equalsBufferLast(final StringBuilder sb, final char value) {
        if(sb.length() > 0) {
            final int length = sb.length();
            return sb.charAt(length -1) == value;
            
        } else {
            return false;
        }
    }
    
    /**
     * 評価中のメッセージの最後の文字が、引数で指定した値'value'でないかどうか検証する。
     * {@link #equalsBufferLast(StringBuilder, char)}の否定。
     * @param sb
     * @param value
     * @return メッセージの最後の文字が引数'value'で指定した値と異なる場合。
     */
    private static boolean notEqualsBufferLast(final StringBuilder sb, final char value) {
        return !equalsBufferLast(sb, value);
    }
    
    private static void appendCondiderEscape(final StringBuilder sb, final char value) {
        
        if(sb.length() == 0) {
            sb.append(value);
            
        } else if(equalsBufferLast(sb, '\\')) {
            
            final int opt = sb.length() -1;
            sb.setCharAt(opt, value);
            
        } else {
            sb.append(value);
        }
        
    }
    
    private static void pushConsiderEscape(final Stack<Character> stack, final char value) {
        
        if(stack.isEmpty()) {
            stack.push(value);
            
        } else if(stack.peek().equals('\\')) {
            stack.pop();
            stack.push(value);
        } else {
            stack.push(value);
        }
        
    }
    
    /**
     * Stackに追加した、評価対象の式が正しいかチェックする。
     * <p>文字列'$' or '{'で始まるかチェックする。
     * @param stack
     * @param value
     * @return
     */
    private static boolean validateExpressionFormatWithStart(final Stack<Character> stack, final char value) {
        if(stack.isEmpty()) {
            return true;
        }
        
        if(stack.size() == 1) {
            final char first = stack.firstElement();
            if(first != '$') {
                return false;
            } else if(value == '{') {
                return true;
            }
            return false;
        }
        
        return true;
        
    }
    
    /**
     * メッセージ中の変数またはEL式を評価する。
     * @param stack
     * @param values
     * @param recursive
     * @param messageResolver
     * @return
     */
    private String evaluate(final Stack<Character> stack, final Map<String, ?> values, final boolean recursive,
            final MessageResolver messageResolver) {
        
        if(stack.firstElement().equals('{')) {
            // 変数の置換の場合
            final String varName = convertStackToString(stack, 1, stack.size() -1);
            
            if(values.containsKey(varName)) {
                // 該当するキーが存在する場合
                final Object value = values.get(varName);
                final String eval = value == null ? "" : value.toString();
                if(!eval.isEmpty() && recursive) {
                    return parse(eval, values, recursive, messageResolver);
                } else {
                    return eval;
                }
                
            } else if(messageResolver != null) {
                // メッセージコードをとして解決をする。
                final String eval = messageResolver.getMessage(varName);
                if(eval == null) {
                    // 該当するキーが存在しない場合は、値をそのまま返す。
                    return String.format("{%s}", varName);
                }
                
                if(recursive) {
                    return parse(eval, values, recursive, messageResolver);
                } else {
                    return eval;
                }
                
            } else {
                // 該当するキーが存在しない場合は、値をそのまま返す。
                return String.format("{%s}", varName);
            }
            
        } else if(stack.firstElement().equals('$')) {
            // EL式で処理する
            final String expr = convertStackToString(stack, 2, stack.size() -1);
            final String eval = evaluateExpression(expr, values);
            if(recursive) {
                return parse(eval, values, recursive, messageResolver);
            } else {
                return eval;
            }
        }
        
        throw new MessageParseException(stack.toString(), "not support expression.");
        
    }
    
    private String convertStackToString(Stack<Character> stack, final int beginIndex, final int endIndex) {
        
        final StringBuilder buff = new StringBuilder();
        for(int i=beginIndex; i < endIndex; i++) {
            buff.append(stack.get(i));
        }
        
        return buff.toString();
        
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
        if(!context.containsKey("formatter")) {
            context.put("formatter", new Formatter());
        }
        
        final String value = expressionLanguage.evaluate(expression, context).toString();
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
