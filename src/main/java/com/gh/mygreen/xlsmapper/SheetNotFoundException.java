package com.gh.mygreen.xlsmapper;

import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;


/**
 * シートが見つからない場合にスローされる例外クラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class SheetNotFoundException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1604967589865552445L;
    
    private final String sheetName;
    
    private final Integer sheetNumber;
    
    private final Integer bookSheetSize;
    
    /**
     * 指定したシート名が見つからない場合に、そのシート名を指定するコンストラクタ。
     * @param sheetName シート名
     */
    public SheetNotFoundException(final String sheetName) {
        this(sheetName, MessageBuilder.create("sheet.notFound.name")
                .var("sheetName", sheetName)
                .format());
        
    }
    
    /**
     * 指定したシート名が見つからない場合に、そのシート名を指定するコンストラクタ。
     * @param sheetName シート名
     * @param message メッセージ
     * @since 0.5
     */
    public SheetNotFoundException(final String sheetName, final String message) {
        super(message);
        this.sheetName = sheetName;
        this.sheetNumber = null;
        this.bookSheetSize = null;
    }
    
    /**
     * 指定したシートのインデックスが見つからない場合に、そのシートインデックスを指定するコンストラクタ。
     * @param sheetNumber シート番号
     * @param bookSheetSize ワークブックのシートサイズ。
     */
    public SheetNotFoundException(final int sheetNumber, final int bookSheetSize) {
        super(MessageBuilder.create("sheet.notFound.overSize")
                .var("sheetNumber", sheetNumber)
                .var("bookSheetSize", bookSheetSize)
                .format());
        this.sheetName = null;
        this.sheetNumber = sheetNumber;
        this.bookSheetSize = bookSheetSize;
    }
    
    /**
     * 検索対象のシート名。{@link XlsSheet}のname属性を指定されていた場合に設定される。
     * @return
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * 検索対象のシート番号。{@link XlsSheet}のnumber属性を指定されていた場合に設定される。
     * @return
     */
    public Integer getSheetNumber() {
        return sheetNumber;
    }
    
    /**
     * Excelファイルのシートの個数。{@link XlsSheet}のnumber属性を指定されていた場合に設定される。
     * @return
     */
    public Integer getBookSheetSize() {
        return bookSheetSize;
    }
    
}
