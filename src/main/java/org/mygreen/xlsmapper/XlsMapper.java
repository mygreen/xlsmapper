package org.mygreen.xlsmapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * ExcelのシートとJavaオブジェクトをマッピングする機能を提供する。
 * 
 * @author T.TSUCHIE
 *
 */
public class XlsMapper {
    
    private XlsMapperConfig config;
    
    private XlsLoader loader;
    
    private XlsSaver saver;
    
    public XlsMapper() {
        this.config = new XlsMapperConfig();
        this.loader = new XlsLoader(getConig());
        this.saver = new XlsSaver(getConig());
    }
    
    public XlsMapperConfig getConig() {
        return config;
    }
    
    public void setConig(XlsMapperConfig config) {
        this.config = config;
        getLoader().setConfig(config);
        getSaver().setConfig(config);
    }
    
    public XlsLoader getLoader() {
        return loader;
    }
    
    public XlsSaver getSaver() {
        return saver;
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
        return loader.load(xlsIn, clazz);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn XMLによる定義を必要としない場合は、nullを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, xmlIn);
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
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final SheetBindingErrors errors) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, errors);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn XMLによる定義を必要としない場合は、nullを指定する。
     * @param errors マッピング時のエラー情報。指定しない場合は、nulを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn, final SheetBindingErrors errors) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, xmlIn, errors);
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
        return loader.loadMultiple(xlsIn, clazz);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param xmlIn
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, xmlIn);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param errorsContainer
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, errorsContainer);
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
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn,
            SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, xmlIn, errorsContainer);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, xmlIn);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, errorsContainer);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, xmlIn, errorsContainer);
    }
    
    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。
     * @param templateXlsIn 雛形となるExcelファイルの
     * @param xlsOut 出力
     * @param beansObj 書き込み元のオブジェクト
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beansObj) throws XlsMapperException, IOException {
        saver.save(templateXlsIn, xlsOut, beansObj);
    }
    
    /**
     * XMLによるマッピングを指定して、JavaのオブジェクトをExcelファイルに出力する。
     * @param templateXlsIn
     * @param xlsOut
     * @param beansObj
     * @param xmlIn
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beansObj, final InputStream xmlIn) throws XlsMapperException, IOException {
        saver.save(templateXlsIn, xlsOut, beansObj, xmlIn);
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
        saver.saveMultiple(templateXlsIn, xlsOut, beanObjs);
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
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs, final InputStream xmlIn) throws XlsMapperException, IOException {
        saver.saveMultiple(templateXlsIn, xlsOut, beanObjs, xmlIn);
    }
    
}
