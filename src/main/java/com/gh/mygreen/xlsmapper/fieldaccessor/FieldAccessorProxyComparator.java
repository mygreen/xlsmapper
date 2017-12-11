package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.util.Comparator;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.annotation.XlsOrder;


/**
 * アノテーション{@link XlsOrder}に従いフィールドの順番を並び替えるComparator。
 * <ul>
 *  <li>{@code @XlsOrder}の属性valueの順に並び替えます。</li>
 *  <li>{@code @XlsOrder}の属性valueの値が同じ場合は、第2並び順としてフィールド名の昇順を使用します。</li>
 *  <li>{@code @XlsOrder}が付与されていないフィールドは、付与されているフィールドよりも後になります。</li>
 *  <li>{@code @XlsOrder}が付与されていないフィールドは、フィールド名の昇順で並び替えます。</li>
 * </ul>
 *
 * @author T.TSUCHIE
 *
 */
public class FieldAccessorProxyComparator implements Comparator<FieldAccessorProxy> {

    @Override
    public int compare(final FieldAccessorProxy o1, final FieldAccessorProxy o2) {

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

    private Optional<Integer> getOrder(final FieldAccessorProxy accessorProxy) {

        return accessorProxy.getField().getAnnotation(XlsOrder.class)
                .map(anno -> anno.value());

    }
}
