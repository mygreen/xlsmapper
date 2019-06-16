package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledComment;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link XlsLabelledComment}を処理するFieldProcessor。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LabelledCommentProcessor extends AbstractFieldProcessor<XlsLabelledComment> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsLabelledComment anno,
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }
        
        validatePropertyType(accessor, anno);
        
        // マッピング対象のセル情報の取得
        LabelledCellHandler labelHandler = new LabelledCellHandler(accessor, sheet, config);
        Optional<CellPosition> labelAddress = labelHandler.getLabelPosition(anno);

        if(!labelAddress.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }

        final Cell xlsCell = POIUtils.getCell(sheet, labelAddress.get());
        accessor.setPosition(beansObj, labelAddress.get());
        accessor.setLabel(beansObj, config.getCellFormatter().format(xlsCell));
        
        config.getCommentOperator().loadCellComment(
                (targetObj, comment) -> accessor.setValue(targetObj, comment),
                xlsCell, beansObj, accessor, config);
        
    }
    
    /**
     * プロパティのクラスタイプの検証。文字列以外はエラーとする。
     * @param accessor プロパティ情報
     * @param anno アノテーション
     */
    private void validatePropertyType(final FieldAccessor accessor, final XlsLabelledComment anno) {
        
        final Class<?> clazz = accessor.getType();
        if(!String.class.isAssignableFrom(clazz)) {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsLabelledComment.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "String")
                    .format());
        }
        
    }

    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsLabelledComment anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }
        
        validatePropertyType(accessor, anno);
        
        // マッピング対象のセル情報の取得
        LabelledCellHandler labelHandler = new LabelledCellHandler(accessor, sheet, config);
        Optional<CellPosition> labelAddress = labelHandler.getLabelPosition(anno);

        if(!labelAddress.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        
        final Cell xlsCell = POIUtils.getCell(sheet, labelAddress.get());
        accessor.setPosition(targetObj, labelAddress.get());
        accessor.setLabel(targetObj, config.getCellFormatter().format(xlsCell));
        
        config.getCommentOperator().saveCellComment((beanObj) -> Optional.ofNullable((String)accessor.getValue(beanObj)),
                xlsCell, targetObj, accessor, config);

    }
}
