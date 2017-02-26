package com.gh.mygreen.xlsmapper.util;


/**
 * {@link IsEmptyBuilder}で独自の実装で空と判定するためのインタフェース。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface IsEmptyComparator {
    
    /**
     * 空かどうか判定する。
     * @return true: 値が空かどうか。
     */
    public boolean isEmpty();
    
}
