package org.mygreen.xlsmapper;

import java.lang.reflect.Method;

/**
 * <code>@PreLoad, @PostLoad, @PreSave, @PostSave</code>などが付与されているメソッド情報を保持する。
 * @author Naoki Takezoe
 */
public class NeedProcess {
    
    private final Object target;
    
    private final Method method;
    
    public NeedProcess(final Object target, final Method method) {
        this.target = target;
        this.method = method;
    }
    
    /**
     * @return Returns the method.
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * @return Returns the target.
     */
    public Object getTarget() {
        return target;
    }
}
