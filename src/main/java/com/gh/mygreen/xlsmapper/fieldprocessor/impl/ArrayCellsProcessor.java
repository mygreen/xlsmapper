package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayCells;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;

/**
 * アノテーション{@link XlsArrayCells}を処理するプロセッサ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellsProcessor extends AbstractFieldProcessor<XlsArrayCells> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final  XlsArrayCells anno, final FieldAccessor accessor,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

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
                    .varWithAnno("anno", XlsArrayCells.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());

        }

    }

    private List<Object> loadValues(final Sheet sheet, final Object beansObj, final XlsArrayCells anno,
            final FieldAccessor accessor, final Class<?> elementClass, final Configuration config,
            final LoadingWorkObject work) {

        final CellPosition initPosition = getCellPosition(accessor, anno);
        final CellConverter<?> converter = getCellConverter(elementClass, accessor, config);

        if(converter instanceof FieldFormatter) {
            work.getErrors().registerFieldFormatter(accessor.getName(), elementClass, (FieldFormatter<?>)converter, true);
        }

        ArrayCellsHandler arrayHandler = new ArrayCellsHandler(accessor, beansObj, elementClass, sheet, config);
        List<Object> result = arrayHandler.handleOnLoading(anno, initPosition, converter, work, anno.direction());

        return result;
    }

    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param accessor フィールド情報
     * @param anno アノテーション
     * @return 値が設定されているセルのアドレス
     * @throws AnnotationInvalidException アドレスの設定値が不正な場合
     */
    private CellPosition getCellPosition(final FieldAccessor accessor, final XlsArrayCells anno) throws AnnotationInvalidException {

        if(Utils.isNotEmpty(anno.address())) {
            try {
                return CellPosition.of(anno.address());
            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCells.class)
                        .var("attrName", "address")
                        .var("attrValue", anno.address())
                        .format());
            }

        } else {
            if(anno.row() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCells.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.row())
                        .var("min", 0)
                        .format());
            }

            if(anno.column() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCells.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.column())
                        .var("min", 0)
                        .format());

            }

            return CellPosition.of(anno.row(), anno.column());
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsArrayCells anno,
            final FieldAccessor accessor, final Configuration config, final SavingWorkObject work)
            throws XlsMapperException {

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
                    .varWithAnno("anno", XlsArrayCells.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }

    }

    @SuppressWarnings("rawtypes")
    private void saveRecords(final Sheet sheet, final XlsArrayCells anno, final FieldAccessor accessor,
            final Class<?> elementClass, final Object beansObj, final List<Object> result, final Configuration config,
            final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        final CellPosition initPosition = getCellPosition(accessor, anno);
        final CellConverter converter = getCellConverter(elementClass, accessor, config);

        ArrayCellsHandler arrayHandler = new ArrayCellsHandler(accessor, beansObj, elementClass, sheet, config);
        arrayHandler.handleOnSaving(result, anno, initPosition, converter, work, anno.direction());

    }

}
