package com.gh.mygreen.xlsmapper.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 解析済みのプロパティ式のオブジェクト。
 * 
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyPath implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -4273945900442385415L;
    
    /**
     * 解析元のパス
     */
    private final String path;
    
    /**
     * パスを解析してトークンに分解した結果。
     */
    private final List<Token> tokens;
    
    /**
     * 解析元のパスを指定するコンストラクタ。
     * @param path 解析元のパス
     * @throws IllegalArgumentException path is null or empty.
     */
    public PropertyPath(final String path) {
        ArgUtils.notEmpty(path, "path");
        this.path = path;
        this.tokens = new ArrayList<>();
    }
    
    /**
     * 解析元のパスを取得する。
     * @return
     */
    public String getPath() {
        return path;
    }
    
    /**
     * トークンを追加する。
     * @param token
     */
    public void add(final Token token) {
        this.tokens.add(token);
    }
    
    /**
     * トークンに分割した結果を取得する。
     * @return
     */
    public List<Token> getPathAsToken() {
        return new ArrayList<>(tokens);
    }
    
    /**
     * パスを分割したもの。
     * @since 1.0
     * @author T.TSUCHIE
     *
     */
    public static class Token implements Serializable {
        
        /** serialVersionUID */
        private static final long serialVersionUID = 2281725403723210214L;
        
        /** トークンの文字 */
        private final String token;
        
        /**
         * エスケープ文字を除去したした文字
         */
        private final String value;
        
        public Token(String token) {
            this.token = token;
            this.value = Utils.removeEscapeChar(token, '\\');
        }
        
        /**
         * トークンの値を取得する
         * @return
         */
        public String getToken() {
            return token;
        }
        
        /**
         * エスケープ文字を取得する。
         * @return
         */
        public String getValue() {
            return value;
        }
        
        /**
         * 括弧で囲まれた条件の書式'[キー]'を表すトークン。
         */
        public static class Key extends Token {
            
            /** serialVersionUID */
            private static final long serialVersionUID = -5951809427315756202L;

            public Key(String value) {
                super(value);
            }
            
            /**
             * 括弧を除いた条件の値の取得。
             * @return
             */
            public String getKey() {
                int length = getValue().length();
                return getValue().substring(1, length-1);
            }
            
        }
        
        /**
         * パスの区切り文字"."を表すトークン。
         *
         */
        public static class Separator extends Token {
            
            /** serialVersionUID */
            private static final long serialVersionUID = -9021975741257702192L;
            
            public Separator() {
                super(".");
            }
            
        }
        
        /**
         * プロパティ名を表すトークン。
         *
         */
        public static class Name extends Token {
            
            /** serialVersionUID */
            private static final long serialVersionUID = -1196971215805919815L;
            
            public Name(String value) {
                super(value);
            }
            
        }
    }
    
}
