package com.gh.mygreen.xlsmapper.cellconverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
/**
 * 標準の{@link ElementConverter}の実装クラス。
 * <p>次の基本的な型のみ対応しています。</p>
 * <ul>
 *   <li>String型</li>
 *   <li>プリミティブ型「boolean/char/byte/short/int/long/float/double」と、そのラッパークラス。</li>
 *   <li>{@link BigDecimal}/{@link BigInteger}</li>
 * </ul>
 * 
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class DefaultElementConverter implements ElementConverter<Object> {
    
    /** プリミティブ型のデフォルト値 */
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<>();
    
    static {
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Byte.TYPE, (byte)0);
        primitiveDefaults.put(Short.TYPE, (short)0);
        primitiveDefaults.put(Character.TYPE, (char)0);
        primitiveDefaults.put(Integer.TYPE, 0);
        primitiveDefaults.put(Long.TYPE, 0L);
        primitiveDefaults.put(Float.TYPE, 0.0f);
        primitiveDefaults.put(Double.TYPE, 0.0);
        primitiveDefaults.put(BigInteger.class, new BigInteger("0"));
        primitiveDefaults.put(BigDecimal.class, new BigDecimal("0.0"));
    }
    
    @Override
    public Object convertToObject(final String str, final Class<Object> targetClass) throws ConversionException {
        
        ArgUtils.notNull(targetClass, "targetClass");
        
        try {
            if(targetClass.isAssignableFrom(String.class)) {
                return (Object) (Utils.isEmpty(str) ? null : str.toString());
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(boolean.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Boolean.TYPE) : Boolean.valueOf(str));
            
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(char.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Character.TYPE) : str.charAt(0));
            
            } else if(targetClass.isAssignableFrom(Character.class)) {
                return (Object) (Utils.isEmpty(str) ? null : str.charAt(0));
                
            } else if(targetClass.isAssignableFrom(Boolean.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Boolean.valueOf(str));
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(short.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Short.TYPE) : Short.valueOf(str));
            
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(byte.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Byte.TYPE) : Byte.valueOf(str));
            
            } else if(targetClass.isAssignableFrom(Byte.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Byte.valueOf(str));
                
            } else if(targetClass.isAssignableFrom(Short.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Short.valueOf(str));
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(int.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Integer.TYPE) : Integer.valueOf(str));
            
            } else if(targetClass.isAssignableFrom(Integer.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Integer.valueOf(str));
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(long.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Long.TYPE) : Long.valueOf(str));
            
            } else if(targetClass.isAssignableFrom(Long.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Long.valueOf(str));
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(float.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Float.TYPE) : Float.valueOf(str));
            
            } else if(targetClass.isAssignableFrom(Float.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Float.valueOf(str));
                
            } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(double.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(Double.TYPE) : Double.valueOf(str));
            
            } else if(targetClass.isAssignableFrom(Double.class)) {
                return (Object) (Utils.isEmpty(str) ? null : Double.valueOf(str));
                
            } else if(targetClass.isAssignableFrom(BigInteger.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(BigInteger.class) : new BigInteger(str));
                
            } else if(targetClass.isAssignableFrom(BigDecimal.class)) {
                return (Object) (Utils.isEmpty(str) ? primitiveDefaults.get(BigDecimal.class) : new BigDecimal(str));
            }
        
        } catch(NumberFormatException e) {
            throw new ConversionException(String.format("Cannot convert string to %s.", str), e, targetClass);
        }
        
        throw new ConversionException(String.format("Cannot convert string to Object [%s].", targetClass.getName()), targetClass);
        
    }
    
    @Override
    public String convertToString(final Object value) {
        
        if(value == null) {
            return "";
        }
        
        return value.toString();
        
    }
    
}
