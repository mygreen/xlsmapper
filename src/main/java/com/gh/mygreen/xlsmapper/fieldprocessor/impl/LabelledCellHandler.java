package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.awt.Point;
import java.lang.annotation.Annotation;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellFinder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * ラベル付きのセルの開始位置を検索するクラス。
 * 
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LabelledCellHandler {

    private final FieldAccessor field;

    private final Sheet sheet;

    private final Configuration config;

    public LabelledCellHandler(final FieldAccessor field, final Sheet sheet, final Configuration config) {
        this.field = field;
        this.sheet = sheet;
        this.config = config;
    }

    /**
     * ラベル情報
     *
     */
    public static class LabelInfo {

        /**
         * 値のセル - ラベルのセルではない。
         */
        Cell valueCell;

        /**
         * 値のセルの位置情報
         */
        CellPosition valueAddress;

        /**
         * ラベルセルの値
         */
        String label;

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
         * 見出しセルから見て値が設定されているセルの位置・方向を指定します。
         * @return アノテーションの属性「type」の値。属性が存在しない場合は、nullを返す。
         */
        public LabelledCellType type() {
            return ClassUtils.getAnnotationAttribute(target, "type", LabelledCellType.class).orElse(null);
        }

        /**
         * 属性「type」の方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。
         * @return アノテーションの属性「range」の値
         */
        public int range() {
            return ClassUtils.getAnnotationAttribute(target, "range", int.class).orElse(1);
        }

        /**
         * ラベルセルから指定したセル数分離れたセルの値をマッピングする際に指定します。
         * @return アノテーションの属性「skip」の値
         */
        public int skip() {
            return ClassUtils.getAnnotationAttribute(target, "skip", int.class).orElse(0);
        }

        /**
         * 見出しとなるセルの値を指定します。
         * @return アノテーションの属性「label」の値
         */
        public String label() {
            return ClassUtils.getAnnotationAttribute(target, "label", String.class).orElse("");
        }

        /**
         * 同じラベルのセルが複数ある場合に領域の見出しを指定します。
         * @return アノテーションの属性「headerLabel」の値
         */
        public String headerLabel() {
            return ClassUtils.getAnnotationAttribute(target, "headerLabel", String.class).orElse("");
        }

        /**
         * 見出しとなるセルの行番号を指定します。
         * @return アノテーションの属性「labelRow」の値
         */
        public int labelRow() {
            return ClassUtils.getAnnotationAttribute(target, "labelRow", int.class).orElse(-1);
        }

        /**
         * 見出しとなるセルの列番号を指定します。
         * @return アノテーションの属性「labelColumn」の値
         */
        public int labelColumn() {
            return ClassUtils.getAnnotationAttribute(target, "labelColumn", int.class).orElse(-1);
        }

        /**
         * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
         * @return アノテーションの属性「optional」の値
         */
        public boolean optional() {
            return ClassUtils.getAnnotationAttribute(target, "optional", boolean.class).orElse(false);
        }

        /**
         * ラベルセルが結合している場合を考慮するかどうか指定します。
         * @return アノテーションの属性「labelMerged」の値
         */
        public boolean labelMerged() {
            return ClassUtils.getAnnotationAttribute(target, "labelMerged", boolean.class).orElse(false);
        }

    }

    /**
     * 見出し付きのセルの情報を取得する。
     * @param anno マッピング情報が設定されているアノテーション
     * @param processCase 処理ケース
     * @return ラベルや値が設定されているセルの開始情報
     */
    public Optional<LabelInfo> handle(final Annotation anno, final ProcessCase processCase) {

        final AnnotationProxy annoProxy = new AnnotationProxy(anno);

        // ラベルの位置を取得する
        final Optional<CellPosition> labelPosition = getLabelPosition(annoProxy);
        if(!labelPosition.isPresent()) {
            return Optional.empty();
        }

        final int column = labelPosition.get().getColumn();
        final int row = labelPosition.get().getRow();

        /*
         * 見出しか結合している場合を考慮する場合
         * ・結合サイズ分で補正する。
         * ・考慮しない場合は、mergedXXXSizeの値は0のまま。
         */
        int mergedRowSize = 0;
        int mergedColumnSize = 0;
        if(annoProxy.labelMerged()) {
            CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
            if(mergedRegion != null) {
                mergedRowSize = mergedRegion.getLastRow() - mergedRegion.getFirstRow();
                mergedColumnSize = mergedRegion.getLastColumn() - mergedRegion.getFirstColumn();
            }
        }

        int range = annoProxy.range();
        if(range < 1){
            range = 1;
        }

        // 値が設定されているセルを検索する。
        Point targetPosition = new Point();
        Cell targetCell = null;
        for(int i=0; i < range; i++){
            final int index = annoProxy.skip() + i +1;
            if(annoProxy.type() == LabelledCellType.Left) {
                targetPosition.x = column - index;
                targetPosition.y = row;
                targetCell = POIUtils.getCell(sheet, targetPosition);

            } else if(annoProxy.type() == LabelledCellType.Right) {
                targetPosition.x = column + index + mergedColumnSize;
                targetPosition.y = row;
                targetCell = POIUtils.getCell(sheet, targetPosition);

            } else if(annoProxy.type() == LabelledCellType.Bottom) {
                targetPosition.x = column;
                targetPosition.y = row + index + mergedRowSize;
                targetCell = POIUtils.getCell(sheet, targetPosition);

            }

            if(POIUtils.getCellContents(targetCell, config.getCellFormatter()).length() > 0){
                break;
            }

            if(processCase.equals(ProcessCase.Save)) {
                /*
                 * 書き込み時は、属性rangeの範囲を考慮しない。
                 * テンプレートファイルの場合、値は空を設定しているため。
                 */
                break;
            }
        }

        final LabelInfo info = new LabelInfo();
        info.valueCell = targetCell;
        info.valueAddress = CellPosition.of(targetPosition);
        info.label = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row), config.getCellFormatter());

        return Optional.of(info);
    }
    
    /**
     * ラベルの位置情報を取得する。
     * @since 2.1
     * @param anno マッピング情報が設定されているアノテーション
     * @return ラベルの位置情報。見つからない場合は、空を返す。
     */
    public Optional<CellPosition> getLabelPosition(final Annotation anno) {
        return getLabelPosition(new AnnotationProxy(anno));
        
    }

    /**
     * ラベルの位置を取得する。
     * @param anno アノテーションの情報
     * @return ラベルの位置。見つからない場合は、空を返す。
     *         ただし、見つからない場合、設定により例外をスローする場合がある。
     */
    private Optional<CellPosition> getLabelPosition(final AnnotationProxy anno) {

        if(Utils.isNotEmpty(anno.label())) {
            // 属性「label」によるラベルの指定がある場合
            try {
                if(Utils.isNotEmpty(anno.headerLabel())){
                    Cell headerCell = CellFinder.query(sheet, anno.headerLabel(), config).findWhenNotFoundException();
                    Cell labelCell = CellFinder.query(sheet, anno.label(), config)
                            .startPosition(headerCell.getColumnIndex(), headerCell.getRowIndex() + 1)
                            .findWhenNotFoundException();
                    return Optional.of(CellPosition.of(labelCell));

                } else {
                    Cell labelCell = CellFinder.query(sheet, anno.label(), config).findWhenNotFoundException();
                    return Optional.of(CellPosition.of(labelCell));
                }
            } catch(CellNotFoundException ex){
                if(anno.optional()){
                    return Optional.empty();
                } else {
                    throw ex;
                }
            }

        } else {
            /*
             * 属性「labelRow」「labelColumn」によるアドレスを直接指定の場合
             * これは、アノテーション @XlsIterateTables の中で指定しているときに設定される。
             */
            if(anno.labelRow() < 0) {
                throw new AnnotationInvalidException(anno.getTarget(), MessageBuilder.create("anno.attr.min")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", anno.annotationType())
                        .var("attrName", "labelRow")
                        .var("attrValue", anno.labelRow())
                        .var("min", 0)
                        .format());
            }

            if(anno.labelColumn() < 0) {
                throw new AnnotationInvalidException(anno.getTarget(), MessageBuilder.create("anno.attr.min")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", anno.annotationType())
                        .var("attrName", "labelColumn")
                        .var("attrValue", anno.labelColumn())
                        .var("min", 0)
                        .format());

            }

            return Optional.of(CellPosition.of(anno.labelRow(), anno.labelColumn()));
        }

    }

}
