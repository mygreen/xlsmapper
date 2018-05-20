package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * シートのマッピング対象のルートクラス（JavaBeans）に付与するアノテーション。
 * <p>マッピング対象のシートをシート番号、シート名、シート名に対する正規表現のいずれかで指定します。</p>
 *
 *
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsSheet {

    /**
     * シート名を指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *   ...
     * }
     * </code></pre>
     *
     * @return シート名
     */
    String name() default "";

    /**
     * シート名を正規表現で指定します。
     * <p>同じ形式の複数のシートを同時にマッピングする際に指定します。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(regex="Sheet_[0-9]+")}
     * public class SampleSheet {
     *    ・・・
     * }
     * </code></pre>
     *
     * <p>書き込み時に正規表現によるマッピングを行う際には、アノテーション{@link XlsSheetName}を利用して、
     *    一意に関連づける必要があります。</p>
     * <pre class="highlight"><code class="java">
     * // マッピング用クラスの定義
     * {@literal @XlsSheet(regex="Sheet_[0-9]+")}
     * public class SampleSheet {
     *
     *    // シート名をマッピングするフィールド
     *    {@literal @XlsSheetName}
     *    private String sheetName;
     *    ・・・
     *
     * }
     *
     * // 書き込み時に、シート名を設定して、一意に関連づけます。
     * SampleSheet sheet1 = new SampleSheet();
     * sheet1.sheetName = "Sheet_1"; // シート名の設定
     *
     * SampleSheet sheet2 = new SampleSheet();
     * sheet2.sheetName = "Sheet_2"; // シート名の設定
     *
     * SampleSheet sheet3 = new SampleSheet();
     * sheet3.sheetName = "Sheet_3"; // シート名の設定
     *
     * // 複数のシートの書き込み
     * XlsMapper xlsMapper = new XlsMapper();
     * xlsMapper.saveMultiple(new FileInputStream("template.xls"),
     *   new FileOutputStream("out.xls"),
     *   new Object[]{sheet1, sheet2, sheet3}
     * );
     * </code></pre>
     *
     * @return シート名の正規表現
     */
    String regex() default "";

    /**
     * マッピング対象のシートを番号で指定します。
     * <p>シート番号は0から始まります。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(number=0)}
     * public class SampleSheet {
     *   ...
     * }
     * </code></pre>
     *
     * @return シート番号
     */
    int number() default -1;

}
