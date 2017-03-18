package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link MapColumnMapColumnPositionSetter}のインスタンスを作成する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class MapColumnPositionSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(MapColumnPositionSetterFactory.class);
    
    /**
     * フィールドの位置情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return 位置情報のsetterが存在しない場合は空を返す。
     * @throws NullPointerException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<MapColumnPositionSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map positionsの場合
        Optional<MapColumnPositionSetter> MapColumnPositionSetter = createMapField(beanClass, fieldName);
        if(MapColumnPositionSetter.isPresent()) {
            return MapColumnPositionSetter;
        }
        
        // setter メソッドの場合
        MapColumnPositionSetter = createMethod(beanClass, fieldName);
        if(MapColumnPositionSetter.isPresent()) {
            return MapColumnPositionSetter;
        }
        
        // フィールド + positionの場合
        MapColumnPositionSetter = createField(beanClass, fieldName);
        if(MapColumnPositionSetter.isPresent()) {
            return MapColumnPositionSetter;
        }
        
        
        return Optional.empty();
    }
    
    private String createMapKey(final String fieldName, final String key) {
        return String.format("%s[%s]", fieldName, key);
    }
    
    /**
     * {@link Map}フィールドに位置情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * <p>マップの値は、{@link CellAddress}、{@link Point}、{@link org.apache.poi.ss.util.CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<MapColumnPositionSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
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
        
        if(keyType.equals(String.class) && valueType.equals(CellAddress.class)) {
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, CellAddress> positionsMapObj = (Map<String, CellAddress>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, key);
                        
                        positionsMapObj.put(mapKey, position);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
        } else if(keyType.equals(String.class) && valueType.equals(Point.class)) {
            
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, Point> positionsMapObj = (Map<String, Point>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, key);
                        positionsMapObj.put(mapKey, position.toPoint());
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
            
        } else if(keyType.equals(String.class) && valueType.equals(org.apache.poi.ss.util.CellAddress.class)) {
            
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, org.apache.poi.ss.util.CellAddress> positionsMapObj = (Map<String, org.apache.poi.ss.util.CellAddress>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            positionsMapObj = new LinkedHashMap<>();
                            positionsField.set(beanObj, positionsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, key);
                        positionsMapObj.put(mapKey, position.toPoiCellAddress());
                        
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
     * <p>引数として、{@link CellAddress}、{@link Point}、{@code int（列番号）, int（行番号）}、 {@link org.apache.poi.ss.util.CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<MapColumnPositionSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String positionMethodName = "set" + Utils.capitalize(fieldName) + "Position";
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, String.class, CellAddress.class);
            method.setAccessible(true);
            
            return Optional.of(new MapColumnPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, key, position);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, String.class, Point.class);
            method.setAccessible(true);
            
            return Optional.of(new MapColumnPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, key, position.toPoint());
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, String.class, org.apache.poi.ss.util.CellAddress.class);
            method.setAccessible(true);
            
            return Optional.of(new MapColumnPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, key, position.toPoiCellAddress());
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        try {
            final Method method = beanClass.getDeclaredMethod(positionMethodName, String.class, Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            
            return Optional.of(new MapColumnPositionSetter() {
                
                
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        method.invoke(beanObj, key, position.getColumn(), position.getRow());
                        
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
     * <p>引数として、{@link CellAddress}、{@link Point}、 {@link org.apache.poi.ss.util.CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return 位置情報の設定用クラス
     */
    private Optional<MapColumnPositionSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String positionFieldName = fieldName + "Position";
        
        final Field positionField;
        try {
            positionField = beanClass.getDeclaredField(positionFieldName);
            positionField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(positionField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) positionField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(CellAddress.class)) {
            
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                   try {
                       Map<String, CellAddress> positionMapObj = (Map<String, CellAddress>) positionField.get(beanObj);
                       if(positionMapObj == null) {
                           positionMapObj = new LinkedHashMap<>();
                           positionField.set(beanObj, positionMapObj);
                       }
                       
                       positionMapObj.put(key, position);
                       
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(keyType.equals(String.class) && valueType.equals(Point.class)) {
            
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, Point> positionMapObj = (Map<String, Point>) positionField.get(beanObj);
                        if(positionMapObj == null) {
                            positionMapObj = new LinkedHashMap<>();
                            positionField.set(beanObj, positionMapObj);
                        }
                        
                        
                        positionMapObj.put(key, position.toPoint());
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(keyType.equals(String.class) && valueType.equals(org.apache.poi.ss.util.CellAddress.class)) {
            
            return Optional.of(new MapColumnPositionSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final CellAddress position, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(position, "position");
                    
                    try {
                        Map<String, org.apache.poi.ss.util.CellAddress> positionMapObj = (Map<String, org.apache.poi.ss.util.CellAddress>) positionField.get(beanObj);
                        if(positionMapObj == null) {
                            positionMapObj = new LinkedHashMap<>();
                            positionField.set(beanObj, positionMapObj);
                        }
                        
                        
                        positionMapObj.put(key, position.toPoiCellAddress());
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
        
        }
        
        return Optional.empty();
    }
}
