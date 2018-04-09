package com.gh.mygreen.xlsmapper;

import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link SheetBindingErrors}のインスタンスを作成するクラス
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrorsFactory {

    /**
     * インスタンスを作成する。
     * @param target 作成対象となるオブジェクト
     * @return 作成した {@SheetBindingErrors}
     */
    public <P> SheetBindingErrors<P> create(P target) {
        return new SheetBindingErrors<P>(target);
    }

}
