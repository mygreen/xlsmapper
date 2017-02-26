package com.gh.mygreen.xlsmapper.util;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;

/**
 * セルの変換処理に関するユーティリティクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ConversionUtils {
    
    /**
     * アノテーション{@link XlsCellOption}を元に、セルの制御の設定「折り返し設定」「縮小して表示」を設定します。
     * @param cell セル
     * @param cellOptionAnno セルの制御を設定するアノテーション。
     * @throws NullPointerException {@literal cell == null.}
     */
    public static void setupCellOption(final Cell cell, final Optional<XlsCellOption> cellOptionAnno) {
        
        ArgUtils.notNull(cell, "cell");
        
        cellOptionAnno.ifPresent(anno -> {
            
            if(anno.shrinkToFit()) {
                cell.getCellStyle().setShrinkToFit(true);
                
            } else if(anno.wrapText()) {
                cell.getCellStyle().setWrapText(true);
                
            }
            
        });
        
        
    }
    
}
