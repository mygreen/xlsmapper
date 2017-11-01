package com.gh.mygreen.xlsmapper.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * メッセージのコードを生成するクラス。
 * <p>Stringの「DefaultMessageCodeResolver」を参照。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class MessageCodeGenerator {
    
    public static final String CODE_SEPARATOR = ".";
    
    /** メッセージの接頭語 */
    private String prefix = "";
    
    /** 型変換エラー時のコード */
    private String typeMismatchCode = "cellTypeMismatch";
    
    /**
     * コードの候補を生成する。
     * @param code
     * @param objectName
     * @return
     */
    public String[] generateCodes(final String code, final String objectName) {
        return generateCodes(code, objectName, null, null);
    }
    
    /**
     * 型変換エラーコードの候補を生成する。
     * @param objectName
     * @param field
     * @param filedType
     * @return
     */
    public String[] generateTypeMismatchCodes(final String objectName, final String field, final Class<?> filedType) {
        return generateCodes(getTypeMismatchCode(), objectName, field, filedType);
    }
    
    /**
     * オブジェクト名のキーの候補を生成する。
     * <p>クラスパスの区切り文字(.)がある場合、それらを除いたものを候補とする。</p>
     * @param objectName クラス名など。
     * @return キーの候補
     */
    public String[] generateObjectNameCodes(final String objectName) {
        
        ArgUtils.notNull(objectName, "objectName");
        
        final List<String> codeList = new ArrayList<String>();
        codeList.add(objectName);
        
        // オブジェクト名の最後の値を取得する
        final int dotIndex = objectName.lastIndexOf('.');
        if(dotIndex > 0) {
            final String subName = objectName.substring(dotIndex + 1);
            codeList.add(subName);
            
        }
        
        return codeList.toArray(new String[codeList.size()]);
    }
    
    /**
     * フィールド名のキーの候補を生成する。
     * @param objectName
     * @param field
     * @return
     */
    public String[] generateFieldNameCodes(final String objectName, final String field) {
        
        final List<String> codeList = new ArrayList<String>();
        codeList.addAll(Arrays.asList(generateCodes(null, objectName, field, null)));
        
        // オブジェクト名の最後の値を取得する
        final int dotIndex = objectName.lastIndexOf('.');
        if(dotIndex > 0) {
            final String subName = objectName.substring(dotIndex + 1);
            for(String code : generateCodes(null, subName, field, null)) {
                if(!codeList.contains(code)) {
                    codeList.add(code);
                }
            }
            
        }
        
        return codeList.toArray(new String[codeList.size()]);
    }
    
    /**
     * フィールドの親のキーの候補を生成する。
     * @param objectName オブジェクト名
     * @param field フィールド
     * @return
     */
    public String[] generateParentNameCodes(final String objectName, final String field) {
        
        if(Utils.isEmpty(field) || !field.contains(".")) {
            return generateObjectNameCodes(objectName);
        }
        
        // フィールド名の前の値を取得する
        final int dotIndex = field.lastIndexOf('.');
        final String subName = field.substring(0, dotIndex);
        return generateFieldNameCodes(objectName, subName);
        
    }
    
    /**
     * キーの候補を生成する。
     * <p>コンテキストのキーの形式として、次の優先順位に一致したものを返す。
     * 
     * @param code 元となるメッセージのコード
     * @param objectName オブジェクト名（クラスのフルパス）
     * @param field フィールド名 （指定しない場合はnullを設定する）
     * @param fieldType フィールドのクラスタイプ（指定しない場合はnullを設定する）
     * @return
     */
    public String[] generateCodes(final String code, final String objectName, final String field, final Class<?> fieldType) {
        
        final String baseCode = getPrefix().isEmpty() ? code : getPrefix() + code;
        final List<String> codeList = new ArrayList<>();
        
        final List<String> objectNameList = Arrays.asList(generateObjectNameCodes(objectName));
        final List<String> fieldList = new ArrayList<>();
        buildFieldList(field, fieldList);
        
        if(Utils.isNotEmpty(field)) {
            // 最後のフィールド名のみを取得する
            int dotIndex = field.lastIndexOf('.');
            if(dotIndex > 0) {
                buildFieldList(field.substring(dotIndex + 1), fieldList);
            }
        }
        
        for(final String name : objectNameList) {
            addCodes(codeList, baseCode, name, fieldList);
        }
        
        addCodes(codeList, code, null, fieldList);
        
        if(fieldType != null) {
            addCode(codeList, code, null, fieldType.getName());
            
            if(Enum.class.isAssignableFrom(fieldType)) {
                // 列挙型の場合は、java.lang.Enumとしてクラスタイプを追加する。
                addCode(codeList, code, null, Enum.class.getName());
                
            } else if(fieldType.isArray()) {
                // 配列の場合は、"配列のクラスタイプ+[]"を追加する。
                Class<?> componentType = fieldType.getComponentType();
                addCode(codeList, code, null, componentType.getName() + "[]");
                addCode(codeList, code, null, "java.lang.Object[]");
            }
        }
        
        addCode(codeList, code, null, null);
        
        return codeList.toArray(new String[codeList.size()]);
    }
    
    /**
     * フィールドのパスを分解して、パスの候補を作成する。
     * <p>インデックスを示す'[0]'を除いたりして組み立てる。
     * @param field
     * @param fieldList
     */
    protected void buildFieldList(final String field, final List<String> fieldList) {
        
        if(Utils.isEmpty(field)) {
            return;
        }
        
        if(!fieldList.contains(field)) {
            fieldList.add(field);
        }
        
        String plainField = String.valueOf(field);
        int keyIndex = plainField.lastIndexOf('[');
        while(keyIndex >= 0) {
            int endKeyIndex = plainField.indexOf(']', keyIndex);
            if(endKeyIndex >= 0) {
                plainField = plainField.substring(0, keyIndex) + plainField.substring(endKeyIndex + 1);
                
                if(!fieldList.contains(plainField)) {
                    fieldList.add(plainField);
                }
                keyIndex = plainField.lastIndexOf('[');
            } else {
                keyIndex = -1;
            }
        }
    }
    
    private void addCodes(final List<String> codeList, final String code, final String objectName, final List<String> fieldList) {
        for(String field : fieldList) {
            addCode(codeList, code, objectName, field);
        }
    }
    
    private void addCode(final List<String> codeList, final String code, final String objectName, final String field) {
        final String formattedCode = formatCode(code, objectName, field);
        if(!codeList.contains(formattedCode)) {
            codeList.add(formattedCode);
        }
    }
    
    private String formatCode(final String... elements) {
        
        // エラーコードを前に付ける場合
        StringBuilder code = new StringBuilder();
        for(String element : elements) {
            if(Utils.isNotEmpty(element)) {
                code.append(code.length() == 0 ? "" : CODE_SEPARATOR);
                code.append(element);
            }
        }
        
        return code.toString();
        
    }
    
    /**
     * メッセージの接頭語を取得する。
     * @return 接頭語を返す。デフォルトは、空文字。
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * メッセージの接頭語を設定する。
     * @param prefix 接頭語
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * 型変換エラー時のエラーコードを取得する。
     * @return デフォルトは、'{@literal cellTypeMismatch}'。
     */
    public String getTypeMismatchCode() {
        return typeMismatchCode;
    }
    
    /**
     * 型変換エラー時のエラーコードを設定する。
     * @param typeMismatchCode 型変換エラー時のエラーコード
     */
    public void setTypeMismatchCode(String typeMismatchCode) {
        this.typeMismatchCode = typeMismatchCode;
    }
}
