package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledCellHandler.LabelInfo;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;


/**
 * {@link XlsLabelledCell}を処理するFieldProcessor。
 *
 * @version 2.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class LabelledCellProcessor extends AbstractFieldProcessor<XlsLabelledCell> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsLabelledCell anno,
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }

        // マッピング対象のセル情報の取得
        LabelledCellHandler labelHandler = new LabelledCellHandler(accessor, sheet, config);
        Optional<LabelInfo> labelInfo = labelHandler.handle(anno, ProcessCase.Load);

        if(!labelInfo.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }

        accessor.setPosition(beansObj, labelInfo.get().valueAddress);
        accessor.setLabel(beansObj, labelInfo.get().label);
        
        accessor.getCommentSetter().ifPresent(setter -> 
                config.getCommentOperator().loadCellComment(setter, labelInfo.get().valueCell, beansObj, accessor, config));

        final CellConverter<?> converter = getCellConverter(accessor, config);
        if(converter instanceof FieldFormatter) {
            work.getErrors().registerFieldFormatter(accessor.getName(), accessor.getType(), (FieldFormatter<?>)converter, true);
        }

        try {
            final Object value = converter.toObject(labelInfo.get().valueCell);
            accessor.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, labelInfo.get().valueAddress, accessor.getName(), labelInfo.get().label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsLabelledCell anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        // マッピング対象のセル情報の取得
        LabelledCellHandler labelHandler = new LabelledCellHandler(accessor, sheet, config);
        Optional<LabelInfo> labelInfo = labelHandler.handle(anno, ProcessCase.Save);

        if(!labelInfo.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }

        accessor.setPosition(targetObj, labelInfo.get().valueAddress);
        accessor.setLabel(targetObj, labelInfo.get().label);
        
        accessor.getCommentGetter().ifPresent(getter -> 
                    config.getCommentOperator().saveCellComment(getter, POIUtils.getCell(sheet, labelInfo.get().valueAddress), 
                        targetObj, accessor, config));

        final CellConverter converter = getCellConverter(accessor, config);
        if(converter instanceof FieldFormatter) {
            work.getErrors().registerFieldFormatter(accessor.getName(), accessor.getType(), (FieldFormatter<?>)converter, true);
        }

        try {
            converter.toCell(accessor.getValue(targetObj), targetObj, sheet, labelInfo.get().valueAddress);

        } catch(TypeBindException e) {
            work.addTypeBindError(e, labelInfo.get().valueAddress, accessor.getName(), labelInfo.get().label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }

    }
}
