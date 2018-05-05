package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.gh.mygreen.xlsmapper.validation.DefaultGroup;

/**
 * バリデーション時のヒントとなるグループを指定することが可能なValidatorのサポートクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class GroupValidatorSupport {

    /**
     * バリデーション時のヒントとなるグループ
     */
    protected Set<Class<?>> settingGroups = new LinkedHashSet<>();

    /**
     * 値が空のときでも検証を行うかどうか。
     * @return trueの場合、検証を行う。
     */
    protected boolean validateOnEmptyValue() {
        return false;
    }

    /**
     * バリデーション時のヒントを追加する。
     * @param groups バリデーション時のヒント。
     * @return 自身のインスタンス。
     */
    public GroupValidatorSupport addGroup(final Class<?>... groups) {
        this.settingGroups.addAll(Arrays.asList(groups));

        return this;
    }

    /**
     * 設定されているバリデーションのグループを取得する。
     * @return
     */
    public Set<Class<?>> getSettingGroups() {
        return settingGroups;
    }

    /**
     * バリデーション時のヒントが該当するかどうか。
     * @param validationGroups 判定対象のグループ
     * @return 該当する。
     */
    protected boolean containsValidationGroups(final List<Class<?>> validationGroups) {

        // バリデーション時のグループの指定が無い場合
        if(getSettingGroups().isEmpty() && validationGroups.isEmpty()) {
            return true;

        }

        // デフォルトグループ指定されている場合、該当する。
        if(validationGroups.isEmpty()) {
            for(Class<?> settingGroup : getSettingGroups()) {
                if(DefaultGroup.class.isAssignableFrom(settingGroup)) {
                    return true;
                }
            }
        }

        for(Class<?> group : validationGroups) {

            if(getSettingGroups().isEmpty() && DefaultGroup.class.isAssignableFrom(group)) {
                return true;
            }

            if(getSettingGroups().contains(group)) {
                return true;
            }

            // 親子関係のチェック
            for(Class<?> parent : getSettingGroups()) {
                if(parent.isAssignableFrom(group)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * エラー用のメッセージキーを取得します。
     * @return メッセージキー。独自に指定するような場合は、nullを返します。
     */
    protected String getMessageKey() {
        return null;
    }


}
