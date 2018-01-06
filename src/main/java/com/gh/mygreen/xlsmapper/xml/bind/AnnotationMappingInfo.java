package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * XMLで定義したアノテーションの設定情報。
 * 
 * XMLの使用：
 * <pre class="highlight"><code class="xml">
 * {@literal <!-- ルート要素--> }
 * {@literal <annotations>}
 *     ・・・
 * {@literal </annotations>}
 * </code></pre>
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 * 
 */
@XmlRootElement(name="annotations")
public class AnnotationMappingInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private List<ClassInfo> classInfos = new ArrayList<>();
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public AnnotationMappingInfo() {
        
    }
    
    private AnnotationMappingInfo(final Builder builder) {
        setClassInfos(builder.classInfos);
    }
    
    /**
     * クラス情報を追加する。
     * <p>ただし、既に同じクラス名が存在する場合は、それと入れ替えされます。</p>
     * @param classInfo FQCN（完全限定クラス名）を指定します。
     * @throws IllegalArgumentException classInfo is null.
     */
    public void addClassInfo(final ClassInfo classInfo) {
        ArgUtils.notNull(classInfo, "classInfo");
        
        removeClassInfo(classInfo.getClassName());
        this.classInfos.add(classInfo);
    }
    
    /**
     * 複数のクラス上方を追加する。
     * @since 2.0
     * @param classInfos 複数のクラス情報
     */
    public void addClassInfos(Collection<ClassInfo> classInfos) {
        classInfos.forEach(info -> addClassInfo(info));
    }
    
    /**
     * クラス名を指定してクラス情報を取得する。
     * @param className FQCN（完全限定クラス名）を指定します。
     * @return 存在しないクラス名の場合、nullを返します。
     */
    public ClassInfo getClassInfo(final String className) {
        for(ClassInfo item : classInfos) {
            if(item.getClassName().equals(className)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * 指定したクラスが含まれるかどうか。
     * @since 1.1
     * @param className FQCN（完全限定クラス名）を指定します。
     * @return true:指定したクラス名を含む場合。
     */
    public boolean containsClassInfo(final String className) {
        return getClassInfo(className) != null;
    }
    
    /**
     * 指定したクラス情報を削除します。
     * @since 1.4.1
     * @param className FQCN（完全限定クラス名）を指定します。
     * @return true:指定したクラス名を含み、それが削除できた場合。
     */
    public boolean removeClassInfo(final String className) {
        
        final ClassInfo existInfo = getClassInfo(className);
        if(existInfo != null) {
            this.classInfos.remove(existInfo);
            return true;
        }
        
        return false;
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("XmlInfo:");
        
        for(ClassInfo clazz : classInfos) {
            sb.append("\n  ").append(clazz.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * JAXB用のクラス情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     *  <br>ただし、Java8からはこのメソッドは呼ばれず、{@link #getClassInfos()} で取得したインスタンスに対して要素が追加されます。
     * </p>
     * <p>既存の情報はクリアされます。</p>
     * @since 1.1
     * @param classInfos クラス情報
     */
    public void setClassInfos(final List<ClassInfo> classInfos) {
        if(classInfos == this.classInfos) {
            // Java7の場合、getterで取得したインスタンスをそのまま設定するため、スキップする。
            return;
        }
        
        this.classInfos.clear();
        for(ClassInfo item : classInfos) {
            addClassInfo(item);
        }
        
    }
    
    /**
     * JAXB用のクラス情報を取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     *  <br>Java8から読み込み時に呼ばれるようになり、取得したインスタンスに対して、読み込んだ要素が呼ばれます。
     * </p>
     * @since 1.1
     * @return
     */
    @XmlElement(name="class")
    public List<ClassInfo> getClassInfos() {
        return classInfos;
    }
    
    /**
     * XML(テキスト)として返す。
     * <p>JAXB標準の設定でXMLを作成します。</p>
     * @since 1.1
     * @return XML情報。
     */
    public String toXml() {
        
        StringWriter writer = new StringWriter();
        JAXB.marshal(this, writer);
        writer.flush();
        
        return writer.toString();
        
    }
    
    /**
     * {@link InputStream}として返す。
     * <p>XlsLoaderなどに直接渡せる形式。</p>
     * <p>{@link #toXml()}にてXMLに変換後に、InputStreamにサイド変換する。</p>
     * @since 1.1
     * @return XML情報。
     */
    public InputStream toInputStream() {
        
        String text = toXml();
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        
    }
    
    /**
     * {@link AnnotationMappingInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private List<ClassInfo> classInfos;
        
        private Builder() {
            this.classInfos = new ArrayList<>();
        }
        
        /**
         * 組み立てた{@link AnnotationMappingInfo}のインスタンスを返す。
         * @return {@link AnnotationMappingInfo}オブジェクト。
         */
        public AnnotationMappingInfo buildXml() {
            return new AnnotationMappingInfo(this);
        }
        
        /**
         * クラス情報を追加する。
         * @param classInfo クラス情報
         * @return
         * @throws IllegalArgumentException classInfo is null
         */
        public Builder classInfo(final ClassInfo classInfo) {
            ArgUtils.notNull(classInfo, "classInfo");
            this.classInfos.add(classInfo);
            return this;
        }
        
    }
}
