package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 数値型のConverterの抽象クラス。
 * <p>数値型のConverterは、基本的にこのクラスを継承して作成する。
 * 
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellConverter<T extends Number> extends AbstractCellConverter<T> {
    
    @Override
    public T toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config) throws TypeBindException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsNumberConverter anno = adapter.getAnnotation(XlsNumberConverter.class)
                .orElseGet(() -> getDefaultNumberConverterAnnotation());
        
        T resultValue = null;
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(!defaultValueAnno.isPresent()) {
                // デフォルト値を持たない場合
                if(adapter.getType().isPrimitive()) {
                    resultValue = getZeroValue();
                }
                
            } else {
                String defaultValue = defaultValueAnno.get().value();
                try {
                    resultValue = parseNumber(defaultValue, createNumberFormat(anno), createMathContext(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adapter, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // セルのタイプが数値型の場合はそのまま取得する。
            try {
                resultValue = convertNumber(cell.getNumericCellValue(), createMathContext(anno));
            } catch(ArithmeticException e) {
                throw newTypeBindException(e, cell, adapter, cell)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            // 式を評価して再帰的に処理する。
            final Workbook workbook = cell.getSheet().getWorkbook();
            final CreationHelper helper = workbook.getCreationHelper();
            final FormulaEvaluator evaluator = helper.createFormulaEvaluator();
            try {
                // 再帰的に処理する
                final Cell evalCell = evaluator.evaluateInCell(cell);
                return toObject(evalCell, adapter, config);
                
            } catch(Exception e) {
                throw newTypeBindException(e, cell, adapter, cell)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, trimAnno);
            if(Utils.isNotEmpty(cellValue)) {
                try {
                    resultValue = parseNumber(cellValue, createNumberFormat(anno), createMathContext(anno));
                } catch(ParseException | ArithmeticException e) {
                    throw newTypeBindException(e, cell, adapter, cellValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
        }
        
        if(resultValue != null) {
            return resultValue;
            
        } else if(adapter.getType().isPrimitive()) {
            return getZeroValue();
        }
        
        return null;
    }
    
    /**
     * アノテーションから数値のフォーマッタを取得する。
     * @param anno 引数がnull(アノテーションが設定されていない場合)は、nullを返す。
     * @return アノテーションに書式が設定されていない場合はnullを返す。
     */
    protected NumberFormat createNumberFormat(final XlsNumberConverter anno) {
        
        final Locale locale;
        if(anno.locale().isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Utils.getLocale(anno.locale());
        }
        
        if(anno.javaPattern().isEmpty()) {
            if(anno.currency().isEmpty()) {
                return null;
            } else {
                // 通貨の場合
                return NumberFormat.getCurrencyInstance(locale);
            }
        }
        
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        final DecimalFormat format = new DecimalFormat(anno.javaPattern(), symbols);
        
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setParseBigDecimal(true);
        
        if(Utils.isNotEmpty(anno.currency())) {
            format.setCurrency(Currency.getInstance(anno.currency()));
        }
        
        return format;
        
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    private Map<String, Object> createTypeErrorMessageVars(final XlsNumberConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("javaPattern", anno.javaPattern());
        vars.put("currency", anno.currency());
        vars.put("locale", anno.locale());
        vars.put("precision", anno.precision());
        return vars;
    }
    
    /**
     * アノテーションを元に、{@link MathContext}のインスタンスを取得する。
     * <p>有効桁数、丸め方法を設定したものを返す。
     * <p>有効桁数は、デフォルトではExcelに仕様に合わせて15桁。
     * <p>丸め方法は、Excelに合わせて、{@link RoundingMode#HALF_UP}で固定。
     * @param anno
     * @return
     */
    protected MathContext createMathContext(final XlsNumberConverter anno) {
        
        if(anno.precision() > 0) {
            return new MathContext(anno.precision(), RoundingMode.HALF_UP);
        } else {
            return new MathContext(15, RoundingMode.HALF_UP);
        }
        
    }
    
    /**
     * その型における数値型を返す。
     * @param value
     * @param context
     * @return
     */
    protected abstract T convertNumber(double value, MathContext context);
    
    /**
     * その型における数値型を返す。
     * @param value
     * @param context
     * @return
     */
    protected abstract T convertNumber(Number value, MathContext context);
    
    /**
     * その型における数値型を返す。
     * @param value
     * @return
     */
    protected abstract T convertNumber(BigDecimal value);
    
    /**
     * 文字列をその型における数値型を返す。
     * <p>アノテーション{@link XlsNumberConverter}でフォーマットが与えられている場合は、パースして返す。
     * @param value
     * @param format フォーマットが指定されていない場合はnullが渡される
     * @param context 数値変換する際の設定
     * @return
     * @throws ParseException 
     */
    protected T parseNumber(final String value, final NumberFormat format, final MathContext context) throws ParseException {
        
        if(format == null) {
            final BigDecimal bg;
            try {
                // 文字列の時は、精度指定しない。
                bg = new BigDecimal(value);
            } catch(NumberFormatException e) {
                throw new ParseException(String.format("Cannot parse '%s'", value), 0);
            }
            
            if(bg.doubleValue() < getMinValue()) {
                throw new ParseException(String.format("'%s' cannot less than %f", value, getMinValue()), 0);
            }
            
            if(bg.doubleValue() > getMaxValue()) {
                throw new ParseException(String.format("'%s' cannot greater than %f", value, getMaxValue()), 0);
            }
            
            return convertNumber(bg);
        }
        
        final ParsePosition position = new ParsePosition(0);
        final Number result = (Number) format.parseObject(value, position);
        
        if(position.getIndex() != value.length()) {
            throw new ParseException(
                    String.format("Cannot parse '%s' using fromat %s", value, format.toString()), position.getIndex());
        }
        
        if(result.doubleValue() < getMinValue()) {
            throw new ParseException(String.format("'%s' cannot less than %f", value, getMinValue()), 0);
        }
        
        if(result.doubleValue() > getMaxValue()) {
            throw new ParseException(String.format("'%s' cannot greater than %f", value, getMaxValue()), 0);
        }
        
        if(result instanceof BigDecimal) {
            // NumberFormatのインスタンスを作成する際に、DecimalFormat#setParseBigDecimal(true)としているため、戻り値がBigDecimalになる。
            return convertNumber((BigDecimal) result);
        } else {
            return convertNumber(result, context);
        }
    }
    
    /**
     * その型におけるゼロ値を返す。
     * @return
     */
    protected abstract T getZeroValue();
    
    /**
     * その型における最大値を返す。
     * @return
     */
    protected abstract double getMaxValue();
    
    /**
     * その型における最小値を返す。
     * @return
     */
    protected abstract double getMinValue();
    
    private XlsNumberConverter getDefaultNumberConverterAnnotation() {
        return new XlsNumberConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsNumberConverter.class;
            }
            
            @Override
            public String javaPattern() {
                return "";
            }
            
            @Override
            public String locale() {
                return "";
            }
            
            @Override
            public String currency() {
                return "";
            }
            
            @Override
            public int precision() {
                return 15;
            }
            
            @Override
            public String excelPattern() {
                return "";
            }
            
        };
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final Number targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        
        final XlsNumberConverter anno = adapter.getAnnotation(XlsNumberConverter.class)
                .orElseGet(() -> getDefaultNumberConverterAnnotation());
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        Number value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && defaultValueAnno.isPresent()) {
            final String defaultValue = defaultValueAnno.get().value();
            final NumberFormat formatter;
            final MathContext context;
            
            if(Utils.isNotEmpty(anno.javaPattern())) {
                formatter = createNumberFormat(anno);
                context = createMathContext(anno);
            } else {
                formatter = createNumberFormat(getDefaultNumberConverterAnnotation());
                context = createMathContext(getDefaultNumberConverterAnnotation());
            }
            
            try {
                value = parseNumber(defaultValue, formatter, context);
            } catch (ParseException e) {
                throw newTypeBindException(e, cell, adapter, defaultValue)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        }
        
        // セルの書式の設定
        if(Utils.isNotEmpty(anno.excelPattern()) && !POIUtils.getCellFormatPattern(cell).equalsIgnoreCase(anno.excelPattern())) {
            
            // 既にCell中に書式が設定され、それが異なる場合
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setDataFormat(POIUtils.getDataFormatIndex(sheet, anno.excelPattern()));
            cell.setCellStyle(style);
            
        }
        
        if(value != null && !primaryFormula) {
            cell.setCellValue(value.doubleValue());
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
        
    }
    
}
