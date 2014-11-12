package com.gh.mygreen.xlsmapper;

import java.util.Comparator;

import com.gh.mygreen.xlsmapper.annotation.XlsHint;


/**
 * アノテーション{@link XlsHint}に従いフィールドの順番を並び替えるComparator。
 * <ul>
 *  <li>{@code @XlsHint}の属性orderの順に並び替えます。
 *  <li>{@code @XlsHint}の属性orderの値が同じ場合は、第2並び順としてフィールド名の昇順を使用します。
 *  <li>{@code @XlsHint}が付与されていないフィールドは、付与されているフィールドよりも後になります。
 *  <li>{@code @XlsHint}が付与されていないフィールドは、フィールド名の昇順で並び替えます。
 * 
 * 
 * @author T.TSUCHIE
 *
 */
public class HintOrderComparator implements Comparator<FieldAdaptorProxy> {
    
    /** ロード処理時かどうか */
    private final boolean onLoad;
    
    public static HintOrderComparator createForLoading() {
        return new HintOrderComparator(true);
    }
    
    public static HintOrderComparator createForSaving() {
        return new HintOrderComparator(false);
    }
    
    private HintOrderComparator(boolean onLoad) {
        this.onLoad = onLoad;
    }
    
    @Override
    public int compare(final FieldAdaptorProxy o1, final FieldAdaptorProxy o2) {
        
        final int order1 = getHintOrder(o1);
        final int order2 = getHintOrder(o2);
        
        if(order1 < 0 && order2 < 0) {
            // 並び順がない場合は、フィールド名の昇順
            return o1.getAdaptor().getName().compareTo(o2.getAdaptor().getName());
        } else if(order1 < 0) {
            return -1;
        } else if(order2 < 0) {
            return 1;
        }
        
        if(order1 == order2) {
            // 並び順が同じ場合は、フィールド名の昇順
            return o1.getAdaptor().getName().compareTo(o2.getAdaptor().getName());
        } else if(order1 > order2) {
            return 1;
        } else {
            return -1;
        }
        
    }
    
    private int getHintOrder(final FieldAdaptorProxy adaptorProxy) {
        
        final XlsHint hintAnno;
        if(onLoad) {
            hintAnno = adaptorProxy.getAdaptor().getLoadingAnnotation(XlsHint.class);
        } else {
            hintAnno = adaptorProxy.getAdaptor().getSavingAnnotation(XlsHint.class);
        }
        
        if(hintAnno == null) {
            return -1;
        } else {
            return hintAnno.order();
        }
        
    }
}
