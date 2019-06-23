package com.gh.mygreen.xlsmapper.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.localization.MessageResolver;
import com.gh.mygreen.xlsmapper.localization.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * シート用のエラーオブジェクトを解釈して、メッセージに変換するクラス。
 * <p>オブジェクトの種類ごとに、デフォルトメッセージ変数が利用できます。</p>
 * <ul>
 *  <li>'fieldLabel'：フィールドのラベル。メッセージ用のプロパティファイルにフィールド名を定義している場合、自動的に設定されます。</li>
 *  <li>'objectLabel'：オブジェクトのラベル。メッセージ用のプロパティファイルにオブジェクト名を定義している場合、自動的に設定されます。</li>
 *  <li>'label'：ラベル変数'label'が指定されていない場合、エラー対象のオブジェクト名またはフィールド名から自動的に決定されます。</li>
 *  <li>'sheetName':シート用のエラーの場合、シート名が設定されます。</li>
 *  <li>'cellAddress':シート用のフィールドエラーの場合、セルのアドレスが設定されます。'A1'のような形式になります。</li>
 * </ul>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SheetErrorFormatter {
    
    private MessageResolver messageResolver = new ResourceBundleMessageResolver();
    
    private MessageInterpolator messageInterporlator = new MessageInterpolator();
    
    private MessageCodeGenerator messageCodeGenerator = new MessageCodeGenerator();
    
    public SheetErrorFormatter() {
        
    }
    
    /**
     * エラーオブジェクトのリストをメッセージにフォーマットする。
     * @param errors 変換対象のエラーオブジェクト。
     * @return
     * @throws IllegalArgumentException errors == null.
     */
    public List<String> format(final Collection<ObjectError> errors) {
        ArgUtils.notNull(errors, "errors");
        
        final List<String> messageList = new ArrayList<>();
        for(ObjectError error : errors) {
            messageList.add(format(error));
        }
        
        return messageList;
    }
    
    /**
     * エラーオブジェクトをメッセージにフォーマットする。
     * @param error エラーオブジェクト
     * @return メッセージ
     * @throws IllegalArgumentException {@literal error == null.}
     */
    public String format(final ObjectError error) {
        ArgUtils.notNull(error, "error");
        
        final Map<String, Object> vars = new HashMap<>();
        vars.putAll(error.getVariables());
        
        if(error instanceof FieldError) {
            // フィールドエラーのメッセージを処理する
            
            final FieldError fieldError = (FieldError) error;
            final String[] labelCode = messageCodeGenerator.generateFieldNameCodes(fieldError.getObjectName(), fieldError.getField());
            
            try {
                vars.put("fieldLabel", getMessage(labelCode, null));
            } catch(Throwable e) {
            }
            
            final Optional<String> label = error.getLabelAsOptional();
            if(label.isPresent()) {
                vars.put("label", label.get());
            } else {
                try {
                    vars.put("label", getMessage(labelCode, null));
                } catch(Throwable e) {
                }
            }
            
            try {
                // 親のラベル名を取得する
                String[] parentCode = messageCodeGenerator.generateParentNameCodes(fieldError.getObjectName(), fieldError.getField());
                vars.put("parentLabel", getMessage(parentCode, null));
            } catch(Throwable e) {
            }
            
            try {
                String[] objectCode = messageCodeGenerator.generateObjectNameCodes(fieldError.getObjectName());
                vars.put("objectLabel", getMessage(objectCode, null));
            } catch(Throwable e) {
                
            }
            
            fieldError.getSheetName().ifPresent(s -> vars.put("sheetName", s));
            fieldError.getAddressAsOptional().ifPresent(a -> vars.put("cellAddress", a));
            
        } else {
            // オブジェクトエラーのメッセージを処理する。
            
            final String[] labelCode = messageCodeGenerator.generateObjectNameCodes(error.getObjectName());
            
            final Optional<String> label = error.getLabelAsOptional();
            if(label.isPresent()) {
                vars.put("label", label.get());
            } else {
                try {
                    vars.put("label", getMessage(labelCode, null));
                } catch(Throwable e) {
                }
            }
            
            try {
                String[] objectCode = messageCodeGenerator.generateObjectNameCodes(error.getObjectName());
                vars.put("objectLabel", getMessage(objectCode, null));
            } catch(Throwable e) {
                
            }
            
            error.getSheetName().ifPresent(s -> vars.put("sheetName", s));
            
        }
        
        final String message = getMessage(error.getCodes(), error.getDefaultMessage());
        return messageInterporlator.interpolate(message, vars, true, messageResolver);
        
    }
    
    /**
     * 指定した引数の候補からメッセージを取得する。
     * @param codes メッセージコードの候補
     * @param defaultMessage メッセージコードが見つからない場合のメッセージ
     * @return メッセージ
     * @throws RuntimeException メッセージコード 'codes' で指定したメッセージキーが見つからない場合。
     */
    private String getMessage(final String[] codes, final Optional<String> defaultMessage) {
        for(String code : codes) {
            try {
                final Optional<String> message = messageResolver.getMessage(code);
                if(message.isPresent()) {
                    return message.get();
                }
                
            } catch(Throwable e) {
                continue;
            }
        }
        
        if(defaultMessage.isPresent()) {
            return defaultMessage.get();
        }
        
        throw new RuntimeException(String.format("not found message code [%s].", Utils.join(codes, ",")));
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
