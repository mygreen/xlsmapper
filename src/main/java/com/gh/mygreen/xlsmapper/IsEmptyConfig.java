package com.gh.mygreen.xlsmapper;



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
    private boolean testArrayValue;
    
    /**
     * Collectionの場合、値も対象とするかどうか。
     */
    private boolean testCollectionValue;
    
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
        this.testArrayValue = true;
        this.testCollectionValue = true;
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
    public boolean isTestArrayValue() {
        return testArrayValue;
    }
    
    /**
     * 配列の値も検証対象とするかどうかを設定します設定します。
     * @param testArrayValue 配列の値も検証対象とするかどうか
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestArrayValue(boolean testArrayValue) {
        this.testArrayValue = testArrayValue;
        return this;
    }
    
    /**
     * Collectionの値も検証対象とするかどうか。
     * @return true:Collectionの値も検証対象とする。初期値はtrueです。
     */
    public boolean isTestCollectionValue() {
        return testCollectionValue;
    }
    
    /**
     * Collectionの値も検証対象とするかどうかを設定します設定します。
     * @param testCollectionValue Collectionの値も検証対象とするかどうか
     * @return 自身のインスタンス
     */
    public IsEmptyConfig withTestCollectionValue(boolean testCollectionValue) {
        this.testCollectionValue = testCollectionValue;
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
