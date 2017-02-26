package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * XMLのメソッド情報を保持するクラス。
 * @version 1.4.1
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class MethodInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String methodName;
    
    private boolean override;
    
    private List<AnnotationInfo> annotationInfos = new ArrayList<>();
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public MethodInfo() {
        
    }
    
    private MethodInfo(final Builder builder) {
        
        this.methodName = builder.methodName;
        this.override = builder.override;
        setAnnotationInfos(builder.annotationInfos);
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MethodInfo")
            .append(String.format(" [name=%s]", getMethodName()))
            .append(String.format(" [override=%b]", isOverride()));
        
        for(AnnotationInfo anno : annotationInfos) {
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
     * @throws IllegalArgumentException methodName is empty.
     */
    @XmlAttribute(name="name", required=true)
    public void setMethodName(final String methodName) {
        ArgUtils.notEmpty(methodName, methodName);
        this.methodName = methodName;
    }
    
    /**
     * 既存のメソッドの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか。
     * <p>ただし、XMLに定義していないアノテーションは、既存のメソッドに定義にあるものを使用する。</p>
     * @since 1.0
     * @return true:XMLの定義で上書きする。
     */
    public boolean isOverride() {
        return override;
    }
    
    /**
     * 既存のメソッドの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
     * <p>ただし、XMLに定義していないアノテーションは、既存のメソッドに定義にあるものを使用する。</p>
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
            if(item.getClassName().equals(annotationClassName)) {
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
     * JAXB用のアノテーション情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getAnnotationInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。</p>
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
        return this.annotationInfos;
    }
    
    /**
     * {@link MethodInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private String methodName;
        
        private boolean override;
        
        private List<AnnotationInfo> annotationInfos;
        
        private Builder() {
            this.annotationInfos = new ArrayList<>();
        }
        
        /**
         * 組み立てた{@link MethodInfo}のインスタンスを取得する。
         * @return
         */
        public MethodInfo buildMethod() {
            
            if(Utils.isEmpty(methodName)) {
                throw new IllegalStateException("method name is required.");
            }
            
            return new MethodInfo(this);
            
        }
        
        /**
         * メソッド名を設定する。
         * @param methodName メソッド名
         * @return
         * @throws IllegalArgumentException methodName is empty.
         */
        public Builder name(final String methodName) {
            ArgUtils.notEmpty(methodName, "methodName");
            this.methodName = methodName;
            return this;
        }
        
        /**
         * 既存のメソッドに定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
         * @param override true:XMLの定義で上書きする。
         * @return
         */
        public Builder override(final boolean override) {
            this.override = override;
            return this;
        }
        
        /**
         * メソッドに対するアノテーション情報を追加する。
         * @param annotationInfo
         * @return
         * @throws IllegalArgumentException annotationInfo is null.
         */
        public Builder annotation(final AnnotationInfo annotationInfo) {
            ArgUtils.notNull(annotationInfo, "annotationInfo");
            this.annotationInfos.add(annotationInfo);
            return this;
        }
        
    }
}
