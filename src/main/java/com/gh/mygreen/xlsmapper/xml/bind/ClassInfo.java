package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


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
    
    private List<AnnotationInfo> annotationInfos = new ArrayList<>();
    
    private List<MethodInfo> methodInfos = new ArrayList<>();
    
    private List<FieldInfo> fieldInfos = new ArrayList<>();
    
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
        
        for(AnnotationInfo anno : annotationInfos) {
            sb.append("  ").append(anno.toString());
        }
        
        for(MethodInfo method : methodInfos) {
            sb.append("  ").append(method.toString());
        }
        
        for(FieldInfo field : fieldInfos) {
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
     * <p>ただし、XMLに定義していないアノテーションは、既存のクラスに定義にあるものを使用する。</p>
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
    public void setOverride(final boolean override) {
        this.override = override;
    }
    
    /**
     * アノテーション情報を追加する。
     * <p>ただし、既に同じアノテーションが存在する場合は、それと入れ替えされます。</p>
     * @param annotationInfo アノテーション情報。
     * @throws IllegalArgumentException annotationInfo is null.
     */
    public void addAnnotationInfo(final AnnotationInfo annotationInfo) {
        ArgUtils.notNull(annotationInfo, "annotationInfo");
        
        removeAnnotationInfo(annotationInfo.getClassName());
        this.annotationInfos.add(annotationInfo);
    }
    
    /**
     * アノテーションのクラス名を指定してアノテーション情報を取得する。
     * @param annotationClassName アノテーションのクラス名(FQCN)。
     * @return 指定したクラスが存在しない場合は、nullを返す。
     */
    public AnnotationInfo getAnnotationInfo(final String annotationClassName) {
        for(AnnotationInfo item : annotationInfos) {
            if(item.getClassName().equals( annotationClassName)) {
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したアノテーション情報を含むかどうか。
     * @since 1.1
     * @param annotationClassName アノテーションのクラス名(FQCN)。
     * @return true:指定したクラスが存在する場合。
     */
    public boolean containsAnnotationInfo(final String annotationClassName) {
        return getAnnotationInfo(annotationClassName) != null;
    }
    
    /**
     * 指定したアノテーション情報を削除します。
     * @since 1.4.1
     * @param annotationClassName アノテーションのクラス名(FQCN)。
     * @return true:指定したアノテーション名を含み、それが削除できた場合。
     */
    public boolean removeAnnotationInfo(final String annotationClassName) {
        
        final AnnotationInfo existInfo = getAnnotationInfo(annotationClassName);
        if(existInfo != null) {
            this.annotationInfos.remove(existInfo);
            return true;
        }
        
        return false;
        
    }
    
    /**
     * メソッド情報を追加する。
     * <p>ただし、既に同じメソッドが存在する場合は、それと入れ替えされます。</p>
     * @param methodInfo メソッド情報。
     * @throws IllegalArgumentException methodInfo is null. 
     */
    public void addMethodInfo(final MethodInfo methodInfo) {
        ArgUtils.notNull(methodInfo, "methodInfo");
        
        removeMethodInfo(methodInfo.getMethodName());
        this.methodInfos.add(methodInfo);
    }
    
    /**
     * メソッド名を指定してメソッド情報を取得する。
     * @param methodName メソッド名。
     * @return 指定したメソッド名が存在しない場合は、nullを返す。
     */
    public MethodInfo getMethodInfo(final String methodName) {
        for(MethodInfo item : methodInfos) {
            if(item.getMethodName().equals(methodName)) {
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したメソッド情報を含むかどうか。
     * @param methodName メソッド名。
     * @return true: 指定したメソッド名が存在する場合。
     */
    public boolean containsMethodInfo(final String methodName) {
        return getMethodInfo(methodName) != null;
    }
    
    /**
     * 指定したメソッド情報を削除します。
     * @since 1.4.1
     * @param methodName メソッド名。
     * @return true:指定したメソッド名を含み、それが削除できた場合。
     */
    public boolean removeMethodInfo(final String methodName) {
        
        final MethodInfo existInfo = getMethodInfo(methodName);
        if(existInfo != null) {
            this.methodInfos.remove(existInfo);
            return true;
        }
        
        return false;
        
    }
    
    /**
     * フィールド情報を追加する。
     * <p>ただし、既に同じフィールドが存在する場合は、それと入れ替えされます。</p>
     * @param fieldInfo フィールド情報。
     * @throws IllegalArgumentException fieldInfo is null.
     */
    public void addFieldInfo(final FieldInfo fieldInfo) {
        ArgUtils.notNull(fieldInfo, "fieldInfo");
        
        removeFieldInfo(fieldInfo.getFieldName());
        this.fieldInfos.add(fieldInfo);
    }
    
    /**
     * フィールド名を指定してフィールド情報を取得する。
     * @param fieldName フィールド名。
     * @return 指定したフィールド名が存在しない場合は、nullを返す。
     */
    public FieldInfo getFieldInfo(final String fieldName) {
        for(FieldInfo item : fieldInfos) {
            if(item.getFieldName().equals(fieldName)) {
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したフィールド情報を含むかどうか。
     * @param fieldName フィールド名。
     * @return true: 指定したフィールド名が存在する場合。
     */
    public boolean containsFieldInfo(final String fieldName) {
        return getFieldInfo(fieldName) != null;
    }
    
    /**
     * 指定したフィールド情報を削除します。
     * @since 1.4.1
     * @param fieldName フィールドのクラス名(FQCN)。
     * @return true:指定したフィールド名を含み、それが削除できた場合。
     */
    public boolean removeFieldInfo(final String fieldName) {
        
        final FieldInfo existInfo = getFieldInfo(fieldName);
        if(existInfo != null) {
            this.fieldInfos.remove(existInfo);
            return true;
        }
        
        return false;
        
    }
    
    /**
     * JAXB用のアノテーション情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getAnnotationInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param annotationInfos アノテーション情報。
     */
    @XmlElement(name="annotation")
    public void setAnnotationInfos(final List<AnnotationInfo> annotationInfos) {
        
        if(annotationInfos == this.annotationInfos) {
            // Java7の場合、getterで取得したインスタンスをそのまま設定するため、スキップする。
            return;
        }
        
        this.annotationInfos.clear();
        for(AnnotationInfo item : annotationInfos) {
            addAnnotationInfo(item);
        }
    }
    
    /**
     * JAXB用のアノテーション情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     *  <br>Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が呼ばれます。
     * </p>
     * @since 1.1
     * @return アノテーション情報。
     */
    public List<AnnotationInfo> getAnnotationInfos() {
        return annotationInfos;
    }
    
    /**
     * JAXB用のメソッド情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getMethodInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。</p>
     * @since 1.1
     * @param methodInfos メソッド情報。
     */
    @XmlElement(name="method")
    public void setMethodInfos(final List<MethodInfo> methodInfos) {
        if(methodInfos == this.methodInfos) {
            // Java7の場合、getterで取得したインスタンスをそのまま設定するため、スキップする。
            return;
        }
        
        this.methodInfos.clear();
        for(MethodInfo item : methodInfos) {
            addMethodInfo(item);
        }
    }
    
    /**
     * JAXB用のメソッド情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     *  <br>Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が呼ばれます。
     * </p>
     * @since 1.1
     * @return メソッド情報。
     */
    public List<MethodInfo> getMethodInfos() {
        return methodInfos;
    }
    
    /**
     * JAXB用のフィールド情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getFieldInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。</p>
     * @since 1.1
     * @param fieldInfos フィールド情報。
     */
    @XmlElement(name="field")
    public void setFieldInfos(final List<FieldInfo> fieldInfos) {
        if(fieldInfos == this.fieldInfos) {
            // Java7の場合、getterで取得したインスタンスをそのまま設定するため、スキップする。
            return;
        }
        
        this.fieldInfos.clear();
        for(FieldInfo item : fieldInfos) {
            addFieldInfo(item);
        }
    }
    
    /**
     * JAXB用のフィールド情報を全て取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     *  <br>Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が呼ばれます。
     * </p>
     * @since 1.1
     * @return フィールド情報。
     */
    public List<FieldInfo> getFieldInfos() {
        return fieldInfos;
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
