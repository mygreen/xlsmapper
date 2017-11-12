package com.gh.mygreen.xlsmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * 読み込み時/書き込み処理時のシートを取得するクラス。
 * <p>アノテーション{@link XlsSheet}の設定値に従いシートを取得する。
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class SheetFinder {
    
    /**
     * 読み込み時のシートを取得する。
     * 
     * @param workbook Excelのワークブック。
     * @param sheetAnno JavaBeanのクラスに付与されているアノテーション{@link XlsSheet}。
     * @param annoReader
     * @param beanClass JavaBeanのクラス。
     * @return Excelのシート情報。複数ヒットする場合は、該当するものを全て返す。
     * @throws SheetNotFoundException 該当のシートが見つからない場合にスローする。
     * @throws AnnotationInvalidException アノテーションの使用方法が不正な場合
     * @throws AnnotationReadException アノテーションをXMLで指定する方法が不正な場合。
     */
    public Sheet[] findForLoading(final Workbook workbook, final XlsSheet sheetAnno,
            final AnnotationReader annoReader, final Class<?> beanClass)
                    throws SheetNotFoundException, AnnotationInvalidException, AnnotationReadException {
        
        if(sheetAnno.name().length() > 0) {
            // シート名から取得する。
            final Sheet xlsSheet = workbook.getSheet(sheetAnno.name());
            if(xlsSheet == null) {
                throw new SheetNotFoundException(sheetAnno.name());
            }
            return new Sheet[]{ xlsSheet };
            
        } else if(sheetAnno.number() >= 0) {
            // シート番号から取得する
            if(sheetAnno.number() >= workbook.getNumberOfSheets()) {
                throw new SheetNotFoundException(sheetAnno.number(), workbook.getNumberOfSheets());
            }
            
            return new Sheet[]{ workbook.getSheetAt(sheetAnno.number()) }; 
            
        } else if(sheetAnno.regex().length() > 0) {
            // シート名（正規表現）をもとにして、取得する。
            final Pattern pattern = Pattern.compile(sheetAnno.regex());
            final List<Sheet> matches = new ArrayList<>();
            for(int i=0; i < workbook.getNumberOfSheets(); i++) {
                final Sheet xlsSheet = workbook.getSheetAt(i);
                if(pattern.matcher(xlsSheet.getSheetName()).matches()) {
                    matches.add(xlsSheet);
                }
            }
            
            if(matches.isEmpty()) {
                throw new SheetNotFoundException(sheetAnno.regex());
            }
            
            return matches.toArray(new Sheet[matches.size()]);
        }
        
        throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.attr.required.any")
                .varWithClass("property", beanClass)
                .varWithAnno("anno", XlsSheet.class)
                .varWithArrays("attrNames", "name", "number", "regex")
                .format());
    }
    
    /**
     * 書き込み時のシートを取得する。
     * @param workbook Excelのワークブック。
     * @param sheetAnno JavaBeanのクラスに付与されているアノテーション{@link XlsSheet}。
     * @param annoReader
     * @param beanObj JavaBeanのオブジェクト。
     * @return Excelのシート情報。複数ヒットする場合は、該当するものを全て返す。
     * @throws SheetNotFoundException 該当のシートが見つからない場合にスローする。
     * @throws AnnotationInvalidException アノテーションの使用方法が不正な場合
     * @throws AnnotationReadException アノテーションをXMLで指定する方法が不正な場合。
     */
    public Sheet[] findForSaving(final Workbook workbook, final XlsSheet sheetAnno,
            final AnnotationReader annoReader, final Object beanObj)
                    throws SheetNotFoundException, AnnotationInvalidException, AnnotationReadException {
        
        if(sheetAnno.name().length() > 0) {
            // シート名から取得する。
            final Sheet xlsSheet = workbook.getSheet(sheetAnno.name());
            if(xlsSheet == null) {
                throw new SheetNotFoundException(sheetAnno.name());
            }
            return new Sheet[]{ xlsSheet };
            
        } else if(sheetAnno.number() >= 0) {
            // シート番号から取得する
            if(sheetAnno.number() >= workbook.getNumberOfSheets()) {
                throw new SheetNotFoundException(sheetAnno.number(), workbook.getNumberOfSheets());
            }
            
            return new Sheet[]{ workbook.getSheetAt(sheetAnno.number()) };
            
        } else if(sheetAnno.regex().length() > 0) {
            // シート名（正規表現）をもとにして、取得する。
            String sheetNameValue = null;
            Optional<FieldAccessor> sheetNameField = getSheetNameField(beanObj, annoReader);
            if(sheetNameField.isPresent()) {
                sheetNameValue = (String)sheetNameField.get().getValue(beanObj);
            }
            
            final Pattern pattern = Pattern.compile(sheetAnno.regex());
            final List<Sheet> matches = new ArrayList<>();
            for(int i=0; i < workbook.getNumberOfSheets(); i++) {
                final Sheet xlsSheet = workbook.getSheetAt(i);
                if(pattern.matcher(xlsSheet.getSheetName()).matches()) {
                    
                    // オブジェクト中の@XslSheetNameで値が設定されている場合、Excelファイル中の一致するシートを元にする比較する
                    if(Utils.isNotEmpty(sheetNameValue) && xlsSheet.getSheetName().equals(sheetNameValue)) {
                        return new Sheet[]{ xlsSheet };
                        
                    }
                    
                    matches.add(xlsSheet);
                }
            }
            
            if(sheetNameValue != null && !matches.isEmpty()) {
                // シート名が直接指定の場合
                throw new SheetNotFoundException(sheetNameValue);
                
            } else if(matches.isEmpty()) {
                throw new SheetNotFoundException(sheetAnno.regex());
                
            } else if(matches.size() == 1) {
                // １つのシートに絞り込めた場合
                return new Sheet[]{ matches.get(0) };
                
            } else {
                // 複数のシートがヒットした場合
                List<String> names = new ArrayList<>();
                for(Sheet sheet : matches) {
                    names.add(sheet.getSheetName());
                }
                throw new SheetNotFoundException(sheetAnno.regex(),
                        MessageBuilder.create("sheet.regexMultipleHit")
                            .var("regex", sheetAnno.regex())
                            .var("names", names)
                            .format());
            }
        }
        
        throw new AnnotationInvalidException(sheetAnno, MessageBuilder.create("anno.attr.required.any")
                .varWithClass("property", beanObj.getClass())
                .varWithAnno("anno", XlsSheet.class)
                .varWithArrays("attrNames", "name", "number", "regex")
                .format());
        
    }
    
    /**
     * アノテーション「@XlsSheetName」が付与されているフィールド／メソッドを取得する。
     * @param beanObj
     * @param config
     * @param annoReader
     * @return 見つからない場合、空を返す。
     * @throws AnnotationReadException 
     */
    private Optional<FieldAccessor> getSheetNameField(final Object beanObj, final AnnotationReader annoReader) throws AnnotationReadException {
        
        FieldAccessorFactory adpterFactory = new FieldAccessorFactory(annoReader);
        
        Class<?> clazz = beanObj.getClass();
        for(Method method : clazz.getMethods()) {
            method.setAccessible(true);
            if(!ClassUtils.isGetterMethod(method)) {
                continue;
            }
            
            if(!annoReader.hasAnnotation(method, XlsSheetName.class)) {
                continue;
            }
            
            return Optional.of(adpterFactory.create(method));
        }
        
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if(!annoReader.hasAnnotation(field, XlsSheetName.class)) {
                continue;
            }
            
            return Optional.of(adpterFactory.create(field));
            
        }
        
        // not found
        return Optional.empty();
    }
    
}
