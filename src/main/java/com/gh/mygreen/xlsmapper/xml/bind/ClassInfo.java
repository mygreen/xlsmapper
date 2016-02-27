package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;


/**
 * XMLのクラス情報を保持する。
 * @version 1.1
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
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public ClassInfo() {
        
    }
    
    private ClassInfo(final Builder builder) {
        this.className = builder.className;
        this.override = builder.override;
        setAnnotationInfos(builder.annotationInfos);
        setMethodInfos(builder.methodInfos);
        setFieldInfos(builder.fieldInfos);
    }
    
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
     * @return FQCN（完全限定クラス名）を指定します。
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * クラス名を設定する。
     * @param className FQCN（完全限定クラス名）を設定します。
     * @throws IllegalArgumentException className is empty.
     */
    @XmlAttribute(name="name", required=true)
    public void setClassName(final String className) {
        ArgUtils.notEmpty(className, "className");
        this.className = className;
    }
    
    /**
     * 既存のクラスの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか。
     * <p>ただし、XMLに定義していないアノテーションは、既存のクラスに定義にあるものを使用する。
     * @since 1.0
     * @return true:XMLの定義で上書きする。
     */
    public boolean isOverride() {
        return override;
    }
    
    /**
     * 既存のクラスの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
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
     * @param annotationInfo アノテーション情報。
     * @throws IllegalArgumentException annotationInfo is null.
     */
    public void addAnnotationInfo(final AnnotationInfo annotationInfo) {
        ArgUtils.notNull(annotationInfo, "annotationInfo");
        this.annotationInfos.put(annotationInfo.getClassName(), annotationInfo);
    }
    
    /**
     * アノテーションのクラス名を指定してアノテーション情報を取得する。
     * @param annotationClassName アノテーションのクラス名(FQCN)。
     * @return 指定したクラスが存在しない場合は、nullを返す。
     */
    public AnnotationInfo getAnnotationInfo(final String annotationClassName){
        return this.annotationInfos.get(annotationClassName);
    }
    
    /**
     * 指定したアノテーション情報を含むかどうか。
     * @since 1.1
     * @param annotationClassName アノテーションのクラス名(FQCN)。
     * @return true:指定したクラスが存在する場合。
     */
    public boolean containsAnnotationInfo(final String annotationClassName) {
        return this.annotationInfos.containsKey(annotationClassName);
    }
    
    /**
     * メソッド情報を追加する。
     * @param methodInfo メソッド情報。
     * @throws IllegalArgumentException methodInfo is null. 
     */
    public void addMethodInfo(final MethodInfo methodInfo) {
        ArgUtils.notNull(methodInfo, "methodInfo");
        this.methodInfos.put(methodInfo.getMethodName(), methodInfo);
    }
    
    /**
     * メソッド名を指定してメソッド情報を取得する。
     * @param methodName メソッド名。
     * @return 指定したメソッド名が存在しない場合は、nullを返す。
     */
    public MethodInfo getMethodInfo(final String methodName){
        return this.methodInfos.get(methodName);
    }
    
    /**
     * 指定したメソッド情報を含むかどうか。
     * @param methodName メソッド名。
     * @return true: 指定したメソッド名が存在する場合。
     */
    public boolean containsMethodInfo(final String methodName) {
        return this.methodInfos.containsKey(methodName);
    }
    
    /**
     * フィールド情報を追加する。
     * @param fieldInfo フィールド情報。
     * @throws IllegalArgumentException fieldInfo is null.
     */
    public void addFieldInfo(final FieldInfo fieldInfo) {
        ArgUtils.notNull(fieldInfo, "fieldInfo");
        this.fieldInfos.put(fieldInfo.getFieldName(), fieldInfo);
    }
    
    /**
     * フィールド名を指定してフィールド情報を取得する。
     * @param fieldName フィールド名。
     * @return 指定したフィールド名が存在しない場合は、nullを返す。
     */
    public FieldInfo getFieldInfo(String fieldName){
        return this.fieldInfos.get(fieldName);
    }
    
    /**
     * 指定したフィールド情報を含むかどうか。
     * @param fieldName フィールド名。
     * @return true: 指定したフィールド名が存在する場合。
     */
    public boolean containsFieldInfo(final String fieldName) {
        return this.fieldInfos.containsKey(fieldName);
    }
    
    /**
     * JAXB用のアノテーション情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param annotationInfos アノテーション情報。
     */
    @XmlElement(name="annotation")
    public void setAnnotationInfos(List<AnnotationInfo> annotationInfos) {
        this.annotationInfos.clear();
        for(AnnotationInfo item : annotationInfos) {
            addAnnotationInfo(item);
        }
    }
    
    /**
     * JAXB用のアノテーション情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     * @since 1.1
     * @return アノテーション情報。
     */
    public List<AnnotationInfo> getAnnotationInfos() {
        return new ArrayList<>(this.annotationInfos.values());
    }
    
    /**
     * JAXB用のメソッド情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param methodInfos メソッド情報。
     */
    @XmlElement(name="method")
    public void setMethodInfos(List<MethodInfo> methodInfos) {
        this.methodInfos.clear();
        for(MethodInfo item : methodInfos) {
            addMethodInfo(item);
        }
    }
    
    /**
     * JAXB用のメソッド情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     * @since 1.1
     * @return メソッド情報。
     */
    public List<MethodInfo> getMethodInfos() {
        return new ArrayList<>(this.methodInfos.values());
    }
    
    /**
     * JAXB用のフィールド情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param fieldInfos フィールド情報。
     */
    @XmlElement(name="field")
    public void setFieldInfos(List<FieldInfo> fieldInfos) {
        this.fieldInfos.clear();
        for(FieldInfo item : fieldInfos) {
            addFieldInfo(item);
        }
    }
    
    /**
     * JAXB用のフィールド情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     * @since 1.1
     * @return フィールド情報。
     */
    public List<FieldInfo> getFieldInfos() {
        return new ArrayList<>(this.fieldInfos.values());
    }
    
    /**
     * {@link ClassInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private String className;
        
        private boolean override;
        
        private List<AnnotationInfo> annotationInfos;
        
        private List<MethodInfo> methodInfos;
        
        private List<FieldInfo> fieldInfos;
        
        private Builder() {
            this.annotationInfos = new ArrayList<>();
            this.methodInfos = new ArrayList<>();
            this.fieldInfos = new ArrayList<>();
        }
        
        /**
         * 組み立てた{@link ClassInfo}のインスタンスを取得する。
         * @return
         */
        public ClassInfo buildClass() {
            
            if(Utils.isEmpty(className)) {
                throw new IllegalStateException("class name is required.");
            }
            
            return new ClassInfo(this);
        }
        
        /**
         * クラス名を設定する。
         * @param className クラス名。FQCN（完全限定クラス名）を設定します。
         * @return
         * @throws IllegalArgumentException calssName is empty.
         */
        public Builder name(final String className) {
            ArgUtils.notEmpty(className, "className");
            this.className = className;
            return this;
        }
        
        /**
         * クラス名を設定する。
         * @param clazz クラス情報。
         * @return
         * @throws IllegalArgumentException clazz is null.
         */
        public Builder name(final Class<?> clazz) {
            ArgUtils.notNull(clazz, "clazz");
            return name(clazz.getName());
            
        }
        
        /**
         * 既存のクラスの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
         * @param override true:XMLの定義で上書きする。
         * @return
         */
        public Builder override(final boolean override) {
            this.override = override;
            return this;
        }
        
        /**
         * クラスに対するアノテーション情報を追加する。
         * @param annotationInfo アノテーション情報
         * @return
         * @throws IllegalArgumentException annotationInfo is null.
         */
        public Builder annotation(final AnnotationInfo annotationInfo) {
            ArgUtils.notNull(annotationInfo, "annotationInfo");
            this.annotationInfos.add(annotationInfo);
            return this;
        }
        
        /**
         * メソッド情報を追加する。
         * @param methodInfo メソッド情報
         * @return
         * @throws IllegalArgumentException methodInfo is null.
         */
        public Builder method(final MethodInfo methodInfo) {
            ArgUtils.notNull(methodInfo, "methodInfo");
            this.methodInfos.add(methodInfo);
            return this;
        }
        
        /**
         * フィールド情報を追加する。
         * @param fieldInfo フィールド情報。
         * @return
         * @throws IllegalArgumentException fieldInfo is null.
         */
        public Builder field(final FieldInfo fieldInfo) {
            ArgUtils.notNull(fieldInfo, "fieldInfo");
            this.fieldInfos.add(fieldInfo);
            return this;
        }
        
    }
}
