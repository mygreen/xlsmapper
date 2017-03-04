package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.xml.OgnlValueFormatter;


/**
 * XMLのアノテーション情報を保持する。
 * 
 * XMLの使用例：
 * 
 * <pre class="highlight"><code class="xml">
 * {@literal <!-- 属性 「name」を持ち必須。--> }
 * {@literal <annotation name="net.java.amateras.xlsbeans.annotation.Sheet">}
 *     {@literal <attribute name="name">'Users'</attribute>}
 * {@literal </annotation>}
 * </code></pre>
 * 
 * @version 1.4.1
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
    
    private List<AttributeInfo> attributes = new ArrayList<>();
    
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
    
    private AnnotationInfo(final Builder builder) {
        this.className = builder.className;
        setAttributeInfos(builder.attributeInfos);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("AnnotationInfo:")
            .append(String.format(" [name=%s]", getClassName()));
        
        for(AttributeInfo entry : attributes) {
            sb.append(String.format(" [(attr)%s=%s]", entry.name, entry.value));
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
     * <p>ただし、既に同じ属性名が存在する場合は、それと入れ替えされます。</p>
     * @param name 属性名。必須です。
     * @param value 値。
     *              <a href="http://s2container.seasar.org/2.4/ja/ognl.html" target="_blank">OGNL形式</a>で指定します。
     * @throws IllegalArgumentException name is empty.
     */
    public void addAttribute(final String name, final String value) {
        ArgUtils.notEmpty(name, "name");
        removeAttribute(name);
        this.attributes.add(AttributeInfo.create(name, value));
    }
    
    /**
     * アノテーションの属性名の一覧を取得する。
     * @return 属性名の一覧情報。
     */
    public String[] getAttributeKeys() {
        
        final List<String> list = new ArrayList<>(attributes.size());
        for(AttributeInfo item : attributes) {
            list.add(item.name);
        }
        
        return list.toArray(new String[list.size()]);
    }
    
    /**
     * アノテーションの属性名を指定して、アノテーションの値を取得する。
     * @param name 属性名。
     * @return 存在しない属性名の場合、nullを返します。
     */
    public String getAttribute(final String name) {
        for(AttributeInfo item : attributes) {
            if(item.name.equals(name)) {
                return item.value;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したアノテーションの属性情報を含むかどうか。
     * @since 1.1
     * @param name アノテーションの属性名。
     * @return true: 指定したアノテーションの属性名が存在する場合。
     */
    public boolean containsAttribute(final String name) {
        return getAttribute(name) != null;
    }
    
    /**
     * 指定したアノテーションの属性情報を削除します。
     * @since 1.4.1
     * @param name アノテーションの属性名。
     * @return true:指定したアノテーション属性名を含み、それが削除できた場合。
     */
    public boolean removeAttribute(final String name) {
        
        AttributeInfo existInfo = null;
        for(AttributeInfo item : attributes) {
            if(item.name.equals(name)) {
                existInfo = item;
                break;
            }
            
        }
        
        if(existInfo != null) {
            this.attributes.remove(existInfo);
            return true;
        }
        
        return false;
        
    }
    
    /**
     * アノテーションの属性情報を保持するクラス。
     * <p>JAXBによるXMLのマッピングのために使用する。</p>
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AttributeInfo implements Serializable {
        
        /** serialVersionUID */
        private static final long serialVersionUID = 5570368711168203217L;
        
        @XmlAttribute(name="name", required=true)
        String name;
        
        @XmlValue
        String value;
        
        public static AttributeInfo create(final String name, final String value) {
            
            AttributeInfo attr = new AttributeInfo();
            attr.name = name;
            attr.value = value;
            return attr;
        }
        
    }
    
    /**
     * JAXB用のアノテーションの属性情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getAttributeInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。</p>
     * @since 1.1
     * @param attributeInfos アノテーションの属性情報。
     */
    @XmlElement(name="attribute")
    public void setAttributeInfos(final List<AttributeInfo> attributeInfos) {
        if(attributeInfos == this.attributes) {
            // Java7の場合、getterで取得したインスタンスをそのまま設定するため、スキップする。
            return;
        }
        
        this.attributes.clear();
        for(AttributeInfo attr : attributeInfos) {
            addAttribute(attr.name, attr.value);
        }
    }
    
    /**
     * JAXB用のアノテーションの属性情報を取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     *  <br>Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が呼ばれます。
     * </p>
     * @since 1.1
     * @return
     */
    public List<AttributeInfo> getAttributeInfos() {
        return attributes;
    }
    
    /**
     * {@link AnnotationInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private static final OgnlValueFormatter DEFAULT_VALUE_FORMATTER = new OgnlValueFormatter();
        
        private OgnlValueFormatter valueFormatter;
        
        private String className;
        
        private List<AttributeInfo> attributeInfos;
        
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
            this.attributeInfos.add(AttributeInfo.create(attrName, attrValue));
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
