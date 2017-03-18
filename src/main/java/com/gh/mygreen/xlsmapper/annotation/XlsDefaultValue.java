package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;

/**
 * 初期値を指定するためのアノテーション。
 * 読み込み時、または書き込み時に値がnullの時に、代わりにとなる値を指定します。
 * 
 * <ul>
 *   <li>日付などの書式がある場合、専用のアノテーションで指定した書式{@link XlsDateConverter#javaPattern()}を元に、
 *       文字列をそのオブジェクトに変換し処理します。
 *   </li>
 *   <li>指定したデフォルト値がマッピング先の型として不正な場合は、
 *       通常の型変換エラーと同様に、例外 {@link TypeBindException} がスローされます。
 *   </li>
 *   <li>読み込みの際には、プリミティブ型に対して{@link XlsDefaultValue}を設定していても、その型の初期値が設定さるため注意してください。
 *       例えば、int型は0、doubleは0.0、booleanはfalse。char型は、'{@literal \}u0000'。
 *       <br>プリミティブ型のラッパークラスや参照型の場合は、nullが設定されます。
 *   </li>
 *   <li>char型にマッピングする場合、デフォルト値が2文字以上でも、先頭の位置文字がマッピングされます。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="ID")}
 *     {@literal @XlsDefaultValue("-1")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="更新日時")}
 *     {@literal @XlsDefaultValue("2010/01/01"} // 属性javaPatternで指定した書式に沿った値を指定します。
 *     {@literal @XlsDateConverter(javaPattern="yyyy/MM/dd")}
 *     private Date updateTime;
 * 
 * }
 * </code></pre>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsDefaultValue {
    
    /**
     * 初期値を指定します。
     * @return 設定した初期値。
     */
    String value();
    
}
