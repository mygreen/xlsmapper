package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * XMLのフィールド情報を保持するクラス。
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class FieldInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String fieldName;
    
    private Map<String, AnnotationInfo> annotationInfos = new HashMap<>();
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FieldInfo")
            .append(String.format(" [name=%s]", getFieldName()));
        
        for(AnnotationInfo anno : annotationInfos.values()) {
            sb.append("  ").append(anno.toString());
        }
        
        return sb.toString();
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @XmlAttribute(name="name", required=true)
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
