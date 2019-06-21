--------------------------------------------------------
シート上のコメントの取得
--------------------------------------------------------


読み込み時にマッピングしたセルに設定されているコメントを取得することができます。 `[ver.2.1]`

書き込み時は、セルにコメントを設定することができます。

取得方法は複数ありますが、 ``Map<String, String> comments`` フィールドを用いるのが記述量が少なく簡単だと思います。
 
.. note:: 
   
   セルのコメントを取得できるのは、アノテーション ``@XlsCell, @XlsLabelledCell, @XlsColumn, @XlsMapColumns, @XlsArrayColumns, @XlsArrayCell, @XlsLabelledArrayCell`` を付与したプロパティです。
   
   見出しセルに対するコメントの取得は、アノテーション :ref:`@XlsLablledComment <annotationXlsLabelledComment>` を使用します。
   さらに、任意の位置のセルのコメントの取得は、アノテーション :ref:`@XlsComment <annotationXlsComment>` を使用します。


1. ``Map<String, String> comments`` というフィールドを定義しておくとプロパティ名をキーにセルの位置がセットされるようになっています。
 
  * アノテーション ``@XlsMapColumns`` のセルのコメント情報のキーは、 *\<プロパティ名\>[<セルの見出し\>]* としてセットされます。

  * アノテーション ``@XlsArrayColumns`` のセルのコメント情報のキーは、 *\<プロパティ名\>[<インデックス\>]* としてセットされます。
 
2. アノテーションを付与した *\<setterメソッド名\>Comment* というメソッドを用意しておくと、引数にセルのコメントが渡されます。
 
  * コメント情報取得用のsetterメソッドは、引数 ``String`` 型を取る必要があります。
  * ただし、``@XlsMapColumns`` に対するsetterメソッドは、第一引数にセルの見出しが必要になります。
  
    * String key, String label
     
  * また、``@XlsArraysColumns`` に対するsetterメソッドは、第一引数にセルのインデックスが必要になります。
  
    * int index, String label
    
3. アノテーションを付与した *\<フィールド名\>Comment* というString型のフィールドを用意しておくと、セルのコメントが渡されます。
 
  * ただし、``@XlsMapColumns`` に対するフィールドは、``Map<String, String>`` 型にする必要があります。キーには見出しが入ります。

  * また、``@XlsArrayColumns`` に対するフィールドは、``List<String>`` 型にする必要があります。


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 汎用的なコメント情報
        public Map<String, String> comments;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private String nameComment;
        
        @XlsColumns(label="名前")
        private String name;
        
        // プロパティごとに個別にコメント情報を定義するメソッド
        // フィールド commentsが定義あれば必要ありません。
        public void setNameComment(String comment) {
            //...
        }
        
        // @XlsMapColumnsの場合
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private Map<String, String> attendedMapComment;
        
        // プロパティごとに個別に見出し情報を定義するメソッド
        // @XlsMapColumnsの場合keyは、セルの見出しの値
        // フィールド commentsが定義あれば必要ありません。
        public void setAttendedMapComment(String key, String comment) {
            //...
        }
        
        // @XlsArrayColumnsの場合
        ＠XlsArrayColumns(columnName="ふりがな")
        private List<String> rubyList;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private Map<String, String> rubyListComment;
        
        // プロパティごとに個別にコメント情報を定義するメソッド
        // @XlsArrayColumnsの場合indexは、インデックスの値
        // フィールド labelsが定義あれば必要ありません。
        public void setRubyListComment(int index, String comment) {
            //...
        }
    
    }


.. note:: 
   
   フィールド ``Map<String, String> comments`` と対応するsetterメソッドやフィールドをそれぞれ定義していた場合、
   優先度 *comments > setterメソッド > フィールド* に従い設定されます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時のコメント情報の設定方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時は、読み込み時と同様に複数の定義方法があります。

書き込むコメント情報の定義方法は複数ありますが、 ``Map<String, String> comments`` フィールドを用いるのが記述量が少なく簡単だと思います。

各プロパティに対するメソッドを定義しておけば、getterメソッド経由で取得されます。


.. sourcecode:: java
    :linenos:
    
    // 書き込むデータの定義
    SampleSheet sheet = new SampleSheet();
    
    SampleRecord record = new SampleRecord();
    
    // コメントを保持するフィールドのインスタンス定義
    record.comments = new HashMal<>();
    
    // プロパティ「name」に対するコメントを設定する
    record.comments.put("name", "コメント1");
    
    // @XlsMapColumnsに対するコメントを設定する。
    record.comments.put("attendedMap[4月1日]", "コメント2");
    record.comments.put("attendedMap[4月2日]", "コメント3");
    
    // @XlsArrayColumnsNi対するコメントを設定する。
    record.comments.put("rubyList[0]", "コメント4");
    record.comments.put("rubyList[1]", "コメント5");
    
    // レコードの定義
    public class SampleRecord {
        
        // 汎用的なコメント情報
        public Map<String, String> comments;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private String nameComment;
        
        @XlsColumns(label="名前")
        private String name;
        
        // プロパティごとに個別にコメント情報を定義するメソッド
        // フィールド commentsが定義あれば必要ありません。
        public String getNameComment() {
            //...
        }
        
        // @XlsMapColumnsの場合
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private Map<String, String> attendedMapComment;
        
        // プロパティごとに個別に見出し情報を定義するメソッド
        // @XlsMapColumnsの場合keyは、セルの見出しの値
        // フィールド commentsが定義あれば必要ありません。
        public String getAttendedMapComment(String key) {
            //...
        }
        
        // @XlsArrayColumnsの場合
        ＠XlsArrayColumns(columnName="ふりがな")
        private List<String> rubyList;
        
        // プロパティごとに個別にコメント情報を定義するフィールド
        private Map<String, String> rubyListComment;
        
        // プロパティごとに個別にコメント情報を定義するメソッド
        // @XlsArrayColumnsの場合indexは、インデックスの値
        // フィールド labelsが定義あれば必要ありません。
        public String setRubyListComment(int index) {
            //...
        }
    
    }
    

.. note:: 

    書き込み時のコメントの枠サイズなどは、アノテーション :ref:`@XlsCommentOption <annotationXlsCommentOption>` を使用します。
    
    フォントなどは、セルの設定値を引き継ぎます。
    ただし、すでにコメントが設定されている場合は、設定されている書式に従います。
    


