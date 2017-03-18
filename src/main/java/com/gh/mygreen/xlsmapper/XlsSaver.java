package com.gh.mygreen.xlsmapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxy;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxyComparator;
import com.gh.mygreen.xlsmapper.fieldprocessor.SavingFieldProcessor;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;
import com.gh.mygreen.xlsmapper.xml.XmlIO;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;


/**
 * JavaBeanをExcelのシートにマッピングし出力するクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsSaver {
    
    private static final Logger logger = LoggerFactory.getLogger(XlsSaver.class);
    
    private XlsMapperConfig config;
    
    public XlsSaver(XlsMapperConfig config) {
        this.config = config;
    }
    
    public XlsSaver() {
        this(new XlsMapperConfig());
    }
    
    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力
     * @param beanObj 書き込むオブジェクト
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beanObj) throws XlsMapperException, IOException {
        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notNull(beanObj, "beanObj");
        
        save(templateXlsIn, xlsOut, beanObj, null);
        
    }
    
    /**
     * XMLによるマッピングを指定して、JavaのオブジェクトをExcelファイルに出力する。
     * @param templateXlsIn
     * @param xlsOut
     * @param beanObj
     * @param xmlIn
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beanObj, final InputStream xmlIn) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notNull(beanObj, "beanObj");
        
        // Xml情報の出力
        XmlInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XmlIO.load(xmlIn);
        }
        
        final AnnotationReader annoReader = new AnnotationReader(xmlInfo);
        final SavingWorkObject work = new SavingWorkObject();
        work.setAnnoReader(annoReader);
        
        work.setErrors(new SheetBindingErrors(beanObj.getClass()));
        
        final Workbook book;
        try {
            book = WorkbookFactory.create(templateXlsIn);
            
        } catch (InvalidFormatException | IOException e) {
            throw new XlsMapperException("fail load template Excel File", e);
        }
        
        final Class<?> clazz = beanObj.getClass();
        final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
        if(sheetAnno == null) {
            throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                    .varWithClass("property", clazz)
                    .varWithAnno("anno", XlsSheet.class)
                    .format());
            
        }
        
        try {
            final Sheet[] xlsSheet = config.getSheetFinder().findForSaving(book, sheetAnno, annoReader, beanObj);
            saveSheet(xlsSheet[0], beanObj, work);
        } catch(SheetNotFoundException e) {
            if(config.isIgnoreSheetNotFound()){
                logger.warn("skip saving by not-found sheet.", e);
                return;
            } else {
                throw e;
            }
        }
        
        if(config.isFormulaRecalcurationOnSave()) {
            book.setForceFormulaRecalculation(true);
        }
        
        book.write(xlsOut);
    }
    
    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力
     * @param beanObjs 書き込むオブジェクトの配列。
     * @throws XlsMapperException
     * @throws IOException 
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notEmpty(beanObjs, "beanObjs");
        
        saveMultiple(templateXlsIn, xlsOut, beanObjs, null);
    }
    
    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力
     * @param beanObjs 書き込むオブジェクトの配列。
     * @param xmlIn アノテーションの定義をしているXMLファイルの入力。
     * @throws XlsMapperException
     * @throws IOException 
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs,
            final InputStream xmlIn) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notEmpty(beanObjs, "beanObjs");
        
        // Xmls情報の出力
        XmlInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XmlIO.load(xmlIn);
        }
        
        final SheetBindingErrorsContainer errorsContainer = new SheetBindingErrorsContainer(getObjectNames(beanObjs));
        
        final AnnotationReader annoReader = new AnnotationReader(xmlInfo);
        final Workbook book;
        try {
            book = WorkbookFactory.create(templateXlsIn);
            
        } catch (InvalidFormatException | IOException e) {
            throw new XlsMapperException("fail load template Excel File", e);
        }
        
        for(int i=0; i < beanObjs.length; i++) {
            final Object beanObj = beanObjs[i];
            final Class<?> clazz = beanObj.getClass();
            
            final XlsSheet sheetAnno = annoReader.getAnnotation(clazz, XlsSheet.class);
            if(sheetAnno == null) {
                throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                        .varWithClass("property", clazz)
                        .varWithAnno("anno", XlsSheet.class)
                        .format());
            }
            
            final SavingWorkObject work = new SavingWorkObject();
            work.setAnnoReader(annoReader);
            
            try {
                final Sheet[] xlsSheet = config.getSheetFinder().findForSaving(book, sheetAnno, annoReader, beanObj);
                work.setErrors(errorsContainer.findBindingResult(i));
                saveSheet(xlsSheet[0], beanObj, work);
            } catch(SheetNotFoundException e) {
                if(config.isIgnoreSheetNotFound()){
                    logger.warn("skip saving by not-found sheet.", e);
                    continue;
                } else {
                    throw e;
                }
            }
        }
        
        if(config.isFormulaRecalcurationOnSave()) {
            book.setForceFormulaRecalculation(true);
        }
        
        book.write(xlsOut);
    }
    
    private String[] getObjectNames(final Object[] beanObjs) {
        List<String> names = new ArrayList<String>();
        for(Object item : beanObjs) {
            names.add(item.getClass().getCanonicalName());
        }
        return names.toArray(new String[names.size()]);
    }
    
    /**
     * 任意のクラスのオブジェクトを、Excelシートにマッピングする。
     * @param sheet
     * @param beanObj
     * @param config
     * @param work
     * @throws XlsMapperException 
     */
    private void saveSheet(final Sheet sheet, final Object beanObj, final SavingWorkObject work) 
            throws XlsMapperException {
        
        final Class<?> clazz = beanObj.getClass();
        
        work.getErrors().setSheetName(sheet.getSheetName());
        
        final AnnotationReader annoReader = work.getAnnoReader();
        final FieldAccessorFactory adpterFactory = new FieldAccessorFactory(annoReader);
        
        // リスナークラスの@PreSave用メソッドの実行
        final XlsListener listenerAnno = annoReader.getAnnotation(beanObj.getClass(), XlsListener.class);
        if(listenerAnno != null) {
            final Object listenerObj = config.createBean(listenerAnno.listenerClass());
            
            for(Method method : listenerObj.getClass().getMethods()) {
                if(annoReader.hasAnnotation(method, XlsPreSave.class)) {
                    Utils.invokeNeedProcessMethod(listenerObj, method, beanObj, sheet, config, work.getErrors());
                }
            }
            
        }
        
        // @PreSave用のメソッドの取得と実行
        for(Method method : clazz.getMethods()) {
            
            final XlsPreSave preProcessAnno = annoReader.getAnnotation(method, XlsPreSave.class);
            if(preProcessAnno != null) {
                Utils.invokeNeedProcessMethod(beanObj, method, beanObj, sheet, config, work.getErrors());
            }
        }
        
        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        
        // public メソッドの処理
        for(Method method : clazz.getMethods()) {
            method.setAccessible(true);
            
            for(Annotation anno : work.getAnnoReader().getAnnotations(method)) {
                
                final SavingFieldProcessor<?> processor = config.getFieldProcessorRegistry().getSavingProcessor(anno.annotationType());
                
                if(processor != null && ClassUtils.isAccessorMethod(method)) {
                    final FieldAccessor accessor = adpterFactory.create(method);
                    
                    final FieldAccessorProxy accessorProxy = new FieldAccessorProxy(anno, processor, accessor);
                    if(!accessorProxies.contains(accessorProxy)) {
                        accessorProxies.add(accessorProxy);
                    }
                    
                } else if(anno instanceof XlsPostSave) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, beanObj, method));
                }
            }
        }
        
        // フィールドの処理
        for(Field field : clazz.getDeclaredFields()) {
            
            field.setAccessible(true);
            final FieldAccessor accessor = adpterFactory.create(field);
            
            for(Annotation anno : work.getAnnoReader().getAnnotations(field)) {
                final SavingFieldProcessor<?> processor = config.getFieldProcessorRegistry().getSavingProcessor(anno.annotationType());
                
                if(processor != null) {
                    final FieldAccessorProxy accessorProxy = new FieldAccessorProxy(anno, processor, accessor);
                    if(!accessorProxies.contains(accessorProxy)) {
                        accessorProxies.add(accessorProxy);
                    }
                }
            }
            
        }
        
        // 順番を並び替えて保存処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy accessorProxy : accessorProxies) {
            accessorProxy.saveProcess(sheet, beanObj, config, work);
        }
        
        // リスナークラスの@PostSaveの取得
        if(listenerAnno != null) {
            final Object listenerObj = config.createBean(listenerAnno.listenerClass());
            for(Method method : listenerObj.getClass().getMethods()) {
                if(annoReader.hasAnnotation(method, XlsPostSave.class)) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, listenerObj, method));
                }
            }
            
        }
        
        //@PostSaveが付与されているメソッドの実行
        for(NeedProcess need : work.getNeedPostProcesses()) {
            Utils.invokeNeedProcessMethod(need.getProcess(), need.getMethod(), need.getTarget(), sheet, config, work.getErrors());
        }
        
    }
    
    public XlsMapperConfig getConfig() {
        return config;
    }
    
    public void setConfig(XlsMapperConfig config) {
        this.config = config;
    }
    
}
