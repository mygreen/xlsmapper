

.. _annotationXlsRecordOption:

--------------------------------------
``@XlsRecordOption``
--------------------------------------

アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、 :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>`  において、書き込み時のレコードの操作を指定するためのアノテーションです。 `[ver.2.0+]`


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時に配列・リストのサイズが不足している場合(overOperation)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーション ``@XlsRecordOption`` を指定することで、書き込み時のレコードの制御を指定することができます。

* 属性 ``overOperation`` で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が足りない場合の操作を指定します。

  * デフォルト値である列挙型 ``OverOperation#Break`` のとき、レコードの書き込みを中断します。
  * 列挙型 ``OverOperation#Copy`` のとき、指定すると上部のセルの書式を下部にコピーして値を設定します。

    * ``@XlsVerticalRecords`` のときは、左側のセルを右側にコピーして値を設定します。

  * 列挙型 ``OverOperation#Insert`` のとき、行を挿入してレコードを書き込みます。その際に、上部のセルの書式をコピーします。

    * ``@XlsVerticalRecords`` のときは、サポートしていません。

.. figure:: ./_static/RecordOption_overOperation.png
   :align: center
   
   RecordOption(overOperation)


.. sourcecode:: java
    :linenos:
    
    // 書き込むデータ
    List<UserRecord> data = new ArrayList<>();
    data.add(new UserRecord(1, "山田　太郎"));
    data.add(new UserRecord(2, "山田　花子"));
    data.add(new UserRecord(3, "鈴木　一郎"));
    
    // マッピングの定義
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<UserRecord> records;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時に配列・リストのサイズが余っている場合(remainedOperation)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーション ``@XlsRecordOption`` を指定することで、書き込み時のセルの制御を指定することができます。


* 属性 ``remainedOperation`` で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が余っている場合の操作を指定します。

  * デフォルト値である列挙型 ``RemainedOperation#None`` の値のとき、レコードを書き込み、その後何もしません。
  * 列挙型 ``RemainedOperation#Clear`` の値のとき、レコードを書き込み、その後、余っているセルの値をクリアします。
  * 列挙型 ``RemainedOperation#Delete`` の値のとき、レコードを書き込み、その後、余っている行を削除します。

    * ``@XlsVerticalRecords`` のときは、サポートしていません。


.. figure:: ./_static/RecordOption_remainedOperation.png
   :align: center
   
   RecordOption(remainedOperation)


.. sourcecode:: java
    :linenos:
    
    // 書き込むデータ
    List<UserRecord> data = new ArrayList<>();
    data.add(new UserRecord(1, "山田　太郎"));
    data.add(new UserRecord(2, "山田　花子"));
    
    // マッピングの定義
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        @XlsRecordOption(remainedOperation=RemainedOperation.Clear)
        private List<UserRecord> records;
        
    }


