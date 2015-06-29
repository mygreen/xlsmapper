package com.gh.mygreen.xlsmapper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 値が全て空かどかチェックするためのクラス。
 * <p>アノテーション{@link com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty}を付与したメソッドの実装に利用します。
 * 
 * <p>リフレクションを利用して判定する場合は、位置情報のフィールドpositions()、ラベル情報のフィールドを除外します。
 * <pre>
 * @XlsIsEmpty
 * public boolean isEmpty() {
 *     return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels");
 * }
 * </pre>
 * 
 * <p>フィールドを1つずつ判定する場合は、{@code append(...)}メソッドを利用します。
 * メソッド{@link #compare(IsEmptyComparator)}を利用することで、独自の実装も可能で、その際にLambda式を利用することもできます。
 * <pre>
 * @XlsIsEmpty
 * public boolean isEmpty() {
 *     return new IsEmptyBuilder()
 *         .append(name)
 *         .append(age)
 *         .compare(() -> StringUtils.isBlank(address))
 *         .isEmpty();
 * }
 * </pre>
 * 
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class IsEmptyBuilder {
    
    /**
     * 現在までの判定結果を保持する。
     * true: 値が空かどうか。
     */
    private final AtomicBoolean empty;
    
    /**
     * 数値の場合、0を空として扱うか。
     */
    private boolean zeroAsEmpty;
    
    /**
     * 配列の場合、値も検証対象とするかどうか。
     */
    private boolean testArrayValue;
    
    /**
     * Collectionの場合、値も検証対象とするかどうか。
     */
    private boolean testCollectionValue;
    
    /**
     * Mapの場合、値も対象とするかどうか。
     */
    private boolean testMapValue;
    
    /**
     * ransientが付与されたフィールドも対象とするかどうか。
     */
    private boolean testTransient;
    
    /**
     * コンストラクタ。
     */
    public IsEmptyBuilder() {
        this(IsEmptyConfig.create());
        
    }
    
    /**
     * {@link IsEmptyConfig}を指定するコンストラクタ。
     * @param config 設定用クラス。
     * @throws IllegalArgumentException config is null.
     */
    public IsEmptyBuilder(final IsEmptyConfig config) {
        ArgUtils.notNull(config, "config");
        
        this.empty = new AtomicBoolean(true);
        
        this.zeroAsEmpty = config.isZeroAsEmpty();
        this.testArrayValue = config.isTestArrayValue();
        this.testCollectionValue = config.isTestCollectionValue();
        this.testMapValue = config.isTestMapValue();
        this.testTransient = config.isTestTransient();
    }
    
    /**
     * リフレクションを使用しフィールドの値を取得し判定する。
     * <p>static修飾子を付与しているフィールドは、除外されます。
     * <p>transient修飾子を付与しているフィールドは、除外されます。
     * @param obj 判定対象のオブジェクト。
     * @param excludedFields 除外対処のフィールド名。
     * @return 引数で指定したobjがnullの場合、trueを返す。
     */
    public static boolean reflectionIsEmpty(final Object obj, final String... excludedFields) {
        return reflectionIsEmpty(obj, IsEmptyConfig.create(), Arrays.asList(excludedFields));
    }
    
    /**
     * リフレクションを使用しフィールドの値を取得し判定する。
     * @since 1.0
     * @param 設定用クラス
     * @param obj 判定対象のオブジェクト。
     * @param config 判定用の設定クラス。
     * @param excludedFields 除外対処のフィールド名。
     * @return 引数で指定したobjがnullの場合、trueを返す。
     */
    public static boolean reflectionIsEmpty(final Object obj, final IsEmptyConfig config, final String... excludedFields) {
        return reflectionIsEmpty(obj, config, Arrays.asList(excludedFields));
    }
    
    /**
     * リフレクションを使用しフィールドの値を取得し判定する。
     * @since 1.0
     * @param 設定用クラス
     * @param obj 判定対象のオブジェクト。
     * @param config 判定用の設定クラス。
     * @param excludedFields 除外対処のフィールド名。
     * @return 引数で指定したobjがnullの場合、trueを返す。
     */
    public static boolean reflectionIsEmpty(final Object obj, final IsEmptyConfig config, final Collection<String> excludedFields) {
        
        if(obj == null) {
            return true;
        }
        
        final IsEmptyBuilder builder = new IsEmptyBuilder(config);
        final Field[] fields = obj.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        
        for(Field field : fields) {
            
            // static フィールドかどうか
            if(Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            // transientのフィールドかどうか。
            if(!builder.testTransient && Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            
            // 除外対象のフィールド名かどうか。
            if(excludedFields != null && excludedFields.contains(field.getName())) {
                continue;
            }
            
            try {
                final Object value = field.get(obj);
                builder.append(value);
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new InternalError("Unexpected IllegalAccessException");
            }
            
        }
        
        return builder.isEmpty();
    }
    
    /**
     * 値が空でないことを設定する。
     */
    private void setNotEmpty() {
        this.empty.set(false);
    }
    
    /**
     * String型の値を追加する。
     * @param value nullまたは空文字の場合、空と判断する。
     * @return this.
     */
    public IsEmptyBuilder append(String value) {
        return append(value, false);
    }
    
    /**
     * String型の値を追加する。
     * @param value nullまたは空文字の場合、空と判断する。
     * @param trim 引数valueをトリムした後空文字と判定するかどうか。
     * @return this.
     */
    public IsEmptyBuilder append(String value, boolean trim) {
        if(trim) {
            if(value != null && !value.trim().isEmpty()) {
                setNotEmpty();
            }
        } else {
            if(value != null && !value.isEmpty()) {
                setNotEmpty();
            }
        }
        
        return this;
    }
    
    /**
     * char型の値を追加する。
     * @param value 空文字の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(char value) {
        final String str = (value == '\u0000' ? "" : String.valueOf(value));
        return append(str);
    }
    
    /**
     * char型の値を追加する。
     * @param value 空文字の場合、空と判断する。
     * @param trim 引数valueをトリムした後空文字と判定するかどうか。
     * @return this
     */
    public IsEmptyBuilder append(char value, boolean trim) {
        final String str = (value == '\u0000' ? "" : String.valueOf(value));
        return append(str, trim);
    }
    
    /**
     * boolean型の値を追加する。
     * @param value false場合、空と判断する。
     * @return this.
     */
    public IsEmptyBuilder append(boolean value) {
        if(value) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * byte型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(byte value) {
        if(!(isZeroAsEmpty() && value == 0)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * short型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(short value) {
        if(!(isZeroAsEmpty() && value == 0)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * int型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(int value) {
        if(!(isZeroAsEmpty() && value == 0)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * long型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(long value) {
        if(!(isZeroAsEmpty() && value == 0L)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * float型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0.0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(float value) {
        if(!(isZeroAsEmpty() && value == 0.0)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * double型の値を追加する。
     * @param value {@link #isZeroAsEmpty()}がtrueの場合、0の値を空として扱う。
     * @return this
     */
    public IsEmptyBuilder append(double value) {
        if(!(isZeroAsEmpty() && value == 0.0)) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * Object型の値を追加する。
     * @param value nullの場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final Object value) {
        
        if(value == null) {
            return this;
        }
        
        final Class<?> clazz = value.getClass();
        if(clazz.isPrimitive()) {
            if(clazz.equals(Boolean.TYPE)) {
                return append((boolean) value);
            } else if(clazz.equals(Byte.TYPE)) {
                return append((byte) value);
            } else if(clazz.equals(Character.TYPE)) {
                return append((char) value);
            } else if(clazz.equals(Short.TYPE)) {
                return append((short) value);
            } else if(clazz.equals(Integer.TYPE)) {
                return append((int) value);
            } else if(clazz.equals(Long.TYPE)) {
                return append((long) value);
            } else if(clazz.equals(Float.TYPE)) {
                return append((float) value);
            } else if(clazz.equals(Double.TYPE)) {
                return append((double) value);
            } 
            
        } else if(clazz.isArray()) {
            if(value instanceof boolean[]) {
                return append((boolean[]) value);
            } else if(value instanceof char[]) {
                return append((char[]) value);
            } else if(value instanceof byte[]) {
                return append((byte[]) value);
            } else if(value instanceof short[]) {
                return append((short[]) value);
            } else if(value instanceof int[]) {
                return append((int[]) value);
            } else if(value instanceof long[]) {
                return append((long[]) value);
            } else if(value instanceof float[]) {
                return append((float[]) value);
            } else if(value instanceof double[]) {
                return append((double[]) value);
            } else {
                return append((Object[]) value);
            }
            
        } else if(value instanceof String) {
            return append((String) value);
            
        } else if(value instanceof Boolean) {
            return append((boolean) value);
            
        } else if(value instanceof Byte) {
            return append((byte) value);
            
        } else if(value instanceof Character) {
            return append((char) value);
            
        } else if(value instanceof Short) {
            return append((short) value);
            
        } else if(value instanceof Integer) {
            return append((int) value);
            
        } else if(value instanceof Long) {
            return append((long) value);
            
        } else if(value instanceof Float) {
            return append((float) value);
            
        } else if(value instanceof Double) {
            return append((double) value);
            
        } else if(value instanceof Collection) {
            return append((Collection<?>) value);
            
        } else if(value instanceof Map) {
            return append((Map<?, ?>) value);
            
        } else {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * 配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final Object[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(Object o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
        
    }
    
    /**
     * booleanの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final boolean[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(boolean o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * charの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final char[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(char o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * byteの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final byte[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(byte o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * shortの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final short[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(short o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * intの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final int[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(int o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * longの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final long[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(long o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * floatの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final float[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(float o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * doubleの配列型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final double[] value) {
        
        if(value != null && isTestArrayValue()) {
            for(double o : value) {
                append(o);
            }
            
        } else if(value != null && value.length != 0) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * Collection型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final Collection<?> value) {
        
        if(value != null && isTestCollectionValue()) {
            // コレクションの値も検証する。
            for(Object o : value) {
                append(o);
            }
            
        } else if(value != null && !value.isEmpty()) {
            setNotEmpty();
        }
        
        return this;
        
    }
    
    /**
     * Map型の値を追加する。
     * @param value nullの場合、サイズが0の場合、空と判断する。
     * @return this
     */
    public IsEmptyBuilder append(final Map<?, ?> value) {
        
        if(value != null && isTestMapValue()) {
            // コレクションの値も検証する。
            for(Object o : value.values()) {
                append(o);
            }
            
        } else if(value != null && !value.isEmpty()) {
            setNotEmpty();
            
        }
        
        return this;
        
    }
    
    /**
     * 独自の実装で値を空かどうか判定する。
     * <p>Java8のLambda式を利用すると簡潔に書ける。
     * <pre>
     * public boolean isEmpty() {
     *      return new IsEmptyBuilder()
     *          .append(age)
     *          .compare(() -> StringUtils.isBlank(address))
     *          .isEmpty();
     *  }
     * </pre>
     * @param appender {@link IsEmptyComparator}のインスタンス。
     * @return this.
     */
    public <T> IsEmptyBuilder compare(final IsEmptyComparator compare) {
        
        if(!compare.isEmpty()) {
            setNotEmpty();
        }
        
        return this;
    }
    
    /**
     * 値が空かどうか。
     * <p>{@code append(XXXX)}メソッドで何も追加されない場合、trueを返します。
     * @return true:値が空。
     */
    public boolean isEmpty() {
        return empty.get();
    }
    
    /**
     * 値が空出ないかどうか。
     * @return true:値が空でない。
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }
    
    /**
     * 数値の0を空として扱うかどうか。
     * @return true:0を空として扱う。
     */
    public boolean isZeroAsEmpty() {
        return zeroAsEmpty;
    }
    
    /**
     * Collectionの値も検証するかどうか。
     * @return true:Collectionの値も検証する。
     */
    public boolean isTestArrayValue() {
        return testArrayValue;
    }
    
    /**
     * Collectionの値も検証するかどうか。
     * @return true:Collectionの値も検証する。
     */
    public boolean isTestCollectionValue() {
        return testCollectionValue;
    }
    
    /**
     * Mapの値も検証するかどうか。
     * @return true:Mapの値も検証する。
     */
    public boolean isTestMapValue() {
        return testMapValue;
    }
    
}
