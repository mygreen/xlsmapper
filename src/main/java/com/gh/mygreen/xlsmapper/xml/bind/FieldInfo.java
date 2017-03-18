package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * XMLのフィールド情報を保持するクラス。
 * 
 * @version 1.1
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class FieldInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private String fieldName;
    
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
    
    public FieldInfo() {
        
    }
    
    private FieldInfo(final Builder builder) {
        this.fieldName = builder.fieldName;
        this.override = builder.override;
        setAnnotationInfos(builder.annotationInfos);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FieldInfo")
            .append(String.format(" [name=%s]", getFieldName()))
            .append(String.format(" [override=%b]", isOverride()));
        
        for(AnnotationInfo anno : annotationInfos) {
            sb.append("  ").append(anno.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * フィールド名を取得する
     * @return フィールド名
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * フィールド名を設定する
     * @param fieldName フィールド名。
     * @throws IllegalArgumentException fieldName is empty.
     */
    @XmlAttribute(name="name", required=true)
    public void setFieldName(final String fieldName) {
        ArgUtils.notEmpty(fieldName, "fieldName");
        this.fieldName = fieldName;
    }
    
    /**
     * 既存のフィールドの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか。
     * <p>ただし、XMLに定義していないアノテーションは、既存のフィールドに定義にあるものを使用する。</p>
     * @since 1.0
     * @return true:XMLの定義で上書きする。
     */
    public boolean isOverride() {
        return override;
    }
    
    /**
     * 既存のフィールドの定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
     * <p>ただし、XMLに定義していないアノテーションは、既存のフィールドに定義にあるものを使用する。</p>
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
     *  <br>ただし、Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が追加されます。
     * </p>
     * @since 1.1
     * @return アノテーション情報。
     */
    public List<AnnotationInfo> getAnnotationInfos() {
        return this.annotationInfos;
    }
    
    /**
     * {@link FieldInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private String fieldName;
        
        private boolean override;
        
        private List<AnnotationInfo> annotationInfos;
        
        private Builder() {
            this.annotationInfos = new ArrayList<>();
        }
        
        /**
         * 組み立てた{@link FieldInfo}のインスタンスを取得する。
         * @return
         */
        public FieldInfo buildField() {
            
            if(Utils.isEmpty(fieldName)) {
                throw new IllegalStateException("field name is required.");
            }
            
            return new FieldInfo(this);
        }
        
        /**
         * フィールド名を設定する。
         * @param fieldName フィールド名
         * @return
         * @throws IllegalArgumentException fieldName is empty.
         */
        public Builder name(final String fieldName) {
            ArgUtils.notEmpty(fieldName, "fieldName");
            this.fieldName = fieldName;
            return this;
        }
        
        /**
         * 既存のフィールドに定義にあるアノテーションの設定をXMLの定義で上書きするかどうか設定する。
         * @param override true:XMLの定義で上書きする。
         * @return
         */
        public Builder override(final boolean override) {
            this.override = override;
            return this;
        }
        
        /**
         * フィールドに対するアノテーション情報を追加する。
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
