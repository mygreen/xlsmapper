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

import com.gh.mygreen.xlsmapper.annotation.XlsFieldProcessor;
import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxy;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxyComparator;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MultipleSheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * JavaBeanをExcelのシートにマッピングし出力するクラス。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsSaver {

    private static final Logger logger = LoggerFactory.getLogger(XlsSaver.class);

    private Configuration configuration;

    /**
     * 独自のシステム情報を設定するコンストラクタ
     * @param configuration システム情報
     */
    public XlsSaver(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * デフォルトのコンストラクタ
     */
    public XlsSaver() {
        this(new Configuration());
    }

    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。</p>
     *
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力先のストリーム
     * @param beanObj 書き込むBeanオブジェクト
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObj == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beanObj)
            throws XlsMapperException, IOException {

        saveDetail(templateXlsIn, xlsOut, beanObj);
    }

    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。</p>
     *
     * @param <P> マッピング対象のクラスタイプ
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力先のストリーム
     * @param beanObj 書き込むBeanオブジェクト
     * @return マッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObj == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public <P> SheetBindingErrors<P> saveDetail(final InputStream templateXlsIn, final OutputStream xlsOut, final P beanObj)
            throws XlsMapperException, IOException {

        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notNull(beanObj, "beanObj");


        final AnnotationReader annoReader = new AnnotationReader(configuration.getAnnotationMapping().orElse(null));

        try(Workbook book = WorkbookFactory.create(templateXlsIn)) {

            final Class<?> clazz = beanObj.getClass();
            final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
            if(sheetAnno == null) {
                throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                        .varWithClass("property", clazz)
                        .varWithAnno("anno", XlsSheet.class)
                        .format());
    
            }
    
            final SheetBindingErrors<P> bindingResult;
            try {
                final Sheet[] xlsSheet = configuration.getSheetFinder().findForSaving(book, sheetAnno, annoReader, beanObj);
                bindingResult = saveSheet(xlsSheet[0], beanObj, annoReader);
    
            } catch(SheetNotFoundException e) {
                if(configuration.isIgnoreSheetNotFound()){
                    logger.warn(MessageBuilder.create("log.skipNotFoundSheet").format(), e);
                    return null;
    
                } else {
                    throw e;
                }
            }
    
            if(configuration.isFormulaRecalcurationOnSave()) {
                book.setForceFormulaRecalculation(true);
            }
    
            book.write(xlsOut);
    
            return bindingResult;
            
        } catch (InvalidFormatException e) {
            throw new XlsMapperException(MessageBuilder.create("file.faiiLoadTemplateExcel.notSupportType").format(), e);
        }

    }

    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut xlsOut 出力先のストリーム
     * @param beanObjs 書き込むオブジェクトの配列。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObjs == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs)
            throws XlsMapperException, IOException {

        saveMultipleDetail(templateXlsIn, xlsOut, beanObjs);
    }

    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut xlsOut 出力先のストリーム
     * @param beanObjs 書き込むオブジェクトの配列。
     * @return マッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、結果に含まれません。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObjs == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public MultipleSheetBindingErrors<Object> saveMultipleDetail(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs)
            throws XlsMapperException, IOException {

        ArgUtils.notNull(templateXlsIn, "templateXlsIn");
        ArgUtils.notNull(xlsOut, "xlsOut");
        ArgUtils.notEmpty(beanObjs, "beanObjs");

        final AnnotationReader annoReader = new AnnotationReader(configuration.getAnnotationMapping().orElse(null));

        final MultipleSheetBindingErrors<Object> multipleResult = new MultipleSheetBindingErrors<>();

        try(Workbook book = WorkbookFactory.create(templateXlsIn)) {

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
    
                try {
                    final Sheet[] xlsSheet = configuration.getSheetFinder().findForSaving(book, sheetAnno, annoReader, beanObj);
                    multipleResult.addBindingErrors(saveSheet(xlsSheet[0], beanObj, annoReader));
    
                } catch(SheetNotFoundException e) {
                    if(configuration.isIgnoreSheetNotFound()){
                        logger.warn(MessageBuilder.create("log.skipNotFoundSheet").format(), e);
                        continue;
                    } else {
                        throw e;
                    }
                }
            }
    
            if(configuration.isFormulaRecalcurationOnSave()) {
                book.setForceFormulaRecalculation(true);
            }
    
            book.write(xlsOut);
    
            return multipleResult;
            
        } catch (InvalidFormatException e) {
            throw new XlsMapperException(MessageBuilder.create("file.faiiLoadTemplateExcel.notSupportType").format(), e);
        }

    }

    /**
     * 任意のクラスのオブジェクトを、Excelシートにマッピングする。
     * @param sheet
     * @param beanObj
     * @param configuration
     * @param work
     * @throws XlsMapperException
     */
    private <P> SheetBindingErrors<P> saveSheet(final Sheet sheet, final P beanObj, final AnnotationReader annoReader)
            throws XlsMapperException {

        final Class<?> clazz = beanObj.getClass();

        final SheetBindingErrors<P> errors =  configuration.getBindingErrorsFactory().create(beanObj);
        errors.setSheetName(sheet.getSheetName());
        errors.setSheetIndex(sheet.getWorkbook().getSheetIndex(sheet));

        final SavingWorkObject work = new SavingWorkObject();
        work.setAnnoReader(annoReader);
        work.setErrors(errors);

        // セルのキャッシュ情報の初期化
        configuration.getCellFormatter().init(false);

        final FieldAccessorFactory adpterFactory = new FieldAccessorFactory(annoReader);

        // リスナークラスの@PreSave用メソッドの実行
        final XlsListener listenerAnno = annoReader.getAnnotation(beanObj.getClass(), XlsListener.class);
        if(listenerAnno != null) {
            for(Class<?> listenerClass : listenerAnno.value()) {
                final Object listenerObj = configuration.createBean(listenerClass);

                for(Method method : listenerObj.getClass().getMethods()) {
                    if(annoReader.hasAnnotation(method, XlsPreSave.class)) {
                        Utils.invokeNeedProcessMethod(listenerObj, method, beanObj, sheet, configuration, work.getErrors(), ProcessCase.Save);
                    }
                }
            }

        }

        // @PreSave用のメソッドの取得と実行
        for(Method method : clazz.getMethods()) {

            final XlsPreSave preProcessAnno = annoReader.getAnnotation(method, XlsPreSave.class);
            if(preProcessAnno != null) {
                Utils.invokeNeedProcessMethod(beanObj, method, beanObj, sheet, configuration, work.getErrors(), ProcessCase.Save);
            }
        }

        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();

        // public メソッドの処理
        for(Method method : clazz.getMethods()) {
            method.setAccessible(true);

            for(Annotation anno : annoReader.getAnnotations(method)) {

                final XlsFieldProcessor annoFieldProcessor = anno.annotationType().getAnnotation(XlsFieldProcessor.class);
                if(ClassUtils.isAccessorMethod(method) && annoFieldProcessor != null) {

                    // 登録済みのFieldProcessorの取得
                    FieldProcessor<?> processor = configuration.getFieldProcessorRegistry().getProcessor(anno.annotationType());

                    // アノテーションに指定されているFieldProcessorの場合
                    if(processor == null && annoFieldProcessor.value().length > 0) {
                        processor = configuration.createBean(annoFieldProcessor.value()[0]);

                    }

                    if(processor != null) {
                        final FieldAccessor accessor = adpterFactory.create(method);

                        final FieldAccessorProxy accessorProxy = new FieldAccessorProxy(anno, processor, accessor);
                        if(!accessorProxies.contains(accessorProxy)) {
                            accessorProxies.add(accessorProxy);
                        }

                    } else {
                        // FieldProcessorが見つからない場合
                        throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.XlsFieldProcessor.notResolve")
                                .varWithAnno("anno", anno.annotationType())
                                .format());
                    }

                }

                if(anno instanceof XlsPostSave) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, beanObj, method));
                }
            }
        }

        // フィールドの処理
        for(Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
            final FieldAccessor accessor = adpterFactory.create(field);

            for(Annotation anno : annoReader.getAnnotations(field)) {

                final XlsFieldProcessor annoFieldProcessor = anno.annotationType().getAnnotation(XlsFieldProcessor.class);
                if(annoFieldProcessor != null) {
                    // 登録済みのFieldProcessorの取得
                    FieldProcessor<?> processor = configuration.getFieldProcessorRegistry().getProcessor(anno.annotationType());

                    // アノテーションに指定されているFieldProcessorの場合
                    if(processor == null && annoFieldProcessor.value().length > 0) {
                        processor = configuration.createBean(annoFieldProcessor.value()[0]);
                    }

                    if(processor != null) {
                        final FieldAccessorProxy accessorProxy = new FieldAccessorProxy(anno, processor, accessor);
                        if(!accessorProxies.contains(accessorProxy)) {
                            accessorProxies.add(accessorProxy);
                        }

                    } else {
                        // FieldProcessorが見つからない場合
                        throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.XlsFieldProcessor.notResolve")
                                .varWithAnno("anno", anno.annotationType())
                                .format());
                    }

                }
            }

        }

        // 順番を並び替えて保存処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy accessorProxy : accessorProxies) {
            accessorProxy.saveProcess(sheet, beanObj, configuration, work);
        }

        // リスナークラスの@PostSaveの取得
        if(listenerAnno != null) {
            for(Class<?> listenerClass : listenerAnno.value()) {
                final Object listenerObj = configuration.createBean(listenerClass);
                for(Method method : listenerObj.getClass().getMethods()) {
                    if(annoReader.hasAnnotation(method, XlsPostSave.class)) {
                        work.addNeedPostProcess(new NeedProcess(beanObj, listenerObj, method));
                    }
                }
            }

        }

        //@PostSaveが付与されているメソッドの実行
        for(NeedProcess need : work.getNeedPostProcesses()) {
            Utils.invokeNeedProcessMethod(need.getProcess(), need.getMethod(), need.getTarget(), sheet, configuration, work.getErrors(), ProcessCase.Save);
        }

        return errors;

    }

    /**
     * システム情報を取得します。
     * @return 現在のシステム情報
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * システム情報を設定します。
     * @param configuration システム情報
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

}
