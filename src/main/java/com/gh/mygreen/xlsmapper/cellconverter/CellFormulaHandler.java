package com.gh.mygreen.xlsmapper.cellconverter;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * セルの数式を処理する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellFormulaHandler {
    
    /**
     * 数式を直接指定している場合
     */
    private Optional<String> formula = Optional.empty();
    
    /**
     * 数式を取得するメソッドを指定している場合
     */
    private Optional<Method> method = Optional.empty();
    
    /**
     * セルの値が設定済みの時に、数式の設定を優先するかどうか。
     */
    private boolean primaryFormula;
    
    /**
     * 数式を直接指定する場合
     * @param formula 数式。EL式で評価可能な形式。
     * @throws IllegalArgumentException {@literal formula == null or empty.}
     */
    public CellFormulaHandler(final String formula) {
        ArgUtils.notEmpty(formula, "formula");
        
        this.formula = Optional.of(formula);
    }
    
    /**
     * 数式を取得するメソッドを指定する場合
     * @param method 数式を取得するためのメソッド
     * @throws IllegalArgumentException {@literal method == null.}
     */
    public CellFormulaHandler(final Method method) {
        ArgUtils.notNull(method, "method");
        
        this.method = Optional.of(method);
        
    }
    
    /**
     * セルに数式を設定する
     * @param field フィールド情報
     * @param config システム情報
     * @param cell セル情報
     * @param targetBean 処理対象のフィールドが定義されているクラスのインスタンス。
     * @throws ConversionException 数式の解析に失敗した場合。
     */
    public void handleFormula(final FieldAccessor field, final Configuration config, final Cell cell, final Object targetBean) {
        
        ArgUtils.notNull(field, "field");
        ArgUtils.notNull(config, "config");
        ArgUtils.notNull(cell, "cell");
        
        final String evaluatedFormula = createFormulaValue(config, cell, targetBean);
        if(Utils.isEmpty(evaluatedFormula)) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        
        try {
            cell.setCellFormula(evaluatedFormula);
            cell.setCellType(CellType.FORMULA);
            
        } catch(FormulaParseException e) {
            // 数式の解析に失敗した場合
            String message = MessageBuilder.create("cell.failParseFormula")
                    .var("property", field.getNameWithClass())
                    .var("cellAddress", CellPosition.of(cell).toString())
                    .var("formula", evaluatedFormula)
                    .format();
            
            throw new ConversionException(message, e, field.getType());
        }
        
    }
    
    /**
     * Excelの式を組み立てる。
     * @param config システム情報設定
     * @param cell セル情報
     * @param targetBean 処理対象のフィールドが定義されているクラスのインスタンス。
     * @return 組み立てた数式
     */
    public String createFormulaValue(final Configuration config, final Cell cell, final Object targetBean) {
        
        if(formula.isPresent()) {
            final Map<String, Object> vars = new HashMap<>();
            vars.put("rowIndex", cell.getRowIndex());
            vars.put("columnIndex", cell.getColumnIndex());
            vars.put("rowNumber", cell.getRowIndex()+1);
            vars.put("columnNumber", cell.getColumnIndex()+1);
            vars.put("columnAlpha", CellReference.convertNumToColString(cell.getColumnIndex()));
            vars.put("address", CellPosition.of(cell).formatAsString());
            vars.put("targetBean", targetBean);
            vars.put("cell", cell);
            
            return config.getFormulaFormatter().interpolate(formula.get(), vars);
            
        } else if(method.isPresent()) {
            
            // メソッドの引数の組み立て
            final Class<?>[] paramTypes = method.get().getParameterTypes();
            final Object[] paramValues = new Object[paramTypes.length];
            
            for(int i=0; i < paramTypes.length; i++) {
                if(Cell.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = cell;
                    
                } else if(CellPosition.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellPosition.of(cell);
                    
                } else if(Point.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellPosition.of(cell).toPoint();
                    
                } else if(org.apache.poi.ss.util.CellAddress.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellPosition.of(cell).toCellAddress();
                    
                } else if(Sheet.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = cell.getSheet();
                    
                } else if(Configuration.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = config;
                    
                } else {
                    paramValues[i] = null;
                }
            }
            
            try {
                return (String) method.get().invoke(targetBean, paramValues);
                
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final Class<?> targetClass = targetBean.getClass();
                final Throwable t = e.getCause() == null ? e : e.getCause();
                throw new XlsMapperException(
                        String.format("Fail execute method '%s#%s'.", targetClass.getName(), method.get().getName()),
                        t);
            }
            
        } else {
            // 数式や対応するメソッドがない場合
            throw new IllegalStateException("not found for formula or method.");
        }
        
    }
    
    /**
     * セルの値が設定済みの時に、数式の設定を優先するかどうか。
     */
    public boolean isPrimaryFormula() {
        return primaryFormula;
    }
    
    /**
     * セルの値が設定済みの時に、数式の設定を優先するかどうか。
     * @param primaryFormula 数式の設定を優先するかどうか
     */
    public void setPrimaryFormula(boolean primaryFormula) {
        this.primaryFormula = primaryFormula;
    }
    
}
