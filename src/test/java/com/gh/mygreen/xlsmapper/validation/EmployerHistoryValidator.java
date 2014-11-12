package com.gh.mygreen.xlsmapper.validation;

import java.util.Date;

import com.gh.mygreen.xlsmapper.EmployerHistory;
import com.gh.mygreen.xlsmapper.validation.AbstractObjectValidator;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.MaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.MinValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.StringValidator;


public class EmployerHistoryValidator extends AbstractObjectValidator<EmployerHistory>{
    
    @Override
    public void validate(final EmployerHistory targetObj, final SheetBindingErrors errors) {
        
        final CellField<Date> historyDateField = new CellField<Date>(targetObj, "historyDate");
        historyDateField.setRequired(true)
            .add(new MinValidator<Date>(new Date(), "yyyy-MM-dd"))
            .validate(errors);
        
        
        final CellField<String> commentField = new CellField<String>(targetObj, "comment");
        commentField.setRequired(false)
            .add(StringValidator.maxLength(5))
            .validate(errors);
        
        if(historyDateField.hasNotErrors(errors) && commentField.hasNotErrors(errors)) {
            // 項目間のチェックの実装など
            if(commentField.isInputEmpty()) {
                errors.reject("error.01");
            }
        }
        
    }
}
