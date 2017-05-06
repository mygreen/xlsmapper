package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
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
    protected Boolean parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        final XlsBooleanConverter convertAnno = accessor.getAnnotation(XlsBooleanConverter.class)
                .orElseGet(() -> getDefaultBooleanConverterAnnotation());
        
        try {
            final Boolean value = convertFromString(defaultValue, convertAnno);
            return value;
            
        } catch(ParseException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
        }
        
    }
    
    @Override
    protected Boolean parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        if(evaluatedCell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
            return evaluatedCell.getBooleanCellValue();
            
        } else if(!formattedValue.isEmpty()) {
            
            final XlsBooleanConverter convertAnno = accessor.getAnnotation(XlsBooleanConverter.class)
                    .orElseGet(() -> getDefaultBooleanConverterAnnotation());
            
            try {
                final Boolean value = convertFromString(formattedValue, convertAnno);
                return value;
                
            } catch(ParseException e) {
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
            }
        }
        
        if(accessor.getType().isPrimitive()) {
            return false;
            
        } else if(accessor.isComponentType() && accessor.getComponentType().isPrimitive()) {
            return false;
        }
        
        return null;
        
    }
    
    @Override
    protected void setupCell(final Cell cell, final Optional<Boolean> cellValue, final FieldAccessor accessor, final Configuration config)
            throws TypeBindException {
        
        final XlsBooleanConverter anno = accessor.getAnnotation(XlsBooleanConverter.class)
                .orElseGet(() -> getDefaultBooleanConverterAnnotation());
        
        if(cellValue.isPresent()) {
            if(anno.saveAsTrue().equalsIgnoreCase("true") 
                    && anno.saveAsTrue().equalsIgnoreCase("false")
                    && cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
                // テンプレートのセルの書式がbooleanの場合はそのまま設定する
                cell.setCellValue(cellValue.get());
                
            } else if(cellValue.get()) {
                
                cell.setCellValue(anno.saveAsTrue());
            } else {
                cell.setCellValue(anno.saveAsFalse());
            }
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
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
    
    private Boolean convertFromString(final String value, final XlsBooleanConverter anno) throws ParseException {
        
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
        
        throw new ParseException(value, 0);
    }

}
