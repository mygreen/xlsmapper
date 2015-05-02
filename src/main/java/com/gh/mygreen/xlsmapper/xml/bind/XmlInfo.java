package com.gh.mygreen.xlsmapper.xml.bind;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * クラス情報を追加する。
     * @param classInfo
     */
    @XmlElement(name="class")
    public void setClassInfo(final ClassInfo classInfo) {
        ArgUtils.notNull(classInfo, "classInfo");
        
        this.classInfos.put(classInfo.getClassName(), classInfo);
    }
    
    /**
     * クラス名を指定してクラス情報を取得する。
     * @param clazzName クラス名
     * @return
     */
    public ClassInfo getClassInfo(final String clazzName) {
        return classInfos.get(clazzName);
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
    
}
