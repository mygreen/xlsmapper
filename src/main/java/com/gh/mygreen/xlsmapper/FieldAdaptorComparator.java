package com.gh.mygreen.xlsmapper;

import java.util.Comparator;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.annotation.XlsOrder;


/**
 * アノテーション{@link XlsOrder}に従いフィールドの順番を並び替えるComparator。
 * <ul>
 *  <li>{@code @XlsHint}の属性orderの順に並び替えます。
 *  <li>{@code @XlsHint}の属性orderの値が同じ場合は、第2並び順としてフィールド名の昇順を使用します。
 *  <li>{@code @XlsHint}が付与されていないフィールドは、付与されているフィールドよりも後になります。
 *  <li>{@code @XlsHint}が付与されていないフィールドは、フィールド名の昇順で並び替えます。
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldAdaptorComparator implements Comparator<FieldAdaptorProxy> {
    
    /** ロード処理時かどうか */
    private final boolean onLoad;
    
    public static FieldAdaptorComparator createForLoading() {
        return new FieldAdaptorComparator(true);
    }
    
    public static FieldAdaptorComparator createForSaving() {
        return new FieldAdaptorComparator(false);
    }
    
    private FieldAdaptorComparator(boolean onLoad) {
        this.onLoad = onLoad;
    }
    
    @Override
    public int compare(final FieldAdaptorProxy o1, final FieldAdaptorProxy o2) {
        
        final Optional<Integer> order1 = getOrder(o1);
        final Optional<Integer> order2 = getOrder(o2);
        
        if(!order1.isPresent() && !order2.isPresent()) {
            // 並び順がない場合は、フィールド名の昇順
            return o1.getAdaptor().getName().compareTo(o2.getAdaptor().getName());
        } else if(!order1.isPresent()) {
            return -1;
        } else if(!order2.isPresent()) {
            return 1;
        }
        
        final int value1 = order1.get();
        final int value2 = order2.get();
        if(value1 == value2) {
            // 並び順が同じ場合は、フィールド名の昇順
            return o1.getAdaptor().getName().compareTo(o2.getAdaptor().getName());
        } else if(value1 > value2) {
            return 1;
        } else {
            return -1;
        }
        
    }
    
    private Optional<Integer> getOrder(final FieldAdaptorProxy adaptorProxy) {
        
        final XlsOrder orderAnno;
        if(onLoad) {
            orderAnno = adaptorProxy.getAdaptor().getLoadingAnnotation(XlsOrder.class);
        } else {
            orderAnno = adaptorProxy.getAdaptor().getSavingAnnotation(XlsOrder.class);
        }
        
        if(orderAnno == null) {
            return Optional.empty();
        } else {
            return Optional.of(orderAnno.value());
        }
        
    }
}
