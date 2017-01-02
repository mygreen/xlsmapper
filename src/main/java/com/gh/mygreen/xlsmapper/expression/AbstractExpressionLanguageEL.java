package com.gh.mygreen.xlsmapper.expression;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;

import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;

/**
 * EL式を利用する際のベースとなる抽象クラス。
 * 
 * @since 1.6
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractExpressionLanguageEL implements ExpressionLanguage {
    
    /** TLDファイルの定義内容 */
    protected List<Taglib> taglibList = new ArrayList<>();
    
    /**
     * カスタムタグの定義ファイル「TLD（Tag Library Defenitioin）」を登録する。
     * <p>{@link TldLoader}クラスで読み込む。
     * 
     * @since 1.5
     * @param taglib カスタムタグの定義内容。
     */
    public void register(final Taglib taglib) {
        this.taglibList.add(taglib);
    }
    
    /**
     * 登録されているEL式中の変数（Bean）が、{@link Formatter}かどうか判定する。
     * <p>{@link Formatter}中のメソッド{@code format(...)}メソッドは、オーバロードで複数のメソッドが定義されいるが、
     *   EL式はメソッドのオーバーロードはサポートされていない。
     *   <br>そのため、{@link FormatterWrapper}でラップして、オーバーロードなしのメソッドする。
     * </p>
     * @param key 判定対象のキー値
     * @param value 判定対象の値のインスタンス
     * @return キー名が{@link RootResolver#FORMATTER}(formatter)と一致し、かつ、valueのインスタンスが{@link Formatter}の場合、trueを返します。
     */
    protected boolean isFormatter(final String key, final Object value) {
        if(!RootResolver.FORMATTER.equals(key)) {
            return false;
        }
        
        if(value instanceof Formatter) {
            return true;
        }
        
        return false;
    }
}
