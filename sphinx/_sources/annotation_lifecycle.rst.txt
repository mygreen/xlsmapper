-----------------------------------------------------------
ライフサイクルイベントの管理
-----------------------------------------------------------

読み込みと書き込み処理の前、後それぞれのライフサイクルのイベントにおいて、任意の処理を呼び出すことができます。 

実装方法として、JavaBeanに直接処理を実装する方法と、リスナークラスを指定して別のクラスで実装する方法の2種類があります。

**publicメソッドに付与** する必要があります。

.. list-table:: ライフサイクル・コールバック用のアノテーション一覧
   :widths: 30 70 
   :header-rows: 1
   
   * - アノテーション
     - 説明
   
   * - ``@XlsPreLoad``
     - | シートの値の読み込み直前に実行されます。
   
   * - ``@XlsPostLoad``
     - | シートの値の読み込み後に実行されます。
       | シート内の全ての値の読み込み後に実行されます。
   
   * - ``@XlsPreSave``
     - オブジェクトの書き込み直前に実行されます。
   
   * - ``@XlsPostSave``
     - | オブジェクトの書き込み後に実行されます。
       | シート内の全ての値の書き込み後に実行されます。


また、特定の引数をとることができます。

* 引数は、とらなくても可能です。
* 引数の順番は任意で指定可能です。


.. list-table:: コールバック用アノテーションのメソッドに指定可能な引数一覧
   :widths: 50 50
   :header-rows: 1
   
   * - 指定可能な引数のタイプ
     - 説明
   
   * - ``org.apache.poi.ss.usermodel.Sheet``
     - | 処理対象のSheetオブジェクト。
   
   * - ``com.gh.mygreen.xlsmapper.Configuration``
     - | :doc:`XlsMapperの設定オブジェクト <configuration>` 。
   
   * - ``com.gh.mygreen.xlsmapper.validation.SheetBindingErrors``
     - | :doc:`シートのエラー情報 <validation>` を格納するオブジェクト。
       | 読み込み時に引数で渡したオブジェクト。
   
   * - `処理対象のBeanクラス`
     - | 処理対象のBeanオブジェクト。 `[ver1.3+]`

   * - ``java.lang.Object``
     - | 処理対象のBeanオブジェクト。 `[ver1.3+]`


以下のような時にライフサイクルコールバック関数を利用します。

* 読み込み前、書き込み前のフィールドの初期化。
* 読み込み後の入力値の検証。
* 書き込み後の本ライブラリで提供されない機能の補完。
    
  * 例えば、Excelファイル上の定義された名前の範囲の変更や入力規則の設定。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
JavaBeanクラスに実装する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シート用クラス、レコード用クラスのどちらにも定義できます。

実行順は、親であるシートクラスの処理が先に処理されます。 

.. sourcecode:: java
    :linenos:
    
    // シートクラス
    @XlsSheet(name="Users")
    public class SampleSheet {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        private List<UserRecord> records;
        
        @XlsPreLoad
        @XlsPreSave
        public void onInit() {
            // 読み込み前と書き込み前に実行される処理
        }
    }
    
    // レコードクラス
    public class UserRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsPostLoad
        public void onPostLoad(Sheet sheet, Configuration config, SheetBindingErrors errors) {
            // 読み込み後に実行される処理
            // 入力値チェックなどを行う
        }
        
    }


.. _annotationXlsListener:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
リスナークラスに実装する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

クラスにアノテーション ``@XlsListener`` を付与し、属性 ``value`` で処理が実装されたクラスを指定します。 `[ver1.0+]`

指定したリスナークラスのインスタンスは、システム設定「beanFactory」経由で作成されるため、:doc:`SpringFrameworkのコンテナからインスタンスを取得 <spring>` することもできます。

.. sourcecode:: java
    :linenos:
    
    // シートクラス
    @XlsSheet(name="Users")
    @XlsListener(SampleSheetListener.class)
    public class SampleSheet {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        private List<UserRecord> records;
        
    }
    
    // SampleSheetクラスのリスナー
    public static class SampleSheetListener {
        
        @XlsPreLoad
        @XlsPreSave
        public void onInit(SampleSheet targetObj) {
            // 読み込み前と書き込み前に実行される処理
        }
    }
    
    // レコードクラス
    @XlsListener(UserRecordListener.class)
    public class UserRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
    }
    
    // UserRecordクラスのリスナー
    public static class UserRecordListener {
        
        @XlsPostLoad
        public void onPostLoad(UserRecord targetObj, Sheet sheet, Configuration config, SheetBindingErrors errors) {
            // 読み込み後に実行される処理
            // 入力値チェックなどを行う
        }
    }


