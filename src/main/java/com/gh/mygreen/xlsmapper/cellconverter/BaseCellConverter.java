package com.gh.mygreen.xlsmapper.cellconverter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;

/**
 * {@link CellConverter}を実装するときのベースとなる抽象クラス。
 * 通常は、このクラスを継承して{@link CellConverter}を実装します。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class BaseCellConverter<T> implements CellConverter<T>, FieldFormatter<T> {

    /**
     * フィールド情報
     */
    protected final FieldAccessor field;

    /**
     * システム設定
     */
    protected final Configuration configuration;

    /**
     * 値をトリムするかどうか
     */
    protected boolean trimmed;

    /**
     * 初期値 - 初期値を持たない場合は空
     */
    protected OptionalProcessCase<T> defaultValue = OptionalProcessCase.empty();

    /**
     * セルの設定 - セルを縮小して表示するかどうか
     */
    protected boolean shrinktToFit;

    /**
     * セルの設定 - 折り返して全体を表示するかどうか
     */
    protected boolean wrapText;

    /**
     * セルの設定 - インデント
     */
    protected short indent;

    /**
     * セルの設定 - 横位置
     */
    protected Optional<HorizontalAlignment> horizontalAlignment = Optional.empty();

    /**
     * セルの設定 - 縦位置
     */
    protected Optional<VerticalAlignment> verticalAlignment = Optional.empty();

    /**
     * 書き込み時の数式を設定する
     */
    protected Optional<CellFormulaHandler> formulaHandler = Optional.empty();

    /**
     * 文字列とオブジェクトを相互変換するフォーマッタ
     */
    protected TextFormatter<T> textFormatter;

    public BaseCellConverter(final FieldAccessor field, final Configuration config) {
        this.field = field;
        this.configuration = config;
    }

    @Override
    public T toObject(final Cell cell) throws XlsMapperException {

        final ProcessCase processCase = ProcessCase.Load;
        final String formattedValue = Utils.trim(configuration.getCellFormatter().format(cell), trimmed);

        // デフォルト値の設定
        if(isEmptyCell(formattedValue, cell) && defaultValue.isPresent(processCase)) {
            return defaultValue.get(processCase);
        }

        // 数式のセルの場合、予め評価しておく
        final Cell evaluatedCell;
        if(cell.getCellTypeEnum().equals(CellType.FORMULA)) {
            final Workbook workbook = cell.getSheet().getWorkbook();
            final CreationHelper helper = workbook.getCreationHelper();
            final FormulaEvaluator evaluator = helper.createFormulaEvaluator();

            evaluatedCell = evaluator.evaluateInCell(cell);
        } else {
            evaluatedCell = cell;
        }

        return parseCell(evaluatedCell, formattedValue);
    }

    /**
     * セルをJavaのオブジェクト型に変換します。
     * @param evaluatedCell 数式を評価済みのセル
     * @param formattedValue フォーマット済みのセルの値。トリミングなど適用済み。
     * @return 変換した値を返す。
     * @throws TypeBindException 変換に失敗した場合
     */
    protected abstract T parseCell(Cell evaluatedCell, String formattedValue) throws TypeBindException;

    /**
     * セルの値をパースしJavaオブジェクトに型変換するとこに失敗したときの例外{@link TypeBindException}を作成します。
     * @since 2.0
     * @param error 例外情報
     * @param cell パースに失敗したセル
     * @param cellValue パースに失敗した値
     * @return マッピングに失敗したときの例外のインスタンス
     */
    public TypeBindException newTypeBindExceptionOnParse(final Exception error,  final Cell cell, final Object cellValue) {

        final String message = MessageBuilder.create("cell.typeBind.failParse")
                .var("property", field.getNameWithClass())
                .var("cellAddress", POIUtils.formatCellAddress(cell))
                .var("cellValue", cellValue.toString())
                .varWithClass("type", field.getType())
                .format();

        final TypeBindException bindException = new TypeBindException(error, message, field.getType(), cellValue);
        if(error instanceof TextParseException) {
            bindException.addAllMessageVars(((TextParseException)error).getErrorVariables());
        }

        return bindException;

    }

    /**
     * セルの値をパースしセルの値をJavaオブジェクトに型変換するとに失敗したときの例外{@link TypeBindException}を作成します。
     * @since 2.0
     * @param cell パースに失敗したセル
     * @param cellValue パースに失敗した値
     * @return マッピングに失敗したときの例外のインスタンス
     */
    public TypeBindException newTypeBindExceptionOnParse(final Cell cell, final Object cellValue) {

        final String message = MessageBuilder.create("cell.typeBind.failParse")
                .var("property", field.getNameWithClass())
                .var("cellAddress", POIUtils.formatCellAddress(cell))
                .var("cellValue", cellValue.toString())
                .varWithClass("type", field.getType())
                .format();

        final TypeBindException bindException = new TypeBindException(message, field.getType(), cellValue);

        return bindException;

    }

    /**
     * セルの値が空かどうか判定します。
     * <p>読み込み時のセルの値をJavaオブジェクトにマッピングする際に呼ばれます。</p>
     * <p>
     *   通常は、{@code formattedValue.isEmpty()} で判定しますが、
     *   ハイパーリンクのようにマッピング対象の値がセルの値だけではない場合は、
     *   オーバライドして判定方法を変更します。
     * </p>
     *
     * @param formattedValue フォーマットしたセルの値
     * @param cell 評価対象のセル
     * @return trueの場合、空と判定する。
     */
    protected boolean isEmptyCell(final String formattedValue, final Cell cell) {
        return formattedValue.isEmpty();
    }

    @Override
    public Cell toCell(final T targetValue, final Object targetBean, final Sheet sheet, final CellPosition address) throws XlsMapperException {

        final ProcessCase processCase = ProcessCase.Save;
        final Cell cell = POIUtils.getCell(sheet, address);

        final CellStyleProxy cellStyle = new CellStyleProxy(cell);

        // セルの制御の設定
        if(shrinktToFit) {
            cellStyle.setShrinkToFit();

        } else if(wrapText) {
            cellStyle.setWrapText();
        }

        // 横位置
        horizontalAlignment.ifPresent(align -> cellStyle.setHorizontalAlignment(align));

        // インデント
        if(indent >= 0) {
            cellStyle.setIndent(indent);
        }

        // 縦位置
        verticalAlignment.ifPresent(align -> cellStyle.setVerticalAlignment(align));

        // デフォルト値の設定
        final T cellValue;
        if(targetValue == null && defaultValue.isPresent(processCase)) {
            cellValue = defaultValue.get(processCase);
        } else {
            cellValue = targetValue;
        }

        // 各書式に沿った値の設定
        setupCell(cell, Optional.ofNullable(cellValue));

        // 数式の設定
        formulaHandler.ifPresent(handler -> {
            if(isEmptyValue(cellValue, configuration) || handler.isPrimaryFormula()) {
                handler.handleFormula(field, configuration, cell, targetBean);
            }
        });

        return cell;
    }

    /**
     * オブジェクトの値を空と判定する
     * @param obj 判定対象の値
     * @param config システム情報
     * @return trueの場合、空と判定する。
     */
    @SuppressWarnings("rawtypes")
    protected boolean isEmptyValue(final T obj, final Configuration config) {
        if(obj == null) {
            return true;
        }

        if(obj instanceof String) {
            return ((String)obj).isEmpty();
        }

        if(char.class.isAssignableFrom(obj.getClass()) || obj instanceof Character) {
            return ((Character)obj) == '\u0000';
        }

        if(obj.getClass().isArray()) {
            return ((Object[])obj).length == 0;
        }

        if(obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        }

        if(obj instanceof Map) {
            return ((Map)obj).isEmpty();
        }

        return false;
    }

    /**
     * 書き込み時のセルに値と書式を設定します。
     * @param cell 設定対象のセル
     * @param cellValue 設定対象の値。
     * @throws TypeBindException 変換に失敗した場合
     */
    protected abstract void setupCell(Cell cell, Optional<T> cellValue) throws TypeBindException;

    /**
     * フィールド情報を取得します。
     * @return フィールド情報
     */
    public FieldAccessor getField() {
        return field;
    }

    /**
     * システム情報を取得します。
     * @return システム情報
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String format(T value) {
        return textFormatter.format(value);
    }

    /**
     * 値をトリミングするかどうか設定する
     * @param trimmed trueの場合、トリムする。
     */
    public void setTrimmed(boolean trimmed) {
        this.trimmed = trimmed;
    }

    /**
     * 値をトリミングするかどうか。
     * @return trueの場合、トリムする。
     */
    public boolean isTrimmed() {
        return trimmed;
    }

    /**
     * 初期値を設定します。
     * @param defaultValue 初期値となるオブジェクト。
     * @param cases 該当する処理ケース
     */
    public void setDefaultValue(T defaultValue, ProcessCase[] cases) {
        this.defaultValue = OptionalProcessCase.of(defaultValue, cases);
    }

    /**
     * 初期値を取得します。
     * @return 設定されていない場合、空を返します。
     */
    public OptionalProcessCase<T> getDefaultValue() {
        return defaultValue;
    }

    /**
     * セルの設定 - セルを縮小して表示するかどうか設定します。
     * @param shrinktToFit trueの場合、セルを縮小して表示します。
     */
    public void setShrinktToFit(boolean shrinktToFit) {
        this.shrinktToFit = shrinktToFit;
    }

    /**
     * セルの設定 - セルを縮小して表示するかどうか。
     * @return trueの場合、セルを縮小して表示します。
     */
    public boolean isShrinktToFit() {
        return shrinktToFit;
    }

    /**
     * セルの設定 - 折り返して全体を表示するかどうか設定します。
     * @param wrapText trueの場合、折り返して全体を表示します。
     */
    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }

    /**
     * セルの設定 - 折り返して全体を表示するかどうか。
     * @return trueの場合、折り返して全体を表示します。
     */
    public boolean isWrapText() {
        return wrapText;
    }

    /**
     * セルの設定 - インデント
     * @return 0以上の場合、有効。
     */
    public short getIndent() {
        return indent;
    }

    /**
     * セルの設定 - インデント
     * @param indent 0以上の場合、有効。
     */
    public void setIndent(short indent) {
        this.indent = indent;
    }

    /**
     * セルの設定 - 横位置
     * @return 横位置。設定がない場合は空を返す。
     */
    public Optional<HorizontalAlignment> getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * セルの設定 - 横位置
     * @param horizontalAlignment 横位置
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = Optional.ofNullable(horizontalAlignment);
    }

    /**
     *  セルの設定 - 縦位置
     * @return 縦位置。設定がない場合は空を返す。
     */
    public Optional<VerticalAlignment> getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * セルの設定 - 縦位置
     * @param verticalAlignment 縦位置
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = Optional.ofNullable(verticalAlignment);
    }

    /**
     * 数式を処理するハンドラを設定する
     * @param formulaHandler 数式を処理するハンドラを
     */
    public void setFormulaHandler(CellFormulaHandler formulaHandler) {
        this.formulaHandler = Optional.of(formulaHandler);
    }

    /**
     * 文字列とオブジェクトを相互変換するフォーマッタを取得します。
     * @param textFormatter フォーマッタ
     */
    public void setTextFormatter(TextFormatter<T> textFormatter) {
        this.textFormatter = textFormatter;
    }

    /**
     * 文字列とオブジェクトを相互変換するフォーマッタを取得します。
     * @return フォーマッタ
     */
    public TextFormatter<T> getTextFormatter() {
        return textFormatter;
    }

}
