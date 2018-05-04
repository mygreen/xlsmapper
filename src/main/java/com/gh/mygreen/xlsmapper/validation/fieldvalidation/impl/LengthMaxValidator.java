package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 文字列が指定した文字長以内かどうか検証します。
 * <ul>
 *   <li>メッセージキーは、「cellFieldError.lengthMax」。</li>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「length」：実際の値の文字長。</li>
 *   <li>「max」：指定した最大文字長。</li>
 * </ul>
 *
 * @since 2.0
 * @author T.TSUCHIE
 */
public class LengthMaxValidator extends AbstractFieldValidator<String> {

    /** 最大文字長 */
    private final int max;

    /**
     * 制約の文字長を指定するコンストラクタ。
     *
     * @param max 最大文字長
     * @throws IllegalArgumentException {@literal max <= 0}
     */
    public LengthMaxValidator(final int max) {
        ArgUtils.notMin(max, 0, "max");
        this.max = max;
    }

    @Override
    public LengthMaxValidator addGroup(final Class<?>... group) {
        return (LengthMaxValidator)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.lengthMax";
    }

    @Override
    protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {

        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("length", cellField.getValue().length());
        vars.put("max", getMax());
        return vars;
    }

    @Override
    protected void onValidate(final CellField<String> cellField) {

        final int valueLength = cellField.getValue().length();
        if(valueLength <= getMax()) {
            return;
        }

        error(cellField);

    }

    /**
     * 指定した最大文字長を取得します。
     * @return 最大文字長
     */
    public int getMax() {
        return max;
    }

}
