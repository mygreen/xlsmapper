package com.gh.mygreen.xlsmapper.annotation;

/**
 * アノテーション{@link XlsLabelledCell}にて、見出しセルにから見て値が設定されているセルの位置を指定します。
 * @author Naoki Takezoe
 */
public enum LabelledCellType {

    /** 左側のセルを指定 */
    Left,

    /** 右側のセルを指定*/
    Right,

    /** 下側のセルを指定 */
    Bottom

}
