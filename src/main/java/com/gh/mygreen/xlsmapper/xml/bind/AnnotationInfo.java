package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.xml.OgnlValueFormatter;


/**
 * XMLのアノテーション情報を保持する
 * 
 * <pre>
 *  XMLの使用：
 *  <annotation name="net.java.amateras.xlsbeans.annotation.Sheet"> <- 属性 「name」を持ち必須。
 *  <attribute name="name">'Users'</attribute> <- 
 *  </annotation>
 * 
 * </pre>
 * 
 * @version 1.1
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnotationInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * アノテーションのクラス名
     */
    private String className;
    
    /**
     * アノテーションの属性値のマップ。
     * <p>キー：属性名。
     * <p>値：属性値。
     * 
     */
    private Map<String, String> attributes = new LinkedHashMap<>();
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @param valueFormatter JavaオブジェクトをOGNL式に変換するためのクラス。
     * @return
     * @throws IllegalArgumentException valueFormatter is null.
     */
    public static Builder builder(final OgnlValueFormatter valueFormatter) {
        ArgUtils.notNull(valueFormatter, "valueFormatter");
        return new Builder(valueFormatter);
    }
    
    public AnnotationInfo() {
        
    }
    
    private AnnotationInfo(Builder builder) {
        this.className = builder.className;
        setAttributeInfos(builder.attributeInfos);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("AnnotationInfo:")
            .append(String.format(" [name=%s]", getClassName()));
        
        for(Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(String.format(" [(attr)%s=%s]", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
        
    }
    
    /**
     * アノテーションのクラス名を取得する。
     * @return FQCN（完全限定クラス名）。
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * アノテーションのクラス名を設定する。
     * @param className FQCN（完全限定クラス名）を指定します。
     * @throws IllegalArgumentException className is empty.
     */
    @XmlAttribute(name="name", required=true)
    public void setClassName(String className) {
        ArgUtils.notEmpty(className, "className");
        this.className = className;
    }
    
    /**
     * アノテーションの属性を追加する。
     * @param name 属性名。必須です。
     * @param value 値。
     *              <a href="http://s2container.seasar.org/2.4/ja/ognl.html" target="_blank">OGNL形式</a>で指定します。
     * @throws IllegalArgumentException name is empty.
     */
    public void addAttribute(final String name, final String value) {
        ArgUtils.notEmpty(name, "name");
        this.attributes.put(name, value);
    }
    
    /**
     * アノテーションの属性名の一覧を取得する。
     * @return 属性名の一覧情報。
     */
    public String[] getAttributeKeys() {
        return this.attributes.keySet().toArray(new String[attributes.size()]);
    }
    
    /**
     * アノテーションの属性名を指定して、アノテーションの値を取得する。
     * @param name 属性名。
     * @return 存在しない属性名の場合、nullを返します。
     */
    public String getAttribute(final String name) {
        return this.attributes.get(name);
    }
    
    /**
     * 指定したアノテーションの属性情報を含むかどうか。
     * @since 1.1
     * @param name アノテーションの属性名。
     * @return true: 指定したアノテーションの属性名が存在する場合。
     */
    public boolean containsAttribute(final String name) {
        return this.attributes.containsKey(name);
    }
    
    /**
     * アノテーションの属性情報を保持するクラス。
     * <p>JAXBによるXMLのマッピングのために使用する。
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AtttributeInfo {
        
        @XmlAttribute(name="name", required=true)
        String name;
        
        @XmlValue
        String value;
        
        public static AtttributeInfo create(final String name, final String value) {
            
            AtttributeInfo attr = new AtttributeInfo();
            attr.name = name;
            attr.value = value;
            return attr;
        }
        
    }
    
    /**
     * JAXB用のアノテーションの属性情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param attributeInfos アノテーションの属性情報。
     */
    @XmlElement(name="attribute")
    public void setAttributeInfos(List<AtttributeInfo> attributeInfos) {
        this.attributes.clear();
        for(AtttributeInfo attr : attributeInfos) {
            this.attributes.put(attr.name, attr.value);
        }
    }
    
    /**
     * JAXB用のアノテーションの属性情報を取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     * @since 1.1
     * @return
     */
    public List<AtttributeInfo> getAttributeInfos() {
        List<AtttributeInfo> attrs = new ArrayList<>();
        for(Map.Entry<String, String> entry : attributes.entrySet()) {
            attrs.add(AtttributeInfo.create(entry.getKey(), entry.getValue()));
        }
        
        return attrs;
    }
    
    /**
     * {@link AnnotationInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private static final OgnlValueFormatter DEFAULT_VALUE_FORMATTER = new OgnlValueFormatter();
        
        private OgnlValueFormatter valueFormatter;
        
        private String className;
        
        private List<AtttributeInfo> attributeInfos;
        
        private Builder() {
            this(DEFAULT_VALUE_FORMATTER);
        }
        
        private Builder(final OgnlValueFormatter valueFormatter) {
            this.valueFormatter = valueFormatter;
            this.attributeInfos = new ArrayList<>();
            
        }
        
        /**
         * 組み立てた{@link AnnotationInfo}のインスタンスを取得する。
         * @return
         */
        public AnnotationInfo buildAnnotation() {
            
            if(Utils.isEmpty(className)) {
                throw new IllegalStateException("class name is required.");
            }
            
            return new AnnotationInfo(this);
            
        }
        
        /**
         * アノテーションのクラス名を設定する。
         * @param className アノテーションのクラス名。FQCN（完全限定クラス名）を指定します。
         * @return
         * @throws IllegalArgumentException className is empty.
         */
        public Builder name(final String className) {
            ArgUtils.notEmpty(className, "className");
            this.className = className;
            return this;
        }
        
        /**
         * アノテーションのクラス名を設定する。
         * @param clazz アノテーションのクラス。
         * @return
         * @throws IllegalArgumentException clazz is null.
         */
        public Builder name(final Class<? extends Annotation> clazz) {
            ArgUtils.notNull(clazz, "clazz");
            return name(clazz.getName());
        }
        
        /**
         * アノテーションの属性値を設定する。
         * @param attrName 属性名
         * @param attrValue 属性の値。
         *              <a href="http://s2container.seasar.org/2.4/ja/ognl.html" target="_blank">OGNL形式</a>で指定します。
         * @return
         * @throws IllegalArgumentException attrName is empty.
         */
        public Builder attributeWithNative(final String attrName, final String attrValue) {
            ArgUtils.notEmpty(attrName, "attrName");
            this.attributeInfos.add(AtttributeInfo.create(attrName, attrValue));
            return this;
        }
        
        /**
         * アノテーションの属性値を設定する。
         * @param attrName 属性名
         * @param attrValue 属性の値。値は自動的にOGNLの書式に変換されて設定されます。
         * @return
         * @throws IllegalArgumentException attrName is empty.
         */
        public Builder attribute(final String attrName, final Object attrValue) {
            ArgUtils.notEmpty(attrName, "attrName");
            
            String ognlValue = valueFormatter.format(attrValue);
            attributeWithNative(attrName, ognlValue);
            
            return this;
        }
        
    }
    
}
