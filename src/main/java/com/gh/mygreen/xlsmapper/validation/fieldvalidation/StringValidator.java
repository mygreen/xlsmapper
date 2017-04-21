package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * 文字列に関する入力値検証を行う。
 * @version 2.0
 */
public abstract class StringValidator extends AbstractFieldValidator<String>{
    
    private StringValidator() {
        super();
    }
    
    /**
     * 文字列が指定した文字長かどうかチェックする。
     * <ul>
     *   <li>メッセージキーは、「fieldError.exactLength」。</li>
     *   <li>「valueLength」：実際の値の文字長。</li>
     *   <li>「length」：指定した文字長。</li>
     * </ul>
     */
    public static class ExactLengthValidator extends StringValidator {
        
        /** 文字長 */
        private final int length;
        
        public ExactLengthValidator(final int length) {
            ArgUtils.notMin(length, 0, "length");
            this.length = length;
        }
        
        @Override
        public String getMessageKey() {
            return "cellFieldError.exactLength";
        }
        
        @Override
        protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
            final Map<String, Object> vars = super.getMessageVariables(cellField);
            
            vars.put("valueLength", cellField.getValue().length());
            vars.put("length", getLength());
            
            return vars;
        }
        
        @Override
        protected void onValidate(final CellField<String> cellField) {
            
            int valueLength = cellField.getValue().length();
            if(valueLength == getLength()) {
                return;
            }
            
            error(cellField);
        }
        
        /**
         * 文字長を取得します。
         * @return
         */
        public int getLength() {
            return length;
        }
        
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.maxLength」。</li>
     *  <li>「valueLength」：実際の値の文字長。</li>
     *  <li>「maxLength」：指定した最大文字長。</li>
     * </ul>
     */
    public static class MaxLengthValidator extends StringValidator {
        
        /** 最大文字長 */
        private final int maxLength;
        
        /**
         * 
         * @param maxLength 最大文字長
         * @throws IllegalArgumentException {@literal maxLength <= 0}
         */
        public MaxLengthValidator(final int maxLength) {
            ArgUtils.notMin(maxLength, 0, "maxLength");
            this.maxLength = maxLength;
        }
        
        @Override
        public String getMessageKey() {
            return "cellFieldError.maxLength";
        }
        
        @Override
        protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
            
            final Map<String, Object> vars = super.getMessageVariables(cellField);
            vars.put("valueLength", cellField.getValue().length());
            vars.put("maxLength", getMaxLength());
            return vars;
        }
        
        @Override
        protected void onValidate(final CellField<String> cellField) {
            
            final int valueLength = cellField.getValue().length();
            if(valueLength <= getMaxLength()) {
                return;
            }
            
            error(cellField);
            
        }
        
        /**
         * 指定した最大文字長を取得します。
         * @return 最大文字長
         */
        public int getMaxLength() {
            return maxLength;
        }
        
    }
    
    /**
     * 文字列が指定した文字長以上かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.minLength」。</li>
     *  <li>「valueLength」：実際の値の文字長。</li>
     *  <li>「minLength」：指定した最小文字長。</li>
     * </ul>
     */
    public static class MinLengthValidator extends StringValidator {
        
        /** 最小文字長 */
        private final int minLength;
        
        /**
         * 
         * @param minLength 最小文字長
         * @throws IllegalArgumentException {@literal minLength <= 0}
         */
        public MinLengthValidator(final int minLength) {
            ArgUtils.notMin(minLength, 0, "minLength");
            this.minLength = minLength;
        }
        
        @Override
        public String getMessageKey() {
            return "cellFieldError.minLength";
        }
        
        @Override
        protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
            
            final Map<String, Object> vars = super.getMessageVariables(cellField);
            vars.put("valueLength", cellField.getValue().length());
            vars.put("minLength", getMinLength());
            return vars;
        }
        
        @Override
        protected void onValidate(final CellField<String> cellField) {
            final int valueLength = cellField.getValue().length();
            if(valueLength >= getMinLength()) {
                return;
            }
            
            error(cellField);
        }
        
        /**
         * 最小文字長を取得する
         * @return
         */
        public int getMinLength() {
            return minLength;
        }
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうかチェックする。
     * <ul>
     *  <li>メッセージキーは、「fieldError.betweenLength」。</li>
     *  <li>「valueLength」：実際の値の文字長。</li>
     *  <li>「minLength」：指定した最小文字長。</li>
     *  <li>「maxLength」：指定した最大文字長。</li>
     * </ul>
     */
    public static class BetweenLengthValidator extends StringValidator {
        
        /** 最小文字長 */
        private final int minLength;
        
        /** 最大文字長 */
        private final int maxLength;
        
        /**
         * 
         * @param minLength 最小文字長
         * @param maxLength 最大文字長
         * @throws IllegalArgumentException {@literal minLength <=0 or maxLength <= 0 or minLength > maxLength}
         */
        public BetweenLengthValidator(final int minLength, final int maxLength) {
            ArgUtils.notMin(minLength, 0, "minLength");
            ArgUtils.notMin(maxLength, 0, "maxLength");
            ArgUtils.notMax(minLength, maxLength, "minLength");
            this.minLength = minLength;
            this.maxLength = maxLength;
        }
        
        @Override
        public String getMessageKey() {
            return "cellFieldError.betweenLength";
            
        }
        
        @Override
        protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
            
            final Map<String, Object> vars = super.getMessageVariables(cellField);
            vars.put("valueLength", cellField.getValue().length());
            vars.put("minLength", getMinLength());
            vars.put("maxLength", getMaxLength());
            
            return vars;
        }
        
        @Override
        protected void onValidate(final CellField<String> cellField) {
            
            final int valueLength = cellField.getValue().length();
            if(getMinLength() <= valueLength && valueLength <= getMaxLength()) {
                return;
            }
            
            error(cellField);
        }
        
        /**
         * 最小文字長を取得する
         * @return 最小文字長
         */
        public int getMinLength() {
            return minLength;
        }
        
        /**
         * 最大文字長を取得する
         * @return 最大文字長
         */
        public int getMaxLength() {
            return maxLength;
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
