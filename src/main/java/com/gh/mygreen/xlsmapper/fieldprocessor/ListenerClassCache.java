package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * リスナークラスのオブジェクトをメソッドをキャッシュする。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ListenerClassCache {

    /**
     * リスナークラスのオブジェクトの実体。
     */
    final Object object;

    public ListenerClassCache(final Object object) {
        ArgUtils.notNull(object, "object");
        this.object = object;
    }

    /**
     * リスナークラスに定義された PreLoad用のメソッド
     */
    List<Method> preLoadMethods = new ArrayList<>();

    /**
     * リスナークラスに定義された PostLoad用のメソッド
     */
    List<Method> postLoadMethods = new ArrayList<>();

    /**
     * リスナークラスに定義された PreSave用のメソッド
     */
    List<Method> preSaveMethods = new ArrayList<>();

    /**
     * リスナークラスに定義された PostSave用のメソッド
     */
    List<Method> postSaveMethods = new ArrayList<>();

    public List<Method> getPreLoadMethods() {
        return preLoadMethods;
    }

    public void setPreLoadMethods(List<Method> preLoadMethods) {
        this.preLoadMethods = preLoadMethods;
    }

    public List<Method> getPostLoadMethods() {
        return postLoadMethods;
    }

    public void setPostLoadMethods(List<Method> postLoadMethods) {
        this.postLoadMethods = postLoadMethods;
    }

    public List<Method> getPreSaveMethods() {
        return preSaveMethods;
    }

    public void setPreSaveMethods(List<Method> preSaveMethods) {
        this.preSaveMethods = preSaveMethods;
    }

    public List<Method> getPostSaveMethods() {
        return postSaveMethods;
    }

    public void setPostSaveMethods(List<Method> postSaveMethods) {
        this.postSaveMethods = postSaveMethods;
    }

    public Object getObject() {
        return object;
    }

}
