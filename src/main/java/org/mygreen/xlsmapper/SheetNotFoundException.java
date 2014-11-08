package org.mygreen.xlsmapper;


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
    
    private Integer sheetIndex;
    
    private Integer bookSheetSize;
    
    /**
     * 指定したシート名が見つからない場合に、そのシート名を指定するコンストラクタ。
     * @param sheetName シート名
     */
    public SheetNotFoundException(final String sheetName) {
        super(String.format("Cannot find sheet '%s'.", sheetName));
        this.sheetName = sheetName;
    }
    
    /**
     * 指定したシートのインデックスが見つからない場合に、そのシートインデックスを指定するコンストラクタ。
     * @param sheetIndex シートインデックス
     * @param bookSheetSize ワークブックのシートサイズ。
     */
    public SheetNotFoundException(final int sheetIndex, final int bookSheetSize) {
        super(String.format("Cannot find sheet index [%d]. book only has number of sheet %d.", sheetIndex, bookSheetSize));
        this.sheetIndex = sheetIndex;
        this.bookSheetSize = bookSheetSize;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public Integer getSheetIndex() {
        return sheetIndex;
    }
    
    public Integer getBookSheetSize() {
        return bookSheetSize;
    }
    
}
