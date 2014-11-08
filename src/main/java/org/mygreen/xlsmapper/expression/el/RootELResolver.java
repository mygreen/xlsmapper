package org.mygreen.xlsmapper.expression.el;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;

/**
 * ルートノードを処理するためのELResolver.
 * <p>ELはルートノードのアクセス時のみgetValueのbaseがnullとなりますので、
 *    その場合のみ指定のオブジェクトを探索するようにし、それ以外はbaseを利用するようにするという程度のものです
 *
 * @author T.TSUCHIE
 *
 */
public class RootELResolver extends ELResolver {
    
    /**
     * EL式中で利用可能な標準のフォーマッターの変数の名称。
     * Name under which to bind a formatter to the EL context.
     */
    public static final String FORMATTER = "formatter";
    private static final String FORMAT = "format";
    
    /**
     * プロパティ（変数）の値のキャッシュ。
     */
    private final Map<String, Object> map = new ConcurrentHashMap<String, Object>();
    
    /**
     * 式中の変数が解決できない場合無視するかどうか。
     */
    private final boolean ignoreNotFoundProperty;
    
    public RootELResolver() {
        this(false);
    }
    
    /**
     * 式中のプロパティが見つからない場合無視するかどうかを指定するコンストラクタ。
     * @param ignoreNotFoundProperty 式中のプロパティが見つからない場合無視するかどうか
     */
    public RootELResolver(final boolean ignoreNotFoundProperty) {
        this.ignoreNotFoundProperty = ignoreNotFoundProperty;
    }
    
    
    /**
     * {@inheritDoc}
     * <p>baseがnull、つまり<code>${first.xxxx}</code>のfirstの場合は、文字列として変数名を受け取ることを示す。
     */
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        return base == null ? String.class : null;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        
        return base == null ? null : Collections.<FeatureDescriptor>emptyListIterator();
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        // nulの場合、次のELResolverへ
        return resolve(context, base, property) ? Object.class : null;
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if(resolve(context, base, property)) {
            if(!isProperty((String) property)) {
                if(isIgnoreNotFoundProperty()) {
                    return null;
                }
                throw new PropertyNotFoundException("Cannot find property " + property);
            }
            return getProperty((String) property);
        }
        
        // 不明な場合は次のELResolverへ
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object value) {
        if(resolve(context, base, property)) {
            setProperty((String) property, value);
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        return true;
    }
    
    @Override
    public Object invoke(final ELContext context, final Object base, final Object method,
            final Class<?>[] paramTypes, final Object[] params) {
        
        if(resolve(context, base, method)) {
            throw new ELException("Invalid property");
        }
        Object returnValue = null;
        // due to bugs in most EL implementations when it comes to evaluating varargs we take care
        // of the formatter call
        // ourselves.
        if(base instanceof FormatterWrapper) {
            returnValue = evaluateFormatExpression(context, method, params);
        }
        return returnValue;
    }
    
    private Object evaluateFormatExpression(final ELContext context, final Object method, final Object[] params) {
        if(!FORMAT.equals(method)) {
            throw new ELException(String.format("Wrong method name 'formatter#%s' does not exist. Only formatter#format is supported.", method));
        }
        
        if(params.length == 0) {
            throw new ELException("Invalid number of arguments to Formatter#format");
        }
        if(!(params[0] instanceof String)) {
            throw new ELException("The first argument to Formatter#format must be String");
        }
        
        final FormatterWrapper formatterWrapper = (FormatterWrapper)context.getVariableMapper()
                .resolveVariable(FORMATTER)
                .getValue(context);
        final Object[] formattingParameters = new Object[params.length - 1];
        System.arraycopy( params, 1, formattingParameters, 0, params.length - 1 );
        
        Object returnValue;
        try {
            returnValue = formatterWrapper.format( (String) params[0], formattingParameters );
            context.setPropertyResolved(true);
        } catch (IllegalFormatException e) {
            throw new ELException("Error in Formatter#format call", e);
        }
        
        return returnValue;
    }
    
    private boolean resolve(final ELContext context, final Object base, final Object property) {
        context.setPropertyResolved( base == null && property instanceof String );
        // 解釈済みであることを示す
        return context.isPropertyResolved();
    }
    
    private Object getProperty(String property) {
        return map.get(property);
    }
    
    private void setProperty(String property, Object value) {
        map.put(property, value);
    }
    
    private boolean isProperty(String property) {
        return map.containsKey(property);
    }
    
    /**
     * 式中のプロパティ（変数）が見つからない場合、無視するかどうか。
     * <p>無視しない場合、例外{@link PropertyNotFoundException}が発生します。
     * @return
     */
    public boolean isIgnoreNotFoundProperty() {
        return ignoreNotFoundProperty;
    }
    
}
