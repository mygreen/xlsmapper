package com.gh.mygreen.xlsmapper;

import java.lang.reflect.Method;

/**
 * <code>@PreLoad, @PostLoad, @PreSave, @PostSave</code>などが付与されているメソッド情報を保持する。
 * 
 * @version 1.3
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class NeedProcess {
    
    /** 処理対象のBeanオブジェクト */
    private final Object target;
    
    /** 処理が埋め込まれたオブジェクト */
    private final Object process;
    
    /** 処理が実装されたメソッド */
    private final Method method;
    
    /**
     * 
     * @param target 処理対象のBeanオブジェクト。
     * @param process 処理が埋め込まれたオブジェクト。
     * @param method 処理が実装されたメソッド。引数process中のクラス。
     */
    public NeedProcess(final Object target, final Object process, final Method method) {
        this.target = target;
        this.process = process;
        this.method = method;
    }
    
    /**
     * @return Returns the target bean object.
     */
    public Object getTarget() {
        return target;
    }
    
    /**
     * @since 1.3
     * @return Returns the processing object.
     */
    public Object getProcess() {
        return process;
    }
    
    /**
     * @return Returns the method.
     */
    public Method getMethod() {
        return method;
    }

}
