package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.reflect.Method;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link RecordMethodCache}のインスタンスを作成するファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RecordMethodFacatory {

    private final AnnotationReader annoReader;

    private final Configuration config;

    /**
     * コンストラクタ
     * @param annoReader XMLで定義したアノテーション情報を提供するクラス。
     * @param config システム設定
     * @throws IllegalArgumentException {@literal annoReader == null or config == null.}
     */
    public RecordMethodFacatory(final AnnotationReader annoReader, final Configuration config) {
        ArgUtils.notNull(annoReader, "annoReader");
        ArgUtils.notNull(config, "config");

        this.annoReader = annoReader;
        this.config = config;
    }

    /**
     * レコードクラスを元に、{@link RecordMethodCache}のインスタンスを組み立てる。
     *
     * @param recordClass レコードクラス
     * @return {@link RecordMethodCache}のインスタンス
     * @throws IllegalArgumentException {@literal recordClass == null}
     */
    public RecordMethodCache create(final Class<?> recordClass) {

        ArgUtils.notNull(recordClass, "recordClass");

        final RecordMethodCache recordMethod = new RecordMethodCache();

        setupIgnoreableMethod(recordMethod, recordClass);
        setupListenerCallbackMethods(recordMethod, recordClass);
        setupRecordCallbackMethods(recordMethod, recordClass);

        return recordMethod;
    }

    /**
     * レコードの値を無視すると判定するためのメソッドの抽出
     * @param recordMethod メソッドの格納先
     * @param recordClass レコードクラス
     */
    private void setupIgnoreableMethod(final RecordMethodCache recordMethod, final Class<?> recordClass) {

        for(Method method : recordClass.getMethods()) {
            method.setAccessible(true);

            if(!annoReader.hasAnnotation(method, XlsIgnorable.class)) {
                continue;
            }

            if(method.getParameterCount() > 0) {
                continue;
            }

            if(!method.getReturnType().equals(Boolean.TYPE)) {
                continue;
            }

            recordMethod.ignoreableMethod = Optional.of(method);
            return;
        }
    }

    /**
     * リスナークラスに定義されているコールバックメソッドの抽出
     *
     * @param recordMethod メソッドの格納先
     * @param recordClass レコードクラス
     */
    private void setupListenerCallbackMethods(final RecordMethodCache recordMethod, final Class<?> recordClass) {

        // リスナーオブジェクトに定義されたコールバックメソッドの抽出
        final XlsListener listenerAnno = annoReader.getAnnotation(recordClass, XlsListener.class);
        if(listenerAnno == null) {
            return;
        }

        final Class<?>[] listenerClasses = listenerAnno.listenerClass();

        for(Class<?> listenerClass : listenerClasses) {

            ListenerClassCache listenerCache = new ListenerClassCache(config.createBean(listenerClass));
            recordMethod.lisnterClasses.add(listenerCache);

            for(Method method : listenerClass.getMethods()) {
                method.setAccessible(true);

                if(annoReader.hasAnnotation(method, XlsPreLoad.class)) {
                    listenerCache.preLoadMethods.add(method);

                } else if(annoReader.hasAnnotation(method, XlsPostLoad.class)) {
                    listenerCache.postLoadMethods.add(method);

                } else if(annoReader.hasAnnotation(method, XlsPreSave.class)) {
                    listenerCache.preSaveMethods.add(method);

                } else if(annoReader.hasAnnotation(method, XlsPostSave.class)) {
                    listenerCache.postSaveMethods.add(method);

                }

            }
        }

    }

    /**
     * レコードクラスに定義されているコールバックメソッドの抽出
     *
     * @param recordMethod メソッドの格納先
     * @param recordClass レコードクラス
     */
    private void setupRecordCallbackMethods(final RecordMethodCache recordMethod, final Class<?> recordClass) {

        for(Method method : recordClass.getMethods()) {
            method.setAccessible(true);

            if(annoReader.hasAnnotation(method, XlsPreLoad.class)) {
                recordMethod.preLoadMethods.add(method);

            } else if(annoReader.hasAnnotation(method, XlsPostLoad.class)) {
                recordMethod.postLoadMethods.add(method);

            } else if(annoReader.hasAnnotation(method, XlsPreSave.class)) {
                recordMethod.preSaveMethods.add(method);

            } else if(annoReader.hasAnnotation(method, XlsPostSave.class)) {
                recordMethod.postSaveMethods.add(method);

            }

        }

    }

}
