package com.gh.mygreen.xlsmapper.cellconverter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.expression.ExpressionEvaluationException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link CellConverter}を作成するための抽象クラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class CellConverterFactorySupport<T>  {

    /**
     * 引数で指定したCellConverterに対して、トリムなどの共通の設定を行う。
     * @param cellConverter 設定を行うCellConverter
     * @param field フィールド情報
     * @param config システム設定情報
     */
    protected void setupCellConverter(final BaseCellConverter<T> cellConverter, final FieldAccessor field, final Configuration config) {

        final TextFormatter<T> textFormatter = createTextFormatter(field, config);
        cellConverter.setTextFormatter(textFormatter);

        // トリムの設定
        final Optional<XlsTrim> trimAnno = field.getAnnotation(XlsTrim.class);
        final boolean trimmed = trimAnno.map(anno -> true).orElse(false);

        cellConverter.setTrimmed(trimmed);

        // 初期値の設定
        final Optional<XlsDefaultValue> defaultValueAnno = field.getAnnotation(XlsDefaultValue.class);
        defaultValueAnno.ifPresent(anno -> {
            String text = Utils.trim(anno.value(), trimmed);

            try {
                T defaultValue = textFormatter.parse(text);
                cellConverter.setDefaultValue(defaultValue, anno.cases());

            } catch(TextParseException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.XlsDefaultValue.failParse")
                        .var("property", field.getNameWithClass())
                        .var("defaultValue", text)
                        .varWithClass("type", field.getType())
                        .format(), e);
            }
        });

        // セルの書式の設定
        final Optional<XlsCellOption> cellOptionAnno = field.getAnnotation(XlsCellOption.class);
        cellOptionAnno.ifPresent(anno -> {
            cellConverter.setShrinktToFit(anno.shrinkToFit());
            cellConverter.setWrapText(anno.wrapText());
            cellConverter.setIndent(anno.indent());
            cellConverter.setHorizontalAlignment(anno.horizontalAlign().poiAlignType());
            cellConverter.setVerticalAlignment(anno.verticalAlign().poiAlignType());
        });

        // 数式の設定
        final Optional<XlsFormula> formulaAnno = field.getAnnotation(XlsFormula.class);
        formulaAnno.ifPresent(anno -> {
            CellFormulaHandler formulaHandler = createCellFormulaHandler(anno, field, config);
            cellConverter.setFormulaHandler(formulaHandler);

        });

        // 各個別の設定
        setupCustom(cellConverter, field, config);

    }

    /**
     * 各個別に、Converterの設定を行う。
     * @param cellConverter 組み立てるCellConverterのインスタンス
     * @param field フィールド情報
     * @param config システム情報
     */
    protected abstract void setupCustom(BaseCellConverter<T> cellConverter, FieldAccessor field, Configuration config);

    /**
     * {@link TextFormatter}のインスタンスを作成する。
     * @param field フィールド情報
     * @param config システム情報
     * @return {@link TextFormatter}のインスタンス
     */
    protected abstract TextFormatter<T> createTextFormatter(FieldAccessor field, Configuration config);

    /**
     * 数式を処理する{@link CellFormulaHandler}を作成する。
     * @param formulaAnno 数式のアノテーション
     * @param field フィールド情報
     * @param config システム情報
     * @return {@link CellFormulaHandler}のインスタンス
     */
    protected CellFormulaHandler createCellFormulaHandler(final XlsFormula formulaAnno, final FieldAccessor field, final Configuration config) {

        if(!formulaAnno.value().isEmpty()) {
            final String formulaExpression = formulaAnno.value();
            try {
                // EL式として正しいか検証する
                config.getFormulaFormatter().interpolate(formulaExpression, Collections.emptyMap());

            } catch(ExpressionEvaluationException e) {
                throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.invalidEL")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", XlsFormula.class)
                        .var("attrName", "value")
                        .var("attrValue", formulaExpression)
                        .format());
            }

            CellFormulaHandler handler = new CellFormulaHandler(formulaExpression);
            handler.setPrimaryFormula(formulaAnno.primary());
            return handler;

        } else if(!formulaAnno.methodName().isEmpty()) {
            // 戻り値が文字列の数式を返すメソッドを探す
            final Class<?> targetClass = field.getDeclaringClass();
            Method method = null;
            for(Method m : targetClass.getDeclaredMethods()) {
                if(m.getName().equals(formulaAnno.methodName())
                        && m.getReturnType().equals(String.class)) {
                    method = m;
                    break;
                }
            }

            if(method == null) {
                throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.notFoundMethod")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", XlsFormula.class)
                        .var("attrName", "methodName")
                        .var("attrValue", formulaAnno.methodName())
                        .varWithClass("definedClass", targetClass)
                        .format());
            }

            method.setAccessible(true);
            CellFormulaHandler handler = new CellFormulaHandler(method);
            handler.setPrimaryFormula(formulaAnno.primary());
            return handler;

        } else {
            throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.required.any")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", XlsFormula.class)
                    .varWithArrays("attrNames", "value", "methodName")
                    .format());
        }

    }

}
