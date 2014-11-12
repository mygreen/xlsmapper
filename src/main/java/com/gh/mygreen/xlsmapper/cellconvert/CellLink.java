package com.gh.mygreen.xlsmapper.cellconvert;

import java.io.Serializable;


/**
 * セルのリンクを表現するクラス
 *
 * @author T.TSUCHIE
 *
 */
public class CellLink implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** リンク */
    private String link;
    
    /** 見出し */
    private String label;
    
    public CellLink() {
        
    }
    
    /**
     * リンクのアドレスと見出しを指定してリンクをこうしくします。
     * @param address リンクのアドレス
     * @param text リンクの見出し。
     */
    public CellLink(final String link, final String label) {
        setLink(link);
        setLabel(label);
    }
    
    /**
     * リンクのアドレスを取得する。
     */
    public String getLink() {
        return link;
    }
    
    /**
     * リンクのアドレスを設定する。
     */
    public void setLink(String link) {
        this.link = link;
    }
    
    /**
     * リンクの見出しを取得する
     * @return
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * リンクの見出しを設定する
     * @return
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
}
