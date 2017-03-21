package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * レコードクラスのコールバック用のメソッドを保持する。
 * 
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RecordMethodCache {
    
    /**
     * レコードの値を無視するかどうかのメソッド
     */
    Optional<Method> ignoreableMethod = Optional.empty();
    
    /**
     * リスナークラスのオブジェクト
     */
    Optional<Object> listenerObject = Optional.empty();
    
    /**
     * リスナークラスに定義された PreLoad用のメソッド
     */
    List<Method> listenerPreLoadMethods = new ArrayList<>();
    
    /**
     * リスナークラスに定義された PostLoad用のメソッド
     */
    List<Method> listenerPostLoadMethods = new ArrayList<>();
    
    /**
     * リスナークラスに定義された PreSave用のメソッド
     */
    List<Method> listenerPreSaveMethods = new ArrayList<>();
    
    /**
     * リスナークラスに定義された PostSave用のメソッド
     */
    List<Method> listenerPostSaveMethods = new ArrayList<>();
    
    /**
     * レコードクラスに定義された PreLoad用のメソッド
     */
    List<Method> preLoadMethods = new ArrayList<>();
    
    /**
     * レコードクラスに定義された PostLoad用のメソッド
     */
    List<Method> postLoadMethods = new ArrayList<>();
    
    /**
     * レコードクラスに定義された PreSave用のメソッド
     */
    List<Method> preSaveMethods = new ArrayList<>();
    
    /**
     * レコードクラスに定義された PostSave用のメソッド
     */
    List<Method> postSaveMethods = new ArrayList<>();
    
    public Optional<Method> getIgnoreableMethod() {
        return ignoreableMethod;
    }
    
    public Optional<Object> getListenerObject() {
        return listenerObject;
    }
    
    public List<Method> getListenerPreLoadMethods() {
        return listenerPreLoadMethods;
    }
    
    public List<Method> getListenerPostLoadMethods() {
        return listenerPostLoadMethods;
    }
    
    public List<Method> getListenerPreSaveMethods() {
        return listenerPreSaveMethods;
    }
    
    public List<Method> getListenerPostSaveMethods() {
        return listenerPostSaveMethods;
    }
    
    public List<Method> getPreLoadMethods() {
        return preLoadMethods;
    }
    
    public List<Method> getPostLoadMethods() {
        return postLoadMethods;
    }
    
    public List<Method> getPreSaveMethods() {
        return preSaveMethods;
    }
    
    public List<Method> getPostSaveMethods() {
        return postSaveMethods;
    }
    
}
