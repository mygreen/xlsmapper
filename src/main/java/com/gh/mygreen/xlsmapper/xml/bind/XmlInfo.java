package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * XMLで定義したアノテーションの設定情報。
 * 
 * <pre>
 *   XMLの使用：
 *   <annotations> <- ルート要素
 *     
 *   </annotations>
 * 
 * </pre>
 * 
 * @version 1.1
 * @since 0.5
 * @author T.TSUCHIE
 * 
 */
@XmlRootElement(name="annotations")
public class XmlInfo implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * クラス名をキーとしたクラス情報のマップ
     */
    private Map<String, ClassInfo> classInfos = new LinkedHashMap<>();
    
    /**
     * ビルダクラスのインスタンスを取得する。
     * @since 1.1
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public XmlInfo() {
        
    }
    
    private XmlInfo(final Builder builder) {
        setClassInfos(builder.classInfos);
    }
    
    /**
     * クラス情報を追加する。
     * @param classInfo FQCN（完全限定クラス名）を指定します。
     * @throws IllegalArgumentException classInfo is null.
     */
    public void addClassInfo(final ClassInfo classInfo) {
        ArgUtils.notNull(classInfo, "classInfo");
        
        this.classInfos.put(classInfo.getClassName(), classInfo);
    }
    
    /**
     * クラス名を指定してクラス情報を取得する。
     * @param className FQCN（完全限定クラス名）を指定します。
     * @return 存在しないクラス名の場合、nullを返します。
     */
    public ClassInfo getClassInfo(final String className) {
        return classInfos.get(className);
    }
    
    /**
     * 指定したクラスが含まれるかどうか。
     * @since 1.1
     * @param className FQCN（完全限定クラス名）を指定します。
     * @return true:指定したクラス名を含む場合。
     */
    public boolean containsClassInfo(final String className) {
        return classInfos.containsKey(className);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("XmlInfo:");
        
        for(ClassInfo clazz : classInfos.values()) {
            sb.append("\n  ").append(clazz.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * JAXB用のクラス情報を設定するメソッド。
     * <p>XMLの読み込み時に呼ばれます。
     * <p>既存の情報はクリアされます。
     * @since 1.1
     * @param classInfos クラス情報
     */
    @XmlElement(name="class")
    public void setClassInfos(List<ClassInfo> classInfos) {
        this.classInfos.clear();
        for(ClassInfo item : classInfos) {
            addClassInfo(item);
        }
    }
    
    /**
     * JAXB用のクラス情報を取得するメソッド。
     * <p>XMLの書き込み時に呼ばれます。
     * @since 1.1
     * @return
     */
    public List<ClassInfo> getClassInfos() {
        return new ArrayList<>(this.classInfos.values());
    }
    
    /**
     * XML(テキスト)として返す。
     * <p>JAXB標準の設定でXMLを作成します。
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
     * <p>XlsLoaderなどに直接渡せる形式。
     * <p>{@link #toXml()}にてXMLに変換後に、InputStreamにサイド変換する。
     * @since 1.1
     * @return XML情報。
     */
    public InputStream toInputStream() {
        
        String text = toXml();
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        
    }
    
    /**
     * {@link XmlInfo}を組み立てるためのクラス。
     *
     */
    public static final class Builder {
        
        private List<ClassInfo> classInfos;
        
        private Builder() {
            this.classInfos = new ArrayList<>();
        }
        
        /**
         * 組み立てた{@link XmlInfo}のインスタンスを返す。
         * @return {@link XmlInfo}オブジェクト。
         */
        public XmlInfo buildXml() {
            return new XmlInfo(this);
        }
        
        /**
         * クラス情報を追加する。
         * @param classInfo クラス情報
         * @return
         * @throws IllegalArgumentException classInfo is null
         */
        public Builder classInfo(ClassInfo classInfo) {
            ArgUtils.notNull(classInfo, "classInfo");
            this.classInfos.add(classInfo);
            return this;
        }
        
    }
}
