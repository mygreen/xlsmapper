-----------------------------------------------------------
ライフサイクル・コールバック用のアノテーション
-----------------------------------------------------------

読み込みと書き込み処理の前、後それぞれの処理イベントにおいて、任意の処理を実装できます。 
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
   
   * - ``com.gh.mygreen.xlsmapper.XlsMapperConfig``
     - | XlsMapperの設定オブジェクト。
   
   * - ``com.gh.mygreen.xlsmapper.validation.SheetBindingErrors``
     - | シートのエラー情報を格納するオブジェクト。
       | 読み込み時に引数で渡したオブジェクト。


以下のような時にライフサイクルコールバック関数を利用します。

* 読み込み前、書き込み前のフィールドの初期化。
* 読み込み後の入力値の検証。
* 書き込み後の本ライブラリで提供されない機能の補完。
    
    * 例えば、Excelファイル上の定義された名前の範囲の変更や入力規則の設定。


.. sourcecode:: java
    
    public class UnitUser {
      
        private Date date;
        
        // 読み込み前、書き込み前の処理
        @XlsPreLoad
        @XlsPreSave
        public void onInit() {
            // フィールドの初期化
            this.date = new Date();
        }
        
        // 読み込み後の処理
        @XlsPostLoad
        public void onPostSave(Sheet sheet, XlsMapperConfig config, SheetBindingErrors errors) {
           // 入力値チェックなどを行う。
        }
        
        // 書き込み後の処理
        @XlsPostSave
        public void onPostSave(Sheet sheet) {
            // 提供されない機能の補完。
            
        }
        
    }

