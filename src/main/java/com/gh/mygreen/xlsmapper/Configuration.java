package com.gh.mygreen.xlsmapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterRegistry;
import com.gh.mygreen.xlsmapper.expression.CustomFunctions;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageJEXLImpl;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessorRegistry;
import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;


/**
 * マッピングする際の設定などを保持するクラス。
 *
 * @version 2.1
 * @author T.TSUCHIE
 *
 */
public class Configuration {

    /** シートが見つからなくても無視するかどうか */
    private boolean ignoreSheetNotFound = false;

    /** ラベルを正規化するかどうか */
    private boolean normalizeLabelText = false;

    /** ラベルを正規表現でマッピングするかどうか */
    private boolean regexLabelText = false;

    /** 型変換エラーが発生しても処理を続けるかどうか */
    private boolean continueTypeBindFailure = false;

    /** 書き込み時にセルの結合を行うかどうか */
    private boolean mergeCellOnSave = false;

    /** 書き込み時に名前の定義範囲を修正するかどうか */
    private boolean correctNameRangeOnSave = false;

    /** 書き込み時にセルの入力規則を修正するかどうか */
    private boolean correctCellDataValidationOnSave = false;

    /** 書き込み時に式の再計算をするかどうか */
    private boolean formulaRecalcurationOnSave = true;

    /** 読み込み時にセルの値のキャッシュを行うかどうか */
    private boolean cacheCellValueOnLoad = true;

    /** POIのセルの値のフォーマッター */
    private CellFormatter cellFormatter = new DefaultCellFormatter();

    /** アノテーションを処理するプロセッサの管理クラス */
    private FieldProcessorRegistry fieldProcessorRegistry = new FieldProcessorRegistry();

    /** セルの値の型変換の管理クラス */
    private CellConverterRegistry converterRegistry = new CellConverterRegistry();

    /** 読み込み時のBeanのインスタンスの作成クラス */
    private BeanFactory<Class<?>, Object> beanFactory = new DefaultBeanFactory();

    /** {@link SheetBindingErrors}のインスタンス作成クラス */
    private SheetBindingErrorsFactory bindingErrorsFactory = new SheetBindingErrorsFactory();

    /** 処理対象のシートを取得するクラス */
    private SheetFinder sheetFinder = new SheetFinder();

    /** 数式をフォーマットするクラス */
    private MessageInterpolator formulaFormatter = new MessageInterpolator();
    
    /** セルコメントを操作するクラス */
    private CellCommentOperator commentOperator = new CellCommentOperator();

    /** Beanに対するアノテーションのマッピング情報 */
    private AnnotationMappingInfo annotationMapping = null;

    public Configuration() {

        // 数式をフォーマットする際のEL関数を登録する。
        ExpressionLanguageJEXLImpl formulaEL = new ExpressionLanguageJEXLImpl();
        Map<String, Object> funcs = new HashMap<>();
        funcs.put("x", CustomFunctions.class);
        formulaEL.getJexlEngine().setFunctions(funcs);

        formulaFormatter.setExpressionLanguage(formulaEL);
    }

    /**
     * 指定したクラスタイプのインスタンスを作成する
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <P> P createBean(final Class<P> clazz) {
        return (P) beanFactory.create(clazz);
    }

    /**
     * シートが見つからなくても無視するかどうか。
     * @return 初期値は、'false'です。
     */
    public boolean isIgnoreSheetNotFound() {
        return ignoreSheetNotFound;
    }

    /**
     * シートが見つからなくても無視するかどうか設定します。
     * @param ignoreSheetNotFound 初期値は、'false'です。
     * @return 自身のインスタンス
     */
    public Configuration setIgnoreSheetNotFound(boolean ignoreSheetNotFound) {
        this.ignoreSheetNotFound = ignoreSheetNotFound;
        return this;
    }

    /**
     * ラベルの文字列を空白などを正規化してマッピングするかどうか。
     * <p>正規化は、改行コード（{@literal \n},{@literal \r}）の除去、タブ（{@literal \t})、空白（全角、半角）の除去を行います。</p>
     * @since 1.1
     * @return 初期値は、'false'です。
     */
    public boolean isNormalizeLabelText() {
        return normalizeLabelText;
    }

    /**
     * ラベルの文字列を空白などを正規化してマッピングするかどうか設定します。
     * <p>正規化は、改行コード（{@literal \n},{@literal \r}）の除去、タブ（{@literal \t})、空白（全角、半角）の除去を行います。</p>
     * @since 1.1
     * @param normalizeLabelText 初期値は、'false'です。
     * @return 自身のインスタンス
     */
    public Configuration setNormalizeLabelText(boolean normalizeLabelText) {
        this.normalizeLabelText = normalizeLabelText;
        return this;
    }

    /**
     * ラベルを正規表現でマッピングするかどうか。
     * <p>ラベルに{@literal /正規表現/}と記述しておくと正規表現でマッピングできる。</p>
     * @since 1.1
     * @return 初期値は、'false'です。
     */
    public boolean isRegexLabelText() {
        return regexLabelText;
    }

    /**
     * ラベルを正規表現でマッピングするかどうか設置します。
     * <p>ラベルに{@literal /正規表現/}と記述しておくと正規表現でマッピングできる。</p>
     * @since 1.1
     * @param regexLabelText 正規表現でマッピングするかどうか。
     * @return 自身のインスタンス
     */
    public Configuration setRegexLabelText(boolean regexLabelText) {
        this.regexLabelText = regexLabelText;
        return this;
    }

    /**
     * 型変換エラーが発生しても処理を続けるかどうか。
     * @return 初期値は、'false'です。
     */
    public boolean isContinueTypeBindFailure() {
        return continueTypeBindFailure;
    }

    /**
     * 型変換エラーが発生しても処理を続けるかどうか。
     * @param continueTypeBindFailure 初期値は、'false'です。
     * @return
     */
    public Configuration setContinueTypeBindFailure(boolean continueTypeBindFailure) {
        this.continueTypeBindFailure = continueTypeBindFailure;
        return this;
    }

    /**
     * 書き込み時にセルの結合を行うかどうか
     * @return 初期値は、'false'です。
     */
    public boolean isMergeCellOnSave() {
        return mergeCellOnSave;
    }

    /**
     * 書き込み時にセルの結合を行うかどうか設定します。
     * @param mergeCellOnSave 初期値は、'false'です。
     * @return 自身のインスタンス
     */
    public Configuration setMergeCellOnSave(boolean mergeCellOnSave) {
        this.mergeCellOnSave = mergeCellOnSave;
        return this;
    }

    /**
     * 書き込み時に名前の定義範囲を修正するかどうか
     * @since 0.3
     * @return 初期値は、'false'です。
     */
    public boolean isCorrectNameRangeOnSave() {
        return correctNameRangeOnSave;
    }

    /**
     * 書き込み時に名前の定義範囲を修正するかどうか設定します。
     * @since 0.3
     * @param correctNameRangeOnSave 初期値は、'false'です。
     * @return 自身のインスタンス
     */
    public Configuration setCorrectNameRangeOnSave(boolean correctNameRangeOnSave) {
        this.correctNameRangeOnSave = correctNameRangeOnSave;
        return this;
    }

    /**
     * 書き込み時にセルの入力規則を修正するかどうか。
     * @since 0.3
     * @return 初期値は、'false'です。
     */
    public boolean isCorrectCellDataValidationOnSave() {
        return correctCellDataValidationOnSave;
    }

    /**
     * 書き込み時にセルの入力規則を修正するかどうか。
     * <p>trueの場合（修正する場合）、POI-3.11以上が必要になります。
     * <p>POI-3.10以前の場合、データの修正は行われません。
     * @param correctCellDataValidationOnSave 初期値は、'false'です。
     */
    public Configuration setCorrectCellDataValidationOnSave(boolean correctCellDataValidationOnSave) {
        this.correctCellDataValidationOnSave = correctCellDataValidationOnSave;
        return this;
    }

     /**
     * 書き込み時に式の再計算をするか設定します。
     * <p>数式を含むシートを出力したファイルを開いた場合、一般的には数式が開いたときに再計算されます。
     * <p>ただし、大量で複雑な数式が記述されていると、パフォーマンスが落ちるため無効にすることもできます。
     * @since 1.5
     * @return 初期値は、'true'です。
     */
    public boolean isFormulaRecalcurationOnSave() {
        return formulaRecalcurationOnSave;
    }

    /**
     * 書き込み時に式の再計算をするか設定します。
     * <p>数式を含むシートを出力したファイルを開いた場合、一般的には数式が開いたときに再計算されます。
     * <p>ただし、大量で複雑な数式が記述されていると、パフォーマンスが落ちるため無効にすることもできます。
     * @since 1.5
     * @param formulaRecalcurationOnSave
     * @return 自身のインスタンス
     */
    public Configuration setFormulaRecalcurationOnSave(boolean formulaRecalcurationOnSave) {
        this.formulaRecalcurationOnSave = formulaRecalcurationOnSave;
        return this;
    }

    /**
     * 読み込み時にセルの値のキャッシュを行うかどうか設定します。
     * @since 2.0
     * @return true 初期値は、'true'です。
     */
    public boolean isCacheCellValueOnLoad() {
        return cacheCellValueOnLoad;
    }

    /**
     * 読み込み時にセルの値のキャッシュを行うかどうか設定します。
     * @since 2.0
     * @param cacheCellValueOnLoad trueのときキャッシュを行います。
     */
    public Configuration setCacheCellValueOnLoad(boolean cacheCellValueOnLoad) {
        this.cacheCellValueOnLoad = cacheCellValueOnLoad;
        return this;
    }

    /**
     * POIのセルのフォーマッターを取得します。
     * @return セルのフォーマッタ。
     */
    public CellFormatter getCellFormatter() {
        return cellFormatter;
    }

    /**
     * POIのセルのフォーマッターを指定します。
     * @param cellFormatter セルのフォーマッタ
     * @return 自身のインスタンス
     */
    public Configuration setCellFormatter(CellFormatter cellFormatter) {
        this.cellFormatter = cellFormatter;
        return this;
    }

    /**
     * セルの値の型変換の管理クラスを取得します。
     * @return
     */
    public CellConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    /**
     * セルの値の型変換の管理クラスを設定します。
     * @param converterRegistry
     * @return 自身のインスタンス
     */
    public Configuration setConverterRegistry(CellConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
        return this;
    }

    /**
     * アノテーションを処理するプロセッサの管理クラスを取得します。
     * @return
     */
    public FieldProcessorRegistry getFieldProcessorRegistry() {
        return fieldProcessorRegistry;
    }

    /**
     * アノテーションを処理するプロセッサの管理クラスを設定します。
     * @return 自身のインスタンス
     */
    public Configuration setFieldProcessorRegistry(FieldProcessorRegistry fieldProcessorRegistry) {
        this.fieldProcessorRegistry = fieldProcessorRegistry;
        return this;
    }

    /**
     * Beanを生成するためのFactoryクラスを設定します。
     * @return
     */
    public Configuration setBeanFactory(BeanFactory<Class<?>, Object> beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }

    /**
     * Beanを生成するためのFactoryクラスを取得します。
     * @since 1.0
     * @return
     */
    public BeanFactory<Class<?>, Object> getBeanFactory() {
        return beanFactory;
    }

    /**
     * {@link SheetBindingErrors}を生成するためのFactoryクラスを取得します。
     * @since 2.0
     * @return
     */
    public SheetBindingErrorsFactory getBindingErrorsFactory() {
        return bindingErrorsFactory;
    }

    /**
     * {@link SheetBindingErrors}を生成するためのFactoryクラスを設定します。
     * @since 2.0
     * @param bindingErrorsFactory
     */
    public void setBindingErrorsFactory(SheetBindingErrorsFactory bindingErrorsFactory) {
        this.bindingErrorsFactory = bindingErrorsFactory;
    }

    /**
     * 処理対象のシートを取得するためのクラスを取得します。
     * <p>アノテーション{@link XlsSheet} を処理します。
     * @since 1.1
     * @return 現在設定されいるシートを処理するクラスのインタンス。
     */
    public SheetFinder getSheetFinder() {
        return sheetFinder;
    }

    /**
     * 処理対象のシートを取得するためのクラスを設定します。
     * <p>アノテーション{@link XlsSheet} を処理します。
     * @since 1.1
     * @param sheetFinder シートを処理するクラスのインタンス。
     * @return 自身のインスタンス
     */
    public Configuration setSheetFinder(SheetFinder sheetFinder) {
        this.sheetFinder = sheetFinder;
        return this;
    }

    /**
     * 数式をフォーマットするためのクラスを取得します。
     * <p>出力するセルの数式を設定する際に、独自の変数やEL式を指定したときにフォーマットする際に利用します。
     *
     * @since 1.5
     * @return 数式をフォーマットするクラス。
     */
    public MessageInterpolator getFormulaFormatter() {
        return formulaFormatter;
    }

    /**
     * 数式をフォーマットするためのクラスを取得します。
     * <p>出力するセルの数式を設定する際に、独自の変数やEL式を指定したときにフォーマットする際に利用します。
     *
     * @since 1.5
     * @param formulaFormatter 数式をフォーマットするクラス。
     * @return 自身のインスタンス
     */
    public Configuration setFormulaFormatter(MessageInterpolator formulaFormatter) {
        this.formulaFormatter = formulaFormatter;
        return this;
    }

    /**
     * アノテーションのマッピング情報を取得する。
     * @since 2.0
     * @return 設定されていない場合は、空を返す。
     */
    public Optional<AnnotationMappingInfo> getAnnotationMapping() {
        return Optional.ofNullable(annotationMapping);
    }

    /**
     * アノテーションのマッピング情報を設定します。
     * @since 2.0
     * @param annotationMapping アノテーションの設定情報
     */
    public void setAnnotationMapping(AnnotationMappingInfo annotationMapping) {
        this.annotationMapping = annotationMapping;
    }

    /**
     * セルコメントを操作するクラスを取得します。
     * @since 2.1
     * @return セルコメントを操作するクラス
     */
    public CellCommentOperator getCommentOperator() {
        return commentOperator;
    }

    /**
     * セルコメントを操作するクラスを設定します。
     * @since 2.1
     * @param commentOperator セルコメントを操作するクラス
     * @return 自身のインスタンス
     */
    public Configuration setCommentOperator(CellCommentOperator commentOperator) {
        this.commentOperator = commentOperator;
        return this;
    }

}
