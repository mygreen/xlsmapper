package org.mygreen.xlsmapper.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mygreen.xlsmapper.ArgUtils;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperException;


/**
 * エラーオブジェクトを解釈して、メッセージに変換するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class SheetMessageConverter {
    
    private MessageResolver messageResolver = new ResourceBundleMessageResolver();
    
    private MessageInterpolator messageInterporlator = new MessageInterpolator();
    
    private MessageCodeGenerator messageCodeGenerator = new MessageCodeGenerator();
    
    public SheetMessageConverter() {
        
    }
    
    public List<String> convertMessages(final Collection<ObjectError> errors) {
        ArgUtils.notNull(errors, "errors");
        
        final List<String> messageList = new ArrayList<String>();
        for(ObjectError error : errors) {
            messageList.add(convertMessage(error));
        }
        
        return messageList;
    }
    
    public String convertMessage(final ObjectError error) {
        
        if(error.getArgs() != null) {
            return convertMessageWithIndexArgs(error);
        } else if(error.getVars() != null) {
            return convertMessageWithNameArgs(error);
        } else {
            return convertMessageWithNameArgs(error);
        }
        
    }
    
    protected String convertMessageWithIndexArgs(final ObjectError error) {
        
        final List<Object> args = new ArrayList<Object>();
        if(error instanceof FieldError) {
            if(error.getLabel() != null) {
                args.add(error.getLabel());
            } else {
                final String[] labelCode = messageCodeGenerator.generateFieldNameCodes(error.getObjectName(), ((FieldError) error).getFieldPath());
                try {
                    args.add(getMessage(labelCode, error.getDefaultMessage()));
                } catch(Throwable e) {
    //                args.add(null);
                }
            }
        } else {
            final String[] labelCode = messageCodeGenerator.generateObjectNameCodes(error.getObjectName());
            if(error.getLabel() != null) {
                args.add(error.getLabel());
            } else {
                try {
                    args.add(getMessage(labelCode, error.getDefaultMessage()));
                } catch(Throwable e) {
    //                args.add(null);
                }
            }
        }
        
        if(error.getArgs() != null) {
            args.add(Arrays.asList(error.getArgs()));
        }
        
        final String message = getMessage(error.getCodes(), error.getDefaultMessage());
        
        return MessageFormat.format(message, args.toArray(new Object[args.size()]));
        
    }
    
    protected String convertMessageWithNameArgs(final ObjectError error) {
        
        final Map<String, Object> vars = new LinkedHashMap<String, Object>();
        if(error.getVars() != null) {
            vars.putAll(error.getVars());
        }
        
        if(error instanceof FieldError) {
            final FieldError fieldError = (FieldError) error;
            final String[] labelCode = messageCodeGenerator.generateFieldNameCodes(fieldError.getObjectName(), fieldError.getFieldPath());
            
            try {
                vars.put("fieldLabel", getMessage(labelCode, error.getDefaultMessage()));
            } catch(Throwable e) {
            }
            
            if(error.getLabel() != null) {
                vars.put("label", error.getLabel());
            } else {
                try {
                    vars.put("field", getMessage(labelCode, error.getDefaultMessage()));
                } catch(Throwable e) {
                }
            }
            
            try {
                String[] objectCode = messageCodeGenerator.generateObjectNameCodes(fieldError.getObjectName());
                vars.put("objectLabel", getMessage(objectCode, error.getDefaultMessage()));
            } catch(Throwable e) {
                
            }
            
            if(error instanceof CellFieldError) {
                final CellFieldError cellFieldError = (CellFieldError) error;
                vars.put("sheetName", cellFieldError.getSheetName());
                vars.put("cellAddress", Utils.formatCellAddress(cellFieldError.getCellAddress()));
            }
            
        } else {
            final String[] labelCode = messageCodeGenerator.generateObjectNameCodes(error.getObjectName());
            if(error.getLabel() != null) {
                vars.put("label", error.getLabel());
            } else {
                try {
                    vars.put("label", getMessage(labelCode, error.getDefaultMessage()));
                } catch(Throwable e) {
                }
            }
            
            try {
                String[] objectCode = messageCodeGenerator.generateObjectNameCodes(error.getObjectName());
                vars.put("objectLabel", getMessage(objectCode, error.getDefaultMessage()));
            } catch(Throwable e) {
                
            }
            
            if(error instanceof SheetObjectError) {
                final SheetObjectError sheetObjectError = (SheetObjectError) error;
                vars.put("sheetName", sheetObjectError.getSheetName());
            }
            
        }
        
        final String message = getMessage(error.getCodes(), error.getDefaultMessage());
        return messageInterporlator.interpolate(message, vars, true);
        
    }
    
    /**
     * 指定した引数の候補からメッセージを取得する。
     * @param codes
     * @param メッセージコードが見つからない場合のメッセージ
     * @return
     */
    public String getMessage(final String[] codes, final String defaultMessage) {
        for(String code : codes) {
            try {
                final String message = messageResolver.getMessage(code);
                if(message != null) {
                    return message;
                }
            } catch(Throwable e) {
                continue;
            }
        }
        
        if(Utils.isNotEmpty(defaultMessage)) {
            return defaultMessage;
        }
        
        throw new RuntimeException(String.format("not found message code [%s].", Utils.join(codes, ",")));
    }
    
    public String format(final String[] codes, final Map<String, Object> vars, final String defaultMessage) throws XlsMapperException {
        
        final String message = getMessage(codes, defaultMessage);
        return messageInterporlator.interpolate(message, vars);
        
    }
    
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
    
    public void setMessageResolver(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }
    
    public MessageInterpolator getMessageInterporlator() {
        return messageInterporlator;
    }
    
    public void setMessageInterporlator(MessageInterpolator messageInterporlator) {
        this.messageInterporlator = messageInterporlator;
    }
    
    public MessageCodeGenerator getMessageCodeGenerator() {
        return messageCodeGenerator;
    }
    
    public void setMessageCodeGenerator(MessageCodeGenerator messageCodeGenerator) {
        this.messageCodeGenerator = messageCodeGenerator;
    }
}
