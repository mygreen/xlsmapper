package com.gh.mygreen.xlsmapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
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
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;
import com.gh.mygreen.xlsmapper.xml.XmlIO;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;


/**
 * ExcelのシートをJavaBeanにマッピングするクラス。
 * 
 * @version 1.4.4
 * @author T.TSUCHIE
 *
 */
public class XlsLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(XlsLoader.class); 
    
    private XlsMapperConfig config;
    
    public XlsLoader(final XlsMapperConfig config) {
        this.config = config;
    }
    
    public XlsLoader() {
        this(new XlsMapperConfig());
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     * 
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return load(xlsIn, clazz, null, null);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn アノテーションの定義をしているXMLファイルの入力。指定しない場合は、nullを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return load(xlsIn, clazz, xmlIn, null);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param errors エラー内容
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     * 
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final SheetBindingErrors errors) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return load(xlsIn, clazz, null, errors);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn アノテーションの定義をしているXMLファイルの入力。指定しない場合は、nullを指定する。
     * @param errors マッピング時のエラー情報。指定しない場合は、nulを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn, 
            final SheetBindingErrors errors)
            throws XlsMapperException, IOException {
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        XmlInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XmlIO.load(xmlIn);
        }
        
        final LoadingWorkObject work = new LoadingWorkObject();
        
        final AnnotationReader annoReader = new AnnotationReader(xmlInfo);
        work.setAnnoReader(annoReader);
        
        if(errors != null) {
            work.setErrors(errors);
        } else {
            work.setErrors(new SheetBindingErrors(clazz));
        }
        
        final XlsSheet sheetAnno = annoReader.getAnnotation(clazz, XlsSheet.class);
        if(sheetAnno == null) {
            throw new AnnotationInvalidException(String.format("With '%s', cannot find annoation '@XlsSheet'",
                    clazz.getName()), sheetAnno);
        }
        
        final Workbook book;
        try {
            book = WorkbookFactory.create(xlsIn);
            
        } catch (InvalidFormatException e) {
            throw new XlsMapperException("fail load Excel File", e);
        }
        
        try {
            final Sheet[] xlsSheet = config.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
            return loadSheet(xlsSheet[0], clazz, work);
        } catch(SheetNotFoundException e) {
            if(config.isIgnoreSheetNotFound()){
                logger.warn("skip loading by not-found sheet.", e);
                return null;
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return loadMultiple(xlsIn, clazz, null, null);
    }
    
    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param xmlIn
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return loadMultiple(xlsIn, clazz, xmlIn, null);
    }
    
    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param errorsContainer
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        return loadMultiple(xlsIn, clazz, null, errorsContainer);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param xmlIn
     * @param errorsContainer
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");
        
        XmlInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XmlIO.load(xmlIn);
        }
        
        final AnnotationReader annoReader = new AnnotationReader(xmlInfo);
        
        final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
        if(sheetAnno == null) {
            throw new AnnotationInvalidException(String.format("With '%s', cannot finld annoation '@XlsSheet'.",
                    clazz.getName()), sheetAnno);
        }
        
        final SheetBindingErrorsContainer container;
        if(errorsContainer != null) {
            container = errorsContainer;
        } else {
            container = new SheetBindingErrorsContainer(clazz);
        }
        
        final Workbook book;
        try {
            book = WorkbookFactory.create(xlsIn);
            
        } catch (InvalidFormatException e) {
            throw new XlsMapperException("fail load Excel File", e);
        }
        
        final List<P> list = new ArrayList<P>();
        
        if(sheetAnno.number() == -1 && sheetAnno.name().isEmpty() && sheetAnno.regex().isEmpty()) {
            // 読み込むシートの条件が指定されていない場合、全て読み込む
            int sheetNum = book.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                final Sheet sheet = book.getSheetAt(i);
                
                final LoadingWorkObject work = new LoadingWorkObject();
                work.setAnnoReader(annoReader);
                work.setErrors(container.findBindingResult(i));
                list.add(loadSheet(sheet, clazz, work));
            }
            
        } else {
            // 読み込むシートの条件が指定されている場合
            try {
                final Sheet[] xlsSheet = config.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
                for(Sheet sheet : xlsSheet) {
                    
                    final LoadingWorkObject work = new LoadingWorkObject();
                    work.setAnnoReader(annoReader);
                    work.setErrors(container.findBindingResult(list.size()));
                    list.add(loadSheet(sheet, clazz, work));
                }
                
            } catch(SheetNotFoundException e) {
                if(config.isIgnoreSheetNotFound()){
                    logger.warn("skip loading by not-found sheet.", e);
                } else {
                    throw e;
                }
            }
            
        }
        
        return list.toArray((P[])Array.newInstance(clazz, list.size()));
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes) throws XlsMapperException {
        return loadMultiple(xlsIn, classes, null, null);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn) throws XlsMapperException {
        return loadMultiple(xlsIn, classes, xmlIn, null);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        return loadMultiple(xlsIn, classes, null, errorsContainer);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn,
            SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        
        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notEmpty(classes, "clazz");
        
        XmlInfo xmlInfo = null;
        if(xmlIn != null) {
            xmlInfo = XmlIO.load(xmlIn);
        }
        
        final AnnotationReader annoReader = new AnnotationReader(xmlInfo);
        
        final SheetBindingErrorsContainer container;
        if(errorsContainer != null) {
            container = errorsContainer;
        } else {
            container = new SheetBindingErrorsContainer(classes);
        }
        
        final Workbook book;
        try {
            book = WorkbookFactory.create(xlsIn);
            
        } catch (InvalidFormatException | IOException e) {
            throw new XlsMapperException("fail load Excel File", e);
        }
        
        final List<Object> list = new ArrayList<Object>();
        for(Class<?> clazz : classes) {
            final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
            if(sheetAnno == null) {
                throw new AnnotationInvalidException(String.format("With '%s', cannot finld annoation '@XlsSheet'.",
                        clazz.getName()), sheetAnno);
            }
            
            try {
                final Sheet[] xlsSheet = config.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
                for(Sheet sheet : xlsSheet) {
                    
                    final LoadingWorkObject work = new LoadingWorkObject();
                    work.setAnnoReader(annoReader);
                    work.setErrors(container.findBindingResult(list.size()));
                    list.add(loadSheet(sheet, clazz, work));
                    
                } 
            } catch(SheetNotFoundException ex){
                if(!config.isIgnoreSheetNotFound()){
                    logger.warn("skip loading by not-found sheet.", ex);
                    throw ex;
                }
            }
            
        }
        
        return list.toArray();
    }
    
    /**
     * シートを読み込み、任意のクラスにマッピングする。
     * @param sheet シート情報
     * @param clazz マッピング先のクラスタイプ。
     * @param work 
     * @return
     * @throws Exception 
     * 
     */
    @SuppressWarnings({"rawtypes"})
    private <P> P loadSheet(final Sheet sheet, final Class<P> clazz, final LoadingWorkObject work) throws XlsMapperException {
        
        // 値の読み込み対象のJavaBeanオブジェクトの作成
        final P beanObj = config.createBean(clazz);
        
        work.getErrors().setSheetName(sheet.getSheetName());
        
        final List<FieldAdaptorProxy> adaptorProxies = new ArrayList<>();
        
        // リスナークラスの@PreLoadd用メソッドの実行
        final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(beanObj.getClass(), XlsListener.class);
        if(listenerAnno != null) {
            Object listenerObj = config.createBean(listenerAnno.listenerClass());
            for(Method method : listenerObj.getClass().getMethods()) {
                final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPreLoad.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(listenerObj, method, beanObj, sheet, config, work.getErrors());
                }
            }
            
        }
        
        // @PreLoad用のメソッドの実行
        for(Method method : clazz.getMethods()) {
            
            final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(beanObj.getClass(), method, XlsPreLoad.class);
            if(preProcessAnno != null) {
                Utils.invokeNeedProcessMethod(beanObj, method, beanObj, sheet, config, work.getErrors());
            }
        }
        
        // public メソッドの処理
        for(Method method : clazz.getMethods()) {
            method.setAccessible(true);
            
            for(Annotation anno : work.getAnnoReader().getAnnotations(clazz, method)) {
                final LoadingFieldProcessor processor = config.getFieldProcessorRegistry().getLoadingProcessor(anno);
                if(Utils.isSetterMethod(method) && processor != null) {
                    final FieldAdaptor adaptor = new FieldAdaptor(clazz, method, work.getAnnoReader());
                    adaptorProxies.add(new FieldAdaptorProxy(anno, processor, adaptor));
                    
                } else if(anno instanceof XlsPostLoad) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, beanObj, method));
                }
            }
            
        }
        
        // public / private / protected / default フィールドの処理
        for(Field field : clazz.getDeclaredFields()) {
            
            field.setAccessible(true);
            final FieldAdaptor adaptor = new FieldAdaptor(clazz, field, work.getAnnoReader());
            
            // メソッドを重複している場合は排除する。
            if(adaptorProxies.contains(adaptor)) {
                continue;
            }
            
            for(Annotation anno : work.getAnnoReader().getAnnotations(clazz, field)) {
                final LoadingFieldProcessor processor = config.getFieldProcessorRegistry().getLoadingProcessor(anno);
                if(processor != null) {
                    adaptorProxies.add(new FieldAdaptorProxy(anno, processor, adaptor));
                }
            }
        }
        
        // 順番を並び替えて保存処理を実行する
        Collections.sort(adaptorProxies, HintOrderComparator.createForLoading());
        for(FieldAdaptorProxy adaptorProxy : adaptorProxies) {
            adaptorProxy.loadProcess(sheet, beanObj, config, work);
        }
        
        // リスナークラスの@PostLoadの取得
        if(listenerAnno != null) {
            Object listenerObj = config.createBean(listenerAnno.listenerClass());
            for(Method method : listenerObj.getClass().getMethods()) {
                final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPostLoad.class);
                if(postProcessAnno != null) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, listenerObj, method));
                }
            }
            
        }
        
        //@PostLoadが付与されているメソッドの実行
        for(NeedProcess need : work.getNeedPostProcesses()) {
            Utils.invokeNeedProcessMethod(need.getProcess(), need.getMethod(), need.getTarget(), sheet, config, work.getErrors());
        }
        
        return beanObj;
    }
    
    public XlsMapperConfig getConfig() {
        return config;
    }
    
    public void setConfig(XlsMapperConfig config) {
        this.config = config;
    }
    
}
