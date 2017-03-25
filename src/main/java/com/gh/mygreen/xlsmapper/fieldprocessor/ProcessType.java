package com.gh.mygreen.xlsmapper.fieldprocessor;


/**
 * 処理の種類
 * <p>読み込み、書き込み時に処理メソッドを共通化しているような時に、フラグとして区別するために利用する。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public enum ProcessType {
    
    Read, Write;
}
