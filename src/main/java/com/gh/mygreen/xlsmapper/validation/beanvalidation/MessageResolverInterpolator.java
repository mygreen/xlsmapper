package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.validation.MessageInterpolator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.MessageResolver;


/**
 * {@code MessageResolver}からメッセージを取得する {@link MessageInterpolator}。
 * <p>BeanValidatorのメッセージの取得先をXlsMapper用の{@link MessageResolver}から取得するよう変更する場合に使用する。
 *
 * @author T.TSUCHIE
 *
 */
public class MessageResolverInterpolator implements MessageInterpolator {
    
    private MessageInterpolator messageInterpolator;
    
    public MessageResolverInterpolator(final MessageResolver messageResolver) {
        ArgUtils.notNull("messageResolver", "messageResolver");
        this.messageInterpolator = new ResourceBundleMessageInterpolator(
                new MessageResolverResourceBundleLocator(messageResolver));
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        return messageInterpolator.interpolate(messageTemplate, context);
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context, Locale locale) {
        return messageInterpolator.interpolate(messageTemplate, context, locale);
    }
    
    private static class MessageResolverResourceBundleLocator implements ResourceBundleLocator {
        
        private final MessageResolver messageResolver;
        
        public MessageResolverResourceBundleLocator(final MessageResolver messageResolver) {
            ArgUtils.notNull(messageResolver, "messageResolver");
            this.messageResolver = messageResolver;
        }
        
        public ResourceBundle getResourceBundle(Locale locale) {
            return new MessageResolverResourceBundle(this.messageResolver, locale);
        }
        
    }
    
    private static class MessageResolverResourceBundle extends ResourceBundle {
        
        private final MessageResolver messageResolver;
        
        private final Locale locale;
        
        public MessageResolverResourceBundle(final MessageResolver messageResolver, final Locale locale) {
            ArgUtils.notNull(messageResolver, "messageResolver");
            this.messageResolver = messageResolver;
            this.locale = locale;
        }
        
        @Override
        protected Object handleGetObject(final String key) {
            return messageResolver.getMessage(key);
        }
        
        @Override
        public boolean containsKey(final String key) {
            return (messageResolver.getMessage(key) != null);
        }
        
        @Override
        public Enumeration<String> getKeys() {
            throw new UnsupportedOperationException(
                    String.format("'%s' does not support enumerating its keys", getClass().getSimpleName()));
        }
        
        @Override
        public Locale getLocale() {
            return locale;
        }
        
    }

}
