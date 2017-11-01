package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.metadata.ConstraintDescriptor;

import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.localization.MessageResolver;
import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * XlsMapperの{@link MessageInterpolator}とBeanValidationの{@link javax.validation.MessageInterpolator}のAdaptor。
 * <p>BeanValidatorのメッセ－時処理時、特に式言語の実装切り替えする場合に利用する。
 *
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolatorAdapter implements javax.validation.MessageInterpolator {
    
    private final MessageResolver messageResolver;
    
    private final MessageInterpolator sheetMessageInterpolator;
    
    public MessageInterpolatorAdapter(final MessageResolver messageResolver, 
            final MessageInterpolator sheetMessageInterpolator) {
        ArgUtils.notNull(messageResolver, "messageResolver");
        ArgUtils.notNull(sheetMessageInterpolator, "sheetMessageInterpolator");
        this.messageResolver = messageResolver;
        this.sheetMessageInterpolator = sheetMessageInterpolator;
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        return sheetMessageInterpolator.interpolate(messageTemplate, createMessageVars(context), true, messageResolver);
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
        return sheetMessageInterpolator.interpolate(messageTemplate, createMessageVars(context), true, messageResolver);
    }
    
    /**
     * メッセージ中で利用可能な変数を作成する
     * @param context コンテキスト
     * @return メッセージ変数のマップ
     */
    protected Map<String, Object> createMessageVars(final Context context) {
        
        final Map<String, Object> vars = new HashMap<String, Object>();
        
        final ConstraintDescriptor<?> descriptor = context.getConstraintDescriptor();
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();
            
            vars.put(attrName, attrValue);
        }
        
        // 検証対象の値
        vars.computeIfAbsent("validatedValue", key -> context.getValidatedValue());
        
        // デフォルトのメッセージ
        final String defaultCode = String.format("%s.message", descriptor.getAnnotation().annotationType().getCanonicalName());
        final Optional<String> defaultMessage = messageResolver.getMessage(defaultCode);
        
        vars.put(defaultCode, 
                defaultMessage.orElseThrow(() -> new RuntimeException(String.format("not found message code '%s'", defaultCode))));
        
        return vars;
    }
}
