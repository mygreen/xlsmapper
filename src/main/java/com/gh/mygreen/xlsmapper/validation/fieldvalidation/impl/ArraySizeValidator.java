package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractArrayFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.ArrayCellField;

/**
 * 配列やリストが指定したサイズかチェックする。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArraySizeValidator<E> extends AbstractArrayFieldValidator<E> {

    /** 最小サイズ */
    private final int min;

    /** 最大サイズ */
    private final int max;

    /**
     * 制約のサイズを指定するコンストラクタ。
     *
     * @param min 最小サイズ
     * @param max 最大サイズ
     * @throws IllegalArgumentException {@literal min <=0 or max <= 0 or min > max}
     */
    public ArraySizeValidator(final int min, final int max) {
        ArgUtils.notMin(min, 0, "min");
        ArgUtils.notMin(max, 0, "max");
        ArgUtils.notMax(min, max, "min");
        this.min = min;
        this.max = max;

    }

    @Override
    public ArraySizeValidator<E> addGroup(final Class<?>... group) {
        return (ArraySizeValidator<E>)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.arraySize";

    }

    @Override
    protected Map<String, Object> getMessageVariables(final ArrayCellField<E> cellField) {

        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("size", cellField.getValueAsList().size());
        vars.put("min", getMin());
        vars.put("max", getMax());

        return vars;
    }

    @Override
    protected void onValidate(final ArrayCellField<E> cellField) {

        final int length = cellField.getValueAsList().size();
        if(getMin() <= length && length <= getMax()) {
            return;
        }

        error(cellField);
    }

    /**
     * 最小サイズを取得する
     * @return 最小サイズ
     */
    public int getMin() {
        return min;
    }

    /**
     * 最大サイズを取得する
     * @return 最大サイズ
     */
    public int getMax() {
        return max;
    }

}
