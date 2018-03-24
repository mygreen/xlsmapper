
.. _annotationXlsIgnorable:

--------------------------------
``@XlsIgnorable``
--------------------------------

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、:ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` を使用して、読み込む際に、空のレコードを読み飛ばしたい場合、
レコードが空と判定するためのメソッドに付与します。

* ``@XlsIgnorable`` を付与したメソッドは、publicかつ引数なしの戻り値がboolean型の書式にする必要があります。
* :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` でも同様に使用できます。

また、この機能は読み込み時のみに有効です。書き込み時は、空のレコードでもそのまま出力されます。

.. sourcecode:: java
    :linenos:
    
    // ルートのオブジェクト
    @XlsSheet(name="シート名")
    public class UnitUser {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        private List<User> users;
        
    }
    
    // レコードのオブジェクト
    public class User {
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="住所")
        private String address;
        
        // レコードが空と判定するためのメソッド
        @XlsIgnorable
        public boolean isEmpty() {
          
          if(name != null || !name.isEmpty()) {
            return false;
          }
          
          if(address != null || !address.isEmpty()) {
            return false;
          }
          
          return true;
        }
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
IsEmptyBuilderを使った記述の簡単化
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``IsEmptyBuilder`` (ver.0.5から追加)を利用することで、より簡潔に記述することも可能です。

* ``IsEmptyBuilder#reflectionIsEmpty(...)`` を利用して判定する場合、位置情報を保持するフィールド ``Map<String, Point> positions`` などは除外対象とする必要があります。
* 独自に判定する場合、``IsEmptyBuilder#append(...)`` を利用します。
* さらに、 ``IsEmptyBuilder#compare(IsEmptyComparator)`` を利用することで独自の判定をすることができます。その際に、Lambda式を利用すると簡潔に記載できます。

.. sourcecode:: java
    :linenos:
    
    // ルートのオブジェクト
    @XlsSheet(name="シート名")
    public class UnitUser {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        private List<User> users;
        
    }
    
    // レコードのオブジェクト
    public class User {
        
        // マッピングしたセルの位置情報を保持する。
        private Map<String, Point> positions;
        
        // マッピングしたセルのラベル情報を保持する。
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="住所")
        private String address;
        
        // レコードが空と判定するためのメソッド
        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels");
            
        }
        
        // 独自に判定する場合
        public boolean isEmpty2() {
            return new IsEmptyBuilder()
                .append(name)
                .compare(() -> StringUtils.isBlank(address))
                .isEmpty();
        }
    }


