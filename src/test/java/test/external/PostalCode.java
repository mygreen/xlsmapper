package test.external;

import java.io.Serializable;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * 外部パッケージの独自の型。
 * <p>郵便番号を表現する。
 *
 * @author T.TSUCHIE
 *
 */
public class PostalCode implements Serializable {
    
    private static final String SEPARATOR = "-";
    
    /** 上3桁 */
    private String code1;
    
    /** 下4桁 */
    private String code2;
    
    public PostalCode(final String value) {
        if(!parse(value)) {
            throw new IllegalArgumentException("not support format : " + value);
        }
    }

    public boolean parse(final String value) {
        
        if(Utils.isEmpty(value)) {
            return false;
        }
        
        String[] splits = value.split(SEPARATOR);
        if(splits.length != 2) {
            return false;
        }
        
        if(splits[0].length() != 3) {
            return false;
        }
        
        this.code1 = splits[0];
        
        if(splits[1].length() != 4) {
            return false;
        }
        
        this.code2 = splits[1];
        
        return true;
    }
    
    @Override
    public String toString() {
        return code1 + SEPARATOR + code2;
    }
    
    public String getCode1() {
        return code1;
    }
    
    public String getCode2() {
        return code2;
    }
    
    /**
     * 変換用のCellConverter
     *
     */
    public static class PostalCellConverter extends BaseCellConverter<PostalCode> {

        public PostalCellConverter(FieldAccessor field, Configuration config) {
            super(field, config);
        }

        @Override
        protected PostalCode parseCell(Cell evaluatedCell, String formattedValue) throws TypeBindException {
            
            try {
                return getTextFormatter().parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        }

        @Override
        protected void setupCell(Cell cell, Optional<PostalCode> cellValue) throws TypeBindException {
            
            if (cellValue.isPresent()) {
                String text = getTextFormatter().format(cellValue.get());
                cell.setCellValue(text);
            } else {
                cell.setBlank();
            }
            
        }
        
    }
    
    /**
     * 変換処理用のCellConverterFactory
     *
     *
     * @author T.TSUCHIE
     *
     */
    public static class PostalCellConverterFactory extends CellConverterFactorySupport<PostalCode> implements CellConverterFactory<PostalCode> {

        @Override
        public CellConverter<PostalCode> create(FieldAccessor accessor, Configuration config) {
            
            final PostalCellConverter cellConverter = new PostalCellConverter(accessor, config);
            setupCellConverter(cellConverter, accessor, config);
            return cellConverter;
        }
        
        @Override
        protected void setupCustom(BaseCellConverter<PostalCode> cellConverter, FieldAccessor field, Configuration config) {
            // no implements
        }

        @Override
        protected TextFormatter<PostalCode> createTextFormatter(FieldAccessor field, Configuration config) {
           
            return new TextFormatter<PostalCode>() {

                @Override
                public PostalCode parse(String text) throws TextParseException {
                    
                    try {
                        return new PostalCode(text);
                    } catch(IllegalArgumentException e) {
                        throw new TextParseException(text, PostalCode.class);
                    }
                    
                }

                @Override
                public String format(PostalCode value) {
                    if (value == null) {
                        return "";
                    }
                    
                    return value.toString();
                }
                
            };
        }
        
    }
    
}
