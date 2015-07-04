package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.ConversionException;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * ユーティリティクラス。
 * 
 * @author T.TSUCHIE
 * @author Naoki Takezoe
 * @author Mitsuyoshi Hasegawa
 *
 */
public class Utils {
    
    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays
     * @param separator
     * @param ignoreEmptyItem
     * @param trim
     * @return
     */
    public static String join(final Object[] arrays, final String separator,
            final boolean ignoreEmptyItem, final boolean trim) {
        
        return join(Arrays.asList(arrays), separator, ignoreEmptyItem, trim);
        
    }
    
    /**
     * コレクションの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col
     * @param separator
     * @param ignoreEmptyItem
     * @param trim
     * @return
     */
    public static String join(final Collection<?> col, final String separator,
            final boolean ignoreEmptyItem, final boolean trim) {
        
        final List<Object> list = new ArrayList<Object>();
        for(Object item : col) {
            if(item == null) {
                continue;
            }
            
            Object value = item;
            
            if(item instanceof String) {
                String str = (String) item;
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim();
                }
                
            } else if(item instanceof Character && isEmpty(item.toString())) {
                String str = item.toString();
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim().charAt(0);
                }
                
            } else if(char.class.isAssignableFrom(item.getClass())) {
                String str = item.toString();
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim().charAt(0);
                }
            }
            
            list.add(value);
            
        }
        
        return join(list, separator);
        
    }
    
    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays
     * @param separator
     * @return
     */
    public static String join(final Object[] arrays, final String separator) {
        
        final int len = arrays.length;
        if(arrays == null || len == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < len; i++) {
            sb.append(arrays[i]);
            
            if(separator != null && (i < len-1)) {
                sb.append(separator);
            }
        }
        
        return sb.toString();
        
    }
    
    /**
     * Collectionの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col
     * @param separator
     * @return
     */
    public static String join(final Collection<?> col, final String separator) {
        
        final int size = col.size();
        if(col == null || size == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for(Iterator<?> itr = col.iterator(); itr.hasNext();) {
            final Object item = itr.next();
            sb.append(item.toString());
            
            if(separator != null && itr.hasNext()) {
                sb.append(separator);
            }
        }
        
        return sb.toString();
        
    }
    
    /**
     * 先頭の文字を大文字にする。
     * <pre>
     * Utils.capitalize(null)  = null
     * Utils.capitalize("")    = ""
     * Utils.capitalize("cat") = "Cat"
     * Utils.capitalize("cAt") = "CAt"
     * </pre>
     * @param str
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String capitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }
        
        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toUpperCase())
            .append(str.substring(1))
            .toString();
    }
    
    /**
     * 先頭の文字を小文字にする。
     * @param str
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String uncapitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }
        
        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toLowerCase())
            .append(str.substring(1))
            .toString();
    }
    
    /**
     * 指定したフィールド名に対するGetterメソッドを取得する。
     * 
     * @param clazz
     * @param fieldName
     * @return 見つからない場合はnullを返す。
     */
    public static Method getGetter(final Class<?> clazz, final String fieldName) {
        
        final String methodName = "get" + capitalize(fieldName);
        
        // public method
        try {
            final Method method = clazz.getMethod(methodName, Void.class);
            return method;
        } catch (SecurityException | NoSuchMethodException e) {
        }
        
        // private / protected method
        try {
            final Method method = clazz.getDeclaredMethod(methodName, Void.class);
            method.setAccessible(true);
            return method;
        } catch (SecurityException | NoSuchMethodException e) {
        }
        
        return null;
    }
    
    /**
     * 指定してたメソッドがBooleanのGetterかどうかチェックする。
     * @param method
     * @return メソッド名がisから始まり、戻り値が booleanの場合trueを返す。
     */
    public static boolean isBooleanGetterMethod(final Method method) {
        if(!method.getName().startsWith("is")) {
            return false;
        }
        
        final Class<?> returnType = method.getReturnType();
        return isPrimitiveBoolean(returnType);
    }
    
    /**
     * 指定してたメソッドがBooleanのGetterでないかどうかチェックする。
     * @param method
     * @return メソッド名がisから始まり、戻り値が booleanの場合falseを返す。
     */
    public static boolean isNotBooleanGetterMethod(final Method method) {
        return !isBooleanGetterMethod(method);
    }
    
    public static boolean isPrimitiveBoolean(final Class<?> clazzType) {
        if(clazzType.isPrimitive() && boolean.class.isAssignableFrom(clazzType)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * フィールドのタイプがboolean型がどうかチェックする。
     * @param field
     * @return プリミティブ型のboolean型の場合trueを返す。
     */
    public static boolean isBooleanField(final Field field) {
        final Class<?> clazzType = field.getType();
        return isPrimitiveBoolean(clazzType);
    }
    
    
    /**
     * 指定したフィールド名に対するbooleanのメソッドを取得する
     * 
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Method getBooleanGetter(final Class<?> clazz, final String fieldName) {
        
        final String methodName = "is" + capitalize(fieldName);
        
        // private / protected method
        try {
            final Method method = clazz.getDeclaredMethod(methodName, Void.class);
            method.setAccessible(true);
            return method;
        } catch (SecurityException | NoSuchMethodException e) {
        }
        
        return null;
    }
    
    /**
     * 指定したフィールド名に対するSetterメソッドを取得する。
     * 
     * @param clazz
     * @param fieldName
     * @return 見つからない場合はnullを返す。
     */
    public static Method getSetter(final Class<?> clazz, final String fieldName, final Class<?>... fieldClass) {
        
        final String methodName = "set" + capitalize(fieldName);
        
        // private / protected method
        try {
            final Method method = clazz.getDeclaredMethod(methodName, fieldClass);
            method.setAccessible(true);
            return method;
        } catch (SecurityException | NoSuchMethodException e) {
        }
        
        return null;
    }
    
    /**
     * 指定した名前のフィールドを取得する。
     * @param clazz
     * @param fieldName
     * @return 見つからない場合はnullを返す。
     */
    public static Field getField(final Class<?> clazz, final String fieldName) {
        
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            
        }
        
        return null;
        
    }
    
    /**
     * セルの位置を設定する。
     * <p>「set + 'フィールド名' + Position」のsetterか「'フィールド名' + Position」というフィールド名で決める。
     * <p>フィールド「Map<String, Point> positions」に、設定する。
     * @param column 列
     * @param row 行
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     */
    @SuppressWarnings("unchecked")
    public static void setPosition(final int x, final int y, final Object obj, final String fieldName) {
        
        final Class<?> clazz = obj.getClass();
        final String positionFieldName = fieldName + "Position";
        
        // フィールド positionsの場合
        final String positionMapFieldName = "positions";
        try {
            Field positionMapField  = clazz.getDeclaredField(positionMapFieldName);
            positionMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(positionMapField.getType())) {
                Object positionMapValue = positionMapField.get(obj);
                if(positionMapValue == null) {
                    positionMapValue = new HashMap<String, Point>();
                    positionMapField.set(obj, positionMapValue);
                }
                
                ((Map<String, Point>) positionMapValue).put(fieldName, new Point(x, y));
                return;
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", positionMapFieldName), 
                    e);
        }
        
        
        // メソッドの場合(引数が int, intの場合)
        final Method positionMethod1 = getSetter(clazz, positionFieldName, Integer.TYPE, Integer.TYPE);
        if(positionMethod1 != null) {
            try {
                positionMethod1.invoke(obj, x, y);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' method", positionMethod1.getName()),
                        e);
            }
            
        }
        
        // メソッドの場合(引数が Pointの場合)
        final Method positionMethod2 = getSetter(clazz, positionFieldName, Point.class);
        if(positionMethod2 != null) {
            try {
                positionMethod2.invoke(obj, new Point(x, y));
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' method", positionMethod2.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合
        final Field positionField = getField(clazz, positionFieldName);
        if(positionField != null) {
            try {
                positionField.setAccessible(true);
                positionField.set(obj, new Point(x, y));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' field", positionField.getName()),
                        e);
            }
        }
        
        
    }
    
    /**
     * セルの位置を取得する
     * <p>「get + 'フィールド名' + Position」のgetterか「'フィールド名' + Position」というフィールド名で決める。
     * <p>フィールド「Map<String, Point> positions」に、設定する。
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     * @return 座標が取得できない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public static Point getPosition(final Object obj, final String fieldName) {
        
        final Class<?> clazz = obj.getClass();
        final String positionFieldName = fieldName + "Position";
        
        // フィールド positionsの場合
        final String positionMapFieldName = "positions";
        try {
            Field positionMapField  = clazz.getDeclaredField(positionMapFieldName);
            positionMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(positionMapField.getType())) {
                Object positionMapValue = positionMapField.get(obj);
                if(positionMapValue == null) {
                    return null;
                }
                
                return ((Map<String, Point>) positionMapValue).get(fieldName);
                
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", positionMapFieldName), 
                    e);
        }
        
        // メソッドの場合
        final Method positionMethod1 = getGetter(clazz, positionFieldName);
        if(positionMethod1 != null) {
            try {
                final Object positionValue = positionMethod1.invoke(obj);
                if(Point.class.isAssignableFrom(positionMethod1.getReturnType())) {
                    return (Point) positionValue;
                } else {
                    throw new RuntimeException(
                            String.format("method '%s' return type not '%s'", positionMethod1.getName(), Point.class.getName()));
                }
                
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' method", positionMethod1.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合
        final Field positionField = getField(clazz, positionFieldName);
        if(positionField != null) {
            try {
                positionField.setAccessible(true);
                
                final Object positionValue = positionField.get(obj);
                if(Point.class.isAssignableFrom(positionValue.getClass())) {
                    return (Point) positionValue;
                } else {
                    throw new RuntimeException(
                            String.format("field '%s' return type not '%s'", positionField.getName(), Point.class.getName()));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' field", positionField.getName()),
                        e);
            }
        }
        
        return null;
        
    }
    
    /**
     * MapColumn形式の場合のセルの位置を設定する。
     * <p>「set + 'フィールド名' + Position」のsetterか「'フィールド名' + Position」というフィールド名で決める。
     * <p>フィールド「Map<String, Point> positions」に、設定する。
     * @param column 列
     * @param row 行
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static void setPositionWithMapColumn(final int x, final int y, final Object obj, final String fieldName, final String key) {
        
        final Class<?> clazz = obj.getClass();
        final String positionFieldName = fieldName + "Position";
        
        // フィールド positionsの場合
        final String positionMapFieldName = "positions";
        try {
            Field positionMapField  = clazz.getDeclaredField(positionMapFieldName);
            positionMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(positionMapField.getType())) {
                Object positionMapValue = positionMapField.get(obj);
                if(positionMapValue == null) {
                    positionMapValue = new HashMap<String, Point>();
                    positionMapField.set(obj, positionMapValue);
                }
                
                final String mapKey = String.format("%s[%s]", fieldName, key);
                ((Map<String, Point>) positionMapValue).put(mapKey, new Point(x, y));
                return;
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", positionMapFieldName), 
                    e);
        }
        
        // メソッドの場合(引数が int, int, Stringの場合)
        final Method positionMethod1 = getSetter(clazz, positionFieldName, String.class, Integer.TYPE, Integer.TYPE);
        if(positionMethod1 != null) {
            try {
                positionMethod1.invoke(obj, key, x, y);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set map position with '%s' method", positionMethod1.getName()),
                        e);
            }
            
        }
        
        // メソッドの場合(引数が String, Pointの場合)
        final Method positionMethod2 = getSetter(clazz, positionFieldName, String.class, Point.class);
        if(positionMethod2 != null) {
            try {
                positionMethod2.invoke(obj, key, new Point(x, y));
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set map position with '%s' method", positionMethod2.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合(Map<String, Point>)の場合
        final Field positionField = getField(clazz, positionFieldName);
        if(positionField != null) {
            try {
                positionField.setAccessible(true);
                if(Map.class.isAssignableFrom(positionField.getType())) {
                    Object positionValue = positionField.get(obj);
                    if(positionValue == null) {
                        positionValue = new HashMap<String, Point>();
                        positionField.set(obj, positionValue);
                    }
                    
                    ((Map<String, Point>) positionValue).put(key, new Point(x, y));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set position with '%s' field", positionField.getName()),
                        e);
            }
        }
        
        
    }
    
    /**
     * セルの見出しを設定する。
     * <p>「set + 'フィールド名' + Label」のsetterか「'フィールド名' + Label」というフィールド名で決める。
     * <p>フィールド「Map<String, String> labels」に、設定する。
     * @param label 設定する見出し
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     */
    @SuppressWarnings("unchecked")
    public static void setLabel(final String label, final Object obj, final String fieldName) {
        
        final Class<?> clazz = obj.getClass();
        final String labelFieldName = fieldName + "Label";
        
        // フィールド labelsの場合
        final String labelMapFieldName = "labels";
        try {
            Field labelMapField  = clazz.getDeclaredField(labelMapFieldName);
            labelMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(labelMapField.getType())) {
                Object labelMapValue = labelMapField.get(obj);
                if(labelMapValue == null) {
                    labelMapValue = new HashMap<String, Point>();
                    labelMapField.set(obj, labelMapValue);
                }
                
                ((Map<String, String>) labelMapValue).put(fieldName, label);
                return;
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", labelMapFieldName), 
                    e);
        }
        
        // メソッドの場合(引数が String の場合)
        final Method labelMethod1 = getSetter(clazz, labelFieldName, String.class);
        if(labelMethod1 != null) {
            try {
                labelMethod1.invoke(obj, label);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' method", labelMethod1.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合
        final Field labelField = getField(clazz, labelFieldName);
        if(labelField != null) {
            try {
                labelField.setAccessible(true);
                labelField.set(obj, label);
                return;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' field", labelField.getName()),
                        e);
            }
        }
        
    }
    
    /**
     * セルの見出しを取得する。
     * <p>「get + 'フィールド名' + Label」のgetterか「'フィールド名' + Label」というフィールド名で決める。
     * <p>フィールド「Map<String, String> labels」に、設定する。
     * @param label 設定する見出し
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     */
    @SuppressWarnings("unchecked")
    public static String getLabel(final Object obj, final String fieldName) {
        
        final Class<?> clazz = obj.getClass();
        final String labelFieldName = fieldName + "Label";
        
        // フィールド labelsの場合
        final String labelMapFieldName = "labels";
        try {
            Field labelMapField  = clazz.getDeclaredField(labelMapFieldName);
            labelMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(labelMapField.getType())) {
                Object labelMapValue = labelMapField.get(obj);
                if(labelMapValue == null) {
                    return null;
                }
                
                return ((Map<String, String>) labelMapValue).get(fieldName);
                
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", labelMapFieldName), 
                    e);
        }
        
        // メソッドの場合
        final Method labelMethod1 = getSetter(clazz, labelFieldName);
        if(labelMethod1 != null) {
            try {
                final Object labelValue = labelMethod1.invoke(obj);
                if(Point.class.isAssignableFrom(labelMethod1.getReturnType())) {
                    return (String) labelValue;
                } else {
                    throw new RuntimeException(
                            String.format("method '%s' return type not '%s'", labelMethod1.getName(), String.class.getName()));
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' method", labelMethod1.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合
        final Field labelField = getField(clazz, labelFieldName);
        if(labelField != null) {
            try {
                labelField.setAccessible(true);
                final Object labelValue = labelField.get(obj);
                if(Point.class.isAssignableFrom(labelField.getType())) {
                    return (String) labelValue;
                } else {
                    throw new RuntimeException(
                            String.format("field '%s' type not '%s'", labelField.getName(), String.class.getName()));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' field", labelField.getName()),
                        e);
            }
        }
        
        return null;
        
    }
    
    /**
     * セルの見出しを設定する。
     * <p>「set + 'フィールド名' + Label」のsetterか「'フィールド名' + Label」というフィールド名で決める。
     * <p>フィールド「Map<String, String> labels」に、設定する。
     * @param label 設定する見出し
     * @param obj メソッドが定義されているオブジェクト
     * @param fieldName フィールド名
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static void setLabelWithMapColumn(final String label, final Object obj, final String fieldName, final String key) {
        
        final Class<?> clazz = obj.getClass();
        final String labelFieldName = fieldName + "Label";
        
        // フィールド labelsの場合
        final String labelMapFieldName = "labels";
        try {
            Field labelMapField  = clazz.getDeclaredField(labelMapFieldName);
            labelMapField.setAccessible(true);
            if(Map.class.isAssignableFrom(labelMapField.getType())) {
                Object labelMapValue = labelMapField.get(obj);
                if(labelMapValue == null) {
                    labelMapValue = new HashMap<String, String>();
                    labelMapField.set(obj, labelMapValue);
                }
                
                final String mapKey = String.format("%s[%s]", fieldName, key);
                ((Map<String, String>) labelMapValue).put(mapKey, label);
                return;
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は何もしない。
            
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("fail set '%s' field", labelMapFieldName), 
                    e);
        }
        
        // メソッドの場合(引数が String, String の場合)
        final Method labelMethod1 = getSetter(clazz, labelFieldName, String.class, String.class);
        if(labelMethod1 != null) {
            try {
                labelMethod1.invoke(obj, key, label);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' method", labelMethod1.getName()),
                        e);
            }
            
        }
        
        // フィールドの場合
        final Field labelField = getField(clazz, labelFieldName);
        if(labelField != null) {
            try {
                labelField.setAccessible(true);
                
                if(Map.class.isAssignableFrom(labelField.getType())) {
                    Object labelValue = labelField.get(obj);
                    if(labelValue == null) {
                        labelValue = new HashMap<String, Point>();
                        labelField.set(obj, labelValue);
                    }
                    
                    ((Map<String, String>) labelValue).put(key, label);
                }
                return;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("fail set label with '%s' field", labelField.getName()),
                        e);
            }
        }
        
    }
    
    /**
     * 文字列が空文字か判定する。
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str) {
        if(str == null || str.isEmpty()) {
            return true;
        }
        
        if(str.length() == 1) {
            return str.charAt(0) == '\u0000';
        }
        
        return false;
    }
    
    /**
     * 文字列が空文字でないか判定する。
     * @param str
     * @return
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
    
    /**
     * コレクションが空か判定する。
     * @param str
     * @return nullまたはサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Collection<?> collection) {
        if(collection == null || collection.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isNotEmpty(final Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 配列がが空か判定する。 
     * @param arrays
     * @return nullまたは、配列のサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Object[] arrays) {
        if(arrays == null || arrays.length == 0) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 配列が空でないか判定する
     * @param arrays
     * @return
     */
    public static boolean isNotEmpty(final Object[] arrays) {
        return !isEmpty(arrays);
    }
    
    private static final Pattern PATTERN_CELL_ADREESS = Pattern.compile("^([a-zA-Z]+)([0-9]+)$");
    
    /**
     * Excelのアドレス形式'A1'を、Pointに変換する。
     * @param address
     * @return 変換できない場合は、nullを返す。
     */
    public static Point parseCellAddress(final String address) {
        
        if(isEmpty(address)) {
            return null;
        }
        
        final Matcher matcher = PATTERN_CELL_ADREESS.matcher(address);
        if(!matcher.matches()) {
            return null;
        }
        
        final CellReference ref = new CellReference(address.toUpperCase());
        return new Point(ref.getCol(), ref.getRow());
    }
    
    /**
     * 座標をExcelのアドレス形式'A1'などに変換する
     * @param rowIndex 行インデックス
     * @param colIndex 列インデックス
     * @return
     */
    public static String formatCellAddress(final int rowIndex, final int colIndex) {
        return POIUtils.formatCellAddress(rowIndex, colIndex);
    }
    
    /**
     * 座標をExcelのアドレス形式'A1'になどに変換する。
     * @param address
     * @return
     * @throws IllegalArgumentException address == null.
     */
    public static String formatCellAddress(final Point cellAddress) {
        return POIUtils.formatCellAddress(cellAddress);
    }
    
    /**
     * セルのアドレス'A1'を取得する。
     * @param cell
     * @return IllegalArgumentException cell == null.
     */
    public static String formatCellAddress(final Cell cell) {
        return POIUtils.formatCellAddress(cell);
    }
    
    /**
     * 指定したラベル（値を持つ）セルを検索し、取得する。
     * <p>見つからない場合は、例外{@link CellNotFoundException}をスローする。
     * @param sheet 検索対象のシート。
     * @param label 検索するセルの値
     * @param from 検索開始位置の列
     * @param config システム設定
     * @return  引数labelで指定した値を持つセル。
     * @throws CellNotFoundException シート中に引数'label'を持つセルが存在しない場合。
     */
    public static Cell getCell(final Sheet sheet, final String label, final int from,
            final XlsMapperConfig config) throws CellNotFoundException {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notMin(from, 0, "from");
        ArgUtils.notNull(config, "config");
        
        return getCell(sheet, label, from, true, config);
    }
    
    /**
     * 指定したラベル（値を持つ）セルを検索し、取得する。
     * @param sheet 検索対象のシート。
     * @param label 検索するセルの値
     * @param from 検索開始位置の列
     * @param throwableWhenNotFound セルが見つからない場合例外をスローするかどうか。falseの場合、nullを返す。
     * @return 引数labelで指定した値を持つセル。見つからに場合は、nullを返す。
     * @throws CellNotFoundException シート中に引数'label'を持つセルが存在しない場合。
     */
    public static Cell getCell(final Sheet sheet, final String label, final int from,
            boolean throwableWhenNotFound, final XlsMapperConfig config) throws CellNotFoundException {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notMin(from, 0, "from");
        ArgUtils.notNull(config, "config");
        
        final int rows = POIUtils.getColumns(sheet);
        for(int i=0; i < rows; i++) {
            final Cell[] columns = POIUtils.getColumn(sheet, i);
            for(int j=from; j < columns.length; j++) {
                final String cellValue = POIUtils.getCellContents(columns[j], config.getCellFormatter());
                if(cellValue.equals(label)) {
                    return columns[j];
                }
                
            }
        }
        
        if(throwableWhenNotFound) {
            throw new CellNotFoundException(sheet.getSheetName(), label);
        }
        
        return null;
    }
    
    /**
     * 指定したラベル（値を持つ）セルを検索し、取得する。
     * 
     * @since 0.5
     * @param sheet 検索対象のシート。
     * @param label 検索するセルの値
     * @param fromCol 検索開始位置の列のインデックス
     * @param fromRow 検索開始位置の行のインデックス
     * @param throwableWhenNotFound セルが見つからない場合例外をスローするかどうか。falseの場合、nullを返す。
     * @return 引数labelで指定した値を持つセル。見つからに場合は、nullを返す。
     * @throws CellNotFoundException シート中に引数'label'を持つセルが存在しない場合。
     */
    public static Cell getCell(final Sheet sheet, final String label, final int fromCol, final int fromRow,
            final XlsMapperConfig config) throws CellNotFoundException {
        return getCell(sheet, label, fromCol, fromRow, true, config);
    }
    
    /**
     * 指定したラベル（値を持つ）セルを検索し、取得する。
     * 
     * @since 0.5
     * @param sheet 検索対象のシート。
     * @param label 検索するセルの値
     * @param fromCol 検索開始位置の列のインデックス
     * @param fromRow 検索開始位置の行のインデックス
     * @param throwableWhenNotFound セルが見つからない場合例外をスローするかどうか。falseの場合、nullを返す。
     * @return 引数labelで指定した値を持つセル。見つからに場合は、nullを返す。
     * @throws CellNotFoundException シート中に引数'label'を持つセルが存在しない場合。ただし、引数throwableWhenNotFound=trueの場合のみ。
     */
    public static Cell getCell(final Sheet sheet, final String label, final int fromCol, final int fromRow,
            final boolean throwableWhenNotFound, final XlsMapperConfig config) throws CellNotFoundException {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notMin(fromCol, 0, "fromCol");
        ArgUtils.notMin(fromRow, 0, "fromRow");
        ArgUtils.notNull(config, "config");
        
        final int maxRow = POIUtils.getRows(sheet);
        for(int i=fromRow; i < maxRow; i++) {
            final Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }
            
            final int maxCol = row.getLastCellNum();;
            for(int j=fromCol; j < maxCol; j++) {
                final Cell cell = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
                final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(cellValue.equals(label)) {
                    return cell;
                }
            }
        }
        
        if(throwableWhenNotFound) {
            throw new CellNotFoundException(sheet.getSheetName(), label);
        }
        
        return null;
    }
    
    /**
     * Return cell object by using first argument sheet.
     * This cell will be found by label name in Excel sheet.
     *
     * NOTICE: When the cell object is specified for the third argument,
     * a lower right cell is scanned from the cell.
     *
     * @param sheet JExcel Api sheet object.
     * @param label Target cell label.
     * @param after A lower right cell is scanned from the cell object.
     * @param includeingAfter Is the third argument cell object scanned?
     *
     * @return Target JExcel Api cell object.
     * @throws CellNotFoundException This occures when the cell is not found.
     */
    public static Cell getCell(final Sheet sheet, final String label, final Cell after,
            final boolean includeAfter, final XlsMapperConfig config) throws CellNotFoundException {
        return getCell(sheet, label, after, includeAfter, true, config);
    }

    /**
     * Return cell object by using first argument sheet.
     * This cell will be found by label name in Excel sheet.
     *
     * NOTICE: When the cell object is specified for the third argument,
     * a lower right cell is scanned from the cell.
     *
     * @param sheet JExcel Api sheet object.
     * @param label Target cell label.
     * @param after A lower right cell is scanned from the cell object.
     * @param includeingAfter Is the third argument cell object scanned?
     * @param throwableWhenNotFound If this argument is true, throws XLSBeansException when we can't find target cell.
     *
     * @return Target JExcel Api cell object.
     * @throws CellNotFoundException This occures when the cell is not found.
     */
    public static Cell getCell(final Sheet sheet, final String label, final Cell after,
            boolean includeAfter, boolean throwableWhenNotFound, final XlsMapperConfig config) throws CellNotFoundException {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(label, "label");
        
        if (after == null) {
            // Call XLSBeans#getCell() - method if third argument is null.
            return Utils.getCell(sheet, label, 0, 0, throwableWhenNotFound, config);
        }
        
        //[TO POI]
        int columnStart = after.getColumnIndex();
        int rowStart = after.getRowIndex();
        
        final int maxRow = POIUtils.getRows(sheet);
        for(int i=rowStart; i < maxRow; i++) {
            final Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }
            
            final int maxCol = row.getLastCellNum();;
            for(int j=columnStart; j < maxCol; j++) {
                
                if(!includeAfter && i == rowStart && j == columnStart) {
                    continue;
                }
                
                final Cell cell = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
                final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(cellValue.equals(label)) {
                    return cell;
                }
            }
        }
        
        if(throwableWhenNotFound) {
            throw new CellNotFoundException(sheet.getSheetName(), label);
        }
        
        return null;
    }
    
    /**
     * Return cell object by using first argument sheet.
     * This cell will be found by label name in Excel sheet.
     *
     * NOTICE: When the cell object is specified for the third argument,
     * a lower right cell is scanned from the cell.
     *
     * @param sheet JExcel Api sheet object.
     * @param label Target cell label.
     * @param after A lower right cell is scanned from this cell object.
     * @param config api configuration.
     * @return Target JExcel Api cell object.
     * @throws XlsMapperException This occures when the cell is not found.
     */
    public static Cell getCell(final Sheet sheet, final String label, final Cell after, final XlsMapperConfig config) throws CellNotFoundException {
        return getCell(sheet, label, after, false, config);
    }
    
    /**
     * Setterメソッドか判定を行う。
     * <ol>判定基準は次の通り。
     *  <li>メソッド名が'set'から始まる。
     *  <li>メソッドの引数が1つのみ。
     *  
     * @param method
     * @return
     * @throws IllegalArgumentException arg 'method' == null.
     */
    public static boolean isSetterMethod(final Method method) {
        ArgUtils.notNull(method, "method");
        
        method.setAccessible(true);
        
        if(!method.getName().startsWith("set")) {
            return false;
        }
        
        if(method.getParameterTypes().length != 1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Setterメソッドでないか判定を行う。
     * {@link #isSetterMethod(Method)}の否定。
     * @param method
     * @return
     */
    public static boolean isNotSetterMethod(final Method method) {
        return !isSetterMethod(method);
    }
    
    /**
     * Getterメソッドか判定を行う。
     * <ol>判定基準は次の通り。
     *  <li>メソッド名が'get'から始まる。
     *  <li>メソッドの引数が0個。
     *  
     * @param method
     * @return
     * @throws IllegalArgumentException arg 'method' == null.
     */
    public static boolean isGetterMethod(final Method method) {
        ArgUtils.notNull(method, "method");
        
        method.setAccessible(true);
        
        if(!method.getName().startsWith("get")) {
            return false;
        }
        
        if(method.getParameterTypes().length != 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Getterメソッドでないか判定を行う。
     * {@link #isGetterMethod(Method)}の否定。
     * @param method
     * @return
     */
    public static boolean isNotGetterMethod(final Method method) {
        return !isGetterMethod(method);
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されている読み込み系の指定したオブジェクトのメソッド（Setter）とフィールド情報を取得する。
     * <p>フィールドは、public以外の全てのメソッドを対象とする。
     * 
     * @param clazz
     * @param name 属性columnNameと比較する値。名前がない場合はnullを指定する。
     * @param annoReader
     * @return
     */
    public static List<FieldAdaptor> getLoadingColumnProperties(final Class<?> clazz, final String name, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(reader, "reader");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        
        for(FieldAdaptor adaptor : getSetterColumnMethods(clazz, name, reader)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getColumnFields(clazz, name, reader)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        
        return list;
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されている書き込み系の指定したオブジェクトのメソッド（Setter）とフィールド情報を取得する。
     * <p>フィールドは、public以外の全てのメソッドを対象とする。
     * 
     * @param clazz
     * @param name 属性columnNameと比較する値。名前がない場合はnullを指定する。
     * @param annoReader
     * @return
     */
    public static List<FieldAdaptor> getSavingColumnProperties(final Class<?> clazz, final String name, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(reader, "reader");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        
        for(FieldAdaptor adaptor : getGetterColumnMethods(clazz, name, reader)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getColumnFields(clazz, name, reader)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        
        return list;
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されているSetterメソッドを取得する。
     * さらに、引数nameで指定した値と属性columnNameと一致するものを取得する。
     * ただし、引数nameがnullの場合は、一致するものを判断する。
     * <p>publicメソッドを取得する。
     * @param clazz
     * @param name 属性columnNameと比較する値。名前がない場合はnullを指定する。
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getSetterColumnMethods(final Class<?> clazz, final String name, final AnnotationReader reader) {
        ArgUtils.notNull(clazz, "clazz");
        
        //TODO: 例外のスロー内容を見直す。
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Method method : clazz.getMethods()) {
            if(isNotSetterMethod(method)) {
                continue;
            }
            
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, reader);
            final XlsColumn column = adaptor.getLoadingAnnotation(XlsColumn.class);
            if(column == null) {
                continue;
            }
            
            final String columnName = column.columnName();
            if(name == null) {
                result.add(adaptor);
            } else if(columnName.equals(name)) {
                result.add(adaptor);
            }
            
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されているGetterメソッドを取得する。
     * さらに、引数nameで指定した値と属性columnNameと一致するものを取得する。
     * ただし、引数nameがnullの場合は、一致するものを判断する。
     * <p>publicメソッドを取得する。
     * @param clazz
     * @param name 属性columnNameと比較する値。名前がない場合はnullを指定する。
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getGetterColumnMethods(final Class<?> clazz, final String name, final AnnotationReader reader) {
        ArgUtils.notNull(clazz, "clazz");
        
        //TODO: 例外のスロー内容を見直す。
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Method method : clazz.getMethods()) {
            if(isNotGetterMethod(method) || isNotBooleanGetterMethod(method)) {
                continue;
            }
            
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, reader);
            final XlsColumn column = adaptor.getSavingAnnotation(XlsColumn.class);
            if(column == null) {
                continue;
            }
            
            final String columnName = column.columnName();
            if(name == null) {
                result.add(adaptor);
            } else if(columnName.equals(name)) {
                result.add(adaptor);
            }
            
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されているフィールドを取得する。
     * さらに、引数nameで指定した値と属性columnNameと一致するものを取得する。
     * ただし、引数nameがnullの場合は、一致するものを判断する。
     * <p>publicメソッド以外も対象とする。
     * @param clazz
     * @param name 名前を指定しない場合はnullを設定。
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getColumnFields(final Class<?> clazz, final String name, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            FieldAdaptor adaptor = new FieldAdaptor(clazz, field, reader);
            final XlsColumn column = adaptor.getLoadingAnnotation(XlsColumn.class);
            if(column == null) {
                continue;
            }
            
            final String columnName = column.columnName();
            if(name == null) {
                result.add(adaptor);
            } else if(columnName.equals(name)) {
                result.add(adaptor);
            }
            
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * アノテーション{@link MapXlsColumn}が付与されている読み込み系の指定したオブジェクトのメソッド（Setter）とフィールド情報を取得する。
     * <p>フィールドは、public以外の全てのメソッドを対象とする。
     * 
     * @param clazz
     * @param annoReader
     * @return
     */
    public static List<FieldAdaptor> getLoadingMapColumnProperties(final Class<?> clazz, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        
        for(FieldAdaptor adaptor : getSetterMapColumnMethods(clazz, reader)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getMapColumnFields(clazz, reader)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        
        return list;
        
    }
    
    /**
     * アノテーション{@link MapXlsColumn}が付与されている読み込み系の指定したオブジェクトのメソッド（Getter）とフィールド情報を取得する。
     * <p>フィールドは、public以外の全てのメソッドを対象とする。
     * 
     * @param clazz
     * @param annoReader
     * @return
     */
    public static List<FieldAdaptor> getSavingMapColumnProperties(final Class<?> clazz, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        
        for(FieldAdaptor adaptor : getGetterMapColumnMethods(clazz, reader)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getMapColumnFields(clazz, reader)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        
        return list;
        
    }
    
    /**
     *  アノテーション{@link XlsMapColumns}が付与されているsetterメソッドを取得する。
     * @param clazz
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getSetterMapColumnMethods(Class<?> clazz, AnnotationReader reader) {
        return getSetterMethodsWithAnnotation(clazz, reader, XlsMapColumns.class);
        
    }
    
    /**
     *  アノテーション{@link XlsMapColumns}が付与されているGetterメソッドを取得する。
     * @param clazz
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getGetterMapColumnMethods(Class<?> clazz, AnnotationReader reader) {
        return getGetterMethodsWithAnnotation(clazz, reader, XlsMapColumns.class);
        
    }
    
    /**
     * アノテーション{@link MapXlsColumn}が付与されているフィールドを取得する。
     * <p>publicメソッド以外も対象とする。
     * @param clazz
     * @param reader
     * @return
     */
    public static FieldAdaptor[] getMapColumnFields(final Class<?> clazz, final AnnotationReader reader) {
        
        return getFieldsWithAnnotation(clazz, reader, XlsMapColumns.class);
    }
    
    /**
     * 指定したアノテーションを持つ読み込み系メソッド（Setter）とフィールド情報を取得する。
     * @param clazz
     * @param reader
     * @param annoClass
     * @return
     */
    public static List<FieldAdaptor> getLoadingPropertiesWithAnnotation(final Class<?> clazz, final AnnotationReader reader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        for(FieldAdaptor adaptor : getSetterMethodsWithAnnotation(clazz, reader, annoClass)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getFieldsWithAnnotation(clazz, reader, annoClass)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        return list;
    }
    
    /**
     * 指定したアノテーションを持つ読み込み系メソッド（Setter）とフィールド情報を取得する。
     * @param clazz
     * @param reader
     * @param annoClass
     * @return
     */
    public static List<FieldAdaptor> getSavingPropertiesWithAnnotation(final Class<?> clazz, final AnnotationReader reader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final List<FieldAdaptor> list = new ArrayList<>();
        for(FieldAdaptor adaptor : getGetterMethodsWithAnnotation(clazz, reader, annoClass)) {
            list.add(adaptor);
        }
        
        for(FieldAdaptor adaptor : getFieldsWithAnnotation(clazz, reader, annoClass)) {
            if(list.contains(adaptor)) {
                continue;
            }
            
            list.add(adaptor);
        }
        return list;
    }
    
    /**
     * 指定したアノテーションが付与されたSetterメソッド情報を取得する。
     * @param clazz
     * @param reader
     * @param annoClass
     * @return
     */
    public static FieldAdaptor[] getSetterMethodsWithAnnotation(final Class<?> clazz, final AnnotationReader reader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Method method : clazz.getMethods()) {
            if(isNotSetterMethod(method)) {
                continue;
            }
            
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, reader);
            if(adaptor.hasLoadingAnnotation(annoClass)) {
                result.add(adaptor);
            }
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * 指定したアノテーションが付与されたGetterメソッド情報を取得する。
     * @param clazz
     * @param reader
     * @param annoClass
     * @return
     */
    public static FieldAdaptor[] getGetterMethodsWithAnnotation(final Class<?> clazz, final AnnotationReader reader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Method method : clazz.getMethods()) {
            if(isNotGetterMethod(method) || isNotBooleanGetterMethod(method)) {
                continue;
            }
            
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, reader);
            if(adaptor.hasSavingAnnotation(annoClass)) {
                result.add(adaptor);
            }
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * 指定したアノテーションが付与されたフィールド情報を取得する。
     * @param clazz
     * @param reader
     * @param annoClass
     * @return
     */
    public static FieldAdaptor[] getFieldsWithAnnotation(final Class<?> clazz, final AnnotationReader reader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final List<FieldAdaptor> result = new ArrayList<>();
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, field, reader);
            if(adaptor.hasLoadingAnnotation(annoClass)) {
                result.add(adaptor);
            }
        }
        
        return result.toArray(new FieldAdaptor[result.size()]);
    }
    
    /**
     * オブジェクトの比較を行う。
     * <p>値がnullの場合を考慮する。
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean equals(final Object obj1, final Object obj2) {
        
        if(obj1 == null && obj2 == null) {
            return true;
        }
        
        if(obj1 == null) {
            return false;
        }
        
        if(obj2 == null) {
            return false;
        }
        
        return obj1.equals(obj2);
        
    }
    
    public static boolean notEquals(final Object obj1, final Object obj2) {
        return !equals(obj1, obj2);
    }
    
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
        primitiveDefaults.put(BigDecimal.class, new BigDecimal(0.0));
    }
    
    /**
     * 文字列をプリミティブ型に変換する。
     * <p>文字列の値がnullまたは空文字の場合、プリミティブ型の場合ゼロなどのデフォルト値を返す。オブジェクト型の場合はnullを返す。
     * @param str
     * @param targetClass 変換後のクラスタイプ
     * @return
     * @throws ConversionException 引数classTypeが対応していないクラス型の場合。
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToObject(final String str, final Class<T> targetClass) throws ConversionException {
        
        ArgUtils.notNull(targetClass, "targetClass");
        
        if(targetClass.isAssignableFrom(String.class)) {
            return (T) (Utils.isEmpty(str) ? null : str.toString());
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(boolean.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Boolean.TYPE) : Boolean.valueOf(str));
        
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(char.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Character.TYPE) : str.charAt(0));
        
        } else if(targetClass.isAssignableFrom(Character.class)) {
            return (T) (Utils.isEmpty(str) ? null : str.charAt(0));
            
        } else if(targetClass.isAssignableFrom(Boolean.class)) {
            return (T) (Utils.isEmpty(str) ? null : Boolean.valueOf(str));
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(short.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Short.TYPE) : Short.valueOf(str));
        
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(byte.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Byte.TYPE) : Byte.valueOf(str));
        
        } else if(targetClass.isAssignableFrom(Byte.class)) {
            return (T) (Utils.isEmpty(str) ? null : Byte.valueOf(str));
            
        } else if(targetClass.isAssignableFrom(Short.class)) {
            return (T) (Utils.isEmpty(str) ? null : Short.valueOf(str));
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(int.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Integer.TYPE) : Integer.valueOf(str));
        
        } else if(targetClass.isAssignableFrom(Integer.class)) {
            return (T) (Utils.isEmpty(str) ? null : Integer.valueOf(str));
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(long.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Long.TYPE) : Long.valueOf(str));
        
        } else if(targetClass.isAssignableFrom(Long.class)) {
            return (T) (Utils.isEmpty(str) ? null : Long.valueOf(str));
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(float.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Float.TYPE) : Float.valueOf(str));
        
        } else if(targetClass.isAssignableFrom(Float.class)) {
            return (T) (Utils.isEmpty(str) ? null : Float.valueOf(str));
            
        } else if(targetClass.isPrimitive() && targetClass.isAssignableFrom(double.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(Double.TYPE) : Double.valueOf(str));
        
        } else if(targetClass.isAssignableFrom(Double.class)) {
            return (T) (Utils.isEmpty(str) ? null : Double.valueOf(str));
            
        } else if(targetClass.isAssignableFrom(BigInteger.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(BigInteger.class) : new BigInteger(str));
            
        } else if(targetClass.isAssignableFrom(BigDecimal.class)) {
            return (T) (Utils.isEmpty(str) ? primitiveDefaults.get(BigDecimal.class) : new BigDecimal(str));
        }
        
        throw new ConversionException(String.format("Cannot convert string to Object [%s].", targetClass.getName()), targetClass);
        
    }
    
    public static String convertToString(final Object value) {
        
        if(value == null) {
            return "null";
        }
        
        return value.toString();
        
    }
    
    /**
     * アノテーションの属性trimに従い、文字列をトリムする。
     * @param value
     * @param converterAnno
     * @return
     */
    public static String trim(final String value, final XlsConverter converterAnno) {
        
        if(converterAnno == null || !converterAnno.trim() || value == null) {
            return value;
        }
        
        return value.trim();
        
    }
    
    /**
     * アノテーションの属性trimに従い、文字列をトリムする。
     * @param value
     * @param converterAnno
     * @return
     */
    public static String trim(final Character value, final XlsConverter converterAnno) {
        
        if(value == null) {
            return "";
        }
        
        if(converterAnno == null || !converterAnno.trim()) {
            return String.valueOf(value);
        }
        
        return String.valueOf(value).trim();
        
    }
    
    /**
     * デフォルト値がアノテーションに設定されているかどうか。
     * @param converterAnno
     * @return
     */
    public static boolean hasDefaultValue(final XlsConverter converterAnno) {
        if(converterAnno == null || isEmpty(converterAnno.defaultValue())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * デフォルト値がアノテーションに設定されていないかどうか。
     * @param converterAnno
     * @return
     */
    public static boolean hasNotDefaultValue(final XlsConverter converterAnno) {
        return !hasDefaultValue(converterAnno);
    }
    
    /**
     * デフォルト値がアノテーションに設定されているかどうか。
     * @param converterAnno
     * @return
     */
    public static String getDefaultValue(final XlsConverter converterAnno) {
        if(converterAnno == null || isEmpty(converterAnno.defaultValue())) {
            return null;
        }
        
        return converterAnno.defaultValue();
    }
    
    /**
     * 引数「value」がnullまたは空文字であれば初期値を取得する。
     * @param value
     * @param converterAnno
     * @return
     */
    public static String getDefaultValueIfEmpty(final String value, final XlsConverter converterAnno) {
        
        if(isNotEmpty(value)) {
            return value;
        }
        
        if(hasDefaultValue(converterAnno)) {
            return converterAnno.defaultValue();
        }
        
        return value;
    }
    
    /**
     * アノテーションXlsConverterの属性のtrimの値を取得する。
     * @param converterAnno
     * @return 引数converterAnnoの値がnullの場合、falseを返す。
     */
    public static boolean getTrimValue(final XlsConverter converterAnno) {
        
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.trim();
        
    }
    
    /**
     * PostProcessなどのメソッドを実行する。
     * <p>メソッドの引数が既知のものであれば、インスタンスを設定する。
     * 
     * @param method 実行対象のメソッド情報
     * @param object 実行対象のインスタンス
     * @param sheet シート情報
     * @param config 共通設定
     * @param errors エラー情報
     * @throws XlsMapperException 
     */
    public static void invokeNeedProcessMethod(final Method method, final Object object, final Sheet sheet,
            final XlsMapperConfig config, final SheetBindingErrors errors) throws XlsMapperException {
        
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Object[] paramValues =  new Object[paramTypes.length];
        
        for(int i=0; i < paramTypes.length; i++) {
            if(Sheet.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = sheet;
                
            } else if(XlsMapperConfig.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = config;
                
            } else if(SheetBindingErrors.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = errors;
            } else {
                paramValues[i] = null;
            }
        }
        
        try {
            method.setAccessible(true);
            method.invoke(object, paramValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable t = e.getCause() == null ? e : e.getCause();
            throw new XlsMapperException(
                    String.format("fail execute method '%s#%s'.", object.getClass().getName(), method.getName()),
                    t);
        }
    }
    
    /**
     * 文字列形式のロケールをオブジェクトに変換する。
     * <p>アンダーバーで区切った'ja_JP'を分解して、Localeに渡す。
     * @param str
     * @return 引数が空の時はデフォルトロケールを返す。
     */
    public static Locale getLocale(final String str) {
        
        if(isEmpty(str)) {
            return Locale.getDefault();
        }
        
        if(!str.contains("_")) {
            return new Locale(str);
        }
        
        final String[] split = str.split("_");
        if(split.length == 2) {
            return new Locale(split[0], split[1]);
            
        } else {
            return new Locale(split[0], split[1], split[2]);
        }
        
    }
    
    /**
     * エスケープ文字を除去した文字列を取得する。
     * @param str
     * @param escapeChar
     * @return
     */
    public static String removeEscapeChar(final String str, final char escapeChar) {
        
        if(str == null || str.isEmpty()) {
            return str;
        }
        
        final String escapeStr = String.valueOf(escapeChar);
        StringBuilder sb = new StringBuilder();
        
        LinkedList<String> stack = new LinkedList<String>();
        
        final int length = str.length();
        for(int i=0; i < length; i++) {
            final char c = str.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, escapeStr)) {
                // スタックの一番上がエスケープ文字の場合
                StackUtils.popup(stack);
                sb.append(c);
                
            } else if(c == escapeChar) {
                // スタックに積む
                stack.push(String.valueOf(c));
                
            } else {
                sb.append(c);
            }
            
        }
        
        if(!stack.isEmpty()) {
            sb.append(StackUtils.popupAndConcat(stack));
        }
        
        return sb.toString();
        
    }
}
