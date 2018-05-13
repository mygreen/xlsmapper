package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link TemporalAccessor}の子クラスに対する{@link CellConverterFactory}のベースクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalCellConverterFactory<T extends TemporalAccessor & Comparable<? super T>>
        extends CellConverterFactorySupport<T> implements CellConverterFactory<T> {

    @Override
    protected void setupCustom(final BaseCellConverter<T> cellConverter, final FieldAccessor field, final Configuration config) {

        ArgUtils.instanceOf(cellConverter, AbstractTemporalCellConverter.class, "cellConverter");

        if(cellConverter instanceof AbstractTemporalCellConverter) {

            final AbstractTemporalCellConverter<T> temporalCellConverter = (AbstractTemporalCellConverter<T>)cellConverter;

            // 書き込み時のセルの書式を設定する
            Optional<XlsDateTimeConverter> converterAnno = field.getAnnotation(XlsDateTimeConverter.class);

            temporalCellConverter.setDefaultExcelPattern(getDefaultExcelPattern());
            converterAnno.ifPresent(ca -> temporalCellConverter.setSettingExcelPattern(ca.excelPattern()));

        }

    }

    @Override
    protected TextFormatter<T> createTextFormatter(final FieldAccessor field, final Configuration config) {

        final Optional<XlsDateTimeConverter> converterAnno = field.getAnnotation(XlsDateTimeConverter.class);
        DateTimeFormatter formatter = createFormatter(converterAnno);

        return new TextFormatter<T>() {

            @Override
            public T parse(final String text) {
                try {
                    return parseTemporal(text, formatter);

                } catch(DateTimeParseException e) {
                    final Map<String, Object> vars = new HashMap<>();
                    vars.put("javaPattern", getJavaPattern(converterAnno));
                    vars.put("excelPattern", getExcelPattern(converterAnno));

                    throw new TextParseException(text, field.getType(), e, vars);
                }
            }

            @Override
            public String format(final T value) {
                return formatter.format(value);
            }
        };

    }

    /**
     * アノテーションを元にフォーマッタを作成する。
     * @param converterAnno 変換用のアノテーション。
     * @return フォーマッタ
     */
    protected DateTimeFormatter createFormatter(final Optional<XlsDateTimeConverter> converterAnno) {

        final boolean lenient = converterAnno.map(a -> a.lenient()).orElse(false);
        final ResolverStyle style = lenient ? ResolverStyle.LENIENT : ResolverStyle.STRICT;
        if(!converterAnno.isPresent()) {
            return DateTimeFormatter.ofPattern(getDefaultJavaPattern())
                    .withResolverStyle(style);
        }

        String pattern = converterAnno.get().javaPattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultJavaPattern();
        }

        final Locale locale = Utils.getLocale(converterAnno.get().locale());
        final ZoneId zone = converterAnno.get().timezone().isEmpty() ? ZoneId.systemDefault()
                : TimeZone.getTimeZone(converterAnno.get().timezone()).toZoneId();

        return DateTimeFormatter.ofPattern(pattern, locale)
                .withResolverStyle(style)
                .withZone(zone);

    }

    private String getJavaPattern(final Optional<XlsDateTimeConverter> converterAnno) {
        if(!converterAnno.isPresent()) {
            return getDefaultJavaPattern();
        }

        String pattern = converterAnno.get().javaPattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultJavaPattern();
        }

        return pattern;
    }

    private String getExcelPattern(final Optional<XlsDateTimeConverter> converterAnno) {
        if(!converterAnno.isPresent()) {
            return getDefaultExcelPattern();
        }

        String pattern = converterAnno.get().excelPattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultExcelPattern();
        }

        return pattern;
    }

    /**
     * 文字列を解析してJavaオブジェクトに変換します。
     * @param str 解析対象の文字列
     * @param formatter フォーマッタ
     * @return パースした結果
     * @throws DateTimeParseException パースに失敗した場合
     */
    protected abstract T parseTemporal(String str, DateTimeFormatter formatter) throws DateTimeParseException;

    /**
     * その型における標準のJavaの書式を返す。
     * @return {@link SimpleDateFormat}で処理可能な形式。
     */
    protected abstract String getDefaultJavaPattern();

    /**
     * その型における標準のExcelの書式を返す。
     * @return Excelの書式
     */
    protected abstract String getDefaultExcelPattern();

}
