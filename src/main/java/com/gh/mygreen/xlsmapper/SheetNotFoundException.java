package com.gh.mygreen.xlsmapper;

import com.gh.mygreen.xlsmapper.annotation.XlsSheet;


/**
 * シートが見つからない場合にスローされる例外クラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class SheetNotFoundException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1604967589865552445L;
    
    private String sheetName;
    
    private Integer sheetNumber;
    
    private Integer bookSheetSize;
    
    /**
     * 指定したシート名が見つからない場合に、そのシート名を指定するコンストラクタ。
     * @param sheetName シート名
     */
    public SheetNotFoundException(final String sheetName) {
        this(sheetName, String.format("Cannot find sheet '%s'.", sheetName));
        
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
    }
    
    /**
     * 指定したシートのインデックスが見つからない場合に、そのシートインデックスを指定するコンストラクタ。
     * @param sheetNumber シート番号
     * @param bookSheetSize ワークブックのシートサイズ。
     */
    public SheetNotFoundException(final int sheetNumber, final int bookSheetSize) {
        super(String.format("Cannot find sheet number [%d]. book only has number of sheet %d.", sheetNumber, bookSheetSize));
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
