package com.gh.mygreen.xlsmapper.xml;

import java.lang.reflect.Member;
import java.util.Map;

import ognl.AbstractMemberAccess;

/**
 * 全ての修飾子のメンバーにアクセス可能なMemberAccess。
 * v3.2.16で削除された <a href="https://github.com/jkuhnert/ognl/blob/master/src/test/java/ognl/DefaultMemberAccess.java">DefaultMemberAccess</a>
 の代わり。
 *
 * @author T.TSUCHIE
 *
 */
public class AllAllowMemberAccess extends AbstractMemberAccess {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        return true;
    }
}
