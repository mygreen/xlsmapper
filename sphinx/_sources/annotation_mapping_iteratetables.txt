
.. _annotationXlsIterateTables:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsIterateTables``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

同一の構造の表がシート内で繰り返し出現する場合に使用します。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
基本的な使い方
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性 ``tableLabel`` で繰り返し部分の表の名称を指定します。

また、属性bottomは、``@XlsIterateTables`` 内で :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用する場合に、
テーブルの開始位置が ``@XlsIterateTables`` の表の名称セルからどれだけ離れているかを指定します。

.. figure:: ./_static/IterateTables.png
   :align: center
   
   IterateTables


.. sourcecode:: java
    
    // シート用クラス
    @XlsSheet(name="シート名")
    public class SampleSheet {
    
        @XlsIterateTables(tableLabel="部門情報", bottom=2)
        private List<SampleTable> tables;
    }


繰り返し部分に対応するJavaBeanでは以下のように、アノテーション :ref:`@XlsLabelledCell <annotationXlsLabelledCell>` :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用することができます。

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用する場合、属性tableLabel、``@XlsIterateTables`` の属性tableLabelと同じ値を指定する必要がある点に注意してください。

.. sourcecode:: java
    
    // テーブル用クラス
    public class SampleTable {
        
        @XlsLabelledCell(label="部門名", type=LabelledCellType.Right)
        private String deptName;
        
        @XlsHorizontalRecords(tableLabel="部門情報")
        private List<SampleRecord> records;
    }


繰り返し部分に対応するJavaBeanで :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用した場合、通常の場合と同じく :ref:`@XlsColumn <annotationXlsColumn>` や :ref:`@XlsMapColumns <annotationXlsMapColumns>` で列とのマッピングを行います。

.. sourcecode:: java
    
    // レコード用クラス
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private String id;
        
        @XlsColumn(columnName="名前")
        private String name;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
表の名称を正規表現、正規化して指定する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。 `[ver1.1+]`

* 正規表現で指定する場合、アノテーションの属性の値を ``/正規表現/`` のように、スラッシュで囲み指定します。
  
  * スラッシュで囲まない場合、通常の文字列として処理されます。
  
  * 正規表現の指定機能を有効にするには、:doc:`システム設定のプロパティ <otheruse_config>` ``regexLabelText`` の値を trueに設定します。
  
* ラベセルの値に改行が空白が入っている場合、それらを除去し正規化してアノテーションの属性値と比較することが可能です。
  
  * 正規化とは、空白、改行、タブを除去することを指します。
   
  * ラベルを正規化する機能を有効にするには、:doc:`システム設定のプロパティ <otheruse_config>` ``normalizeLabelText`` の値を trueに設定します。
  

これらの指定が可能な属性は、``tableLabel`` です。

.. sourcecode:: java
    
    // システム設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfig()
            .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
            .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
    
    // シート用クラス
    @XlsSheet(name="シート名")
    public class SampleSheet {
        
        // 正規表現による指定
        @XlsIterateTables(tableLabel="/部門情報.+/", bottom=2)
        private List<SampleTable> tables;
        
    }



