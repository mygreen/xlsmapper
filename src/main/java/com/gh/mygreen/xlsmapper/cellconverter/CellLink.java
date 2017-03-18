package com.gh.mygreen.xlsmapper.cellconverter;

import java.io.Serializable;


/**
 * セルのリンクを表現するクラス
 * 
 * @version 0.5
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
     * @param link リンクのアドレス
     * @param label リンクの見出し。
     */
    public CellLink(final String link, final String label) {
        setLink(link);
        setLabel(label);
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((link == null) ? 0 : link.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        CellLink other = (CellLink) obj;
        if(label == null) {
            if(other.label != null) {
                return false;
            }
        } else if(!label.equals(other.label)) {
            return false;
        }
        if(link == null) {
            if(other.link != null) {
                return false;
            }
        } else if(!link.equals(other.link)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "CellLink"
                + "@" + super.toString()
                + "["
                + "link=" + link
                + ", label=" + label
                + "]";
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
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
}
