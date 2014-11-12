package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.Employer;
import com.gh.mygreen.xlsmapper.XlsLoader;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageRegistry;
import com.gh.mygreen.xlsmapper.validation.MessageInterpolator;
import com.gh.mygreen.xlsmapper.validation.MessageResolver;
import com.gh.mygreen.xlsmapper.validation.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetMessageConverter;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.MessageInterpolatorAdapter;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.MessageResolverInterpolator;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.SheetBeanValidator;


public class BeanValidationTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testValidation1() {
        
        try {
            Employer beanObj = loadSheet(new File("./src/test/data/employer.xlsx"));
            
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.usingContext()
                    .getValidator();
            
            Set<ConstraintViolation<Employer>> violoadtions = validator.validate(beanObj);
            for(ConstraintViolation<Employer> violation : violoadtions) {
                
                final Object leafBean = violation.getLeafBean();
                final Path propertyPath = violation.getPropertyPath();
                
                System.out.printf("parentClss=[%s], field=[%s], message=[%s]\n",
                        leafBean.getClass().getCanonicalName(),
                        propertyPath.toString(),
                        violation.getMessage());
                
                final PathImpl pathImpl = (PathImpl) propertyPath;
                System.out.println(pathImpl.getLeafNode().toString());
                
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testValidation2() {
        
        try {
            SheetBindingErrors errors = new SheetBindingErrors(Employer.class);
            Employer beanObj = loadSheet(new File("./src/test/data/employer.xlsx"), errors);
            
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.usingContext()
                    .messageInterpolator(new MessageResolverInterpolator(new ResourceBundleMessageResolver()))
                    .getValidator();
            
            SheetBeanValidator validatorAdaptor = new SheetBeanValidator(validator);
            validatorAdaptor.validate(beanObj, errors);
            
            SheetMessageConverter messageConverter = new SheetMessageConverter();
            for(String message : messageConverter.convertMessages(errors.getAllErrors())) {
                System.out.println(message);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testValidation3() {
        
        try {
            SheetBindingErrors errors = new SheetBindingErrors(Employer.class);
            Employer beanObj = loadSheet(new File("./src/test/data/employer.xlsx"), errors);
            
            ExpressionLanguageRegistry elRegistry = new ExpressionLanguageRegistry();
            MessageInterpolator messageInterpolator = new MessageInterpolator();
            messageInterpolator.setExpressionLanguage(elRegistry.getExpressionLanguage("mvel"));
            
            MessageResolver messageResolver = new ResourceBundleMessageResolver();
            
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.usingContext()
                    .messageInterpolator(new MessageInterpolatorAdapter(messageResolver, messageInterpolator))
                    .getValidator();
            
            SheetBeanValidator validatorAdaptor = new SheetBeanValidator(validator);
            validatorAdaptor.validate(beanObj, errors);
            
            SheetMessageConverter messageConverter = new SheetMessageConverter();
            for(String message : messageConverter.convertMessages(errors.getAllErrors())) {
                System.out.println(message);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    private Employer loadSheet(final File file) throws Exception {
        
        FileInputStream xlsIn = new FileInputStream(file);
        XlsLoader loader = new XlsLoader();
        Employer bean = loader.load(xlsIn, Employer.class);
        
        return bean;
    }
    
    private Employer loadSheet(final File file, SheetBindingErrors errors) throws Exception {
        
        FileInputStream xlsIn = new FileInputStream(file);
        XlsLoader loader = new XlsLoader();
        loader.getConfig().setSkipTypeBindFailure(true);
        Employer bean = loader.load(xlsIn, Employer.class, errors);
        
        return bean;
    }
}
