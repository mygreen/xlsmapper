package com.gh.mygreen.xlsmapper.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * XMLの読み込み、書き込みなどを行うユーティリティクラス。
 * 
 * @version 1.1
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class XmlIO {
    
    /**
     * XMLを読み込み、{@link AnnotationMappingInfo}として取得する。
     * @param in
     * @return
     * @throws XmlOperateException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException in is null.
     */
    public static AnnotationMappingInfo load(final InputStream in) throws XmlOperateException {
        ArgUtils.notNull(in, "in");
        
        final AnnotationMappingInfo xmlInfo;
        
        try {
            xmlInfo = JAXB.unmarshal(in, AnnotationMappingInfo.class);
        } catch (DataBindingException e) {
            throw new XmlOperateException("fail load xml with JAXB.", e);
        }
        
        return xmlInfo;
    }
    
    /**
     * XMLを読み込み、{@link AnnotationMappingInfo}として取得する。
     * @since 0.5
     * @param reader
     * @return
     * @throws XmlOperateException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException in is null.
     */
    public static AnnotationMappingInfo load(final Reader reader) throws XmlOperateException {
        ArgUtils.notNull(reader, "reader");
        
        final AnnotationMappingInfo xmlInfo;
        
        try {
            xmlInfo = JAXB.unmarshal(reader, AnnotationMappingInfo.class);
        } catch (DataBindingException e) {
            throw new XmlOperateException("fail load xml with JAXB.", e);
        }
        
        return xmlInfo;
    }
    
    /**
     * XMLファイルを読み込み、{@link AnnotationMappingInfo}として取得する。
     * @param file 読み込むファイル
     * @param encoding 読み込むファイルの文字コード
     * @return 
     * @throws XmlOperateException XMLの読み込みに失敗した場合。
     * @throws IllegalArgumentException file is null or encoding is empty.
     */
    public static AnnotationMappingInfo load(final File file, final String encoding) throws XmlOperateException {
        ArgUtils.notNull(file, "file");
        ArgUtils.notEmpty(encoding, "encoding");
        
        final AnnotationMappingInfo xmlInfo;
        
        try(Reader reader = new InputStreamReader(new FileInputStream(file), encoding)) {
            xmlInfo = load(reader);
            
        } catch (IOException e) {
            throw new XmlOperateException(String.format("fail load xml file '%s'.", file.getPath()), e);
        }
        
        return xmlInfo;
    }
    
    /**
     * XMLをファイルに保存する。
     * @since 1.1
     * @param xmlInfo XML情報。
     * @param out
     * @throws XmlOperateException XMLの書き込みに失敗した場合。
     * @throws IllegalArgumentException xmlInfo is null.
     * @throws IllegalArgumentException writer is null.
     */
    public static void save(final AnnotationMappingInfo xmlInfo, final OutputStream out) throws XmlOperateException {
        ArgUtils.notNull(xmlInfo, "xmlInfo");
        ArgUtils.notNull(out, "out");
        
        try {
            JAXB.marshal(xmlInfo, out);
            
        } catch (DataBindingException e) {
            throw new XmlOperateException("fail save xml with JAXB.", e);
        }
        
    }
    
    /**
     * XMLをファイルに保存する。
     * @since 1.1
     * @param xmlInfo XML情報。
     * @param writer
     * @throws XmlOperateException XMLの書き込みに失敗した場合。
     * @throws IllegalArgumentException xmlInfo is null.
     * @throws IllegalArgumentException writer is null.
     */
    public static void save(final AnnotationMappingInfo xmlInfo, final Writer writer) throws XmlOperateException {
        ArgUtils.notNull(xmlInfo, "xmlInfo");
        ArgUtils.notNull(writer, "writer");
        
        try {
            JAXB.marshal(xmlInfo, writer);
            
        } catch (DataBindingException e) {
            throw new XmlOperateException("fail save xml with JAXB.", e);
        }
        
    }
    
    /**
     * XMLをファイルに保存する。
     * @since 1.1
     * @param xmlInfo XML情報。
     * @param file 書き込み先
     * @param encoding ファイルの文字コード。
     * @throws XmlOperateException XMLの書き込みに失敗した場合。
     * @throws IllegalArgumentException xmlInfo is null.
     * @throws IllegalArgumentException file is null or encoding is empty.
     */
    public static void save(final AnnotationMappingInfo xmlInfo, final File file, final String encoding) throws XmlOperateException {
        ArgUtils.notNull(xmlInfo, "xmlInfo");
        ArgUtils.notNull(file, "file");
        ArgUtils.notEmpty(encoding, "encoding");
        
        final Marshaller marshaller;
        try {
            JAXBContext context = JAXBContext.newInstance(xmlInfo.getClass());
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
        } catch (JAXBException e) {
            throw new XmlOperateException("fail setting JAXB context.", e);
        }
        
        File dir = file.getParentFile();
        if(dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
            
            marshaller.marshal(xmlInfo, writer);
            
        } catch(JAXBException | IOException e) {
            throw new XmlOperateException(String.format("fail save xml file '%s'.", file.getPath()), e);
        }
        
    }
    
}
