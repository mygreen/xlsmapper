package test.external;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.SheetFinder;
import com.gh.mygreen.xlsmapper.SheetNotFoundException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.ObjectError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.SheetBeanValidator;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * 外部パッケージのテスト。
 *
 * @author T.TSUCHIE
 *
 */
public class ExternalTest {
    
    /**
     * テスト用に動的にシート名を切り替えるSheetFinder
     *
     */
    private static class CustomSheetFinder extends SheetFinder {
        
        private String mySheetName;
        
        CustomSheetFinder(String mySheetName) {
            this.mySheetName = mySheetName;
        }
        
        @Override
        public Sheet[] findForLoading(final Workbook workbook, final XlsSheet sheetAnno,
                final AnnotationReader annoReader, final Class<?> beanClass)
                        throws SheetNotFoundException, AnnotationInvalidException, AnnotationReadException {
            
            if(Utils.isNotEmpty(mySheetName)) {
                // シート名から取得する。
                final Sheet xlsSheet = workbook.getSheet(mySheetName);
                if(xlsSheet == null) {
                    throw new SheetNotFoundException(mySheetName);
                }
                return new Sheet[]{ xlsSheet };
            }
            return super.findForLoading(workbook, sheetAnno, annoReader, beanClass);
        }
    }
    
    @BeforeClass
    public static void setup() {
        System.setProperty("xlsmapper.jexlRestricted", "true");
//        System.setProperty("xlsmapper.jexlPermissions", "test.external.*");
    }
    
    @Test
    public void testRead_normal() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/external.xlsx")) {
            
            SheetBindingErrors<ExternalSheet> bindingErrors = mapper.loadDetail(in, ExternalSheet.class);
            
            ExternalSheet sheet = bindingErrors.getTarget();
            
            // 入力値検証
            SheetBeanValidator validatorAdaptor = new SheetBeanValidator();
            validatorAdaptor.validate(sheet, bindingErrors);
            
            List<ObjectError> errors = bindingErrors.getAllErrors();
            assertThat(errors, hasSize(0));
            
        }
    }
    
    @Test
    public void testRead_validationError_Enum() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration()
            .setContinueTypeBindFailure(true)
            .setSheetFinder(new CustomSheetFinder("エラー_Enum"));

        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/external.xlsx")) {
            
            SheetBindingErrors<ExternalSheet> bindingErrors = mapper.loadDetail(in, ExternalSheet.class);
            
            ExternalSheet sheet = bindingErrors.getTarget();
            
            // 入力値検証
            SheetBeanValidator validatorAdaptor = new SheetBeanValidator();
            validatorAdaptor.validate(sheet, bindingErrors);
            
            SheetErrorFormatter errorFormatter = new SheetErrorFormatter();
            
            List<ObjectError> errors = bindingErrors.getAllErrors();
            assertThat(errors, hasSize(1));
            assertThat(errorFormatter.format(errors.get(0)), is("[エラー_Enum]:ロール - セル(C5)の値'aaa'は、何れかの値[Admin, Developer, Repoter]で設定してください。"));
            
        }
    }
    
    @Test
    public void testRead_validationError_CutomType() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration()
            .setContinueTypeBindFailure(true)
            .setSheetFinder(new CustomSheetFinder("エラー_カスタムタイプ"));

        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/external.xlsx")) {
            
            SheetBindingErrors<ExternalSheet> bindingErrors = mapper.loadDetail(in, ExternalSheet.class);
            
            ExternalSheet sheet = bindingErrors.getTarget();
            
            // 入力値検証
            SheetBeanValidator validatorAdaptor = new SheetBeanValidator();
            validatorAdaptor.validate(sheet, bindingErrors);
            
            SheetErrorFormatter errorFormatter = new SheetErrorFormatter();
            
            List<ObjectError> errors = bindingErrors.getAllErrors();
            assertThat(errors, hasSize(1));
            assertThat(errorFormatter.format(errors.get(0)), is("[エラー_カスタムタイプ]:郵便番号 - セル(C6)の値'0001111'の型変換に失敗しました。"));
            
        }
    }
    
    @Test
    public void testRead_AnnoError() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/external.xlsx")) {
            
            AnnotationInvalidException exception = assertThrows(AnnotationInvalidException.class, () -> 
                mapper.loadDetail(in, ExeranalWronngAnnoSheet.class));
            
            assertThat(exception.getMessage(), is("'test.external.ExternalTest$ExeranalWronngAnnoSheet'において、アノテーション'@XlsSheet'の何れか属性[name or number or regex]の設定は必須です。"));
            
        }
    }
    
    /**
     * アノテーションの定義間違い。
     * <p>シートの指定がない。
     *
     */
    @XlsSheet
    public static class ExeranalWronngAnnoSheet {
        
        @NotEmpty
        @XlsLabelledCell(label = "名前", type = LabelledCellType.Right)
        private String name;
        
    }
}
