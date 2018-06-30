package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.ArrayDirection;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledCellHandler.LabelInfo;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link XlsLabelledArrayCells}を処理するプロセッサ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LabelledArrayCellsProcessor extends AbstractFieldProcessor<XlsLabelledArrayCells> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsLabelledArrayCells anno,
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work)
            throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }

        final Class<?> clazz = accessor.getType();
        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> elementClass = anno.elementClass();
            if(elementClass == Object.class) {
                elementClass = accessor.getComponentType();
            }

            List<?> value = loadValues(sheet, beansObj, anno, accessor, elementClass, config, work);
            if(value != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                accessor.setValue(beansObj, collection);
            }

        } else if(clazz.isArray()) {

            Class<?> elementClass = anno.elementClass();
            if(elementClass == Object.class) {
                elementClass = accessor.getComponentType();
            }

            final List<?> value = loadValues(sheet, beansObj, anno, accessor, elementClass, config, work);
            if(value != null) {
                final Object array = Array.newInstance(elementClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }

                accessor.setValue(beansObj, array);
            }

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsLabelledArrayCells.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());

        }

    }

    private List<Object> loadValues(final Sheet sheet, final Object beansObj, final XlsLabelledArrayCells anno,
            final FieldAccessor accessor, final Class<?> elementClass, final Configuration config,
            final LoadingWorkObject work) {

        // マッピング対象のセル情報の取得
        LabelledCellHandler labelHandler = new LabelledCellHandler(accessor, sheet, config);
        Optional<LabelInfo> labelInfo = labelHandler.handle(anno, ProcessCase.Load);

        if(!labelInfo.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return null;
        }

        final CellPosition initPosition = labelInfo.get().valueAddress;
        final CellConverter<?> converter = getCellConverter(elementClass, accessor, config);

        validateAnnotation(accessor, anno);

        ArrayCellsHandler arrayHandler = new ArrayCellsHandler(accessor, beansObj, elementClass, sheet, config);
        arrayHandler.setLabel(labelInfo.get().label);

        List<Object> result = arrayHandler.handleOnLoading(anno, initPosition, converter, work, anno.direction());

        if(result != null) {
            // インデックスが付いていないラベルの設定
            accessor.setLabel(beansObj, labelInfo.get().label);
        }

        return result;

    }

    /**
     * アノテーションの設定値のチェック
     *
     * @param accessor フィールド情報
     * @param anno チェック対象のアノテーション
     */
    private void validateAnnotation(final FieldAccessor accessor, final XlsLabelledArrayCells anno) {

        // 左側のとき、水平方向の配列はサポートしない
        if(anno.type().equals(LabelledCellType.Left) && anno.direction().equals(ArrayDirection.Horizon)) {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.XlsLabelledArrayCell.notSupportTypeAndDirection")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsLabelledArrayCells.class)
                    .varWithEnum("typeValue", anno.type())
                    .varWithEnum("directionValue", anno.direction())
                    .format());
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsLabelledArrayCells anno,
            final FieldAccessor accessor, final Configuration config, final SavingWorkObject work)
            throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        final Class<?> clazz = accessor.getType();
        final Object result = accessor.getValue(beansObj);
        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> elementClass = anno.elementClass();
            if(elementClass == Object.class) {
                elementClass = accessor.getComponentType();
            }

            final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
            final List<Object> list = Utils.convertCollectionToList(value);
            saveRecords(sheet, anno, accessor, elementClass, beansObj, list, config, work);

        } else if(clazz.isArray()) {

            Class<?> elementClass = anno.elementClass();
            if(elementClass == Object.class) {
                elementClass = accessor.getComponentType();
            }

            final List<Object> list = Utils.asList(result, elementClass);
            saveRecords(sheet, anno, accessor, elementClass, beansObj, list, config, work);

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsLabelledArrayCells.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }

    }

    @SuppressWarnings("rawtypes")
    private void saveRecords(final Sheet sheet, final XlsLabelledArrayCells anno, final FieldAccessor accessor,
            final Class<?> elementClass, final Object beansObj, final List<Object> result, final Configuration config,
            final SavingWorkObject work) throws XlsMapperException {

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

        final CellPosition initPosition = labelInfo.get().valueAddress;
        final CellConverter converter = getCellConverter(elementClass, accessor, config);

        validateAnnotation(accessor, anno);

        ArrayCellsHandler arrayHandler = new ArrayCellsHandler(accessor, beansObj, elementClass, sheet, config);
        arrayHandler.setLabel(labelInfo.get().label);

        if(result != null) {
            // インデックスが付いていないラベルの設定
            accessor.setLabel(beansObj, labelInfo.get().label);
        }

        arrayHandler.handleOnSaving(result, anno, initPosition, converter, work, anno.direction());

    }

}
