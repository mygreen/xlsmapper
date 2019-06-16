package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsComment;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * アノテーション {@link XlsComment} を処理するクラスです。
 *
 * @since 2.1
 * @author T.TSUCHIE
 */
public class CommentProcessor extends AbstractFieldProcessor<XlsComment> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsComment anno, final FieldAccessor accessor,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }
        
        validatePropertyType(accessor, anno);

        final CellPosition cellAddress = getCellPosition(accessor, anno);
        accessor.setPosition(beansObj, cellAddress);
        
        final Cell xlsCell = POIUtils.getCell(sheet, cellAddress);
        
        config.getCommentOperator().loadCellComment(
                (targetObj, comment) -> accessor.setValue(targetObj, comment),
                xlsCell, beansObj, accessor, config);
        
    }
    
    /**
     * プロパティのクラスタイプの検証。文字列以外はエラーとする。
     * @param accessor プロパティ情報
     * @param anno アノテーション
     */
    private void validatePropertyType(final FieldAccessor accessor, final XlsComment anno) {
        
        final Class<?> clazz = accessor.getType();
        if(!String.class.isAssignableFrom(clazz)) {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsComment.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "String")
                    .format());
        }
    }

    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param accessor フィールド情報
     * @param anno アノテーション
     * @return 値が設定されているセルのアドレス
     * @throws AnnotationInvalidException アドレスの設定値が不正な場合
     */
    private CellPosition getCellPosition(final FieldAccessor accessor, final XlsComment anno) throws AnnotationInvalidException {

        if(Utils.isNotEmpty(anno.address())) {
            try {
                return CellPosition.of(anno.address());
            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsComment.class)
                        .var("attrName", "address")
                        .var("attrValue", anno.address())
                        .format());
            }

        } else {
            if(anno.row() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsComment.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.row())
                        .var("min", 0)
                        .format());
            }

            if(anno.column() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsComment.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.column())
                        .var("min", 0)
                        .format());

            }

            return CellPosition.of(anno.row(), anno.column());
        }

    }

    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsComment anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }
        
        validatePropertyType(accessor, anno);

        final CellPosition cellAddress = getCellPosition(accessor, anno);
        final Cell xlsCell = POIUtils.getCell(sheet, cellAddress);
        accessor.setPosition(targetObj, cellAddress);
        
        config.getCommentOperator().saveCellComment((beanObj) -> Optional.ofNullable((String)accessor.getValue(beanObj)),
                xlsCell, targetObj, accessor, config);
        
    }

}
