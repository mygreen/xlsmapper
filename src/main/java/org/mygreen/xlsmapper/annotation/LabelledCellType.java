package org.mygreen.xlsmapper.annotation;

/**
 * アノテーション{@link LabelledCellType}にて、見出しセルにから見て値が設定されているセルの位置・方向を指定します。
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
