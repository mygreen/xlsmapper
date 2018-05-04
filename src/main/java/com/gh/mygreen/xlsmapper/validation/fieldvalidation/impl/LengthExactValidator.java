package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 文字列が指定した文字長かどうか検証する。
 * <ul>
 *   <li>メッセージキーは、「cellFieldError.lengthExact」。</li>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「length」：実際の値の文字長。</li>
 *   <li>「requiredLengths」：指定した文字長。</li>
 * </ul>
 *
 * @since 2.0
 * @author T.TSUCHIE
 */
public class LengthExactValidator extends AbstractFieldValidator<String> {

    /** 必要な文字長 */
    private final List<Integer> requiredLengths;

    /**
     * 文字長を指定するコンストラクタ
     *
     * @param requiredLengths 必要な文字長。複数指定可能。
     */
    public LengthExactValidator(final int... requiredLengths) {
        ArgUtils.notEmpty(requiredLengths, "requiredLengths");
        this.requiredLengths = Arrays.stream(requiredLengths)
                .filter(l -> l >= 0)
                .sorted()
                .distinct()
                .boxed()
                .collect(Collectors.toList());
    }

    @Override
    public LengthExactValidator addGroup(final Class<?>... group) {
        return (LengthExactValidator)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.lengthExact";
    }

    @Override
    protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
        final Map<String, Object> vars = super.getMessageVariables(cellField);

        vars.put("length", cellField.getValue().length());
        vars.put("requiredLengths", getRequiredLengths());

        return vars;
    }

    @Override
    protected void onValidate(final CellField<String> cellField) {

        final int length = cellField.getValue().length();
        if(requiredLengths.contains(length)) {
            return;
        }

        error(cellField);
    }

    /**
     * 比較対象の文字長の候補を取得する。
     * @return 文字長の候補を取得する。
     */
    public int[] getRequiredLengths() {
        return Utils.toArray(requiredLengths);
    }

}
