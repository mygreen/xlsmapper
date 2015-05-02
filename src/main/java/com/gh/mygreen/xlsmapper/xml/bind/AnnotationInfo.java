package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * XMLのアノテーション情報を保持する
 * 
 * <pre>
 *  XMLの使用：
 *  <annotation name="net.java.amateras.xlsbeans.annotation.Sheet"> <- 属性 「name」を持ち必須。
 *    <attribute name="name">'Users'</attribute> <- 
 *  </annotation>
 * 
 * </pre>
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnotationInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String annotationClass;
    
    private Map<String, String> annotationAttributes = new LinkedHashMap<>();
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("AnnotationInfo:")
            .append(String.format(" [name=%s]", getAnnotationClass()));
        
        for(Map.Entry<String, String> entry : annotationAttributes.entrySet()) {
            sb.append(String.format(" [(attr)%s=%s]", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
        
    }
    
    /**
     * アノテーションのクラス名を取得する。
     * @return
     */
    public String getAnnotationClass() {
        return annotationClass;
    }
    
    /**
     * アノテーションのクラス名を設定する。
     * @param annotationClass
     */
    @XmlAttribute(name="name", required=true)
    public void setAnnotationClass(String annotationClass) {
        ArgUtils.notEmpty(annotationClass, "annotationClass");
        this.annotationClass = annotationClass;
    }
    
    @XmlElement(name="attribute")
    private void setAnnotationAttribute(AnnotationAtttribute annoAttr) {
        addAnnotationAttribute(annoAttr.name, annoAttr.value);
    }
    
    /**
     * アノテーションの属性を追加する。
     * @param name 属性名
     * @param value 値
     */
    public void addAnnotationAttribute(final String name, final String value) {
        this.annotationAttributes.put(name, value);
    }
    
    /**
     * アノテーションの属性のキー一覧を取得する。
     * @return
     */
    public String[] getAnnotationAttributeKeys() {
        return this.annotationAttributes.keySet().toArray(new String[0]);
    }
    
    public Map<String, String> getAnnotationAttributes() {
        return annotationAttributes;
    }
    
    public void setAnnotationAttributes(Map<String, String> annotationAttributes) {
        this.annotationAttributes = annotationAttributes;
    }
    
    /**
     * アノテーションの属性名を指定して、アノテーションの値を取得する。
     * @param name
     * @return
     */
    public String getAnnotationAttribute(String name){
        return this.annotationAttributes.get(name);
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class AnnotationAtttribute {
        
        @XmlAttribute(name="name", required=true)
        String name;
        
        @XmlValue
        String value;

    }
    
}
