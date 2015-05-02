package com.gh.mygreen.xlsmapper.expression.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;


/**
 * EL式を簡単に利用するためのインタフェース。
 * 
 * @author T.TSUCHIE
 *
 */
public class ELProcessor {
    
    private ELManager elManager = new ELManager();
    
    private ExpressionFactory factory = ELManager.getExpressionFactory();
    
    public ELManager getELManager() {
        return elManager;
    }
    
    /**
     * 戻り値のクラスタイプを指定して式を評価する
     * @param expression
     * @param expectedType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C> C eval(final String expression, final Class<C> expectedType) {
        
        final ValueExpression exp = factory.createValueExpression(
                elManager.getELContext(),
                bracket(expression),
                expectedType);
        
        return (C) exp.getValue(elManager.getELContext());
        
    }
    
    /**
     * 式を評価する
     * @param expression
     * @return
     */
    public Object eval(final String expression) {
        return eval(expression, Object.class);
    }
    
    /**
     * 変数を設定する。
     * @param expression
     * @param value
     */
    public void setValue(final String expression, final Object value) {
        final ValueExpression exp = factory.createValueExpression(
                elManager.getELContext(),
                bracket(expression),
                Object.class);
        exp.setValue(elManager.getELContext(), value);
    }
    
    /**
     * 変数を設定する
     * @param variable
     * @param value
     */
    public void setVariable(final String variable, final Object value) {
        final ValueExpression exp = factory.createValueExpression(value, Object.class);
        elManager.setVariable(variable, exp);
    }
    
    /**
     * マップを元に変数を設定する
     * @param variables
     */
    public void setVariables(final Map<String, Object> variables) {
        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            setVariable(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 独自のEL関数を登録する。
     * @param prefix EL式中での接頭語としての名前空間を指定する。
     * @param function EL式中での関数名を指定する。空文字("")を指定した場合、Javaのメソッド名で指定されます。
     * @param className 呼び出すJavaのメソッドが定義されているクラス名を指定する。
     * @param method 呼び出すJavaのメソッドを指定する。引数を持つ場合はクラス型も指定する。メソッドはstaticである必要があります。
     *        例: {@code int sum(int,int)}。引数が複数ある場合はカンマで区切り、空白は開けないようにします。
     * @throws ClassNotFoundException 指定したクラスが見つからない場合。
     * @throws NoSuchMethodException 指定したJavaのメソッドが見つからない場合、またはstaticでない場合。
     * @throws IllegalArgumentException 引数がnullの場合。
     */
    public void defineFunction(final String prefix, final String function,
            final String className, final String method) throws ClassNotFoundException, NoSuchMethodException {
        
        if(prefix == null || function == null || className == null || method == null) {
            throw new IllegalArgumentException("Null argument for defineFunction");
        }
        
        Method meth = null;
        ClassLoader loader = getClass().getClassLoader();
        Class<?> klass = Class.forName(className, false, loader);
        int j = method.indexOf('(');
        if (j < 0) {
            // Just a name is given
            for (Method m: klass.getDeclaredMethods()) {
                if (m.getName().equals(method)) {
                    meth = m;
                }
            }
            if (meth == null) {
                throw new NoSuchMethodException();
            }
        } else {
            // method is the signature
            // First get the method name, ignore the return type
            int p = method.indexOf(' ');
            if (p < 0) {
                throw new NoSuchMethodException(
                    "Bad method singnature: " + method);
            }
            String methodName = method.substring(p+1, j).trim();
            // Extract parameter types
            p = method.indexOf(')', j+1);
            if (p < 0) {
                throw new NoSuchMethodException(
                    "Bad method singnature: " + method);
            }
            String[] params = method.substring(j+1, p).split(",");
            Class<?>[] paramTypes = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = toClass(params[i], loader);
            }
            meth = klass.getDeclaredMethod(methodName, paramTypes);
        }
        
        defineFunction(prefix, function, meth);
        
    }
    
    /**
     * 独自のEL関数を登録する。
     * @param prefix EL式中での接頭語としての名前空間を指定する。
     * @param function EL式中での関数名を指定する。
     * @param method 呼び出すJavaのメソッド。メソッドはstaticである必要があります。
     * @throws NoSuchMethodException 指定したメソッドがstaticでない場合。
     * @throws IllegalArgumentException 引数がnullの場合。
     */
    public void defineFunction(final String prefix, final String function, final Method method) throws NoSuchMethodException {
        
        if (prefix == null || function == null || method == null) {
            throw new IllegalArgumentException("Null argument for defineFunction");
        }
        
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException("The method specified in defineFunction must be static: " + method);
        }
        
        // ELの関数名を省略した場合は、Javaのメソッド名を使用する。
        final String functionName = function.isEmpty() ? method.getName() : function;
        
        elManager.mapFunction(prefix, functionName, method);
        
    }
    
    /**
     * Return the Class object associated with the class or interface with
     * the given name.
     */
    private static Class<?> toClass(String type, ClassLoader loader)
            throws ClassNotFoundException {

        Class<?> c = null;
        int i0 = type.indexOf('[');
        int dims = 0;
        if (i0 > 0) {
            // This is an array.  Count the dimensions
            for (int i = 0; i < type.length(); i++) {
                if (type.charAt(i) == '[')
                    dims++;
            }
            type = type.substring(0, i0);
        }

        if ("boolean".equals(type))
            c = boolean.class;
        else if ("char".equals(type))
            c = char.class;
        else if ("byte".equals(type))
            c =  byte.class;
        else if ("short".equals(type))
            c = short.class;
        else if ("int".equals(type))
            c = int.class;
        else if ("long".equals(type))
            c = long.class;
        else if ("float".equals(type))
            c = float.class;
        else if ("double".equals(type))
            c = double.class;
        else
            c = loader.loadClass(type);

        if (dims == 0)
            return c;

        if (dims == 1)
            return java.lang.reflect.Array.newInstance(c, 1).getClass();

        // Array of more than i dimension
        return java.lang.reflect.Array.newInstance(c, new int[dims]).getClass();
    }
    
    /**
     * EL式を括弧で囲む
     * @param expression
     * @return
     */
    private String bracket(String expression) {
        return "${" + expression + '}';
    }
    
}
