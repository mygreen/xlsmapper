package com.gh.mygreen.xlsmapper.cellconverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 処理ケースが該当する
 *
 *
 * @param <T> 保持する値のクラスタイプ
 * @author T.TSUCHIE
 */
public class OptionalProcessCase<T> {

    /**
     * 空オブジェクト
     */
    private static final OptionalProcessCase<?> EMPTY = new OptionalProcessCase<>(Optional.empty(), Collections.emptyList());

    private final Optional<T> value;

    private final Set<ProcessCase> cases;

    private OptionalProcessCase(Optional<T> value, Collection<ProcessCase> cases) {
        this.value = value;
        this.cases = new HashSet<>(cases);
    }

    /**
     * 指定された非null値を含む{@link OptionalProcessCase} を返します。
     * @param value 存在する値、非nullである必要がある
     * @param cases 処理ケース。
     * @return 存在する値での{@link OptionalProcessCase}.
     */
    public static <T> OptionalProcessCase<T> of(T value, ProcessCase... cases) {
        return new OptionalProcessCase<>(Optional.of(value), Arrays.asList(cases));

    }

    /**
     * 指定された値がnullでない場合はその値を記述する{@link OptionalProcessCase}を返し、
     * それ以外の場合は空の{@link OptionalProcessCase}を返します。
     *
     * @param value 記述する値(nullも可)
     * @param cases 処理ケース。
     * @return 指定された値がnullでない場合は存在する値での{@link OptionalProcessCase}、それ以外の場合は空の{@link OptionalProcessCase}.
     */
    public static <T> OptionalProcessCase<T> ofNulable(T value, ProcessCase... cases) {
        return new OptionalProcessCase<>(Optional.ofNullable(value), Arrays.asList(cases));
    }

    /**
     * 空の{@link OptionalProcessCase}インスタンスを返します。この{@link OptionalProcessCase}の値は存在しません。
     * @return 空の{@link OptionalProcessCase}
     */
    @SuppressWarnings("unchecked")
    public static <T> OptionalProcessCase<T> empty() {
        return (OptionalProcessCase<T>)EMPTY;
    }

    /**
     * 存在する値がある場合はtrueを返し、それ以外の場合はfalseを返します。
     * 処理ケースに該当する必要もあります。
     * @param processCase 処理ケース
     * @return 存在する値がない場合はtrue、それ以外の場合はfalse
     */
    public boolean isPresent(final ProcessCase processCase) {
        return value.isPresent() & contains(processCase);
    }

    /**
     * この{@link OptionalProcessCase}に値が存在する場合は値を返し、それ以外の場合は{@link NoSuchElementException}を返します。
     * @param processCase 処理ケース
     * @return この{@link NoSuchElementException}が保持する非null値
     * @throws NoSuchElementException 存在する値がない場合
     */
    public T get(final ProcessCase processCase) {

        if(!value.isPresent() || !isPresent(processCase)) {
            throw new NoSuchElementException(String.format("No value present in processCase '%s'.", processCase.name()));
        }

        return value.get();
    }

    /**
     * 処理ケースに該当するうかどうか判定します。
     * 処理ケースが空の場合は、全てに該当します。
     * @param processCase 判定する処理ケース
     * @return trueの場合、該当する。
     */
    public boolean contains(final ProcessCase processCase) {
        Objects.requireNonNull(processCase);

        if(cases.isEmpty()) {
            return true;
        }

        return cases.contains(processCase);

    }

}
