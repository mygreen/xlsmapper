package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * オブジェクトのフィールドであるセルのエラー情報を保持するクラスです。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldError extends ObjectError {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * フィールド名
     */
    private final String field;
    
    /**
     * 型変換に失敗したときのエラーかどうか
     */
    private final boolean conversionFailure;
    
    /**
     * エラーとなる値
     */
    private Object rejectedValue;
    
    /**
     * セルのアドレス情報
     */
    private CellPosition address;
    
    /**
     * コンストラクタ
     * 
     * @param objectName オブジェクト名
     * @param field フィールド名
     * @param conversionFailure 型変換に失敗したときのエラーかどうか
     * @param codes メッセージコード
     * @param variables メッセージの引数
     */
    public FieldError(final String objectName, final String field, final boolean conversionFailure,
            final String[] codes, final Map<String, Object> variables) {
        super(objectName, codes, variables);
        
        this.field = field;
        this.conversionFailure = conversionFailure;
    }
    
    /**
     * 型変換に失敗したかどうか。
     * <p>型変換に失敗した場合、検証対象のBeanやフィールドに値が設定されないないため、
     *    後から値を検証する際に検証をスキップする判定に利用する。
     * </p>
     * @return trueの場合、型変換にしっぱいしたエラー。
     */
    public boolean isConversionFailure() {
        return conversionFailure;
    }    
    
    /**
     * フィールド名を取得する。
     * <p>ネストしている場合は、親のパスを付与した形式（e.g. person.name）となります。</p>
     * @return Beanにされたフィールドの名称を返す。
     */
    public String getField() {
        return field;
    }
    
    /**
     * エラートとなったフィールドの値を取得する。
     * <p>ただし、型変換エラーの場合、変換前の値となります。</p>
     * @return フィールドの値。
     */
    public Object getRejectedValue() {
       return rejectedValue;
    }
    
    /**
     * エラートとなったフィールドの値を設定する。
     * <p>ただし、型変換エラーの場合、変換前の値となります。</p>
     * @param rejectedValue フィールドの値。
     */
    public void setRejectedValue(Object rejectedValue) {
       this.rejectedValue = rejectedValue;
    }
    
    /**
     * セルのアドレス情報を取得します。
     * @return 設定されていない場合は、空を返します。
     */
    public Optional<CellPosition> getAddressAsOptional() {
        return Optional.ofNullable(address);
    }
    
    /**
     * セルのアドレス情報を取得します。
     * @return 設定されていない場合は、nullを返します。
     */
    public CellPosition getAddress() {
        return address;
    }
    
    /**
     * セルのアドレス情報を設定します。
     * @param address アドレス情報
     */
    public void setAddress(CellPosition address) {
        this.address = address;
    }
    
}
