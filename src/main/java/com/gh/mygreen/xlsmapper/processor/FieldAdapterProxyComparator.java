package com.gh.mygreen.xlsmapper.processor;

import java.util.Comparator;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.annotation.XlsOrder;


/**
 * アノテーション{@link XlsOrder}に従いフィールドの順番を並び替えるComparator。
 * <ul>
 *  <li>{@code @XlsHint}の属性orderの順に並び替えます。</li>
 *  <li>{@code @XlsHint}の属性orderの値が同じ場合は、第2並び順としてフィールド名の昇順を使用します。</li>
 *  <li>{@code @XlsHint}が付与されていないフィールドは、付与されているフィールドよりも後になります。</li>
 *  <li>{@code @XlsHint}が付与されていないフィールドは、フィールド名の昇順で並び替えます。</li>
 * </ul>
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldAdapterProxyComparator implements Comparator<FieldAdapterProxy> {
    
    @Override
    public int compare(final FieldAdapterProxy o1, final FieldAdapterProxy o2) {
        
        final Optional<Integer> order1 = getOrder(o1);
        final Optional<Integer> order2 = getOrder(o2);
        
        if(!order1.isPresent() && !order2.isPresent()) {
            // 並び順がない場合は、フィールド名の昇順
            return o1.getField().getName().compareTo(o2.getField().getName());
        } else if(!order1.isPresent()) {
            return -1;
        } else if(!order2.isPresent()) {
            return 1;
        }
        
        final int value1 = order1.get();
        final int value2 = order2.get();
        if(value1 == value2) {
            // 並び順が同じ場合は、フィールド名の昇順
            return o1.getField().getName().compareTo(o2.getField().getName());
        } else if(value1 > value2) {
            return 1;
        } else {
            return -1;
        }
        
    }
    
    private Optional<Integer> getOrder(final FieldAdapterProxy adapterProxy) {
        
        return adapterProxy.getField().getAnnotation(XlsOrder.class)
                .map(anno -> anno.value());
        
    }
}
