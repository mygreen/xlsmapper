package com.gh.mygreen.xlsmapper.converter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * {@link CellConverter}を実装するときのベースとなる抽象クラス。
 * 通常は、このクラスを継承して{@link CellConverter}を実装します。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCellConverter<T> implements CellConverter<T> {
    
    @Override
    public T toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        // 文字列として取得する
        final String formattedValue = Utils.trim(config.getCellFormatter().format(cell), trimAnno);
        
        // デフォルト値の処理
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        if(isEmptyCell(formattedValue, cell, config) && defaultValueAnno.isPresent()) {
            final String defaultValue = Utils.trim(defaultValueAnno.get().value(), trimAnno);
            return parseDefaultValue(defaultValue, adapter, config);
        }
        
        // 数式のセルの場合、予め評価しておく
        final Cell evaluatedCell;
        if(cell.getCellTypeEnum() == CellType.FORMULA) {
            final Workbook workbook = cell.getSheet().getWorkbook();
            final CreationHelper helper = workbook.getCreationHelper();
            final FormulaEvaluator evaluator = helper.createFormulaEvaluator();
            
            evaluatedCell = evaluator.evaluateInCell(cell);
            
        } else {
            evaluatedCell = cell;
        }
        
        return parseCell(evaluatedCell, formattedValue, adapter, config);
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
     * @param config システム情報設定
     * @return trueの場合、空と判定する。
     */
    protected boolean isEmptyCell(final String formattedValue, final Cell cell, final XlsMapperConfig config) {
        return formattedValue.isEmpty();
    }
    
    /**
     * 文字列の初期値をJavaのオブジェクト型に変換します。
     * @param defaultValue 変換対象のオブジェクト
     * @param adapter フィールド情報
     * @param config システム情報の設定
     * @return 変換した値を返す。
     * @throws TypeBindException 変換に失敗した場合
     */
    protected abstract T parseDefaultValue(String defaultValue, FieldAdapter adapter, XlsMapperConfig config) throws TypeBindException;
    
    /**
     * セルをJavaのオブジェクト型に変換します。
     * @param evaluatedCell 数式を評価済みのセル
     * @param formattedValue フォーマット済みのセルの値。トリミングなど定期用済み。
     * @param adapter フィールド情報
     * @param config システム情報の設定
     * @return 変換した値を返す。
     * @throws TypeBindException 変換に失敗した場合
     */
    protected abstract T parseCell(Cell evaluatedCell, String formattedValue, FieldAdapter adapter, XlsMapperConfig config) throws TypeBindException;
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final T targetValue, final Object targetBean, final Sheet sheet,
            final CellAddress address, final XlsMapperConfig config) throws XlsMapperException {
        
        final Cell cell = POIUtils.getCell(sheet, address);
        
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        // セルの制御の設定
        adapter.getAnnotation(XlsCellOption.class).ifPresent(cellOptionAnno -> {
            ConversionUtils.setupCellOption(cell, cellOptionAnno);
        });
        
        // デフォルト値の設定
        final T cellValue;
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        if(targetValue == null && defaultValueAnno.isPresent()) {
            final String defaultValue = Utils.trim(defaultValueAnno.get().value(), trimAnno);
            cellValue = parseDefaultValue(defaultValue, adapter, config);
        } else {
            cellValue = targetValue;
        }
        
        // 各書式に沿った値の設定
        setupCell(cell, Optional.ofNullable(cellValue), adapter, config);
        
        // 数式の設定
        adapter.getAnnotation(XlsFormula.class).ifPresent(formulaAnno -> {
            if(isEmptyValue(cellValue, adapter, config) || formulaAnno.primary()) {
                Utils.setupCellFormula(adapter, formulaAnno, config, cell, targetBean);
            }
        });
        
        return cell;
    }
    
    /**
     * オブジェクトの値を空と判定する
     * @param obj 判定対象の値
     * @param adapter フィールド情報
     * @param config システム情報
     * @return trueの場合、空と判定する。
     */
    @SuppressWarnings("rawtypes")
    protected boolean isEmptyValue(final T obj, final FieldAdapter adapter, final XlsMapperConfig config) {
        if(obj == null) {
            return true;
        }
        
        if(obj instanceof String) {
            return ((String)obj).isEmpty();
        }
        
        if(char.class.isAssignableFrom(obj.getClass()) || obj instanceof Character) {
            return ((char)obj) == '\u0000';
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
     * @param adapter フィールド情報
     * @param config システム情報の設定
     * @throws TypeBindException 変換に失敗した場合
     */
    protected abstract void setupCell(Cell cell, Optional<T> cellValue, FieldAdapter adapter, XlsMapperConfig config) throws TypeBindException;
    
    /**
     * 初期値の型変換失敗したときの例外{@link TypeBindException}をスローします。
     * @since 2.0
     * @param error 例外情報
     * @param adapter フィールド情報
     * @param defaultValue 変換に失敗したときの初期値
     * @return マッピングに失敗したときの例外のインスタンス
     */
    public TypeBindException newTypeBindExceptionWithDefaultValue(final Exception error, 
            final FieldAdapter adapter, final String defaultValue) {
        
        final String message = MessageBuilder.create("anno.XlsDefaultValue.failParse")
                .var("property", adapter.getNameWithClass())
                .var("defaultValue", defaultValue)
                .varWithClass("type", adapter.getType())
                .format();
        
        return new TypeBindException(error, message, adapter.getType(), defaultValue);
        
    }
    
    /**
     * セルの値をJavaオブジェクトに型変換するとに失敗したときの例外{@link TypeBindException}をスローします。
     * @since 2.0
     * @param error 例外情報
     * @param cell 例外が発生したセル
     * @param adapter フィールド情報
     * @param cellValue マッピングに失敗した値
     * @return マッピングに失敗したときの例外のインスタンス
     */
    public TypeBindException newTypeBindExceptionWithParse(final Exception error, 
            final Cell cell, final FieldAdapter adapter, final Object cellValue) {
        
        final String message = MessageBuilder.create("cell.typeBind.failParse")
                .var("property", adapter.getNameWithClass())
                .var("cellAddress", POIUtils.formatCellAddress(cell))
                .var("cellValue", cellValue.toString())
                .varWithClass("type", adapter.getType())
                .format();
        
        return new TypeBindException(error, message, adapter.getType(), cellValue);
        
    }
    
}
