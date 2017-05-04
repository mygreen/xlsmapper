package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
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
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.ArrayDirection;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayCell;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayOperator;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;

/**
 * アノテーション{@link XlsArrayCell}を処理するプロセッサ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellProcessor extends AbstractFieldProcessor<XlsArrayCell> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final  XlsArrayCell anno, final FieldAccessor accessor,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {
        
        final Class<?> clazz = accessor.getType();
        if(Collection.class.isAssignableFrom(clazz)) {
            
            Class<?> itemClass = anno.itemClass();
            if(itemClass == Object.class) {
                itemClass = accessor.getComponentType();
            }
            
            List<?> value = loadValues(sheet, beansObj, anno, accessor, itemClass, config, work);
            if(value != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                accessor.setValue(beansObj, collection);
            }
            
        } else if(clazz.isArray()) {
            
            Class<?> itemClass = anno.itemClass();
            if(itemClass == Object.class) {
                itemClass = accessor.getComponentType();
            }
            
            final List<?> value = loadValues(sheet, beansObj, anno, accessor, itemClass, config, work);
            if(value != null) {
                final Object array = Array.newInstance(itemClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }
                
                accessor.setValue(beansObj, array);
            }
            
        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
            
        }
    }
    
    private List<Object> loadValues(final Sheet sheet, final Object beansObj, XlsArrayCell anno, final FieldAccessor accessor, 
            final Class<?> itemClass, final Configuration config, final LoadingWorkObject work) {
        
        final CellPosition initPosition = getCellPosition(accessor, anno);
        final CellConverter<?> converter = getCellConverter(itemClass, accessor, config);
        
        final List<Object> result = new ArrayList<>();
        
        // 属性sizeの値のチェック
        if(anno.size() <= 0) {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .var("attrName", "size")
                    .var("attrValue", anno.size())
                    .var("min", 1)
                    .format());
        }
        
        if(anno.direction().equals(ArrayDirection.Horizon)) {
            
            int column = initPosition.getColumn();
            int row = initPosition.getRow();
            
            for(int i=0; i < anno.size(); i++) {
                
                final CellPosition cellAddress = CellPosition.of(row, column);
                final Cell cell = POIUtils.getCell(sheet, cellAddress);
                
                accessor.setArrayPosition(beansObj, cellAddress, i);
                
                try {
                    final Object value = converter.toObject(cell, accessor, config);
                    result.add(value);
                    
                } catch(TypeBindException e) {
                    work.addTypeBindError(e, cellAddress, accessor.getName(), null);
                    if(!config.isContinueTypeBindFailure()) {
                        throw e;
                    } else {
                        // 処理を続ける場合は、nullなどを入れる
                        result.add(Utils.getPrimitiveDefaultValue(itemClass));
                    }
                }
                
                if(anno.itemMerged()) {
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
            
        } else if(anno.direction().equals(ArrayDirection.Vertical)) {
            
            int column = initPosition.getColumn();
            int row = initPosition.getRow();
            
            for(int i=0; i < anno.size(); i++) {
                
                final CellPosition cellAddress = CellPosition.of(row, column);
                final Cell cell = POIUtils.getCell(sheet, cellAddress);
                
                accessor.setArrayPosition(beansObj, cellAddress, i);
                
                try {
                    final Object value = converter.toObject(cell, accessor, config);
                    result.add(value);
                    
                } catch(TypeBindException e) {
                    work.addTypeBindError(e, cellAddress, accessor.getName(), null);
                    if(!config.isContinueTypeBindFailure()) {
                        throw e;
                    } else {
                        // 処理を続ける場合は、nullなどを入れる
                        result.add(Utils.getPrimitiveDefaultValue(itemClass));
                    }
                }
                
                if(anno.itemMerged()) {
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
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .var("attrName", "direction")
                    .varWithEnum("attrValue", anno.direction())
                    .format());
        }
        
        return result;
    }
    
    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param accessor フィールド情報
     * @param anno アノテーション
     * @return 値が設定されているセルのアドレス
     * @throws AnnotationInvalidException アドレスの設定値が不正な場合
     */
    private CellPosition getCellPosition(final FieldAccessor accessor, final XlsArrayCell anno) throws AnnotationInvalidException {
        
        if(Utils.isNotEmpty(anno.address())) {
            try {
                return CellPosition.of(anno.address());
            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCell.class)
                        .var("attrName", "address")
                        .var("attrValue", anno.address())
                        .format());
            }
        
        } else {
            if(anno.row() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCell.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.row())
                        .var("min", 0)
                        .format());
            }
            
            if(anno.column() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCell.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.column())
                        .var("min", 0)
                        .format());
                
            }
            
            return CellPosition.of(anno.row(), anno.column());
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsArrayCell anno,
            final FieldAccessor accessor, final Configuration config, final SavingWorkObject work)
            throws XlsMapperException {
        
        final Class<?> clazz = accessor.getType();
        final Object result = accessor.getValue(beansObj);
        if(Collection.class.isAssignableFrom(clazz)) {
            
            Class<?> itemClass = anno.itemClass();
            if(itemClass == Object.class) {
                itemClass = accessor.getComponentType();
            }
            
            final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
            final List<Object> list = Utils.convertCollectionToList(value);
            saveRecords(sheet, anno, accessor, itemClass, beansObj, list, config, work);
            
        } else if(clazz.isArray()) {
            
            Class<?> itemClass = anno.itemClass();
            if(itemClass == Object.class) {
                itemClass = accessor.getComponentType();
            }
            
            final List<Object> list = Utils.asList(result, itemClass);
            saveRecords(sheet, anno, accessor, itemClass, beansObj, list, config, work);
            
        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }
        
    }
    
    private void saveRecords(final Sheet sheet, final XlsArrayCell anno, final FieldAccessor accessor, 
            final Class<?> itemClass, final Object beansObj, final List<Object> result, final Configuration config, final SavingWorkObject work)
                    throws XlsMapperException {
        
        final CellPosition initPosition = getCellPosition(accessor, anno);
        final CellConverter converter = getCellConverter(itemClass, accessor, config);
        
        final Optional<XlsArrayOperator> arrayOperator = accessor.getAnnotation(XlsArrayOperator.class);
        final XlsArrayOperator.OverOperate overOp = arrayOperator.map(op -> op.overCase())
                .orElse(XlsArrayOperator.OverOperate.Break);
        
        final XlsArrayOperator.RemainedOperate remainedOp = arrayOperator.map(op -> op.remainedCase())
                .orElse(XlsArrayOperator.RemainedOperate.None);
        
        // 属性sizeの値のチェック
        if(anno.size() <= 0) {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .var("attrName", "size")
                    .var("attrValue", anno.size())
                    .var("min", 1)
                    .format());
            
        } else if(anno.size() < result.size()) {
            // 書き込むデータサイズが、アノテーションの指定よりも多く、テンプレート側が不足している場合
            if(overOp.equals(XlsArrayOperator.OverOperate.Error)) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.arraySizeOver")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsArrayCell.class)
                        .var("attrName", "size")
                        .var("attrValue", anno.size())
                        .var("dataSize", result.size())
                        .format());
                
            }
        }
        
        if(anno.direction().equals(ArrayDirection.Horizon)) {
            
            int column = initPosition.getColumn();
            int row = initPosition.getRow();
            
            for(int i=0; i < anno.size(); i++) {
                
                final CellPosition cellAddress = CellPosition.of(row, column);
                accessor.setArrayPosition(beansObj, cellAddress, i);
                
                if(i < result.size()) {
                    final Object itemValue = result.get(i);
                    try {
                        converter.toCell(accessor, itemValue, beansObj, sheet, cellAddress, config);
                        
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cellAddress, accessor.getName(), null);
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                } else {
                    // 書き込むリストのサイズを超えている場合、値をクリアする
                    final Cell cell = POIUtils.getCell(sheet, cellAddress);
                    cell.setCellType(CellType.BLANK);
                    
                }
                
                final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                if(anno.itemMerged() && mergedRegion != null) {
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
                
                if(i >= result.size()-1) {
                    // 書き込むデータサイズが少なく、テンプレート側が余っている場合
                    if(remainedOp.equals(XlsArrayOperator.RemainedOperate.None)) {
                        // 処理を終了する場合
                        break;
                    }
                    
                }
                
            }
            
        } else if(anno.direction().equals(ArrayDirection.Vertical)) {
            
            int column = initPosition.getColumn();
            int row = initPosition.getRow();
            
            for(int i=0; i < anno.size(); i++) {
                
                final CellPosition cellAddress = CellPosition.of(row, column);
                accessor.setArrayPosition(beansObj, cellAddress, i);
                
                if(i < result.size()) {
                    final Object itemValue = result.get(i);
                    try {
                        converter.toCell(accessor, itemValue, beansObj, sheet, cellAddress, config);
                        
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cellAddress, accessor.getName(), null);
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                } else {
                    // 書き込むリストのサイズを超えている場合、値をクリアする
                    final Cell cell = POIUtils.getCell(sheet, cellAddress);
                    cell.setCellType(CellType.BLANK);
                    
                }
                
                final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
                if(anno.itemMerged() && mergedRegion != null) {
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
                
                if(i >= result.size()-1) {
                    // 書き込むデータサイズが少なく、テンプレート側が余っている場合
                    if(remainedOp.equals(XlsArrayOperator.RemainedOperate.None)) {
                        // 処理を終了する場合
                        break;
                    }
                    
                }
                
            }
            
            
        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsArrayCell.class)
                    .var("attrName", "direction")
                    .varWithEnum("attrValue", anno.direction())
                    .format());
        }
    }
    
}
