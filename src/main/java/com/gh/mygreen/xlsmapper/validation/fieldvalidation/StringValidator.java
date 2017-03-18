package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * 文字列に関する入力値検証を行う。
 */
public abstract class StringValidator extends AbstractFieldValidator<String>{
    
    private StringValidator() {
        super();
    }
    
    /**
     * 文字列が指定した文字長かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.exactLength」。</li>
     *  <li>メッセージ引数{0}は、文字長。</li>
     * </ul>
     */
    public static class ExactLengthValidator extends StringValidator {
        
        /** 文字長 */
        private final int length;
        
        public ExactLengthValidator(final int length) {
            super();
            ArgUtils.notMin(length, 0, "length");
            this.length = length;
        }
        
        public int getLength() {
            return length;
        }
        
        @Override
        public String getDefaultMessageKey() {
            return "cellFieldError.exactLength";
        }
        
        @Override
        protected boolean validate(final String value) {
            if(isNullValue(value)) {
                return true;
            }
            
            if(value.length() == getLength()) {
                return true;
            }
            return false;
        }
        
        @Override
        protected LinkedHashMap<String, Object> getMessageVars(final String value) {
            final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
            vars.put("validatedValue", value);
            vars.put("valueLength", value.length());
            vars.put("length", getLength());
            return vars;
        }
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.maxLength」。</li>
     *  <li>メッセージ引数{0}は、最大文字長。</li>
     * </ul>
     */
    public static class MaxLengthValidator extends StringValidator {
        
        /** 最大文字長 */
        private final int maxLength;
        
        public MaxLengthValidator(final int maxLength) {
            super();
            ArgUtils.notMin(maxLength, 0, "maxLength");
            this.maxLength = maxLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }
        
        @Override
        public String getDefaultMessageKey() {
            return "cellFieldError.maxLength";
        }
        
        @Override
        protected boolean validate(final String value) {
            if(isNullValue(value)) {
                return true;
            }
            
            if(value.length() <= getMaxLength()) {
                return true;
            }
            return false;
        }
        
        @Override
        protected LinkedHashMap<String, Object> getMessageVars(final String value) {
            final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
            vars.put("validatedValue", value);
            vars.put("valueLength", value.length());
            vars.put("maxLength", getMaxLength());
            return vars;
        }
        
    }
    
    /**
     * 文字列が指定した文字長以上かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.minLength」。</li>
     *  <li>メッセージ引数{0}は、最小文字長。</li>
     * </ul>
     */
    public static class MinLengthValidator extends StringValidator {
        
        /** 最小文字長 */
        private final int minLength;
        
        public MinLengthValidator(final int minLength) {
            super();
            ArgUtils.notMin(minLength, 0, "minLength");
            this.minLength = minLength;
        }
        
        public int getMinLength() {
            return minLength;
        }
        
        @Override
        public String getDefaultMessageKey() {
            return "cellFieldError.minLength";
        }
        
        @Override
        protected boolean validate(final String value) {
            if(isNullValue(value)) {
                return true;
            }
            
            if(value.length() >= getMinLength()) {
                return true;
            }
            return false;
        }
        
        @Override
        protected LinkedHashMap<String, Object> getMessageVars(final String value) {
            final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
            vars.put("validatedValue", value);
            vars.put("valueLength", value.length());
            vars.put("minLength", getMinLength());
            return vars;
        }
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.betweenLength」。</li>
     *  <li>メッセージ引数{0}は、最小文字長。</li>
     *  <li>メッセージ引数{1}は、最大文字長。</li>
     * </ul>
     */
    public static class BetweenLengthValidator extends StringValidator {
        
        /** 最小文字長 */
        private final int minLength;
        
        /** 最大文字長 */
        private final int maxLength;
        
        public BetweenLengthValidator(final int minLength, final int maxLength) {
            super();
            ArgUtils.notMin(minLength, 0, "minLength");
            ArgUtils.notMin(maxLength, 0, "maxLength");
            ArgUtils.notMax(minLength, maxLength, "minLength");
            this.minLength = minLength;
            this.maxLength = maxLength;
        }
        
        public int getMinLength() {
            return minLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }
        
        @Override
        public String getDefaultMessageKey() {
            return "cellFieldError.betweenLength";
            
        }
        
        @Override
        protected boolean validate(final String value) {
            if(isNullValue(value)) {
                return true;
            }
            
            final int strLength = value.length();
            if(getMinLength() <= strLength && strLength <= getMaxLength()) {
                return true;
            }
            
            return false;
        }
        
        @Override
        protected LinkedHashMap<String, Object> getMessageVars(final String value) {
            final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
            vars.put("validatedValue", value);
            vars.put("valueLength", value.length());
            vars.put("minLength", getMinLength());
            vars.put("maxLength", getMaxLength());
            return vars;
        }
        
    }
    
    /**
     * 文字長をチェックするValidatorを取得する。
     * @param length 文字長。
     * @return
     */
    public static StringValidator exactLength(final int length) {
        return  new ExactLengthValidator(length);
    }
    
    /**
     * 文字長が指定した文字長以下かチェックするValidatorを取得する。
     * @param maxLength 最大文字長
     * @return
     */
    public static StringValidator maxLength(final int maxLength) {
        return  new MaxLengthValidator(maxLength);
    }
    
    /**
     * 文字長が指定した文字長以上かチェックするValidatorを取得する。
     * @param minLength 最小文字長
     * @return
     */
    public static StringValidator minLength(final int minLength) {
        return  new MinLengthValidator(minLength);
    }
    
    /**
     * 文字長が指定した文字長の範囲内かかチェックするValidatorを取得する。
     * @param minLength 最小文字長
     * @param maxLength 最大文字長
     * @return
     */
    public static StringValidator betweenLength(final int minLength, final int maxLength) {
        return  new BetweenLengthValidator(minLength, maxLength);
    }
    
}
