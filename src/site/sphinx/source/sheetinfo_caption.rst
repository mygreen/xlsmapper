--------------------------------------------------------
シート上の見出しの取得
--------------------------------------------------------


読み込み時、書き込み時にマッピングしたセルの見出しを取得することができます。

入力値検証の際などのメッセージの引数に使用したりします。

取得方法は複数ありますが、 ``Map<String, String> labels`` フィールドを用いるのが記述量が少なく簡単だと思います。
 
.. note:: 
   
   セルの見出しを取得できるのは、アノテーション ``@XlsLabelledCell, @XlsColumn, @XlsMapColumns`` を付与したプロパティです。



1. ``Map<String, String> labels`` というフィールドを定義しておくとプロパティ名をキーにセルの位置がセットされるようになっています。
 
  * アノテーション ``@XlsMapColumns`` のセルの位置情報のキーは、 *\<プロパティ名\>[<セルの見出し\>]* としてセットされます。
 
  * アノテーション ``@XlsArrayColumns`` のセルの位置情報のキーは、 *\<プロパティ名\>[<インデックス\>]* としてセットされます。
  
2. アノテーションを付与した *\<setterメソッド名\>Label* というメソッドを用意しておくと、引数にセルの位置が渡されます。
 
  * 位置情報を取得用のsetterメソッドは、引数 ``String`` 型を取る必要があります。
  * ただし、``@XlsMapColumns`` に対するsetterメソッドは、第一引数にセルの見出しが必要になります。
  
    * String key, String label
     
  * ただし、``@XlsArrayColumns`` に対するsetterメソッドは、第一引数にセルのインデックスが必要になります。
  
    * int index, String label

3. アノテーションを付与した *\<フィールド名\>Label* というString型のフィールドを用意しておくと、セルの位置が渡されます。
 
  * ただし、``@XlsMapColumns`` に対するフィールドは、``Map<String, String>`` 型にする必要があります。キーには見出しが入ります。
  
  * また、``@XlsArrayColumns`` に対するフィールドは、``List<String>`` 型にする必要があります。


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 汎用的な見出し情報
        public Map<String, String> labels;
        
        // プロパティごとに個別に見出し情報を定義するフィールド
        private String nameLabel;
        
        @XlsColumns(label="名前")
        private String name;
        
        // プロパティごとに個別に見出し情報を定義するメソッド
        // フィールド labelsが定義あれば必要ありません。
        public void setNameLabel(String label) {
            //...
        }
        
        // @XlsMapColumnsの場合
        @XlsMapColumns(previousColumnName="出欠")
        private Map<String, String> attendedMap;
        
        // プロパティごとに個別に見出し情報を定義するフィールド
        private Map<String, String> attendedMapLabel;
        
        // プロパティごとに個別に見出し情報を定義するメソッド
        // @XlsMapColumnsの場合keyは、セルの見出しの値
        // フィールド labelsが定義あれば必要ありません。
        public void setAttendedMapLabel(String key, String label) {
            //...
        }
        
        // @XlsArrayColumnsの場合
        ＠XlsArrayColumns(columnName="ふりがな")
        private List<String> rubyList;
        
        // プロパティごとに個別に見出し情報を定義するフィールド
        private Map<String, String> rubyListLabel;
        
        // プロパティごとに個別に見出し情報を定義するメソッド
        // @XlsArrayColumnsの場合indexは、インデックスの値
        // フィールド labelsが定義あれば必要ありません。
        public void setRubyListLabel(int index, String label) {
            //...
        }
        
    
    }


.. note:: 
   
   フィールド ``Map<String, String> labels`` と対応するsetterメソッドやフィールドをそれぞれ定義していた場合、
   優先度 *labels > setterメソッド > フィールド* に従い設定されます。

