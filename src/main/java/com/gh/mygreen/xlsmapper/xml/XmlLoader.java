package com.gh.mygreen.xlsmapper.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;

/**
 * XMLに定義したアノテーション情報を読み込むクラス。
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class XmlLoader {
    
    /**
     * XMLを読み込み、{@link XmlInfo}として取得する。
     * @param in
     * @return
     * @throws XmlLoadException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException in is null.
     */
    public static XmlInfo load(final InputStream in) throws XmlLoadException {
        ArgUtils.notNull(in, "in");
        
        final XmlInfo xmlInfo;
        
        try {
            xmlInfo = JAXB.unmarshal(in, XmlInfo.class);
        } catch (DataBindingException e) {
            throw new XmlLoadException("fail load xml with JAXB.", e);
        }
        
        return xmlInfo;
    }
    
    /**
     * XMLを読み込み、{@link XmlInfo}として取得する。
     * @since 0.5
     * @param reader
     * @return
     * @throws XmlLoadException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException in is null.
     */
    public static XmlInfo load(final Reader reader) throws XmlLoadException {
        ArgUtils.notNull(reader, "reader");
        
        final XmlInfo xmlInfo;
        
        try {
            xmlInfo = JAXB.unmarshal(reader, XmlInfo.class);
        } catch (DataBindingException e) {
            throw new XmlLoadException("fail load xml with JAXB.", e);
        }
        
        return xmlInfo;
    }
    
    /**
     * XMLファイルを読み込み、{@link XmlInfo}として取得する。
     * @param file 読み込むファイル
     * @param encoding 読み込むファイルの文字コード
     * @return 
     * @throws XmlLoadException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException file is null or encoding is empty.
     */
    public static XmlInfo load(final File file, final String encoding) throws XmlLoadException {
        ArgUtils.notNull(file, "file");
        ArgUtils.notEmpty(encoding, "encoding");
        
        final XmlInfo xmlInfo;
        
        try(Reader reader = new InputStreamReader(new FileInputStream(file), encoding)) {
            xmlInfo = load(reader);
            
        } catch (IOException e) {
            throw new XmlLoadException(String.format("fail load xml file '%s'.", file.getPath()), e);
        }
        
        return xmlInfo;
    }
    
}
