package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * XMLのメソッド情報を保持するクラス。
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class MethodInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String methodName;
    
    private boolean override;
    
    private Map<String, AnnotationInfo> annotationInfos = new HashMap<>();
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MethodInfo")
            .append(String.format(" [name=%s]", getMethodName()))
            .append(String.format(" [override=%b]", isOverride()));
        
        for(AnnotationInfo anno : annotationInfos.values()) {
            sb.append("  ").append(anno.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * メソッド名を取得する
     * @return メソッド名
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * メソッド名を設定する
     * @param methodName メソッド名
     */
    @XmlAttribute(name="name", required=true)
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    /**
     * 既存のクラスに定義にあるアノテーションの設定をXMLの定義で上書きするかどうか。
     * <p>ただし、XMLに定義していないアノテーションは、既存のクラスに定義にあるものを使用する。
     * @since 1.0
     * @return true:XMLの定義で上書きする。
     */
    public boolean isOverride() {
        return override;
    }
    
    /**
     * 既存のクラスに定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
     * <p>ただし、XMLに定義していないアノテーションは、既存のクラスに定義にあるものを使用する。
     * @since 1.0
     * @param override true:XMLの定義で上書きする。
     */
    @XmlAttribute(name="override", required=false)
    public void setOverride(boolean override) {
        this.override = override;
    }
    
    /**
     * アノテーション情報を追加する。
     * @param info
     */
    @XmlElement(name="annotation")
    public void setAnnotationInfo(AnnotationInfo info){
        this.annotationInfos.put(info.getAnnotationClass(), info);
    }
    
    /**
     * アノテーションのクラス名を指定してアノテーション情報を取得する。
     * @param annotationClass
     * @return
     */
    public AnnotationInfo getAnnotationInfo(String annotationClass){
        return this.annotationInfos.get(annotationClass);
    }
    
    /**
     * アノテーション情報の一覧を取得する。
     * @return
     */
    public AnnotationInfo[] getAnnotationInfos(){
        return annotationInfos.values().toArray(new AnnotationInfo[0]);
    }
}
