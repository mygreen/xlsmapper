package com.gh.mygreen.xlsmapper.cellconvert;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.XlsMapperException;




/**
 * ExcelのCellとJavaオブジェクト間の型変換に失敗した際にスローされる例外。
 * 
 * @author T.TSUCHIE
 *
 */
public class TypeBindException extends XlsMapperException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -5571437827158347334L;
    
    /** バインド先の クラスタイプ*/
    private Class<?> bindClass;
    
    /** バインド対象の値 */
    private final Object targetValue;
    
    /** validation時のメッセージの変数に使用する */
    private final Map<String, Object> messageVars;
    
    public TypeBindException(final Exception e, final String message, final Class<?> bindClass, final Object targetValue) {
        super(message, e);
        this.bindClass = bindClass;
        this.targetValue = targetValue;
        this.messageVars = new LinkedHashMap<String, Object>();
    }
    
    public TypeBindException(final String message, final Class<?> bindClass, final Object targetValue) {
        super(message);
        this.bindClass = bindClass;
        this.targetValue = targetValue;
        this.messageVars = new LinkedHashMap<String, Object>();
    }
    
    public void setBindClass(Class<?> bindClass) {
        this.bindClass = bindClass;
    }
    
    public Class<?> getBindClass() {
        return bindClass;
    }
    
    public Object getTargetValue() {
        return targetValue;
    }
    
    public Map<String, Object> getMessageVars() {
        return messageVars;
    }
    
    public TypeBindException addMessageVar(final String key, Object value) {
        ArgUtils.notEmpty(key, "key");
        getMessageVars().put(key, value);
        return this;
    }
    
    public TypeBindException addAllMessageVars(final Map<String, Object> messageVars) {
        ArgUtils.notNull(messageVars, "messageVars");
        getMessageVars().putAll(messageVars);
        return this;
    }
    
}
