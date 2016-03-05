

.. _annotationXlsMapColumns:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsMapColumns``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
基本的な使い方
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` もしくは :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` において、
指定されたレコード用クラスのカラム数が可変の場合に、それらのカラムを ``java.util.Map`` として設定します。

BeanにはMapを引数に取るフィールドまたはメソッドを用意し、このアノテーションを記述します。

属性 ``previousColumnName`` で指定された次のカラム以降、カラム名をキーとしたMapが生成され、Beanにセットされます。

.. figure:: ./_static/MapColumns.png
   :align: center
   
   MapColumns


.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
型変換する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

アノテーション :ref:`@XlsConverter <annotationXlsConverter>` などで型変換を適用するときは、Mapの値が変換対象となります。
マップのキーは必ずString型を指定してください。

.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        // 型変換用のアノテーションを指定した場合、Mapの値に適用されます。
        @XlsMapColumns(previousColumnName="名前")
        @XlsBooleanConverter(loadForTrue={"出席"}, loadForFalse={"欠席"},
                saveAsTrue="出席", saveAsFalse"欠席"
                failToFalse=true)
        private Map<String, Boolean> attendedMap;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
位置情報／見出し情報を取得する際の注意事項
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

マッピング対象のセルのアドレスを取得する際に、フィールド ``Map<String, Point> positions`` を定義しておけば、自動的にアドレスがマッピングされます。

通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。

アノテーション ``@XlsMapColumns`` でマッピングしたセルのキーは、 `\<プロパティ名\>[\<セルの見出し\>]` の形式になります。


同様に、マッピング対象の見出しを取得する、フィールド ``Map<String, String> labels`` へのアクセスも、
キーは、 `\<プロパティ名\>[\<セルの見出し\>]` の形式になります。


.. figure:: ./_static/MapColumns_positions.png
   :align: center
   
   MapColumns(positions/labels)


.. sourcecode:: java
    
    public class SampleRecord {
        
        // 位置情報
        private Map<String, Point> positions;
        
        // 見出し情報
        private Map<String, String> labels;
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
    }
    
    // 位置情報・見出し情報へのアクセス
    SampleRecord record = /* レコードのインスタンスの取得 */;
    
    Point position = record.positions.get("attendedMap[4月2日]");
    
    String label = recrod.labeles.get("attendedMap[4月2日]");
    


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
見出しを正規表現、正規化して指定する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。 `[ver1.1+]`

* 正規表現で指定する場合、アノテーションの属性の値を ``/正規表現/`` のように、スラッシュで囲み指定します。
  
  * スラッシュで囲まない場合、通常の文字列として処理されます。
  
  * 正規表現の指定機能を有効にするには、:doc:`システム設定のプロパティ <otheruse_config>` ``regexLabelText`` の値を trueに設定します。
  
* ラベセルの値に改行が空白が入っている場合、それらを除去し、正規化してアノテーションの属性値と比較することが可能です。
  
  * 正規化とは、空白、改行、タブを除去することを指します。
   
  * ラベルを正規化する機能を有効にするには、システム設定のプロパティ ``normalizeLabelText`` の値を trueに設定します。
  

これらの指定が可能な属性は、``previousColumnName`` です。


.. sourcecode:: java
    
    // システム設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfig()
            .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
            .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
    
    // レコード用クラス
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        // 正規表現による指定
        @XlsColumn(columnName="/名前.+/")
        private String name;
        
        // 正規表現による指定
        @XlsMapColumns(previousColumnName="/名前.+/")
        private Map<String, String> attendedMap;
        
    }





