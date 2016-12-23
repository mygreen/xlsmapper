package com.gh.mygreen.xlsmapper.validation;

import java.util.Properties;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * The resolves messages based on the registered {@link Properties}.
 *
 * @author T.TSUCHIE
 *
 */
public class PropertiesMessageResolver implements MessageResolver {
    
    protected Properties properties = new Properties();
    
    public PropertiesMessageResolver() {
        this.properties = new Properties();
    }
    
    public PropertiesMessageResolver(final Properties properties) {
        ArgUtils.notNull(properties, "properties");
        
        this.properties = properties;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(final String code) {
        return properties.getProperty(code);
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
}
