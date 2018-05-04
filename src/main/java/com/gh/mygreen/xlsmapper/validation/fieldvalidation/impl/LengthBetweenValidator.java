package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 文字列が指定した文字長の範囲内かどうかチェックする。
 * <ul>
 *   <li>メッセージキーは、「cellFieldError.lengthBetween」。</li>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「length」：実際の値の文字長。</li>
 *   <li>「min」：指定した最小文字長。</li>
 *   <li>「max」：指定した最大文字長。</li>
 * </ul>
 *
 * @since 2.0
 * @author T.TSUCHIE
 */
public class LengthBetweenValidator extends AbstractFieldValidator<String> {

    /** 最小文字長 */
    private final int min;

    /** 最大文字長 */
    private final int max;

    /**
     * 制約の文字長を指定するコンストラクタ。
     *
     * @param min 最小文字長
     * @param max 最大文字長
     * @throws IllegalArgumentException {@literal min <=0 or max <= 0 or min > max}
     */
    public LengthBetweenValidator(final int min, final int max) {
        ArgUtils.notMin(min, 0, "min");
        ArgUtils.notMin(max, 0, "max");
        ArgUtils.notMax(min, max, "min");
        this.min = min;
        this.max = max;

    }

    @Override
    public LengthBetweenValidator addGroup(final Class<?>... group) {
        return (LengthBetweenValidator)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.lengthBetween";

    }

    @Override
    protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {

        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("length", cellField.getValue().length());
        vars.put("min", getMin());
        vars.put("max", getMax());

        return vars;
    }

    @Override
    protected void onValidate(final CellField<String> cellField) {

        final int length = cellField.getValue().length();
        if(getMin() <= length && length <= getMax()) {
            return;
        }

        error(cellField);
    }

    /**
     * 最小文字長を取得する
     * @return 最小文字長
     */
    public int getMin() {
        return min;
    }

    /**
     * 最大文字長を取得する
     * @return 最大文字長
     */
    public int getMax() {
        return max;
    }

}
