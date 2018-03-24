

.. _annotationXlsRecordFinder:

--------------------------------------
``@XlsRecordFinder``
--------------------------------------

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、 :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>`  において、データレコードの開始位置が既存のアノテーションの属性だと表現できない場合に、任意の実装方法を指定するようにします。 `[ver.2.0+]`

* 属性 ``value`` で、レコードの開始位置を検索する ``RecordFinder`` の実装クラスを指定します。
* 属性 ``args`` で、レコードの開始位置を検索する実装クラスに渡す引数を指定します。


.. figure:: ./_static/RecordFinder.png
   :align: center
   
   RecordFinder


.. sourcecode:: java
    :linenos:
    :caption: 任意の位置のレコードをマッピングする場合
    
    // マッピングの定義
    @XlsSheet(name="Users")
    public class SheetSheet {
        
        // クラスAに対するマッピング定義
        @XlsOrder(1)
        // マッピングの終了条件が、「クラスB」であるため、terminalLabelを指定します。汎用的に正規表現で指定します。
        @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")
        // クラスAの見出しを探すために、属性argsでクラス名を指定します。
        @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスA")
        private List<Record> classA;
        
        // クラスBに対するマッピング定義
        @XlsOrder(2)
        // マッピングの終了条件が、終端のセルに罫線があるのため、terminalを指定します。
        @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")
        // クラスAの見出しを探すために、属性argsでクラス名を指定します。
        @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスB")
        private List<Record> classB;
        
    }
    
    // クラス用の見出しのレコードを探すクラス
    class ClassNameRecordFinder implements RecordFinder {
    
        @Override
        public CellPosition find(ProcessCase processCase, String[] args, Sheet sheet,
                CellPosition initAddress, Object beanObj, Configuration config) {
            
            // アノテーション @XlsRecordFinder の属性argsで指定された値を元にセルを検索します。
            final String className = args[0];
            Cell classNameCell = CellFinder.query(sheet, className, config)
                    .startPosition(initAddress)
                    .findWhenNotFoundException();
            
            // 見出し用のセルから1つ下がデータレコードの開始位置
            return CellPosition.of(classNameCell.getRowIndex()+1, initAddress.getColumn());
        }
        
    }
    
    // ユーザレコードの定義
    public class UserRecord {

        @XlsColumn(columnName="No.", optional=true)
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsColumn(columnName="算数")
        private Integer sansu;
        
        @XlsColumn(columnName="国語")
        private Integer kokugo;
        
        @XlsColumn(columnName="合計")
        @XlsFormula(value="SUM(D{rowNumber}:E{rowNumber})", primary=true)
        private Integer sum;
        
        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        // getter、setterは省略
        
    }
    
    



