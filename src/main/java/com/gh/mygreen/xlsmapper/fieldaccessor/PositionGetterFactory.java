package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link PositionGetter}のインスタンスを作成する
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PositionGetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(PositionGetterFactory.class);
    
    /**
     * フィールドの位置情報を取得するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return 位置情報のgetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<PositionGetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map positionsの場合
        Optional<PositionGetter> positionGetter = createMapField(beanClass, fieldName);
        if(positionGetter.isPresent()) {
            return positionGetter;
        }
        
        // setter メソッドの場合
        positionGetter = createMethod(beanClass, fieldName);
        if(positionGetter.isPresent()) {
            return positionGetter;
        }
        
        // フィールド + positionの場合
        positionGetter = createField(beanClass, fieldName);
        if(positionGetter.isPresent()) {
            return positionGetter;
        }
        
        
        return Optional.empty();
        
        
    }
    
    private Optional<PositionGetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
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
            return Optional.of(new PositionGetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        Map<String, CellPosition> positionsMapObj = (Map<String, CellPosition>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            return Optional.empty();
                        }
                        
                        return Optional.ofNullable(positionsMapObj.get(fieldName));
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
        } else if(keyType.equals(String.class) && valueType.equals(Point.class)) {
            
            return Optional.of(new PositionGetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        Map<String, Point> positionsMapObj = (Map<String, Point>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            return Optional.empty();
                        }
                        
                        return Optional.ofNullable(positionsMapObj.get(fieldName))
                                .map(a -> CellPosition.of(a));
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access positions field.", e);
                    }
                }
            });
            
            
        } else if(keyType.equals(String.class) && valueType.equals(org.apache.poi.ss.util.CellAddress.class)) {
            
            return Optional.of(new PositionGetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        Map<String, org.apache.poi.ss.util.CellAddress> positionsMapObj = (Map<String, org.apache.poi.ss.util.CellAddress>) positionsField.get(beanObj);
                        if(positionsMapObj == null) {
                            return Optional.empty();
                        }
                        
                        return Optional.ofNullable(positionsMapObj.get(fieldName))
                                .map(a -> CellPosition.of(a));
                        
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
    
    private Optional<PositionGetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String positionMethodName = "get" + Utils.capitalize(fieldName) + "Position";
        
        final Method method;
        try {
            method = beanClass.getDeclaredMethod(positionMethodName);
            method.setAccessible(true);
            
        } catch (NoSuchMethodException | SecurityException e) {
            return Optional.empty();
        }
        
        if(method.getReturnType().equals(CellPosition.class)) {
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final CellPosition address = (CellPosition)method.invoke(beanObj);
                        return Optional.ofNullable(address);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access positions getter method.", e);
                    }
                    
                }
            });
            
        } else if(method.getReturnType().equals(Point.class)) {
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final Point point = (Point)method.invoke(beanObj);
                        return Optional.ofNullable(point).map(p -> CellPosition.of(p));
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access positions getter method.", e);
                    }
                    
                }
            });
            
        } else if(method.getReturnType().equals(org.apache.poi.ss.util.CellAddress.class)) {
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final org.apache.poi.ss.util.CellAddress address = (org.apache.poi.ss.util.CellAddress)method.invoke(beanObj);
                        return Optional.ofNullable(address).map(a -> CellPosition.of(a));
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access positions getter method.", e);
                    }
                    
                }
            });
        }
        
        return Optional.empty();
        
    }
    
    private Optional<PositionGetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String positionFieldName = fieldName + "Position";
        
        final Field positionField;
        try {
            positionField = beanClass.getDeclaredField(positionFieldName);
            positionField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(positionField.getType().equals(CellPosition.class)) {
            
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final CellPosition address = (CellPosition) positionField.get(beanObj);
                        return Optional.ofNullable(address);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(positionField.getType().equals(Point.class)) {
            
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final Point point = (Point) positionField.get(beanObj);
                        return Optional.ofNullable(point).map(p -> CellPosition.of(p));
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
            
        } else if(positionField.getType().equals(org.apache.poi.ss.util.CellAddress.class)) {
            
            return Optional.of(new PositionGetter() {
                
                @Override
                public Optional<CellPosition> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final org.apache.poi.ss.util.CellAddress address = (org.apache.poi.ss.util.CellAddress) positionField.get(beanObj);
                        return Optional.ofNullable(address).map(a -> CellPosition.of(a));
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access position field.", e);
                    }
                }
            });
        
        }
        
        return Optional.empty();
    }
    
}
