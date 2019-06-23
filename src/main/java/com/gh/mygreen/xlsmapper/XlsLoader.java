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

import com.gh.mygreen.xlsmapper.annotation.XlsFieldProcessor;
import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
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
 * ExcelのシートをJavaBeanにマッピングするクラス。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsLoader {

    private static final Logger logger = LoggerFactory.getLogger(XlsLoader.class);

    private Configuration configuration;

    /**
     * 独自のシステム情報を設定するコンストラクタ
     * @param configuration システム情報
     */
    public XlsLoader(final Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * デフォルトのコンストラクタ
     */
    public XlsLoader() {
        this(new Configuration());
    }

    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return シートをマッピングしたオブジェクト。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException Excelファイルのマッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     *
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz)  throws XlsMapperException, IOException {

        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");

        return loadDetail(xlsIn, clazz).getTarget();
    }

    /**
     * Excelファイルの1シートを読み込み、任意のクラスにマッピングする。
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return マッピングの詳細情報。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     *
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException Excelファイルのマッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public <P> SheetBindingErrors<P> loadDetail(final InputStream xlsIn, final Class<P> clazz)
            throws XlsMapperException, IOException {

        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");

        final AnnotationReader annoReader = new AnnotationReader(configuration.getAnnotationMapping().orElse(null));

        final XlsSheet sheetAnno = annoReader.getAnnotation(clazz, XlsSheet.class);
        if(sheetAnno == null) {
            throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                    .varWithClass("property", clazz)
                    .varWithAnno("anno", XlsSheet.class)
                    .format());
        }

        Workbook book = null;
        try {
            book = WorkbookFactory.create(xlsIn);

        } catch (InvalidFormatException e) {
            throw new XlsMapperException(MessageBuilder.create("file.failLoadExcel.notSupportType").format(), e);
        } finally {
            if(book != null) {
                book.close();
            }
        }

        try {
            final Sheet[] xlsSheet = configuration.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
            return loadSheet(xlsSheet[0], clazz, annoReader);

        } catch(SheetNotFoundException e) {
            if(configuration.isIgnoreSheetNotFound()){
                logger.warn(MessageBuilder.create("log.skipNotFoundSheet").format(), e);
                return null;

            } else {
                throw e;
            }
        }
    }

    /**
     * Excelファイルの同じ形式の複数シートを読み込み、任意のクラスにマップする。
     * <p>{@link XlsSheet#regex()}により、複数のシートが同じ形式で、同じクラスにマッピングすする際に使用します。</p>
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return マッピングした複数のシート。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    @SuppressWarnings("unchecked")
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {

        return loadMultipleDetail(xlsIn, clazz).getAll().stream()
                .map(s -> s.getTarget())
                .toArray(n -> (P[])Array.newInstance(clazz, n));
    }

    /**
     * Excelファイルの同じ形式の複数シートを読み込み、任意のクラスにマップする。
     * <p>{@link XlsSheet#regex()}により、複数のシートが同じ形式で、同じクラスにマッピングすする際に使用します。</p>
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return 複数のシートのマッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public <P> MultipleSheetBindingErrors<P> loadMultipleDetail(final InputStream xlsIn, final Class<P> clazz)
            throws XlsMapperException, IOException {

        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notNull(clazz, "clazz");

        final AnnotationReader annoReader = new AnnotationReader(configuration.getAnnotationMapping().orElse(null));

        final XlsSheet sheetAnno = annoReader.getAnnotation(clazz, XlsSheet.class);
        if(sheetAnno == null) {
            throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                    .varWithClass("property", clazz)
                    .varWithAnno("anno", XlsSheet.class)
                    .format());
        }

        final MultipleSheetBindingErrors<P> multipleResult = new MultipleSheetBindingErrors<>();

        Workbook book = null;
        try {
            book = WorkbookFactory.create(xlsIn);

        } catch (InvalidFormatException e) {
            throw new XlsMapperException(MessageBuilder.create("file.failLoadExcel.notSupportType").format(), e);
        } finally {
            if(book != null) {
                book.close();
            }
        }

        if(sheetAnno.number() == -1 && sheetAnno.name().isEmpty() && sheetAnno.regex().isEmpty()) {
            // 読み込むシートの条件が指定されていない場合、全て読み込む
            int sheetNum = book.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                final Sheet sheet = book.getSheetAt(i);

                multipleResult.addBindingErrors(loadSheet(sheet, clazz, annoReader));

            }

        } else {
            // 読み込むシートの条件が指定されている場合
            try {
                final Sheet[] xlsSheet = configuration.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
                for(Sheet sheet : xlsSheet) {
                    multipleResult.addBindingErrors(loadSheet(sheet, clazz, annoReader));

                }

            } catch(SheetNotFoundException e) {
                if(configuration.isIgnoreSheetNotFound()){
                    logger.warn(MessageBuilder.create("log.skipNotFoundSheet").format(), e);
                } else {
                    throw e;
                }
            }

        }

        return multipleResult;
    }

    /**
     * Excelファイルの異なる形式の複数シートを読み込み、任意のクラスにマップする。
     * <p>複数のシートの形式を一度に読み込む際に使用します。</p>
     *
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param classes マッピング先のクラスタイプの配列。
     * @return マッピングした複数のシート。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or classes == null}
     * @throws IllegalArgumentException {@literal calsses.length == 0}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes)
            throws XlsMapperException, IOException {
        return loadMultipleDetail(xlsIn, classes).getAll().stream()
                .map(s -> s.getTarget())
                .toArray();
    }

    /**
     * Excelファイルの異なる形式の複数シートを読み込み、任意のクラスにマップする。
     * <p>複数のシートの形式を一度に読み込む際に使用します。</p>
     *
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param classes マッピング先のクラスタイプの配列。
     * @return マッピングした複数のシートの結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or classes == null}
     * @throws IllegalArgumentException {@literal calsses.length == 0}
     * @throws IOException ファイルの読み込みに失敗した場合
     * @throws XlsMapperException マッピングに失敗した場合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public MultipleSheetBindingErrors<Object> loadMultipleDetail(final InputStream xlsIn, final Class<?>[] classes)
            throws XlsMapperException, IOException {

        ArgUtils.notNull(xlsIn, "xlsIn");
        ArgUtils.notEmpty(classes, "classes");

        final AnnotationReader annoReader = new AnnotationReader(configuration.getAnnotationMapping().orElse(null));

        final MultipleSheetBindingErrors<Object> multipleStore = new MultipleSheetBindingErrors<>();

        Workbook book = null;
        try {
            book = WorkbookFactory.create(xlsIn);

        } catch (InvalidFormatException e) {
            throw new XlsMapperException(MessageBuilder.create("file.failLoadExcel.notSupportType").format(), e);
        } finally {
            if(book != null) {
                book.close();
            }
        }

        for(Class<?> clazz : classes) {
            final XlsSheet sheetAnno = clazz.getAnnotation(XlsSheet.class);
            if(sheetAnno == null) {
                throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.notFound")
                        .varWithClass("property", clazz)
                        .varWithAnno("anno", XlsSheet.class)
                        .format());
            }

            try {
                final Sheet[] xlsSheet = configuration.getSheetFinder().findForLoading(book, sheetAnno, annoReader, clazz);
                for(Sheet sheet : xlsSheet) {
                    multipleStore.addBindingErrors(loadSheet(sheet, (Class)clazz, annoReader));

                }

            } catch(SheetNotFoundException ex){
                if(!configuration.isIgnoreSheetNotFound()){
                    logger.warn(MessageBuilder.create("log.skipNotFoundSheet").format(), ex);
                    throw ex;
                }
            }

        }

        return multipleStore;
    }

    /**
     * シートを読み込み、任意のクラスにマッピングする。
     * @param sheet シート情報
     * @param clazz マッピング先のクラスタイプ。
     * @param annoReader
     * @return シートのマッピング情報
     * @throws XlsMapperException
     *
     */
    private <P> SheetBindingErrors<P> loadSheet(final Sheet sheet, final Class<P> clazz, final AnnotationReader annoReader)
            throws XlsMapperException {

        // 値の読み込み対象のJavaBeanオブジェクトの作成
        final P beanObj = configuration.createBean(clazz);

        final SheetBindingErrors<P> errors =  configuration.getBindingErrorsFactory().create(beanObj);
        errors.setSheetName(sheet.getSheetName());
        errors.setSheetIndex(sheet.getWorkbook().getSheetIndex(sheet));

        final LoadingWorkObject work = new LoadingWorkObject();
        work.setAnnoReader(annoReader);
        work.setErrors(errors);

        // セルのキャッシュ情報の初期化
        configuration.getCellFormatter().init(configuration.isCacheCellValueOnLoad());

        final FieldAccessorFactory adpterFactory = new FieldAccessorFactory(annoReader);

        // リスナークラスの@PreLoad用メソッドの実行
        final XlsListener listenerAnno = annoReader.getAnnotation(beanObj.getClass(), XlsListener.class);
        if(listenerAnno != null) {
            for(Class<?> listenerClass : listenerAnno.value()) {
                final Object listenerObj = configuration.createBean(listenerClass);

                for(Method method : listenerObj.getClass().getMethods()) {
                    if(annoReader.hasAnnotation(method, XlsPreLoad.class)) {
                        Utils.invokeNeedProcessMethod(listenerObj, method, beanObj, sheet, configuration, work.getErrors(), ProcessCase.Load);
                    }
                }
            }

        }

        // @PreLoad用のメソッドの実行
        for(Method method : clazz.getMethods()) {

            if(annoReader.hasAnnotation(method, XlsPreLoad.class)) {
                Utils.invokeNeedProcessMethod(beanObj, method, beanObj, sheet, configuration, work.getErrors(), ProcessCase.Load);
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

                if(anno instanceof XlsPostLoad) {
                    work.addNeedPostProcess(new NeedProcess(beanObj, beanObj, method));
                }
            }

        }

        // フィールドの処理
        for(Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
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
                        final FieldAccessor accessor = adpterFactory.create(field);
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
            accessorProxy.loadProcess(sheet, beanObj, configuration, work);
        }

        // リスナークラスの@PostLoadの取得
        if(listenerAnno != null) {
            for(Class<?> listenerClass : listenerAnno.value()) {
                Object listenerObj = configuration.createBean(listenerClass);
                for(Method method : listenerObj.getClass().getMethods()) {
                    if(annoReader.hasAnnotation(method, XlsPostLoad.class)) {
                        work.addNeedPostProcess(new NeedProcess(beanObj, listenerObj, method));
                    }
                }
            }

        }

        //@PostLoadが付与されているメソッドの実行
        for(NeedProcess need : work.getNeedPostProcesses()) {
            Utils.invokeNeedProcessMethod(need.getProcess(), need.getMethod(), need.getTarget(), sheet, configuration, work.getErrors(), ProcessCase.Load);
        }

        // セルのキャッシュ情報の初期化
        configuration.getCellFormatter().init(configuration.isCacheCellValueOnLoad());

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
