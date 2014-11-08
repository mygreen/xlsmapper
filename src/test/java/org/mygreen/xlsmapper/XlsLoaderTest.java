package org.mygreen.xlsmapper;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mygreen.xlsmapper.XlsLoader;
import org.mygreen.xlsmapper.expression.ExpressionLanguageRegistry;
import org.mygreen.xlsmapper.validation.EmployerValidator;
import org.mygreen.xlsmapper.validation.ObjectError;
import org.mygreen.xlsmapper.validation.SheetBindingErrors;
import org.mygreen.xlsmapper.validation.SheetMessageConverter;


public class XlsLoaderTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testLoad() throws Exception {
        
        try {
            FileInputStream xlsIn = new FileInputStream("./src/test/data/employer.xlsx");
            XlsLoader loader = new XlsLoader();
            Employer bean = loader.load(xlsIn, Employer.class);
            
            System.out.printf("Sheet name : %s.\n", bean.fieldSheetName);
            System.out.printf("Sheet name : %s.\n", bean.getMethodSheetName());
            
            System.out.println(bean.getCellValue());
            System.out.printf("LabelledCell=%s\n", bean.labelledCell);
            System.out.printf("intValue=%s\n", bean.intValue);
            System.out.printf("DateValue=%s\n", bean.dateValue);
            System.out.printf("EnumValue=%s\n", bean.getUpdateType());
            System.out.printf("EnumValue(alias)=%s\n", bean.updateType2);
            
            for(String split : bean.getSplit()) {
                System.out.printf("ArrayValue item=%s\n", split);
            }
            
            List<EmployerHistory> histories = bean.getHistory();
            for(EmployerHistory history : histories) {
                System.out.println(history);
            }
            
//            for(EmployerHistory history : bean.getHistoryVertical()) {
//                System.out.println(history);
//            }
            
            for(EmployerInfo list : bean.getEmployerInfoList()) {
                System.out.println(list);
            }
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testLoadValidate() {
        
        try {
            XlsMapper xlsMapper = new XlsMapper();
            xlsMapper.getConig().setSkipTypeBindFailure(true);
            
            FileInputStream xlsIn = new FileInputStream("./src/test/data/employer_bind_error.xlsx");
            XlsLoader loader = new XlsLoader();
            loader.getConfig().setSkipTypeBindFailure(true);
            SheetBindingErrors errors = new SheetBindingErrors(Employer.class);
            Employer bean = loader.load(xlsIn, Employer.class, errors);
            
            
            EmployerValidator validator = new EmployerValidator();
            validator.validate(bean, errors);
            
//            ExpressionLanguageRegistry elRegistry = new ExpressionLanguageRegistry();
            SheetMessageConverter messageConverter = new SheetMessageConverter();
//            messageConverter.getMessageInterporlator()
//                .setExpressionLanguage(elRegistry.getExpressionLanguage("mvel"));
            
            if(errors.hasErrors()) {
                System.out.println(" =========== has error ======");
                for(ObjectError error : errors.getAllErrors()) {
//                    System.out.println(error.toString());
                    System.out.println(messageConverter.convertMessage(error));
                }
            }
            
//            System.out.printf("currentPath=%s\n", errors.getCurrentPath());
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
}
