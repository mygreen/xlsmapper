package com.gh.mygreen.xlsmapper.util;



/**
 * {@link IsEmptyBuilder}の設定を組み立てるクラス。
 * 
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class IsEmptyConfig {
    
    /**
     * 数値の場合、0を空として扱うか。
     */
    private boolean zeroAsEmpty;
    
    /**
     * 配列の場合、値も対象とするかどうか。
     */
    private boolean testArrayElement;
    
    /**
     * Collectionの場合、値も対象とするかどうか。
     */
    private boolean testCollectionElement;
    
    /**
     * Mapの場合、値も対象とするかどうか。
     */
    private boolean testMapValue;
    
    /**
     * ransientが付与されたフィールドも対象とするかどうか。
     */
    private boolean testTransient;
    
    /**
     * インスタンスを作成する。
     * @return
     */
    public static IsEmptyConfig create() {
        return new IsEmptyConfig();
    }
    
    /**
     * コンストラクタ
     */
    public IsEmptyConfig() {
        
        this.zeroAsEmpty = true;
        this.testArrayElement = true;
        this.testCollectionElement = true;
        this.testMapValue = true;
        this.testTransient = false;
    }
    
    /**
     * 数値の0を空として扱うかどうか。
     * @return true:0を空として扱う。初期値はtrueです。
     */
    public boolean isZeroAsEmpty() {
        return zeroAsEmpty;
    }
    
    /**
     * 数値の0を空として扱うかどうか設定します。
     * @param zeroAsEmpty 数値の0を空として扱うかどうか。
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withZeroAsEmpty(boolean zeroAsEmpty) {
        this.zeroAsEmpty = zeroAsEmpty;
        return this;
        
    }
    
    /**
     * 配列の値も検証対象とするかどうか。
     * @return true:配列の値も検証対象とする。初期値はtrueです。
     */
    public boolean isTestArrayElement() {
        return testArrayElement;
    }
    
    /**
     * 配列の値も検証対象とするかどうかを設定します設定します。
     * @param testArrayElement 配列の値も検証対象とするかどうか
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestArrayElement(boolean testArrayElement) {
        this.testArrayElement = testArrayElement;
        return this;
    }
    
    /**
     * Collectionの値も検証対象とするかどうか。
     * @return true:Collectionの値も検証対象とする。初期値はtrueです。
     */
    public boolean isTestCollectionElement() {
        return testCollectionElement;
    }
    
    /**
     * Collectionの値も検証対象とするかどうかを設定します設定します。
     * @param testCollectionElement Collectionの値も検証対象とするかどうか
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestCollectionElement(boolean testCollectionElement) {
        this.testCollectionElement = testCollectionElement;
        return this;
    }
    
    /**
     * Mapの値も検証対象とするかどうか。
     * @return true:Mapの値も検証対象とする。初期値はtrueです。
     */
    public boolean isTestMapValue() {
        return testMapValue;
    }
    
    /**
     * Mapの値も検証対象とするかどうかを設定します設定します。
     * @param testMapValue Mapの値も検証対象とするかどうか
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestMapValue(boolean testMapValue) {
        this.testMapValue = testMapValue;
        return this;
    }
    
    /**
     * transientが付与されたフィールドも対象とするかどうか。
     * @return true:場合テスト対象となります。初期値はfalseです。
     */
    public boolean isTestTransient() {
        return testTransient;
    }
    
    /**
     * transientが付与されたフィールドも対象とするかどうか設定します。
     * @param testTransient transientが付与されたフィールドも対象とするかどうか。
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestTransient(boolean testTransient) {
        this.testTransient = testTransient;
        return this;
    }
    
}
