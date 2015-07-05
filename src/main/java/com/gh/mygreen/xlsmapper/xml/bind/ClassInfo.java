package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * XMLのクラス情報を保持する。
 * @version 1.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class ClassInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String className;
    
    private boolean override;
    
    private Map<String, AnnotationInfo> annotationInfos = new LinkedHashMap<>();
    
    private Map<String, MethodInfo> methodInfos = new LinkedHashMap<>();
    
    private Map<String, FieldInfo> fieldInfos = new LinkedHashMap<>();
    
    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        
        sb.append("ClassInfo:")
            .append(String.format(" [name=%s]", getClassName()))
            .append(String.format(" [override=%b]", isOverride()));
        
        for(AnnotationInfo anno : annotationInfos.values()) {
            sb.append("  ").append(anno.toString());
        }
        
        for(MethodInfo method : methodInfos.values()) {
            sb.append("  ").append(method.toString());
        }
        
        for(FieldInfo field : fieldInfos.values()) {
            sb.append("  ").append(field.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * クラス名を取得する。
     * @return クラス名。
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * クラス名を設定する。
     * @param className クラス名。
     * @throws IllegalArgumentException className is empty.
     */
    @XmlAttribute(name="name", required=true)
    public void setClassName(final String className) {
        ArgUtils.notEmpty(className, "className");
        this.className = className;
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
     * @param annotationInfo
     */
    @XmlElement(name="annotation")
    public void setAnnotationInfo(final AnnotationInfo annotationInfo){
        addAnnotationInfo(annotationInfo);
    }
    
    public void addAnnotationInfo(final AnnotationInfo annotationInfo){
        this.annotationInfos.put(annotationInfo.getAnnotationClass(), annotationInfo);
    }
    
    /**
     * クラス名を指定してアノテーション情報を取得する。
     * @param annotationClass
     * @return 指定したクラスが存在しない場合は、nullを返す。
     */
    public AnnotationInfo getAnnotationInfo(String annotationClass){
        return this.annotationInfos.get(annotationClass);
    }
    
    /**
     * 設定されている全てのアノテーション情報を取得する。
     * @return
     */
    public AnnotationInfo[] getAnnotationInfos(){
        return annotationInfos.values().toArray(new AnnotationInfo[0]);
    }
    
    /**
     * メソッド情報を追加する。
     * @param methodInfo
     */
    @XmlElement(name="method")
    public void setMethodInfo(final MethodInfo methodInfo){
        this.methodInfos.put(methodInfo.getMethodName(), methodInfo);
    }
    
    public MethodInfo getMethodInfo(final String methodName){
        return this.methodInfos.get(methodName);
    }
    
    /**
     * 指定したメソッドを含むかどうか。
     * @param methodName
     * @return
     */
    public boolean containsMethodInfo(final String methodName) {
        return methodInfos.containsKey(methodName);
    }
    
    /**
     * フィールド情報を追加する。
     * @param fieldInfo
     */
    @XmlElement(name="field")
    public void setFieldInfo(final FieldInfo fieldInfo){
        this.fieldInfos.put(fieldInfo.getFieldName(), fieldInfo);
    }
    
    public FieldInfo getFieldInfo(String fieldName){
        return this.fieldInfos.get(fieldName);
    }
    
}
