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
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.SavingFieldProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;
import com.gh.mygreen.xlsmapper.xml.XMLInfo;
import com.gh.mygreen.xlsmapper.xml.XMLLoader;


/**
 * JavaBeanをExcelのシートにマッピングし出力するクラス。
 *
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
        
        // Xmls情報の出力
        XMLInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XMLLoader.load(xmlIn);
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
            throw new AnnotationInvalidException("Cannot finld annoation '@XlsSheet'", sheetAnno);
        }
        
        try {
            final org.apache.poi.ss.usermodel.Sheet[] xlsSheet = findSheet(book, sheetAnno, beanObj, annoReader);
            saveSheet(xlsSheet[0], beanObj, work);
        } catch(SheetNotFoundException e) {
            if(config.isIgnoreSheetNotFound()){
                logger.warn("skip loading by not-found sheet.", e);
                return;
            } else {
                throw e;
            }
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
        XMLInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XMLLoader.load(xmlIn);
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
            
            final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
            if(sheetAnno == null) {
                throw new AnnotationInvalidException("Cannot finld annoation '@XlsSheet'", sheetAnno);
            }
            
            final SavingWorkObject work = new SavingWorkObject();
            work.setAnnoReader(annoReader);
            
            try {
                final org.apache.poi.ss.usermodel.Sheet[] xlsSheet = findSheet(book, sheetAnno, beanObj, annoReader);
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
    @SuppressWarnings({"rawtypes"})
    private void saveSheet(final org.apache.poi.ss.usermodel.Sheet sheet, final Object beanObj,
            final SavingWorkObject work) throws XlsMapperException {
        
        final Class<?> clazz = beanObj.getClass();
        
        work.getErrors().setSheetName(sheet.getSheetName());
        
        // @PreSave用のメソッドの取得と実行
        for(Method method : clazz.getMethods()) {
            
            final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(beanObj.getClass(), method, XlsPreSave.class);
            if(preProcessAnno != null) {
                Utils.invokeNeedProcessMethod(method, beanObj, sheet, config, work.getErrors());
            }
        }
        
        final List<FieldAdaptorProxy> adaptorProxies = new ArrayList<>();
        
        // public メソッドの処理
        for(Method method : clazz.getMethods()) {
            method.setAccessible(true);
            for(Annotation anno : work.getAnnoReader().getAnnotations(clazz, method)) {
                final SavingFieldProcessor processor = config.getFieldProcessorRegistry().getSavingProcessor(anno);
                if((Utils.isGetterMethod(method) || Utils.isBooleanGetterMethod(method))&& processor != null) {
                    final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, work.getAnnoReader());
                    adaptorProxies.add(new FieldAdaptorProxy(anno, processor, adaptor));
                    break;
                    
                } else if(anno instanceof XlsPostSave) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, method));
                    break;
                }
            }
        }
        
        // public / private/ protected /default フィールドの処理
        for(Field field : clazz.getDeclaredFields()) {
            
            field.setAccessible(true);
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, field, work.getAnnoReader());
            
            //メソッドと重複している場合は排除する
            if(adaptorProxies.contains(adaptor)) {
                continue;
            }
            
            for(Annotation anno : work.getAnnoReader().getAnnotations(clazz, field)) {
                final SavingFieldProcessor processor = config.getFieldProcessorRegistry().getSavingProcessor(anno);
                if(processor != null) {
                    adaptorProxies.add(new FieldAdaptorProxy(anno, processor, adaptor));
                    break;
                }
            }
            
        }
        
        // 順番を並び替えて保存処理を実行する
        Collections.sort(adaptorProxies, HintOrderComparator.createForSaving());
        for(FieldAdaptorProxy adaptorProxy : adaptorProxies) {
            adaptorProxy.saveProcess(sheet, beanObj, config, work);
        }
        
        //@PostSaveが付与されているメソッドの実行
        for(NeedProcess need : work.getNeedPostProcesses()) {
            Utils.invokeNeedProcessMethod(need.getMethod(), need.getTarget(), sheet, config, work.getErrors());
        }
        
    }
    
    /**
     * {@code @XlsSheet}の値をもとにして、シートを取得する。
     * <p>正規表現にマッチする場合、アノテーション「@XlsSheet」で設定されているフィールド、またはGetterから取得できる名前で一致させる。
     * 
     * @param book Excelのワークブック。
     * @param sheetAnno アノテーション{@link XlsSheet}
     * @param obj
     * @param config
     * @param annoReader
     * @return Excelのシート情報。複数ヒットする場合は、該当するものを全て返す。
     * @throws SheetNotFoundException
     * @throws AnnotationInvalidException
     * @throws AnnotationReadException 
     */
    private org.apache.poi.ss.usermodel.Sheet[] findSheet(final Workbook book, final XlsSheet sheetAnno,
            final Object obj, final AnnotationReader annoReader) throws SheetNotFoundException, AnnotationInvalidException, AnnotationReadException {
        
        if(sheetAnno.name().length() > 0) {
            // シート名から取得する。
            final org.apache.poi.ss.usermodel.Sheet xlsSheet = book.getSheet(sheetAnno.name());
            if(xlsSheet == null) {
                throw new SheetNotFoundException(sheetAnno.name());
            }
            return new org.apache.poi.ss.usermodel.Sheet[]{ xlsSheet };
            
        } else if(sheetAnno.number() != -1) {
            // シート番号から取得する
            if(sheetAnno.number() >= book.getNumberOfSheets()) {
                throw new SheetNotFoundException(sheetAnno.number(), book.getNumberOfSheets());
            }
            
        } else if(sheetAnno.regex().length() > 0) {
            // シート名（正規表現）をもとにして、取得する。
            final String sheetNameValue;
            FieldAdaptor sheetNameField = getSheetNameField(obj, annoReader);
            if(sheetNameField != null) {
                sheetNameValue = sheetNameField.getValue(obj).toString();
            } else {
                throw new SheetNotFoundException(sheetAnno.regex());
            }
            
            final Pattern pattern = Pattern.compile(sheetAnno.regex());
            final List<org.apache.poi.ss.usermodel.Sheet> matches = new ArrayList<>();
            for(int i=0; i < book.getNumberOfSheets(); i++) {
                final org.apache.poi.ss.usermodel.Sheet xlsSheet = book.getSheetAt(i);
                if(pattern.matcher(xlsSheet.getSheetName()).matches()) {
                    
                    // オブジェクト中の@XslSheetNameで値が設定されている場合、Excelファイル中の一致するシートを元にする比較する
                    if(Utils.isNotEmpty(sheetNameValue) && xlsSheet.getSheetName().equals(sheetNameValue)) {
                        matches.add(xlsSheet);
                    } else {
                        matches.add(xlsSheet);
                    }
                }
            }
            
            if(matches.isEmpty()) {
                throw new SheetNotFoundException(sheetAnno.regex());
            }
            
            return matches.toArray(new org.apache.poi.ss.usermodel.Sheet[matches.size()]);
        }
        
        throw new AnnotationInvalidException("@Sheet requires name or number or regex parameter.", sheetAnno);
        
    }
    
    /**
     * アノテーション「@XlsSheetName」が付与されているフィールド／メソッドを取得する。
     * @param obj
     * @param config
     * @param annoReader
     * @return
     * @throws AnnotationReadException 
     */
    private FieldAdaptor getSheetNameField(final Object obj, final AnnotationReader annoReader) throws AnnotationReadException {
        
        Class<?> clazz = obj.getClass();
        for(Method method : clazz.getMethods()) {
//            method.setAccessible(true);
            if(!Utils.isGetterMethod(method)) {
                continue;
            }
            
            XlsSheetName sheetNameAnno = annoReader.getAnnotation(clazz, method, XlsSheetName.class);
            if(sheetNameAnno == null) {
                continue;
            }
            
            return new FieldAdaptor(clazz, method, annoReader);
        }
        
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            XlsSheetName sheetNameAnno = annoReader.getAnnotation(clazz, field, XlsSheetName.class);
            if(sheetNameAnno == null) {
                continue;
            }
            
            return new FieldAdaptor(clazz, field, annoReader);
        }
        
        // not found
        return null;
    }
    
    public XlsMapperConfig getConfig() {
        return config;
    }
    
    public void setConfig(XlsMapperConfig config) {
        this.config = config;
    }
}
