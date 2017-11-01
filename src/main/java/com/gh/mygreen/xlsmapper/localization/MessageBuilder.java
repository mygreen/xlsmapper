package com.gh.mygreen.xlsmapper.localization;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * エラーメッセージを組み立てたりするためのユーティリティクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class MessageBuilder {
    
    private static final MessageResolver MESSAGE_RESOLVER = new ResourceBundleMessageResolver("com.gh.mygreen.xlsmapper.localization.Messages", false);
    private static final MessageInterpolator MESSAGE_INTERPOLATOR = new MessageInterpolator();
    
    private final String code;
    
    private Map<String, Object> vars = new HashMap<>();
    
    /**
     * メッセージコードを指定してインスタンスを作成します。
     * @param code メッセージコード
     * @throws IllegalArgumentException {@literal code is null or empty.}
     */
    public MessageBuilder(final String code) {
        ArgUtils.notEmpty(code, "code");
        this.code = code;
    }
    
    /**
     * メッセージ変数を追加する。
     * @param key 変数名
     * @param value 値
     * @return 自身のインスタンス
     */
    public MessageBuilder var(final String key, final Object value) {
        vars.put(key, value);
        return this;
    }
    
    /**
     * メッセージ変数として配列を追加する。
     * @param key 変数名
     * @param values 値
     * @return 自身のインスタンス
     */
    public MessageBuilder varWithArrays(final String key, final Object... values) {
        vars.put(key, values);
        return this;
    }
    
    /**
     * メッセージ変数としてアノテーション名を追加する。
     * @param key 変数名
     * @param annoClass アノテーションのクラス名
     * @return 自身のインスタンス
     */
    public MessageBuilder varWithAnno(final String key, final Class<? extends Annotation> annoClass) {
        return var(key, "@" + annoClass.getSimpleName());
    }
    
    /**
     * メッセージ変数としてクラス名を追加する。
     * <p>クラス名は、FQCNの形式</p>
     * @param key 変数名
     * @param clazz クラスタイプ
     * @return 自身のインスタンス
     */
    public MessageBuilder varWithClass(final String key, final Class<?> clazz) {
        
        final String className;
        if(clazz.isArray()) {
            // 配列の場合
            Class<?> elementType = clazz.getComponentType();
            className = elementType.getName() + "[]";
            
        } else {
            className = clazz.getName();
            
        }
        
        return var(key, className);
    }
    
    /**
     * メッセージ変数として列挙型を追加する。
     * @param key 変数名
     * @param enums 列挙型の要素
     * @return 自身のタイプ
     */
    public MessageBuilder varWithEnum(final String key, final Enum<?> enums) {
        
        vars.put(key, enums.getClass().getSimpleName() + "#" + enums.name());
        return this;
        
    }
    
    /**
     * メッセージをフォーマットして値を取得します。
     * <p>変換したメッセージに対しても再帰的に処理しません。</p>
     * @return フォーマットしたメッセージ
     * @throws IllegalArgumentException 指定したメッセージコードが見つからない場合
     */
    public String format() {
        
        return format(false);
    }
    
    /**
     * メッセージをフォーマットして値を取得します。
     * @param recusrsive 変換したメッセージに対しても再帰的に処理するかどうか。
     * @return フォーマットしたメッセージ
     * @throws IllegalArgumentException 指定したメッセージコードが見つからない場合
     */
    public String format(final boolean recusrsive) {
        
        final String message = MESSAGE_RESOLVER.getMessage(code)
                .orElseThrow(() -> new IllegalStateException(String.format("not found message key=%s", code)));
        
        return MESSAGE_INTERPOLATOR.interpolate(message, vars, recusrsive, MESSAGE_RESOLVER);
    }
    
    public static MessageBuilder create(final String code) {
        
        return new MessageBuilder(code);
        
    }
}
