
.. _annotationXlsIterateTables:

-------------------------------------
``@XlsIterateTables``
-------------------------------------

同一の構造の表がシート内で繰り返し出現する場合に使用し、Collection(List、Set)または配列にマッピングします。
次のアノテーションを組み合わせて構成します。

* 横方向の表をマッピングするアノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 。
* 縦方向の表をマッピングするアノテーション :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` `[ver.2.0+]` 。

  * ただし、アノテーション ``@XlsHorizontalRecords`` と同時に使用することはできません。

* 見出し付きの1つのセルをマッピングするアノテーション :ref:`@XlsLabelledCell <annotationXlsLabelledCell>` 。
* 見出し付きの連続し隣接する複数のセルをマッピングするアノテーション :ref:`@XlsLabelledArrayCells <annotationXlsLabelledArrayCells>` `[ver.2.0+]`。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
基本的な使い方
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``tableLabel`` で繰り返し部分の表の名称を指定します。

また、属性bottomは、``@XlsIterateTables`` 内で :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用する場合に、
テーブルの開始位置が ``@XlsIterateTables`` の表の名称セルからどれだけ離れているかを指定します。

.. figure:: ./_static/IterateTables.png
   :align: center
   
   IterateTables


.. sourcecode:: java
    :linenos:
    :caption: シート用クラスの定義
    
    @XlsSheet(name="シート名")
    public class SampleSheet {
    
        @XlsIterateTables(tableLabel="部門情報", bottom=2)
        private List<SampleTable> tables;
    }


繰り返し部分に対応するJavaBeanでは以下のように、アノテーション :ref:`@XlsLabelledCell <annotationXlsLabelledCell>` :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用することができます。

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用する場合、属性tableLabel は設定する必要はありません。
``@XlsIterateTables`` の属性 tableLabelとbottomの値を引き継ぐため、指定しなくても問題ないためです。

.. sourcecode:: java
    :linenos:
    :caption: テーブル用クラスの定義
    
    public class SampleTable {
        
        @XlsLabelledCell(label="部門名", type=LabelledCellType.Right)
        private String deptName;
        
        @XlsHorizontalRecords(terminal=RecordTerminal.Border)
        private List<SampleRecord> records;
    }


繰り返し部分に対応するJavaBeanで :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を使用した場合、通常の場合と同じく :ref:`@XlsColumn <annotationXlsColumn>` や :ref:`@XlsMapColumns <annotationXlsMapColumns>` で列とのマッピングを行います。

.. sourcecode:: java
    :linenos:
    :caption: レコード用クラスの定義
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private String id;
        
        @XlsColumn(columnName="名前")
        private String name;
    }


.. note::
    
    * ver.2.0から、Collection型(List型、Set型)にも対応しています。
    * インタフェースの型を指定する場合、次の実装クラスのインスタンスが設定されます。
    
      * List型の場合、 ``java.util.ArrayList`` クラス。
      * Set型の場合、 ``java.util.LinkedHashSet`` クラス。
      * Collection型の場合、 ``java.util.ArrayList`` クラス。
    
    * 実装クラスを指定した場合、そのインスタンスが設定されます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
縦方向の表を組み合わせてマッピングする場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

縦方向の表をマッピングするアノテーション :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` も使用することができます。

* ただし、横方向の表をマッピングするアノテーション ``@XlsHorizontalRecords`` と同時に使用することはできません。
* 属性 ``tableLabelAbove=true`` が自動的に有効になり、表の見出しが上部にあることを前提に処理されます。


.. figure:: ./_static/IterateTables_VerticalRecords.png
   :align: center
   
   IterateTables(縦方向)


.. sourcecode:: java
    :linenos:
    
    // シート用クラス
    @XlsSheet(name="観測データ")
    public class SampleSheet {
    
        @XlsIterateTables(tableLabel="/観測情報.+/", bottom=2)
        private List<DataTable> tables;
    }
    
    // テーブル用クラス
    public class DataTable {
        
        @XlsLabelledCell(label="日付", type=LabelledCellType.Right)
        private LocalDate date;
        
        @XlsVerticalRecords(terminal=RecordTerminal.Border)
        private List<WeatherRecord> records;
    }
    
    // レコード用クラス
    public class WeatherRecord {
        
        @XlsColumn(columnName="時間")
        private String time;
        
        @XlsColumn(columnName="降水")
        private double precipitation;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
表の名称を正規表現、正規化して指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。 `[ver1.1+]`

* 正規表現で指定する場合、アノテーションの属性の値を ``/正規表現/`` のように、スラッシュで囲み指定します。
  
  * スラッシュで囲まない場合、通常の文字列として処理されます。
  
  * 正規表現の指定機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``regexLabelText`` の値を trueに設定します。
  
* ラベセルの値に改行が空白が入っている場合、それらを除去し正規化してアノテーションの属性値と比較することが可能です。
  
  * 正規化とは、空白、改行、タブを除去することを指します。
   
  * ラベルを正規化する機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``normalizeLabelText`` の値を trueに設定します。
  

これらの指定が可能な属性は、``tableLabel`` です。

.. sourcecode:: java
    :linenos:
    
    // システム設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfiguration()
            .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
            .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
    
    // シート用クラス
    @XlsSheet(name="シート名")
    public class SampleSheet {
        
        // 正規表現による指定
        @XlsIterateTables(tableLabel="/部門情報.+/", bottom=2)
        private List<SampleTable> tables;
        
    }



