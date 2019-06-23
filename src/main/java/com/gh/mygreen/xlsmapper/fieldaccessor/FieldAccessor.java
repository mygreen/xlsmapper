package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * メソッド（setter/getter）とフィールドのアクセスを吸収するクラス。
 * <p>インスタンスは、{@link FieldAccessorFactory}から作成します。</p>
 *
 * @version 2.1
 * @author T.TSUCHIE
 *
 */
public class FieldAccessor {
    
    /**
     * フィールド（プロパティ）の名称
     */
    String name;
    
    /**
     * フィールドやメソッドが定義されているクラス
     */
    Class<?> declaringClass;
    
    /**
     * フィールドのクラスタイプ
     */
    Class<?> targetType;
    
    /**
     * フィールド情報
     */
    Optional<Field> targetField = Optional.empty();
    
    /**
     * Getterメソッド
     */
    Optional<Method> targetGetter = Optional.empty();
    
    /**
     * Setterメソッド
     */
    Optional<Method> targetSetter = Optional.empty();
    
    /**
     * フィールドのタイプがListや配列の時の要素のクラスタイプ
     */
    Optional<Class<?>> componentType = Optional.empty();
    
    /** 
     * アノテーションの情報
     */
    Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
    
    /**
     * 位置情報のSetter
     */
    Optional<PositionSetter> positionSetter = Optional.empty();
    
    /**
     * 位置情報のgetter
     */
    Optional<PositionGetter> positionGetter = Optional.empty();
    
    /**
     * {@link XlsMapColumns}用の位置情報のSetter
     */
    Optional<MapPositionSetter> mapPositionSetter = Optional.empty();
    
    /**
     * {@link XlsArrayColumns}用の位置情報のSetter
     */
    Optional<ArrayPositionSetter> arrayPositionSetter = Optional.empty();
    
    /**
     * ラベル情報のSetter
     */
    Optional<LabelSetter> labelSetter = Optional.empty();
    
    /**
     * ラベル情報のgetter
     */
    Optional<LabelGetter> labelGetter = Optional.empty();
    
    /**
     * {@link XlsMapColumns}用のラベル情報のSetter
     */
    Optional<MapLabelSetter> mapLabelSetter = Optional.empty();
    
    /**
     * {@link XlsArrayColumns}用のラベル情報のSetter
     */
    Optional<ArrayLabelSetter> arrayLabelSetter = Optional.empty();
    
    /**
     * コメント情報のSetter
     */
    Optional<CommentSetter> commentSetter = Optional.empty();
    
    /**
     * コメント情報のgetter
     */
    Optional<CommentGetter> commentGetter = Optional.empty();
    
    /**
     * {@link XlsMapColumns}用のコメント情報のSetter
     */
    Optional<MapCommentSetter> mapCommentSetter = Optional.empty();
    
    /**
     * {@link XlsMapColumns}用のコメント情報のGetter
     */
    Optional<MapCommentGetter> mapCommentGetter = Optional.empty();
    
    /**
     * {@link XlsArrayColumns}用のコメント情報のSetter
     */
    Optional<ArrayCommentSetter> arrayCommentSetter = Optional.empty();
    
    /**
     * {@link XlsArrayColumns}用のコメント情報のGetter
     */
    Optional<ArrayCommentGetter> arrayCommentGetter = Optional.empty();
    
    FieldAccessor() {
        
    }
    
    /**
     * フィールドとメソッドに同じアノテーションが付与されているときに重複を除外するための判定に使用する。
     * そのため、{@link FieldAccessor#getNameWithClass()}が等しいかで判定します。
     * 
     */
    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof FieldAccessor)) {
            return false;
        }
        
        final FieldAccessor target = (FieldAccessor)obj;
        
        // クラス名#フィールドの比較
        if(!getNameWithClass().equals(target.getNameWithClass())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 読み込み可能かどうか判定します。
     * @return フィールドまたはgetterメソッドが存在する場合にtrueを返します。
     */
    public boolean isReadable() {
        
        return targetField.isPresent() || targetGetter.isPresent();
        
    }
    
    /**
     * 書き込み可能かどうか判定します。
     * @return フィールドまたはsetterメソッドが存在する場合にtrueを返します。
     */
    public boolean isWritable() {
        
        return targetField.isPresent() || targetSetter.isPresent();
    }
    
    /**
     * オブジェクトのフィールドの値を取得します。
     * <p>getterが存在する場合は、getterメソッド経由で取得します。</p>
     * @param targetObj オブジェクト（インスタンス）
     * @return フィールドの値。
     * @throws IllegalArgumentException {@literal targetObj == null.}
     * @throws FieldAccessException {@literal 値の取得に失敗した場合。}
     */
    public Object getValue(final Object targetObj) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(targetGetter.isPresent()) {
            try {
                return targetGetter.get().invoke(targetObj);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new FieldAccessException(this, "fail getter value", e);
            }
            
        } else if(targetField.isPresent()) {
            try {
                return targetField.get().get(targetObj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new FieldAccessException(this, "fail get field value", e);
            }
            
        } else {
            throw new FieldAccessException(this, "not found getter method or field.");
        }
        
    }
    
    /**
     * オブジェクトのフィールドに値を設定する。
     * <p>setterが存在する場合は、setterメソッド経由で値を設定します。</p>
     * @param targetObj オブジェクト（インスタンス）
     * @param value フィールドの値。
     * @throws IllegalArgumentException {@literal targetObj == null.}
     * @throws FieldAccessException {@literal 値の設定に失敗した場合。}
     * 
     */
    public void setValue(final Object targetObj, final Object value) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(targetSetter.isPresent()) {
            try {
                targetSetter.get().invoke(targetObj, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new FieldAccessException(this, "fail setter value", e);
            }
            
        } else if(targetField.isPresent()) {
            try {
                targetField.get().set(targetObj, value);
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new FieldAccessException(this, "fail setter field value", e);
            }
            
        } else {
            throw new FieldAccessException(this, "not found setter method or field.");
        }
    }
    
    /**
     * フィールドがマップ形式の場合に、キーを指定して値を取得する。
     * 
     * @param key マップキーの値
     * @param targetObj オブジェクト（インスタンス）
     * @return マップの値
     * @throws IllegalArgumentException {@literal targetObj == null.}
     * @throws IllegalStateException {@literal フィールドのタイプがMap出ない場合}
     */
    @SuppressWarnings("unchecked")
    public Object getValueOfMap(final Object key, final Object targetObj) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(!Map.class.isAssignableFrom(targetType)) {
            throw new IllegalStateException("this method cannot call Map. This target type is " + targetType.getName());
        }
        
        final Map<Object, Object> map = (Map<Object, Object>) getValue(targetObj);
        if(map == null) {
            return null;
        }
        
        return map.get(key);
        
    }
    
    /**
     * アノテーションの一覧を取得します。
     * @return 付与されているアノテーションの一覧
     */
    public Collection<? extends Annotation> getAnnotations() {
        return annotationMap.values();
    }
    
    /**
     * 指定したアノテーションを持つか判定します。
     * @param annoClass アノテーションのクラスタイプ。
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(final Class<A> annoClass) {
        return annotationMap.containsKey(annoClass);
    }
    
    /**
     * タイプを指定して、アノテーションを取得する。
     * @param annoClass アノテーションのクラスタイプ。
     * @return 存在しない場合、空を返します。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annoClass) {
        return Optional.ofNullable((A)annotationMap.get(annoClass));
    }
    
    /**
     * タイプを指定して、アノテーションを取得する。
     * <p>
     * 確実に存在しているアノテーションを取得する場合に使用します。
     * そうでない場合は、{@link #getAnnotation(Class)}を使用します。
     * </p>
     * @since 2.1
     * @param annoClass アノテーションのクラスタイプ。
     * @return 存在しない場合、nullを返します。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotationNullable(final Class<A> annoClass) {
        return (A)annotationMap.get(annoClass);
    }
    
    /**
     * フィールドの名称を取得します。
     * @return フィールドの名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * フィールドのタイプを取得します。
     * @return フィールドのクラスタイプ
     */
    public Class<?> getType() {
        
        return targetType;
    }
    
    /**
     * フィールドのタイプがListや配列の時の要素のGenericsのクラスタイプを取得します。
     * @return クラスタイプ。
     * @throws IllegalStateException {@literal Genericsの指定がないとき、またはサポートしていないクラスタイプの場合}
     */
    public Class<?> getComponentType() {
        return componentType.orElseThrow(() ->
                new IllegalStateException("Because this field is List.class or Map.class, this field has not component type."));
    }
    
    /**
     * フィールドタイプがListや配列の時の要素のGenericsのクラスタイプかどうか。
     * @return trueの場合、Listや配列の時の要素のGenericsのクラスタイプの場合。
     */
    public boolean isComponentType() {
        return componentType.isPresent();
    }
    
    /**
     * フィールド情報を取得します。
     * @return フィールドを持たない場合は、空を返します。
     */
    public Optional<Field> getField() {
        return targetField;
    }
    
    /**
     * Getterメソッドを取得します。
     * @return getterメソッドを持たない場合は、空を返します。
     */
    public Optional<Method> getGetter() {
        return targetGetter;
    }
    
    /**
     * Setterメソッドを取得します。
     * @return setterメソッドを持たない場合は、空を返します。
     */
    public Optional<Method> getSetter() {
        return targetSetter;
    }
    
    /**
     * フィールドが定義されているクラス情報を取得します。
     * @return フィールドが定義されているクラス情報
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }
    
    /**
     * クラス名付きのフィールド名称を取得する。
     * @since 1.4
     * @return {@literal <クラス名#フィールド名>}
     */
    public String getNameWithClass() {
        return getDeclaringClass().getName() + "#" + getName();
    }
    
    /**
     * 位置情報を設定します。
     * <p>位置情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param position 位置情報
     * @throws IllegalArgumentException {@literal targetObj == null or position == null}
     */
    public void setPosition(final Object targetObj, final CellPosition position) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notNull(position, "position");
        
        positionSetter.ifPresent(setter -> setter.set(targetObj, position));
    }
    
    /**
     * 位置情報を取得します。
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @return 位置情報を保持するフィールドがない場合や、値が設定されていないときは空を返します。
     * @throws IllegalArgumentException {@literal targetObj == null or position == null}
     */
    public Optional<CellPosition> getPosition(final Object targetObj) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        return positionGetter.map(getter -> getter.get(targetObj)).orElse(Optional.empty());
    }
    
    /**
     * {@link XlsMapColumns}フィールド用の位置情報を設定します。
     * <p>位置情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * 
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param position 位置情報
     * @param key マップのキー
     * @throws IllegalArgumentException {@literal targetObj == null or position == null or key == null}
     * @throws IllegalArgumentException {@literal key is empty.}
     */
    public void setMapPosition(final Object targetObj, final CellPosition position, final String key) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notNull(position, "position");
        ArgUtils.notEmpty(key, "key");
        
        mapPositionSetter.ifPresent(setter -> setter.set(targetObj, position, key));
        
    }
    
    /**
     * {@link XlsArrayColumns}フィールド用の位置情報を設定します。
     * <p>位置情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * 
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param position 位置情報
     * @param index インデックスのキー。0以上を指定します。
     * @throws IllegalArgumentException {@literal targetObj == null or position == null}
     * @throws IllegalArgumentException {@literal index < 0}
     */
    public void setArrayPosition(final Object targetObj, final CellPosition position, final int index) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notNull(position, "position");
        ArgUtils.notMin(index, 0, "index");
        
        arrayPositionSetter.ifPresent(setter -> setter.set(targetObj, position, index));
        
    }
    
    /**
     * ラベル情報を設定します。
     * <p>ラベル情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param label ラベル情報
     * @throws IllegalArgumentException {@literal targetObj == null or label == null}
     * @throws IllegalArgumentException {@literal label is empty}
     */
    public void setLabel(final Object targetObj, final String label) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notEmpty(label, "label");
        
        labelSetter.ifPresent(setter -> setter.set(targetObj, label));
    }
    
    /**
     * ラベル情報を取得します。
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @return ラベル情報を保持するフィールドがない場合や、値が設定されていないときは空を返します。
     * @throws IllegalArgumentException {@literal targetObj == null or label == null}
     */
    public Optional<String> getLabel(final Object targetObj) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        return labelGetter.map(getter -> getter.get(targetObj)).orElse(Optional.empty());
    }
    
    /**
     * {@link XlsMapColumns}フィールド用のラベル情報を設定します。
     * <p>ラベル情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * 
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param label ラベル情報
     * @param key マップのキー
     * @throws IllegalArgumentException {@literal targetObj == null or label == null or key == null}
     * @throws IllegalArgumentException {@literal label or key is empty.}
     */
    public void setMapLabel(final Object targetObj, final String label, final String key) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notEmpty(key, "key");
        
        mapLabelSetter.ifPresent(setter -> setter.set(targetObj, label, key));
        
    }
    
    /**
     * {@link XlsArrayColumns}フィールド用のラベル情報を設定します。
     * <p>ラベル情報を保持するフィールドがない場合は、処理はスキップされます。</p>
     * 
     * @param targetObj フィールドが定義されているクラスのインスタンス
     * @param label ラベル情報
     * @param index インデックスのキー。0以上を指定します。
     * @throws IllegalArgumentException {@literal targetObj == null or label == null.}
     * @throws IllegalArgumentException {@literal label or index < 0}
     */
    public void setArrayLabel(final Object targetObj, final String label, final int index) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notMin(index, 0, "index");
        
        arrayLabelSetter.ifPresent(setter -> setter.set(targetObj, label, index));
        
    }
    
    /**
     * フィールドに対する{@link CommentSetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<CommentSetter> getCommentSetter() {
        return commentSetter;
    }
    
    /**
     * フィールドに対する{@link CommentGetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<CommentGetter> getCommentGetter() {
        return commentGetter;
    }
    
    /**
     * フィールドに対する{@link MapCommentSetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<MapCommentSetter> getMapCommentSetter() {
        return mapCommentSetter;
    }
    
    /**
     * フィールドに対する{@link MapCommentGetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<MapCommentGetter> getMapCommentGetter() {
        return mapCommentGetter;
    }
    
    /**
     * フィールドに対する{@link ArrayCommentSetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<ArrayCommentSetter> getArrayCommentSetter() {
        return arrayCommentSetter;
    }
    
    /**
     * フィールドに対する{@link ArrayCommentGetter}を取得する。
     * @since 2.1
     * @return コメントの格納先がない場合は空を返す。
     */
    public Optional<ArrayCommentGetter> getArrayCommentGetter() {
        return arrayCommentGetter;
    }
    
}
