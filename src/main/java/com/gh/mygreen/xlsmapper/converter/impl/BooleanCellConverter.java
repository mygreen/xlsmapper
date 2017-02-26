package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * Boolean/boolean型を処理するConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanCellConverter extends AbstractCellConverter<Boolean> {
    
    @Override
    public Boolean toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config) throws TypeBindException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsBooleanConverter anno = adapter.getAnnotation(XlsBooleanConverter.class)
                .orElseGet(() -> getDefaultBooleanConverterAnnotation());
        
        if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue();
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, trimAnno);
            cellValue = Utils.getDefaultValueIfEmpty(cellValue, defaultValueAnno);
            
            final Boolean result = convertFromString(cellValue, anno);
            if(result == null && Utils.isNotEmpty(cellValue)) {
                // 値が入っていて変換できない場合
                throw newTypeBindException(cell, adapter, cellValue)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
            if(result != null) {
                return result;
            }
        }
        
        if(adapter.getType().isPrimitive()) {
            return Boolean.FALSE;
        }
        
        return null;
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    private Map<String, Object> createTypeErrorMessageVars(final XlsBooleanConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("candidateValues", Utils.join(getLoadingAvailableValue(anno), ", "));
        vars.put("loadForTrue", Utils.join(anno.loadForTrue(), ", "));
        vars.put("loadForFalse", Utils.join(anno.loadForFalse(), ", "));
        vars.put("saveAsTrue", anno.saveAsTrue());
        vars.put("saveAsFalse", anno.saveAsFalse());
        vars.put("ignoreCase", anno.ignoreCase());
        vars.put("failToFalse", anno.failToFalse());
        
        return vars;
    }
    
    private XlsBooleanConverter getDefaultBooleanConverterAnnotation() {
        return new XlsBooleanConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsBooleanConverter.class;
            }
            
            @Override
            public String saveAsTrue() {
                return "true";
            }
            
            @Override
            public String saveAsFalse() {
                return "false";
            }
            
            @Override
            public boolean ignoreCase() {
                return true;
            }
            
            @Override
            public String[] loadForTrue() {
                return new String[]{"true", "1", "yes", "on", "y", "t"};
            }
            
            @Override
            public String[] loadForFalse() {
                return new String[]{"false", "0", "no", "off", "f", "n"};
            }
            
            @Override
            public boolean failToFalse() {
                return false;
            }
        };
    }
    
    /**
     * 読み込み時の入力の候補となる値を取得する
     * @param anno
     * @return
     */
    private Collection<String> getLoadingAvailableValue(final XlsBooleanConverter anno) {
        
        final Set<String> values = new LinkedHashSet<String>();
        values.addAll(Arrays.asList(anno.loadForTrue()));
        values.addAll(Arrays.asList(anno.loadForFalse()));
        return values;
        
    }
    
    private Boolean convertFromString(final String value, final XlsBooleanConverter anno) {
        for(String trueValues : anno.loadForTrue()) {
            if(anno.ignoreCase() && value.equalsIgnoreCase(trueValues)) {
                return Boolean.TRUE;
                
            } else if(!anno.ignoreCase() && value.equals(trueValues)) {
                return Boolean.TRUE;
            }
        }
        
        for(String falseValues : anno.loadForFalse()) {
            if(anno.ignoreCase() && value.equalsIgnoreCase(falseValues)) {
                return Boolean.FALSE;
                
            } else if(!anno.ignoreCase() && value.equals(falseValues)) {
                return Boolean.FALSE;
            }
        }
        
        // 変換できない場合に強制的にエラーとする場合
        if(anno.failToFalse()) {
            return Boolean.FALSE;
        }
        
        return null;
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final Boolean targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsBooleanConverter anno = adapter.getAnnotation(XlsBooleanConverter.class)
                .orElseGet(() -> getDefaultBooleanConverterAnnotation());
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        Boolean value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && defaultValueAnno.isPresent()) {
            value = convertFromString(defaultValueAnno.get().value(), anno);
            
            // 初期値が設定されているが、変換できないような時はエラーとする
            if(value == null) {
                throw newTypeBindException(cell, adapter, defaultValueAnno.get().value())
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
        }
        
        if(value != null && !primaryFormula) {
            if(anno.saveAsTrue().equalsIgnoreCase("true") 
                    && anno.saveAsTrue().equalsIgnoreCase("false")
                    && cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                // テンプレートのセルの書式がbooleanの場合はそのまま設定する
                cell.setCellValue(value);
            } else if(value) {
                cell.setCellValue(anno.saveAsTrue());
            } else {
                cell.setCellValue(anno.saveAsFalse());
            }
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }

}
