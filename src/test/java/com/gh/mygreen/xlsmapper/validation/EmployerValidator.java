package com.gh.mygreen.xlsmapper.validation;

import com.gh.mygreen.xlsmapper.Employer;
import com.gh.mygreen.xlsmapper.validation.AbstractObjectValidator;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


public class EmployerValidator extends AbstractObjectValidator<Employer>{
    
    // ネストしたBeanのValidator
    private EmployerHistoryValidator historyValidator;
    
    public EmployerValidator() {
        this.historyValidator = new EmployerHistoryValidator();
    }
    
    @Override
    public void validate(final Employer targetObj, final SheetBindingErrors errors) {
        
        // 型変換などのエラーがない場合、文字長のチェックを行う
        // チェック対象のフィールド名を指定します。
//        if(!errors.hasFieldErrors("name")) {
//            if(targetObj.getName().length() > 10) {
//                errors.rejectValue("name", "error.maxLength", new Object[]{10});
//                errors.rejectSheetValue(field, cellAddress, errorCode);
//            }
//        }
        
        for(int i=0; i < targetObj.getHistory().size(); i++) {
            // ネストしたBeanの検証の実行
            // パスをネストする。リストの場合はインデックスを指定する。
//            errors.pushNestedPath("history", i);
//            historyValidator.validate(targetObj.getHistory().get(i), errors);
//            // 検証後は、パスを戻す
//            errors.popNestedPath();
            
            // パスのネストと戻しは、invokeNestedValidatorで自動的にもできます。
             invokeNestedValidator(historyValidator, targetObj.getHistory().get(i), errors, "history", i);
        }
        
    }
}
