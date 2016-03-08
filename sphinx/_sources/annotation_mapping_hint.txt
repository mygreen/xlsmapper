
.. _annotationXlsHint:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsHint``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時に、``@XlsHoriontalRecords`` を使用して行の挿入や削除を行う設定を行っている場合、
フィールドの処理順序によって、``Map<String, Point> positions`` フィールドで座標がずれる場合があります。

このようなときに、``@XlsHint`` の属性 ``order`` で書き込む処理順序を指定し一定に保つことができます。

``@XlsHint`` を付与しないフィールドは、付与しているフィールドよりも後から処理が実行されます。
属性orderが同じ値を設定されているときは、 フィールド名の昇順で優先度を決めて処理されます。


.. figure:: ./_static/Hint.png
   :align: center
   
   Hint

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // セルの位置情報
        private Map<String, Point> positions;
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
        private List<UserRecord> records;
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="更新日", type=LabelledCellType.Right)
        private Date updateTime;
        
    }


.. note::
    
    ソースコード上で定義したフィールドやメソッドの記述順は、実行時には保証されないため、``@XlsHint`` で順番を指定し、処理順序を一定にすることができます。
    
    ``@XlsHint`` を付与すると、書き込み時だけでなく読み込み時にも処理順序が一定になります。


