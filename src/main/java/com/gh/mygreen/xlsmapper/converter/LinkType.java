package com.gh.mygreen.xlsmapper.converter;

import org.apache.poi.ss.usermodel.Hyperlink;

/**
 * リンクの種類を示す列挙型
 *
 * @author T.TSUCHIE
 *
 */
public enum LinkType {
    
    /**
     * ドキュメント形式。例：<code>Sheet0!A1</code>
     */
    DOCUMENT(Hyperlink.LINK_DOCUMENT),
    
    /**
     * メールアドレスの形式。例：<code>usr@example.jp</code>
     */
    EMAIL(Hyperlink.LINK_EMAIL),
    
    /**
     * ファイル形式のアドレス。例:<code>sample.xls</code>
     */
    FILE(Hyperlink.LINK_FILE),
    
    /**
     * URLの形式のアドレス。例:<code>sample.xls</code>
     */
    URL(Hyperlink.LINK_URL),
    
    /**
     * 不明な場合。
     */
    UNKNOWN(-1),
    ;
    
    /**
     * POIの形式のタイプ
     */
    private final int poiType;
    
    private LinkType(final int poiType) {
        this.poiType = poiType;
        
    }
    
    /**
     * POIの{@link Hyperlink}のタイプ
     * @return
     */
    public int poiType() {
        return this.poiType;
    }
    
    /**
     * 指定されたPOIのタイプを持つリンクタイプを返します。
     * @param poiType POIのリンクタイプ。{@link Hyperlink}の<code>LINK_XXXX</code>の値。
     * @return 指定されたリンクのタイプを持つ{@link LinkType}の列挙型。不明な場合は、{@link #UNKNOWN}を返す。
     */
    public LinkType valueOfPoiType(final int poiType) {
        
        if(poiType == DOCUMENT.poiType()) {
            return DOCUMENT;
            
        } else if(poiType == EMAIL.poiType()) {
            return EMAIL;
            
        } else if(poiType == FILE.poiType()) {
            return FILE;
            
        } else if(poiType == URL.poiType()) {
            return URL;
        }
        
        return UNKNOWN;
    }
    
}
