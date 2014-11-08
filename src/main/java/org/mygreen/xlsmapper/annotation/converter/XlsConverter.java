package org.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mygreen.xlsmapper.cellconvert.CellConverter;
import org.mygreen.xlsmapper.cellconvert.DefaultCellConverter;


/**
 * Converterの共通設定。
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsConverter {
    
    /**
     * 読み込み時、書き込み時にトリミングします。
     * @return
     */
    boolean trim() default false;
    
    /**
     * 'true'のとき書き込み時にセルの「折り返し設定」を強制的に有効にします。'false'の場合は、既存の折り返し設定は変更されません。
     * @return
     */
    boolean forceWrapText() default false;
    
    /**
     * 'true'のとき書き込み時にセルの「縮小して表示」を強制的に有効にします。'false'の場合は、既存の縮小して表示は変更されません。
     */
    boolean forceShrinkToFit() default false;
    
    /**
     * 読み込み時または書き込み時のデフォルト値を指定します。日付など書式が設定されている場合は、書式に沿った値を設定してください。
     * @return
     */
    String defaultValue() default"";
    
    /**
     * 独自のConverterで処理したい場合に指定します。
     * @return
     */
    Class<? extends CellConverter> converterClass() default DefaultCellConverter.class;
}
