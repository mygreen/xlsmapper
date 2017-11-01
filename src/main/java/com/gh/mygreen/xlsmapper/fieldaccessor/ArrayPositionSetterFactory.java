package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link ArrayPositionSetter}のインスタンスを作成する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayPositionSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ArrayPositionSetterFactory.class);
    
    /**
     * フィールドの位置情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return 位置情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<ArrayPositionSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map positionsの場合
        Optional<ArrayPositionSetter> arrayPositionSetter = createMapField(beanClass, fieldName);
        if(arrayPositionSetter.isPresent()) {
            return arrayPositionSetter;
        }
        
        // setter メソッドの場合
        arrayPositionSetter = createMethod(beanClass, fieldName);
        if(arrayPositionSetter.isPresent()) {
            return arrayPositionSetter;
        }
        
        // フィールド + positionの場合
        arrayPositionSetter = createField(beanClass, fieldName);
        if(arrayPositionSetter.isPresent()) {
            return arrayPositionSetter;
        }
        
        
        return Optional.empty();
    }
    
    private String createMapKey(final String fieldName, final int index) {
        return String.format("%s[%d]", fieldName, index);
    }
    
    /**
     * {@link Map}フィールドに位置情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * <p>マップの値は、{@link CellPosition}、{@link Point}、{@link CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<ArrayPositionSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
        final Field positionsField;
        try {
            positionsField = beanClass.getDeclaredField("positions");
            positionsField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は、何もしない。
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(positionsField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) positionsField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(CellPosition.class)) {
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, CellPosition> positionsMapObj = (Map<String, CellPosition>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, index);
                        
                        positionsMapObj.put(mapKey, position);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
        } else if(keyType.equals(String.class) && valueType.equals(Point.class)) {
            
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, Point> positionsMapObj = (Map<String, Point>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, index);
                        positionsMapObj.put(mapKey, position.toPoint());
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
            
        } else if(keyType.equals(String.class) && valueType.equals(CellAddress.class)) {
            
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, CellAddress> positionsMapObj = (Map<String, CellAddress>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, index);
                        positionsMapObj.put(mapKey, position.toCellAddress());
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
            
        } else {
            // タイプが一致しない場合
            log.warn("not match generics type of positions. key type:{}, value type:{}.", keyType.getName(), valueType.getName());
            return Optional.empty();
        }
        
    }
    
    /**
     * setterメソッドによる位置情報を格納する場合。
     * <p>{@code set + <フィールド名> + Position}のメソッド名</p>
     * <p>引数として、{@link CellPosition}、{@link Point}、{@code int（列番号）, int（行番号）}、 {@link CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<ArrayPositionSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String positionMethodName = "set" + Utils.capitalize(fieldName) + "Position";
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, Integer.TYPE, CellPosition.class);
            method.setAccessible(true);
            
            return Optional.of(new ArrayPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, index, position);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, Integer.TYPE, Point.class);
            method.setAccessible(true);
            
            return Optional.of(new ArrayPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, index, position.toPoint());
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, Integer.TYPE, CellAddress.class);
            method.setAccessible(true);
            
            return Optional.of(new ArrayPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, index, position.toCellAddress());
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            
            return Optional.of(new ArrayPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, index, position.getColumn(), position.getRow());
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        return Optional.empty();
        
    }
    
    /**
     * フィールドによる位置情報を格納する場合。
     * <p>{@code <フィールド名> + Position}のメソッド名</p>
     * <p>引数として、{@link CellPosition}、{@link Point}、 {@link CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<ArrayPositionSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String positionFieldName = fieldName + "Position";
        
        final Field positionField;
        try {
            positionField = beanClass.getDeclaredField(positionFieldName);
            positionField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(!List.class.isAssignableFrom(positionField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) positionField.getGenericType();
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[0];
        
        if(valueType.equals(CellPosition.class)) {
            
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                   try {
                       List<CellPosition> positionListObj = (List<CellPosition>) positionField.get(beanObj);
                       if(positionListObj == null) {
                           positionListObj = new ArrayList<>();
                           positionField.set(beanObj, positionListObj);
                       }
                       
                       Utils.addListWithIndex(positionListObj, position, index);
                       
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(valueType.equals(Point.class)) {
            
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        List<Point> positionListObj = (List<Point>) positionField.get(beanObj);
                        if(positionListObj == null) {
                            positionListObj = new ArrayList<>();
                            positionField.set(beanObj, positionListObj);
                        }
                        
                        
                        Utils.addListWithIndex(positionListObj, position.toPoint(), index);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(valueType.equals(CellAddress.class)) {
            
            return Optional.of(new ArrayPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellPosition position, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        List<CellAddress> positionListObj = (List<CellAddress>) positionField.get(beanObj);
                        if(positionListObj == null) {
                            positionListObj = new ArrayList<>();
                            positionField.set(beanObj, positionListObj);
                        }
                        
                        Utils.addListWithIndex(positionListObj, position.toCellAddress(), index);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
        
        }
        
        return Optional.empty();
    }
    
}
