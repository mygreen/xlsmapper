

.. _annotationXlsVerticalRecords:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsVerticalRecords``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

垂直方向に連続する列をListまたは配列にマッピングします。
要するに :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を垂直方向にしたものです。

メソッドに定義する場合、:ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` と同じくList型の引数を1つだけ取るsetterメソッドに対して付与します。

ここでは、アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` と異なる部分を説明します。
共通の使い方は、アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` の説明を参照してください。

.. figure:: ./_static/VerticalRecord.png
   :align: center
   
   VerticalRecords


.. sourcecode:: java
    
    // シート用クラス
    @XlsSheet(name="Weather")
    public class SampleSheet {
        
        @XlsVerticalRecords(tableLabel="天気情報")
        private List<WeatherRecord> records;
        
    }
    
    // レコード用クラス
    public class WeatherRecord {
        
        @XlsColumn(columnName="時間")
        private String time;
        
        @XlsColumn(columnName="降水")
        private double precipitation;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
表の名称位置の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

実際に表を作る場合、垂直方向ですが表の名称は上方に設定することが一般的です。
そのような場合、属性 ``tableLabelAbove`` の値を ``true`` に設定すると表のタイトルが上方に位置するとして処理します。 `[ver1.0+]`

.. figure:: ./_static/VerticalRecord_tableLabelAbove.png
   :align: center
   
   VerticalRecords（tableLabelAbove）


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SampleSheet {
    
        @XlsVerticalRecords(tableLabel="天気情報", tableLabelAbove=true)
        private List<WeatherRecord> records;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
表の名称から開始位置が離れた場所にある場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

表の名称が定義してあるセルの直後に表がなく離れている場合、属性 ``right`` で表の開始位置がどれだけ離れているか指定します。 `[ver1.0]+`

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` の属性 ``bottom`` と同じような意味になります。

さらに、属性 ``tableLabelAbove=true`` と組み合わせると、下方向にどれだけ離れているかの意味になります。

.. figure:: ./_static/VerticalRecord_right.png
   :align: center
   
   VerticalRecords（right）


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SampleSheet {
    
        @XlsVerticalRecords(tableLabel="天気情報", right=3)
        private List<WeatherRecord> records;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
表の見出しが横に結合されデータレコードの開始位置が離れた場所にある場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

表の見出しセルが横に結合され、データレコードの開始位置が離れている場合、属性 ``headerRight`` でデータレコードの開始位置がどれだけ離れているか指定します。 `[ver1.1+]`

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` の属性 ``headerBottom`` と同じような意味になります。

下記の例の場合、見出しの「テスト結果」は横に結合されているため :ref:`@XlsColumn(headerMerged=N) <annotationXlsColumnHeaderMerged>` と組み合わせて利用します。


.. figure:: ./_static/VerticalRecord_headerRight.png
   :align: center
   
   VerticalRecords(headerRight)


.. sourcecode:: java
    
    // シート用クラス
    @XlsSheet(name="Weather")
    public class SampleSheet {
        
        // 見出しが横に結合され、データのレコードの開始位置が離れている場合
        @XlsVerticalRecords(tableLabel="天気情報", headerRight=2)
        private List<SampleRecord> records;
    
    }
    
    // レコード用クラス
    public class SampleRecord {
        
        @XlsColumn(columnName="時間")
        private String time;
        
        // セル「降水」のマッピング
        @XlsColumn(columnName="測定結果")
        private double precipitation;
        
        // セル「気温」のマッピング
        // 結合されている見出しから離れている数を指定する
        @XlsColumn(columnName="測定結果", headerMerged=1)
        private int temperature;
        
        // セル「天気」のマッピング
        // 結合されている見出しから離れている数を指定する
        @XlsColumn(columnName="測定結果", headerMerged=2)
        private String wather;
        
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
書き込み時にレコードが不足、余分である場合の操作の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性overRecord、remainedRecordで、書き込み時のレコードの操作を指定することができますが、 ``@XlsVerticalRecords`` 場合は **一部の設定が使用できません** 。

* ``@XlsVerticalRecords`` の場合、属性 ``overRecord`` では、列の挿入を行う ``OverRecordOperate.Insert`` は使用できません。
* ``@XlsVerticalRecords`` の場合、属性 ``remaindRecord`` では、列の削除を行う ``RemainedRecordOperate.Delete`` は使用できません。

これらの操作をサポートしていない理由は、Apache POIが、一括で列の挿入、削除をサポートしていないためです。


