package com.gh.mygreen.xlsmapper;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.annotation.XlsCommentOption;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link CellCommentHandler}の標準の実装。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class DefaultCellCommentHandler implements CellCommentHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCellCommentHandler.class);
    
    /**
     * コメントの縦方向の開始位置。
     * 行数分で表現する。
     */
    private int vertialPrefix = 1;
    
    /**
     * コメントの横方向の開始位置。
     * 列数分で表現する。
     */
    private int horizontalPrefix = 1;
    
    /**
     * コメントの縦方向の最大サイズ。
     * 行数分で表現する。
     */
    private int maxVerticalSize = 4;
    
    /**
     * コメントの列方向の最大サイズ。
     * 列数分で表現する。
     */
    private int maxHorizontalSize = 3;
    
    @Override
    public Optional<String> handleLoad(final Cell cell, Optional<XlsCommentOption> commentOption) {
        
        Comment comment = getMergedCellComment(cell);
        if(comment == null) {
            return Optional.empty();
        }
        
        String commentText = comment.getString().getString();
        return Optional.of(commentText);
    }
    
    /**
     * 結合を考慮したセルのコメントを取得する。
     * @param cell 元となるセル。
     * @return コメント。コメントが設定されていなければ、nullを返す。
     */
    private Comment getMergedCellComment(final Cell cell) {
        Comment comment = cell.getCellComment();
        if(comment != null) {
            return comment;
        }
        
        final Sheet sheet = cell.getSheet();
        final int size = sheet.getNumMergedRegions();
        
        for(int i=0; i < size; i++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            if(!range.isInRange(cell)) {
                continue;
            }
            
            // nullでないセルを取得する。
            for(int rowIdx=range.getFirstRow(); rowIdx <= range.getLastRow(); rowIdx++) {
                final Row row = sheet.getRow(rowIdx);
                if(row == null) {
                    continue;
                }

                for(int colIdx=range.getFirstColumn(); colIdx <= range.getLastColumn(); colIdx++) {
                    final Cell valueCell = row.getCell(colIdx);
                    if(valueCell == null) {
                        continue;
                    }

                    comment = valueCell.getCellComment();
                    if(comment != null) {
                        return comment;
                    }
                }
            }
        }
        
        return null;
        
    }

    @Override
    public void handleSave(final Cell cell, final Optional<String> text, final Optional<XlsCommentOption> commentOption) {
        
        if(!text.isPresent()) {
            // コメントが空のとき
            commentOption.ifPresent(option -> {
                if(option.removeIfEmpty()) {
                    // コメントが空のとき既存のコメントを削除する
                    cell.removeCellComment();
                }
            });
            return;
        }
        
        final Sheet sheet = cell.getSheet();
        final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        final Drawing<?> drawing = sheet.createDrawingPatriarch();
        
        final Comment comment;
        RichTextString richText = helper.createRichTextString(text.get());
        if(cell.getCellComment() == null) {
            ClientAnchor anchor = createAnchor(drawing, text.get(), cell, commentOption);
            comment = drawing.createCellComment(anchor);
            applyCommentFormat(richText, cell);
        } else {
            // 既存のコメントが存在する場合は、書式やサイズをコピーして使用する。
            comment = cell.getCellComment();
            RichTextString orgText = comment.getString();
            if(orgText.numFormattingRuns() > 0) {
                copyCommentFormat(richText, orgText);
            } else {
                applyCommentFormat(richText, cell);
            }
        }
        
        comment.setString(richText);
        
        // コメントの表示状態の更新
        commentOption.ifPresent(option -> comment.setVisible(option.visible()));
        
        cell.setCellComment(comment);
        
    }
    
    /**
     * コメントの位置、サイズを作成する。
     * @param drawing
     * @param text 書き込むコメント
     * @param cell 書込み対象のセル
     * @param commentOption コメントのオプション
     * @return コメントの表示位置
     */
    protected ClientAnchor createAnchor(final Drawing<?> drawing, final String text, final Cell cell,
            final Optional<XlsCommentOption> commentOption) {
        final CellPosition address = CellPosition.of(cell);
        
        // コメントを開業で分割し、最長の行を取得する。
        String[] split = text.split("\r\n|\r|\n");
        int maxLength = Arrays.stream(split)
                .mapToInt(str -> str.getBytes(Charset.forName("Windows-31j")).length)
                .max().orElse(0);
        
        /*
         * コメントの横サイズ。文字数（バイト数）をもとに決定。
         * ・1セルの文字数を元に出す。
         * ・columnWidthは、1文字の幅を1/256にしたものが単位となる。
         * ・最大3列分とする。
         */
        int charPerColumn = cell.getSheet().getColumnWidth(cell.getColumnIndex())/256;
        int commentColumnSize = (int)Math.ceil(maxLength*1.0 / charPerColumn);
        
        int columnSize = commentColumnSize;
        int lineWrappingCount = 0;
        if(commentColumnSize > maxHorizontalSize) {
            columnSize = maxHorizontalSize;
            // 行の折り返し回数を計算する
            lineWrappingCount = commentColumnSize / maxHorizontalSize;
        }
        
        if(commentOption.isPresent() && commentOption.get().horizontalSize() > 0) {
            // 直接指定されている場合
            columnSize = commentOption.get().horizontalSize();
            // 行の折り返し回数を計算する
            lineWrappingCount = columnSize / maxHorizontalSize;
        }
        
        // コメントの縦サイズ。行数をもとに決定。
        int rowSize = split.length + lineWrappingCount > maxVerticalSize ? maxVerticalSize : split.length + lineWrappingCount;
        if(commentOption.isPresent() && commentOption.get().verticalSize() > 0) {
            // 直接指定されている場合
            rowSize = commentOption.get().verticalSize();
        }
        
        return drawing.createAnchor(
                0, 0, 0, 0,
                address.getColumn() + horizontalPrefix, address.getRow() + vertialPrefix,
                address.getColumn() + horizontalPrefix + columnSize, address.getRow() + vertialPrefix + rowSize);
    }
    
    /**
     * 新規にコメントの装飾を設定する。
     * セルの装飾に合わせる。
     * 
     * @param toRichText 設定先のコメント
     * @param cell コメントを設定する先のセル
     */
    protected void applyCommentFormat(final RichTextString toRichText, final Cell cell) {
        
        toRichText.applyFont(cell.getCellStyle().getFontIndex());
    }
    
    /**
     * 既にコメントが設定されているときのコメントの装飾を設定する。
     * 既存のコメントの装飾をコピーするが、そのとき、１つ目のフォント設定のみとする。
     * 
     * @param toRichText コピー先
     * @param fromrichText コピー元
     */
    protected void copyCommentFormat(final RichTextString toRichText, final RichTextString fromrichText) {
        
        if(toRichText instanceof XSSFRichTextString) {
            toRichText.applyFont(((XSSFRichTextString)fromrichText).getFontOfFormattingRun(0));
            
        } else if(toRichText instanceof HSSFRichTextString) {
            toRichText.applyFont(((HSSFRichTextString)fromrichText).getFontOfFormattingRun(0));
            
        } else {
            logger.warn("not suuported exdcel format comment : {}", toRichText.getClass().getName());
        }
        
    }

    /**
     * コメントの縦方向の開始位置を取得する。
     * 行数分で表現する。
     * @return
     */
    public int getVertialPrefix() {
        return vertialPrefix;
    }

    /**
     * コメントの縦方向の開始位置を設定する。
     * 行数分で表現する。
     * @param vertialPrefix コメントの縦方向の開始位置。(0以上)
     * @throws IllegalArgumentException {@literal vertialPrefix < 0}
     */
    public void setVertialPrefix(int vertialPrefix) {
        ArgUtils.notMin(vertialPrefix, 0, "vertialPrefix");
        this.vertialPrefix = vertialPrefix;
    }

    /**
     * コメントの横方向の開始位置を取得する。
     * 列数分で表現する。
     * @return
     */
    public int getHorizontalPrefix() {
        return horizontalPrefix;
    }

    /**
     * コメントの横方向の開始位置を設定する。
     * 列数分で表現する。
     * @param horizontalPrefix コメントの横方向の開始位置。(0以上)
     * @throws IllegalArgumentException {@literal horizontalPrefix < 0}
     */
    public void setHorizontalPrefix(int horizontalPrefix) {
        ArgUtils.notMin(horizontalPrefix, 0, "horizontalPrefix");
        this.horizontalPrefix = horizontalPrefix;
    }
    
    /**
     * コメントの縦方向の最大サイズを取得する。
     * 行数分で表現する。
     * @return the maxVerticalSize
     */
    public int getMaxVerticalSize() {
        return maxVerticalSize;
    }

    
    /**
     * コメントの縦方向の最大サイズを設定する。
     * 行数分で表現する。
     * @param maxVerticalSize コメントの縦方向の最大サイズ。(1以上)
     * @throws IllegalArgumentException {@literal maxVerticalSize < 1}
     */
    public void setMaxVerticalSize(int maxVerticalSize) {
        ArgUtils.notMin(maxVerticalSize, 1, "maxVerticalSize");
        this.maxVerticalSize = maxVerticalSize;
    }

    
    /**
     * コメントの列方向の最大サイズ。
     * 列数分で表現する。
     * maxHorizontalSize を取得する
     * @return the maxHorizontalSize
     */
    public int getMaxHorizontalSize() {
        return maxHorizontalSize;
    }

    
    /**
     * コメントの列方向の最大サイズ。
     * 列数分で表現する。
     * @param maxHorizontalSize コメントの横方向の最大サイズ。(1以上)
     * @throws IllegalArgumentException {@literal maxHorizontalSize < 1}
     */
    public void setMaxHorizontalSize(int maxHorizontalSize) {
        ArgUtils.notMin(maxHorizontalSize, 1, "maxHorizontalSize");
        this.maxHorizontalSize = maxHorizontalSize;
    }

    
}
