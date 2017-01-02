package com.gh.mygreen.xlsmapper.xml;

/**
 * Javaのオブジェクトを<a href="http://s2container.seasar.org/2.4/ja/ognl.html" target="_blank">OGNL形式</a>の文字列に変換します。
 * <ul>
 *  <li>このクラスは、{@link XmlBuilder}でアノテーション定義用のXMLを組み立てるために利用します。</li>
 *  <li>アノテーションで利用可能な属性の型（プリミティブ型/String/Class/列挙型、それらの一次元配列）に対応しています。</li>
 *  <li>プリミティブ型のラッパークラスは、プリミティブ型の形式に変換して処理されます。</li>
 * </ul>
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class OgnlValueFormatter {
    
    /**
     * JavaオブジェクトをOGNL式に変換する。
     * @param value 変換対象のオブジェクト。
     * @return OGNL式。
     * @throws IllegalArgumentException value class type is not supported.
     */
    public String format(final Object value) {
        
        if(value == null) {
            return "null";
        }
        
        final Class<?> clazz = value.getClass();
        if(clazz.isPrimitive()) {
            if(clazz.equals(Boolean.TYPE)) {
                return format((boolean) value);
            } else if(clazz.equals(Byte.TYPE)) {
                return format((byte) value);
            } else if(clazz.equals(Character.TYPE)) {
                return format((char) value);
            } else if(clazz.equals(Short.TYPE)) {
                return format((short) value);
            } else if(clazz.equals(Integer.TYPE)) {
                return format((int) value);
            } else if(clazz.equals(Long.TYPE)) {
                return format((long) value);
            } else if(clazz.equals(Float.TYPE)) {
                return format((float) value);
            } else if(clazz.equals(Double.TYPE)) {
                return format((double) value);
            } 
            
        } else if(clazz.isArray()) {
            if(value instanceof boolean[]) {
                return format((boolean[]) value);
            } else if(value instanceof char[]) {
                return format((char[]) value);
            } else if(value instanceof byte[]) {
                return format((byte[]) value);
            } else if(value instanceof short[]) {
                return format((short[]) value);
            } else if(value instanceof int[]) {
                return format((int[]) value);
            } else if(value instanceof long[]) {
                return format((long[]) value);
            } else if(value instanceof float[]) {
                return format((float[]) value);
            } else if(value instanceof double[]) {
                return format((double[]) value);
            } else if(value instanceof String[]) {
                return format((String[]) value);
            } else if(value instanceof Enum[]) {
                return format((Enum[]) value);
            } else if(value instanceof Class[]) {
                return format((Class[]) value);
            }
            
        } else if(value instanceof String) {
            return format((String) value);
        } else if(value instanceof Boolean) {
            return format((boolean) value);
        } else if(value instanceof Byte) {
            return format((byte) value);
        } else if(value instanceof Character) {
            return format((char) value);
        } else if(value instanceof Short) {
            return format((short) value);
        } else if(value instanceof Integer) {
            return format((int) value);
        } else if(value instanceof Long) {
            return format((long) value);
        } else if(value instanceof Float) {
            return format((float) value);
        } else if(value instanceof Double) {
            return format((double) value);
        } else if(Class.class.isAssignableFrom(clazz)) {
            return format((Class<?>) value);
        } else if(Enum.class.isAssignableFrom(clazz)) {
            return format((Enum<?>) value);
        }
        
        throw new IllegalArgumentException(String.format("not support type '%s'.", clazz.getName()));
    }
    
    private String format(final boolean value) {
        return String.valueOf(value);
    }
    
    private String format(final char value) {
        return String.format("'\\u%04X'", (int)value);
    }
    
    private String format(final byte value) {
        return String.format("@Byte@valueOf('%d').byteValue()", value);
    }
    
    private String format(final short value) {
        return String.format("@Short@valueOf('%d').shortValue()", value);
    }
    
    private String format(final int value) {
        return String.valueOf(value);
    }
    
    private String format(final long value) {
        return String.valueOf(value) + "L";
    }
    
    private String format(final float value) {
        return String.valueOf(value) + "F";
    }
    
    private String format(final double value) {
        return String.valueOf(value) + "D";
    }
    
    private String format(final String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        
        final int len = value.length();
        for(int i=0; i < len; i++) {
            char c = value.charAt(i);
            if(c == '"') {
                // エスケープ
                sb.append("\\\"");
            } else {
                sb.append(c);
            }
        }
        
        sb.append("\"");
        return sb.toString();
    }
    
    private String format(final Enum<?> value) {
        
        String className = value.getClass().getName();
        String itemName = value.name();
        
        return "@" + className + "@" + itemName;
        
    }
    
    private String format(final Class<?> value) {
        
        String className = value.getName();
        return "@" + className + "@class";
        
    }
    
    private String format(final boolean[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new boolean[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final char[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new char[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final byte[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new byte[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final short[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new short[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final int[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new int[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final long[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new long[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final float[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new float[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final double[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new double[] {");
        
        final int len = value.length;
        for(int i=0; i < len; i++) {
            sb.append(format(value[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final String[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("new String[] {");
        
        joinedFormat(value, sb);
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private void joinedFormat(final Object[] array, final StringBuilder sb) {
        
        final int len = array.length;
        for(int i=0; i < len; i++) {
            sb.append(format(array[i]));
            
            if(i < len - 1) {
                sb.append(", ");
            }
        }
        
    }
    
    private String format(final Enum<?>[] value) {
        StringBuilder sb = new StringBuilder();
        
        String className = value.getClass().getComponentType().getName();
        sb.append(String.format("new %s[] {", className));
        
        joinedFormat(value, sb);
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String format(final Class<?>[] value) {
        StringBuilder sb = new StringBuilder();
        
        String className = value.getClass().getComponentType().getName();
        sb.append(String.format("new %s[] {", className));
        
        joinedFormat(value, sb);
        
        sb.append("}");
        
        return sb.toString();
    }
    
}
