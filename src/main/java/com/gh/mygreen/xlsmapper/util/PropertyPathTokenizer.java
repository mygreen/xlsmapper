package com.gh.mygreen.xlsmapper.util;

import java.util.LinkedList;

import com.gh.mygreen.xlsmapper.util.PropertyPath.Token;

/**
 * プロパティアクセス用の式をパースする。
 * <p>{@link PropertyValueNavigator}で使用する。
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyPathTokenizer {
    
    /**
     * エスケープ文字（バックスペース\\）
     */
    private static final String STR_ESCAPE_CHAR = "\\";
    
    /**
     * プロパティのアクセス用の書式をトークンに分割する。
     * @param path
     * @return
     * @throws IllegalArgumentException path is null or empty.
     */
    public PropertyPath parse(final String path) {
        
        ArgUtils.notEmpty(path, "path");
        
        final PropertyPath tokenStore = new PropertyPath(path);
        splitToken(tokenStore, path);
        return tokenStore;
    }
    
    /**
     * パスを最小限の項目に分割する。
     * 
     * @param tokenStore 分割した結果
     * @param path パス
     * 
     */
    private void splitToken(final PropertyPath tokenStore, final String path) {
        
        // 解析時の途中の文字を一時的に補完しておく。
        final LinkedList<String> stack = new LinkedList<String>();
        
        final int length = path.length();
        for(int i=0; i < length; i++) {
            final char c = path.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, STR_ESCAPE_CHAR)) {
                // スタックの一番上がエスケープの文字の場合、通常の文字として扱う。
                stack.push(String.valueOf(c));
                
            } else if (c == '.') {
                
                if(StackUtils.equalsBottomElement(stack, "[")) {
                    // キーの囲み文字の途中の場合、文字列としてスタックに積む。
                    stack.push(String.valueOf(c));
                    
                } else if(!stack.isEmpty()) {
                    // 既に文字が入っている場合は、既存のものを取り出し分割する。
                    tokenStore.add(new Token.Name(StackUtils.popupAndConcat(stack).trim()));
                    tokenStore.add(new Token.Separator());
                    
                } else {
                    tokenStore.add(new Token.Separator());
                }
                
            } else if(c == '[') {
                
                if(!stack.isEmpty()) {
                    // 既に文字が入っている場合は、既存のものを取り出し分割する。
                    tokenStore.add(new Token.Name(StackUtils.popupAndConcat(stack).trim()));
                }
                
                stack.push(String.valueOf(c));
                
            } else if(c == ']') {
                if(StackUtils.equalsBottomElement(stack, "[")) {
                    // 条件の終わりの場合
                    tokenStore.add(new Token.Key(StackUtils.popupAndConcat(stack) + c));
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
            } else if(c == ' ') {
                // キーの囲み文字の中であれば、空白は無視しない。
                if(StackUtils.equalsBottomElement(stack, "[")) {
                    stack.push(String.valueOf(c));
                }
                
            } else {
                stack.push(String.valueOf(c));
            }
            
        }
        
        if(!stack.isEmpty()) {
            tokenStore.add(new Token.Name(StackUtils.popupAndConcat(stack).trim()));
        }
        
    }
    
}
