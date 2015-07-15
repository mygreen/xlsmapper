package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 垂直方向に連続する列をListまたは配列にマッピングする際に指定します。
 * <p>表には最左部にテーブルの名称と列名を記述した行が必要になります。
 * An annotation for the property which is mapped to the vertical table records.
 * 
 * TODO Is this necessary?
 * 
 * @version 1.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsVerticalRecords {
    
    /**
     * レコードが見つからない場合に、エラーとしないで、無視して処理を続行させてい場合trueを指定します。
     * 
     * @return
     */
    boolean optional() default false;
    
    /**
     * 表の見出し（タイトル）ラベル。値を指定した場合、ラベルと一致するセルを起点に走査を行う。
     * <p>属性{@link #headerAddress()}のどちらか一方を指定可能。
     */
    String tableLabel() default "";
    
    /**
     * 表の見出し（タイトル）ラベルの位置が上方に位置するかどうか。
     * @since 1.0
     */
    boolean tableLabelAbove() default false;
    
    /**
     * テーブルが他のテーブルと連続しておりterminal属性でBorder、Emptyのいずれを指定しても終端を検出できない場合があります。 
     * このような場合はterminateLabel属性で終端を示すセルの文字列を指定します。
     * @return
     */
    String terminateLabel() default "";
    
    /**
     * 表の開始位置（見出し列）セルの行番号を指定する。
     * <p>値は'0'から始まる。
     * @return
     */
    int headerColumn() default -1;
    
    /**
     * 表の開始位置（見出し行）セルの行番号を指定する。
     * <p>値は'0'から始まる。
     * @return
     */
    int headerRow() default -1;
    
    /**
     * 表の開始位置のセルのアドレスを'A1'などのように指定します。値を指定した場合、指定したアドレスを起点に走査を行います
     * <p>属性{@link #headerRow()},{@link #headerColumn()}のどちらか一方を指定可能です
     */
    String headerAddress() default "";
    
    /** 
     * レコードのマッピング先のクラスを指定します。
     * <p>指定しない場合は、Genericsの定義タイプを使用します。
     */
    Class<?> recordClass() default Object.class;
    
    /** 
     * 表の終端の種類を指定します
     */
    RecordTerminal terminal() default RecordTerminal.Empty;
    
    /**
     * 右方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルを見出しとします。
     * @return
     */
    int range() default 1;
    
    /**
     * {@link #tableLabel()}で指定した表のタイトルから、実際の表の開始位置がどれだけ離れているか指定する。
     * <p>右方向の列数を指定する。
     * <p>{@link #tableLabelAbove()}の値がtrueの場合は、下方向に行数になる。
     * @since 1.0
     * @return
     */
    int right() default 1;
    
    /**
     * テーブルのカラムが指定数見つかったタイミングで Excelシートの走査を終了したい場合に指定します。
     * <p>主に無駄な走査を抑制したい場合にしますが、 {@link XlsIterateTables}使用時に、
     * テーブルが隣接しており終端を検出できない場合などに カラム数を明示的に指定してテーブルの区切りを指定する場合にも使用できます。 
     * @return
     */
    int headerLimit() default 0;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
     * {@link XlsVerticalRecords}の場合、{@link OverRecordOperate#Insert}は対応していません。
     * @return
     */
    OverRecordOperate overRecord() default OverRecordOperate.Break;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
     * {@link XlsVerticalRecords}の場合、{@link RemainedRecordOperate#Delete}は対応していません。
     * @return
     */
    RemainedRecordOperate remainedRecord() default RemainedRecordOperate.None; 
    
    /**
     * 空のレコードの場合、処理をスキップするかどうか。
     * <p>レコードの判定用のメソッドに、アノテーション{@link XlsIsEmpty}を付与する必要があります。
     * @return
     * @since 0.2
     */
    boolean skipEmptyRecord() default false;
}
