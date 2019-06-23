package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 書き込み時のセルの配置などのを指定するためのアノテーションです。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsCellOption {

    /**
     * 'true'のとき書き込み時にセルの「折り返し設定」を有効にします。
     * 'false'の場合は、既存の折り返し設定は変更せずに、テンプレートファイルの設定を引き継ぎます。
     * <p>属性{@link #wrapText()}と{@link #shrinkToFit()}の両方の値をtrueに指定する場合、
     *    {@link #shrinkToFit()}の設定が優先され、「縮小して全体を表示する」が有効になります。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="ID")}
     *     {@literal @XlsCellOption(wrapText=true)} // 「縮小して全体を表示する」が有効になる。
     *     private int id;
     *
     * }
     * </code></pre>
     *
     * @return trueの場合、「折り返し設定」が有効になります。
     */
    boolean wrapText() default false;

    /**
     * 'true'のとき書き込み時にセルの「縮小して表示」を有効にします。
     * 'false'の場合は、既存の縮小して表示は変更せずに、テンプレートファイルの設定を引き継ぎます。
     * <p>属性{@link #wrapText()}と{@link #shrinkToFit()}の両方の値をtrueに指定する場合、
     *    {@link #shrinkToFit()}の設定が優先され、「縮小して全体を表示する」が有効になります。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     {@literal @XlsCellOption(shrinkToFit=true)} //「折り返して全体を表示する」が有効になる。
     *     private String name;
     *
     *     {@literal @XlsColumn(columnName="備考")}
     *     {@literal @XlsCellOption(shrinkToFit=false)} // 設定しない場合は、テンプレート設定が有効になる。
     *     private String comment;
     *
     * }
     * </code></pre>
     *
     * @return trueの場合、「縮小して表示」が有効になります。
     */
    boolean shrinkToFit() default false;

    /**
     * インデントを指定します。
     * <p>インデントが指定可能な横位置(左詰め/右詰め/均等割り付け)のときのみ有効になります。</p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     // インデント可能な横位置と一緒にインデントを指定します。
     *     {@literal @XlsColumn(columnName="名前")}
     *     {@literal @XlsCellOption(horizontalAlign=HorizontalAlign.Left, indent=2)}
     *     private String name;
     *
     * }
     * </code></pre>
     *
     * @return 0以上の値を設定します。-1以下のとき値は現在の設定を引き継ぎます。
     */
    short indent() default -1;

    /**
     * セルの横位置を指定します。
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     {@literal @XlsCellOption(horizontalAlign=HorizontalAlign.Center)}
     *     private String name;
     *
     * }
     * </code></pre>
     *
     * @return セルの横位置を表す列挙型 {@link HorizontalAlign}を指定します。
     */
    HorizontalAlign horizontalAlign() default HorizontalAlign.Default;

    /**
     * セルの縦位置を指定します。
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     {@literal @XlsCellOption(verticalAlign=VerticalAlign.Top)}
     *     private String name;
     *
     * }
     * </code></pre>
     *
     * @return セルの縦位置を表す列挙型 {@link VerticalAlign}を指定します。
     */
    VerticalAlign verticalAlign() default VerticalAlign.Default;

    /**
     * セルの横位置のタイプ
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public enum HorizontalAlign {

        /** デフォルト（既存の設定を引き継ぐ） */
        Default(null),
        /** 標準 */
        General(HorizontalAlignment.GENERAL),
        /** 左詰め（インデント） */
        Left(HorizontalAlignment.LEFT),
        /** 中央揃え */
        Center(HorizontalAlignment.CENTER),
        /** 右詰め（インデント） */
        Right(HorizontalAlignment.RIGHT),
        /** 繰り返し */
        Fill(HorizontalAlignment.FILL),
        /** 両端揃え */
        Justify(HorizontalAlignment.JUSTIFY),
        /** 選択範囲内で中央 */
        CenterSelection(HorizontalAlignment.CENTER_SELECTION),
        /** 均等割り付け（インデント） */
        Distributed(HorizontalAlignment.DISTRIBUTED)
        ;

        private final HorizontalAlignment poiAlignType;

        private HorizontalAlign(HorizontalAlignment poiAlignType) {
            this.poiAlignType = poiAlignType;
        }

        public HorizontalAlignment poiAlignType() {
            return poiAlignType;
        }

    }

    /**
     * セルの縦位置のタイプ
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public enum VerticalAlign {

        /** デフォルト（既存の設定を引き継ぐ） */
        Default(null),
        /** 上詰め */
        Top(VerticalAlignment.TOP),
        /** 中央揃え */
        Center(VerticalAlignment.CENTER),
        /** 下詰め */
        Bottom(VerticalAlignment.BOTTOM),
        /** 両端揃え */
        Justify(VerticalAlignment.JUSTIFY),
        /** 均等割り付け */
        Distibuted(VerticalAlignment.DISTRIBUTED)
        ;

        private final VerticalAlignment poiAlignType;

        private VerticalAlign(VerticalAlignment poiAlignType) {
            this.poiAlignType = poiAlignType;
        }

        public VerticalAlignment poiAlignType() {
            return poiAlignType;
        }
    }

}
