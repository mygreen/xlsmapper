package com.gh.mygreen.xlsmapper;

import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;


/**
 * セルのコメント情報を保持するクラス。
 * <p>POI-3.10～3.12の場合、コメント付きのシートに対して行をずらす処理を行うとファイルが壊れるため、一時的に取得するために利用する。
 * <p>POIの不良情報：<a href="https://issues.apache.org/bugzilla/show_bug.cgi?id=56017" target="_blank">Bug 56017</a>
 * 
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public class CellCommentStore implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private int column;
    
    private int row;
    
    private String author;
    
    private boolean visible;
    
    private AnchorStore anchor;
    
    private TextStore text;
    
    /**
     * コメントの表示位置を保持する。
     */
    public static class AnchorStore {
        
        private int type;
        
        private int dx1;
        
        private int dx2;
        
        private int dy1;
        
        private int dy2;
        
        // 横方向のコメント枠のサイズ
        private int columnSize;
        
        // 縦方向のコメントの枠のサイズ
        private int rowSize;
        
        public static AnchorStore get(final ClientAnchor anchor) {
            final AnchorStore anchorStore = new AnchorStore();
            anchorStore.type = anchor.getAnchorType();
            anchorStore.dx1 = anchor.getDx1();
            anchorStore.dx2 = anchor.getDx2();
            anchorStore.dy1 = anchor.getDy1();
            anchorStore.dy2 = anchor.getDy2();
            
            anchorStore.columnSize = anchor.getCol2() - anchor.getCol1();
            anchorStore.rowSize = anchor.getRow2() - anchor.getRow1();
            
            return anchorStore;
        }
        
    }
    
    /**
     * フォントなどの装飾を含めてテキストの情報を保持する。
     *
     */
    public static class TextStore {
        
        private String text;
        
        private TextFontStore[] fonts;
        
        public static TextStore get(final RichTextString richText) {
            
            final TextStore textStore = new TextStore();
            textStore.text = richText.getString();
            
            // フォントの取得
            textStore.fonts = new TextFontStore[richText.numFormattingRuns()];
            
            if(richText instanceof XSSFRichTextString) {
                final XSSFRichTextString xssfRichText = (XSSFRichTextString)richText;
                for(int i=0; i < xssfRichText.numFormattingRuns(); i++) {
                    final TextFontStore fontStore = new TextFontStore();
                    fontStore.font = xssfRichText.getFontOfFormattingRun(i);
                    fontStore.startIndex = xssfRichText.getIndexOfFormattingRun(i);
                    fontStore.endIndex = fontStore.startIndex + xssfRichText.getLengthOfFormattingRun(i);
                    textStore.fonts[i] = fontStore;
                }
                
            } else if(richText instanceof HSSFRichTextString) {
                final HSSFRichTextString hssfRichText = (HSSFRichTextString)richText;
                for(int i=0; i < hssfRichText.numFormattingRuns(); i++) {
                    final TextFontStore fontStore = new TextFontStore();
                    fontStore.fontIndex = hssfRichText.getFontOfFormattingRun(i);
                    fontStore.startIndex = hssfRichText.getIndexOfFormattingRun(i);
                    if(i > 0) {
                        // 2003形式の場合、フォントの終了位置がとれないので、次のフォントの開始位置から終了位置を決める。
                        textStore.fonts[i-1].endIndex = fontStore.startIndex;
                        
                    }
                    
                    if(i == hssfRichText.numFormattingRuns() -1) {
                        fontStore.endIndex = hssfRichText.length();
                    }
                    
                    textStore.fonts[i] = fontStore;
                }
            }
            
            return textStore;
        }
    }
    
    /**
     * コメントのフォント情報を保持するクラス。
     * <p>Excelのバージョンによって、フォントはインデックス番号かフォントオブジェクトかで持つか変わる。
     */
    public static class TextFontStore {
        
        private Font font;
        
        private short fontIndex;
        
        private int startIndex;
        
        private int endIndex;
        
    }
    
    /**
     * セルのコメントを取得する。その際に、コメントを削除する。
     * @param cell
     * @return コメントが設定されていない場合は、nullを返す。
     */
    public static CellCommentStore getAndRemove(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        final Comment comment = cell.getCellComment();
        if(comment == null) {
            return null;
        }
        
        final CellCommentStore commentStore = get(comment);
        cell.removeCellComment();
        return commentStore;
    }
    
    /**
     * セルのコメントを取得する。
     * @param comment 元となるPOIのセルノコメント。
     * @return
     * @throws IllegalArgumentException comment is null.
     */
    public static CellCommentStore get(final Comment comment) {
        ArgUtils.notNull(comment, "comment");
        
        final CellCommentStore dest = new CellCommentStore();
        dest.column = comment.getColumn();
        dest.row = comment.getRow();
        dest.author = comment.getAuthor();
        dest.text = TextStore.get(comment.getString());
        dest.visible = comment.isVisible();
        dest.anchor = AnchorStore.get(comment.getClientAnchor());
        
        return dest;
        
    }
    
    /**
     * 保持している情報を元に、シートのセルにコメントを設定する。
     * @param sheet
     * @return POIの設定したコメントオブジェクト。
     * @throws IllegalArgumentException sheet is null.
     */
    public Comment set(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        
        final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        final Drawing drawing = sheet.createDrawingPatriarch();
        
        // コメントの位置、サイズの指定
        int col1 = column + 1;
        int row1 = row;
        
        if(sheet instanceof HSSFSheet) {
            // 2003形式の場合は、行の位置をずらす。
            row1--;
        }
        
        int col2 = col1 + anchor.columnSize;
        int row2 = row1 + anchor.rowSize;
        final ClientAnchor clientAnchor = drawing.createAnchor(
                anchor.dx1, anchor.dy1, anchor.dx2, anchor.dy2,
                col1, row1, col2, row2
                );
        clientAnchor.setAnchorType(anchor.type);
        
        final Comment comment = drawing.createCellComment(clientAnchor);
        comment.setColumn(column);
        comment.setRow(row);
        comment.setAuthor(author);
        comment.setVisible(visible);
        
        // 装飾を適用する。
        final RichTextString richText = helper.createRichTextString(text.text);
        if(text.fonts != null) {
            for(TextFontStore fontStore : text.fonts) {
                if(fontStore.font != null) {
                    richText.applyFont(fontStore.startIndex, fontStore.endIndex, fontStore.font);
                } else {
                    richText.applyFont(fontStore.startIndex, fontStore.endIndex, fontStore.fontIndex);
                }
            }
        }
        
        comment.setString(richText);
        
        return comment;
        
    }
    
    /**
     * 現在の行の位置に対して、指定した値を加算する。
     * @param value 加算する値。
     * @return 加算した結果を返す。
     */
    public int addRow(int value) {
        this.row += value;
        return row;
    }
    
    /**
     * 現在の列の位置に対して、指定した値を加算する。
     * @param value 加算する値。
     * @return 加算した結果を返す。
     */
    public int addColumn(int value) {
        this.column += value;
        return column;
    }
    
    public int getColumn() {
        return column;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    public int getRow() {
        return row;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
}
