package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.annotation.ArrayDirection;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayOption;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * 配列やリスト形式の要素を処理するためのクラス。
 * 
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellsHandler {

    private final FieldAccessor field;

    private final Object beansObj;

    /**
     * リストや配列の要素のクラスタイプ
     */
    private final Class<?> elementClass;

    private final Sheet sheet;

    private final Configuration config;

    /**
     * 見出しの設定 - 見出しがある場合のみ設定する
     */
    private String label;

    public ArrayCellsHandler(final FieldAccessor field, final Object beansObj, final Class<?> elementClass,
            final Sheet sheet, final Configuration config) {
        this.field = field;
        this.beansObj = beansObj;
        this.elementClass = elementClass;
        this.sheet = sheet;
        this.config = config;
    }

    /**
     * 汎用的にアノテーションの属性にアクセスするためのクラス。
     *
     */
    private static class AnnotationProxy {

        private final Annotation target;

        public AnnotationProxy(final Annotation target) {
            ArgUtils.notNull(target, "target");
            this.target = target;
        }

        /**
         * 対象となるアノテーションを取得する
         * @return アノテーションのインスタンス。
         */
        public Annotation getTarget() {
            return target;
        }

        /**
         * アノテーションのクラスタイプを取得する。
         * @return アノテーションのクラスタイプ
         */
        public Class<? extends Annotation> annotationType() {
            return target.annotationType();
        }

        /**
         * 連続するセルの個数を指定します。
         * @return アノテーションの属性「size」の値。存在しない場合、{@literal -1} を返す。
         */
        public int size() {
            return ClassUtils.getAnnotationAttribute(target, "size", int.class).orElse(-1);
        }

        /**
         * 値のセルが結合しているかどうか考慮するかどうか指定します。
         * @return アノテーションの属性「elementMerged」の値
         */
        public boolean elementMerged() {
            return ClassUtils.getAnnotationAttribute(target, "elementMerged", boolean.class).orElse(true);
        }

    }

    public List<Object> handleOnLoading(final Annotation anno, final CellPosition initPosition, final CellConverter<?> converter,
            final LoadingWorkObject work, final ArrayDirection direction) {

        final AnnotationProxy annoProxy = new AnnotationProxy(anno);

        // 属性sizeの値のチェック
        if(annoProxy.size() <= 0) {
            throw new AnnotationInvalidException(annoProxy.getTarget(), MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", annoProxy.annotationType())
                    .var("attrName", "size")
                    .var("attrValue", annoProxy.size())
                    .var("min", 1)
                    .format());
        }

        final List<Object> result = new ArrayList<>();

        if(direction.equals(ArrayDirection.Horizon)) {
            int column = initPosition.getColumn();
            int row = initPosition.getRow();

            for(int i=0; i < annoProxy.size(); i++) {

                final CellPosition cellAddress = CellPosition.of(row, column);
                final Cell cell = POIUtils.getCell(sheet, cellAddress);

                field.setArrayPosition(beansObj, cellAddress, i);

                if(Utils.isNotEmpty(label)) {
                    field.setArrayLabel(beansObj, label, i);
                }
                
                final int comemntIndex = i;
                field.getArrayCommentSetter().ifPresent(setter -> 
                    config.getCommentOperator().loadArrayCellComment(setter, cell, beansObj, comemntIndex, field, config));


                try {
                    final Object value = converter.toObject(cell);
                    result.add(value);

                } catch(TypeBindException e) {
                    work.addTypeBindError(e, cellAddress, field.getName(), label);
                    if(!config.isContinueTypeBindFailure()) {
                        throw e;
                    } else {
                        // 処理を続ける場合は、nullなどを入れる
                        result.add(Utils.getPrimitiveDefaultValue(elementClass));
                    }
                }

                if(annoProxy.elementMerged()) {
                    // 結合を考慮する場合
                    final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                    if(mergedRegion != null) {
                        column += POIUtils.getColumnSize(mergedRegion);
                    } else {
                        column++;
                    }
                } else {
                    column++;
                }

            }

        } else if(direction.equals(ArrayDirection.Vertical)) {
            int column = initPosition.getColumn();
            int row = initPosition.getRow();

            for(int i=0; i < annoProxy.size(); i++) {

                final CellPosition cellAddress = CellPosition.of(row, column);
                final Cell cell = POIUtils.getCell(sheet, cellAddress);

                field.setArrayPosition(beansObj, cellAddress, i);

                if(Utils.isNotEmpty(label)) {
                    field.setArrayLabel(beansObj, label, i);
                }
                
                final int comemntIndex = i;
                field.getArrayCommentSetter().ifPresent(setter -> 
                    config.getCommentOperator().loadArrayCellComment(setter, cell, beansObj, comemntIndex, field, config));

                try {
                    final Object value = converter.toObject(cell);
                    result.add(value);

                } catch(TypeBindException e) {
                    work.addTypeBindError(e, cellAddress, field.getName(), label);
                    if(!config.isContinueTypeBindFailure()) {
                        throw e;
                    } else {
                        // 処理を続ける場合は、nullなどを入れる
                        result.add(Utils.getPrimitiveDefaultValue(elementClass));
                    }
                }

                if(annoProxy.elementMerged()) {
                    CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                    if(mergedRegion != null) {
                        // 結合を考慮する場合
                        row += POIUtils.getRowSize(mergedRegion);
                    } else {
                        row++;
                    }

                } else {
                    row++;
                }

            }


        } else {
            throw new AnnotationInvalidException(annoProxy.getTarget(), MessageBuilder.create("anno.attr.notSupportValue")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", annoProxy.annotationType())
                    .var("attrName", "direction")
                    .varWithEnum("attrValue", direction)
                    .format());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handleOnSaving(final List<Object> dataList, final Annotation anno, final CellPosition initPosition,
            final CellConverter converter, final SavingWorkObject work, final ArrayDirection direction) {

        final AnnotationProxy annoProxy = new AnnotationProxy(anno);

        final Optional<XlsArrayOption> arrayOption = field.getAnnotation(XlsArrayOption.class);
        final XlsArrayOption.OverOperation overOp = arrayOption.map(op -> op.overOpration())
                .orElse(XlsArrayOption.OverOperation.Break);

        final XlsArrayOption.RemainedOperation remainedOp = arrayOption.map(op -> op.remainedOperation())
                .orElse(XlsArrayOption.RemainedOperation.None);

        // 属性sizeの値のチェック
        if(annoProxy.size() <= 0) {
            throw new AnnotationInvalidException(annoProxy.getTarget(), MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", annoProxy.annotationType())
                    .var("attrName", "size")
                    .var("attrValue", annoProxy.size())
                    .var("min", 1)
                    .format());

        } else if(annoProxy.size() < dataList.size()) {
            // 書き込むデータサイズが、アノテーションの指定よりも多く、テンプレート側が不足している場合
            if(overOp.equals(XlsArrayOption.OverOperation.Error)) {
                throw new AnnotationInvalidException(annoProxy.getTarget(), MessageBuilder.create("anno.attr.arraySizeOver")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", annoProxy.annotationType())
                        .var("attrName", "size")
                        .var("attrValue", annoProxy.size())
                        .var("dataSize", dataList.size())
                        .format());

            }
        }

        if(direction.equals(ArrayDirection.Horizon)) {

            int column = initPosition.getColumn();
            int row = initPosition.getRow();

            for(int i=0; i < annoProxy.size(); i++) {

                final CellPosition cellAddress = CellPosition.of(row, column);
                field.setArrayPosition(beansObj, cellAddress, i);

                if(Utils.isNotEmpty(label)) {
                    field.setArrayLabel(beansObj, label, i);
                }
                
                final int commentIndex = i;
                final Cell tempCellComment = POIUtils.getCell(sheet, cellAddress);
                field.getArrayCommentGetter().ifPresent(getter -> config.getCommentOperator().saveArrayCellComment(
                        getter, tempCellComment, beansObj, commentIndex, field, config));

                if(i < dataList.size()) {
                    final Object elementValue = dataList.get(i);
                    try {
                        converter.toCell(elementValue, beansObj, sheet, cellAddress);

                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cellAddress, field.getName(), label);
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                } else {
                    // 書き込むリストのサイズを超えている場合、値をクリアする
                    final Cell cell = POIUtils.getCell(sheet, cellAddress);
                    cell.setCellType(CellType.BLANK);
                    
                    if(cell.getCellComment() != null) {
                        cell.removeCellComment();
                    }

                }

                final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                if(annoProxy.elementMerged() && mergedRegion != null) {
                    // 結合を考慮する場合
                    column += POIUtils.getColumnSize(mergedRegion);

                } else if(mergedRegion != null) {
                    // 結合を考慮しないで、結合されている場合は、解除する
                    POIUtils.removeMergedRange(sheet, mergedRegion);
                    column++;

                } else {
                    // 結合されていない場合
                    column++;
                }

                if(i >= dataList.size()-1) {
                    // 書き込むデータサイズが少なく、テンプレート側が余っている場合
                    if(remainedOp.equals(XlsArrayOption.RemainedOperation.None)) {
                        // 処理を終了する場合
                        break;
                    }

                }

            }

        } else if(direction.equals(ArrayDirection.Vertical)) {

            int column = initPosition.getColumn();
            int row = initPosition.getRow();

            for(int i=0; i < annoProxy.size(); i++) {

                final CellPosition cellAddress = CellPosition.of(row, column);
                field.setArrayPosition(beansObj, cellAddress, i);

                if(Utils.isNotEmpty(label)) {
                    field.setArrayLabel(beansObj, label, i);
                }
                
                final int commentIndex = i;
                final Cell tempCellComment = POIUtils.getCell(sheet, cellAddress);
                field.getArrayCommentGetter().ifPresent(getter -> config.getCommentOperator().saveArrayCellComment(
                        getter, tempCellComment, beansObj, commentIndex, field, config));

                if(i < dataList.size()) {
                    final Object elementValue = dataList.get(i);
                    try {
                        converter.toCell(elementValue, beansObj, sheet, cellAddress);

                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cellAddress, field.getName(), label);
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                } else {
                    // 書き込むリストのサイズを超えている場合、値をクリアする
                    final Cell cell = POIUtils.getCell(sheet, cellAddress);
                    cell.setCellType(CellType.BLANK);
                    
                    if(cell.getCellComment() != null) {
                        cell.removeCellComment();
                    }

                }

                final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                if(annoProxy.elementMerged() && mergedRegion != null) {
                    // 結合を考慮する場合
                    row += POIUtils.getRowSize(mergedRegion);

                } else if(mergedRegion != null) {
                    // 結合を考慮しないで、結合されている場合は、解除する
                    POIUtils.removeMergedRange(sheet, mergedRegion);
                    row++;

                } else {
                    // 結合されていない場合
                    row++;
                }

                if(i >= dataList.size()-1) {
                    // 書き込むデータサイズが少なく、テンプレート側が余っている場合
                    if(remainedOp.equals(XlsArrayOption.RemainedOperation.None)) {
                        // 処理を終了する場合
                        break;
                    }

                }

            }


        } else {
            throw new AnnotationInvalidException(annoProxy.getTarget(), MessageBuilder.create("anno.attr.notSupportValue")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", annoProxy.annotationType())
                    .var("attrName", "direction")
                    .varWithEnum("attrValue", direction)
                    .format());
        }
    }

    /**
     * ラベルを設定する
     * @param label ラベル
     */
    public void setLabel(String label) {
        this.label = label;
    }

}
