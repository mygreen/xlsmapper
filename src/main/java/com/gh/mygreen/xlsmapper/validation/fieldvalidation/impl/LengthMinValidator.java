package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 文字列が指定した文字長以上かどうか検証します。
 * <ul>
 *   <li>メッセージキーは、「cellFieldError.lengthMin」。</li>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「length」：実際の値の文字長。</li>
 *   <li>「min」：指定した最小文字長。</li>
 * </ul>
 *
 * @since 2.0
 * @author T.TSUCHIE
 */
public class LengthMinValidator extends AbstractFieldValidator<String> {

    /** 最小文字長 */
    private final int min;

    /**
     * 制約の文字長を指定するコンストラクタ。
     *
     * @param min 最小文字長
     * @throws IllegalArgumentException {@literal min <= 0}
     */
    public LengthMinValidator(final int min) {
        ArgUtils.notMin(min, 0, "min");
        this.min = min;
    }

    @Override
    public LengthMinValidator addGroup(final Class<?>... group) {
        return (LengthMinValidator)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.lengthMin";
    }

    @Override
    protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {

        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("length", cellField.getValue().length());
        vars.put("min", getMin());
        return vars;
    }

    @Override
    protected void onValidate(final CellField<String> cellField) {

        final int valueLength = cellField.getValue().length();
        if(valueLength >= getMin()) {
            return;
        }

        error(cellField);

    }

    /**
     * 指定した最小文字長を取得します。
     * @return 最小文字長
     */
    public int getMin() {
        return min;
    }

}
