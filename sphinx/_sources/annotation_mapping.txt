--------------------------------------
マッピング用のアノテーション
--------------------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsSheet``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

読み込むシートをシート番号、シート名、シート名に対する正規表現のいずれかで指定します。

クラスに付与します。

.. sourcecode:: java
    
    /** シート番号で指定する場合 */
    @XlsSheet(number=0)
    public class SheetObject {
        ...
    }


.. sourcecode:: java
    
    /** シート名で指定する場合 */
    @XlsSheet(name="Users")
    public class SheetObject {
        ...
    }



正規表現で指定する場合は、 ``XlsMapper#loadMultiple(...)`` メソッドを用いることでマッチしたシートの情報を一度に取得することができます。

書き込み時は、複数のシートが一致する可能性があり、1つに特定できない場合があるため注意が必要です。

    * 正規表現に一致するシートが1つしかない場合は、そのまま書き込みます。`[ver0.5+]`
    * 正規表現に一致するシートが複数ある場合、アノテーション ``@XlsSheetName`` を付与したフィールドの値元に決定します。
      そのため、予めフィールドに設定しておく必要があります。
    * アノテーション ``@XlsSheetName`` を付与しているフィールドを指定し、その値に一致しなくても、正規表現に一致するシートが1つ一致すれば、そのシートに書き込まれます。`[ver0.5+]`


.. sourcecode:: java
    
    /** 正規表現で指定する場合 */
    @XlsSheet(regex="Sheet_[0-9]+")
    public class SheetObject {
        ...
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsCell``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

セルの列、行を指定してBeanのプロパティにマッピングします。

フィールドまたはメソッドに対して付与します。

* 属性column、rowで座標を指定します。
   
   * columnは列番号で、0から始まります。
   * rowは行番号で、0から始まります。
    
* 属性addressで、 'A1'のようにシートのアドレス形式で指定もできます。
   
   * 属性addressを指定する場合は、column, rowは指定しないでください。
   * 属性addressの両方を指定した場合、addressの値が優先されます。


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        // インデックス形式で指定する場合
        @XlsCell(column=0, row=0)
        private String title;
        
        // アドレス形式で指定する場合
        @XlsCell(address="B3")
        private String name;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsLabelledCell``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

セルの文字列を指定し、その左右もしくは下側のセルの値をマッピングします。

フィールドまたはメソッドに対して付与します。
 
* 属性labelで、見出しとなるセルの値を指定します。
* 属性typeで、見出しセルから見て値が設定されている位置を指定します。
    
    * 列挙型 ``LabelledCellType`` で、左右もしくは下側のセルを指定できます。
    
* 属性optionalで、見出しとなるセルが見つからない場合に無視するかどうかを指定しできます。
 

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
    
    @XlsLabelledCell(label="Title", type=LabelledCellType.Right)
    private String title;



range属性を指定すると、type属性の方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsLabelledCell(label="Title", type=LabelledCellType.Right, range=3)
        private String title;
        
    }


同じラベルのセルが複数ある場合は、領域の見出しをheaderLabel属性で指定します。
headerLabel属性で指定されたセルからlabel属性で指定されたセルを下方向に検索し、最初に見つかったセルをラベルセルとして使用します。

.. figure:: ./_static/LabelledCell_headerLabel.png
   :align: center
   
   LabelledCell


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクション")
        private String actionClassName;
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクションフォーム")
        private String formClassName;
        
      }


skip属性を指定することで、ラベルセルから指定したセル数分離れたセルの値をマッピングすることができます。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
    
        // クラス名というセルから右側に2つ離れたセルの値をマッピング
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, skip=2)
        private String actionClassName;
        
    }


.. note:: 
    
    セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsSheetName``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シート名をString型のプロパティにマッピングします。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsSheetName
        private String sheetName;
    }


.. note:: 
    書き込み時で、シート名を正規表現で指定している場合は、 ``@XlsSheetName`` を付与しているフィールドで書き込むシートを決定します。
    そのため書き込む前に、シート名を指定する必要があります。



アノテーションをメソッドに付与する場合、書き込み時はgetterメソッドメソッドの付与が必要になります。
さらに、アノテーションは付与しなくてもよいですが、setterメソッドの定義が必要になります。
そのため、 ``@XlsSheetName`` を指定する際にはフィールドに付与することをお薦めします。

.. sourcecode:: java
    
    // メソッドにアノテーションを付与する場合
    // 読み込み時は、setterメソッドに付与する。
    @XlsSheet(name="Users")
    public class SheetObject {
        
        private String sheetName;
        
        // 読み込み時は、setterメソッドにアノテーションの付与が必要。
        @XlsSheetName
        public void setSheetName(String sheetName) {
            return sheetName;
        }
        
    }
    
.. sourcecode:: java
    
    // メソッドにアノテーションを付与する場合
    // 書き込み時は、getterメソッドに付与し、かつsetterメソッドの定義が必要。
    @XlsSheet(name="Users")
    public class SheetObject {
        
        private String sheetName;
        
        // 書き込み時は、getterメソッドにアノテーションの付与が必要。
        @XlsSheetName
        public String getSheetName() {
            return sheetName;
        }
        
        // アノテーションの付与は必要ないが、定義が必要。
        public void setSheetName(String sheetName) {
            return sheetName;
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsHorizontalRecords``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

水平方向に連続する行をListまたは配列にマッピングします。表には最上部にテーブルの名称と列名を記述した行が必要になります。

.. figure:: ./_static/HorizontalRecord.png
   :align: center
   
   HorizontalRecords


tableLabel属性でテーブルの名称を指定します。

List型または配列のフィールドに付与します。

List型などの場合、Genericsのタイプとして、マッピング先のBeanクラスを指定します。
指定しない場合は、アノテーションの属性 ``recordClass`` でクラス型を指定します。


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        // 通常は、Genericsでクラス型を指定します。
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        private List<Record> records;
        
        // Generics型を使用しない場合は、属性 recordClass で指定します。
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", recordClass=Record.class)
        private List record2;
    }

.. note::
    
    * ver1.0から、Collection型(List型、Set型)にも対応しています。
    * インタフェースの型を指定する場合、次の実装クラスのインスタンスが設定されます。
    
        * List型の場合、 ``java.util.ArrayList`` クラス。
        * Set型の場合、 ``java.util.LinkedHashMap`` クラス。
        * Collection型の場合、 ``java.util.ArrayList`` クラス。
    
    * 実装クラスを指定した場合、そのインスタンスが設定されます。



デフォルトでは行に1つもデータが存在しない場合、そのテーブルの終端となります。
行の一番左側の列のボーダーによってテーブルの終端を検出する方法もあります。
この場合は ``@XlsHorizontalRecordsのterminal`` 属性に ``RecordTerminal.Border`` を指定してください。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", terminal=RecordTerminal.Border)
        private List<Record> records;
    }

.. note::
    
    書き込む際にはテンプレート用のセルは空を設定しているため、属性 ``terminal=RecordTermial.Empty`` を指定していると処理が終了してしまうため、強制的に ``terminal=RecordTerminal.Border`` に補正して処理されます。`[ver0.5+]`


テーブルが他のテーブルと連続しておりterminal属性でBorder、Emptyのいずれを指定しても終端を検出できない場合があります。
このような場合はterminateLabel属性で終端を示すセルの文字列を指定します。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", terminateLabel="Terminate")
        private List<Record> records;
    }

headerLimit属性を指定すると、テーブルのカラムが指定数見つかったタイミングでExcelシートの走査を終了します。
主に無駄な走査を抑制したい場合にしますが、``@XlsIterateTables`` 使用時に、テーブルが隣接しており終端を検出できない場合などに
カラム数を明示的に指定してテーブルの区切りを指定する場合にも使用できます。

たとえば以下の例は、カラムのヘッダを4つ分検出したところでそのテーブルの終端と見なします。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", terminal=RecordTerminal.Border, headerLimit=4)
        private List<Record> records;
    }


なお、セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。


テーブルの名称用のセルが存在しない場合、属性headerColumn, headerRowで表の開始位置（左上部の端）の座標を指定できます。
また、座標はheaderAddressで'A1'のようにシートのアドレス形式で指定可能です。

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        
        @XlsHorizontalRecords(headerColumn=0, headerRow=1, terminal=RecordTerminal.Border)
        private List<Record> records;
        
        @XlsHorizontalRecords(headerAddress="B13", terminal=RecordTerminal.Border)
        private List<Record> sample;
    
    }

 
 overRecord、remainedRecord属性で、書き込み時のレコードの操作を指定することができます。
 
* overRecored属性で、書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
    
    * ``OverRecordOperate.Insert`` を指定すると行を挿入してレコードを書き込みます。その際に、上部のセルのスタイルなどをコピーします。
    * ``OverRecordOperate.Copy`` を指定すると上部のセルを下部にコピーして値を設定します。
    * ``OverRecordOperate.Break`` を指定すると、レコードの書き込みをその時点で止めます。
    
* remainedRecord属性で、書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
    
     * ``RemainedRecordOperate.Clear`` でセルの値をクリアします。
     * ``RemainedRecordOperate.Delete`` で行を削除します。
     * ``RemainedRecordOperate.Noneは`` 何もしません。


.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Clear)
        private List<Record> records;
    }


skipEmptyRecord属性で、読み込み時に空のレコードを読み飛ばすことができます。

レコード用のクラスには、レコードを空と判定するためのメソッド用意し、アノテーション@XlsIsEmptyを付与します。

また、この属性は読み込み時のみに有効です。書き込み時は、空のレコードでもそのまま出力されます。


.. sourcecode:: java
    
    // ルートのオブジェクト
    @XlsSheet(name="シート名")
    public class UnitUser {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", skipEmptyRecord=true)
        private List<User> users;
    }
    
    // レコードのオブジェクト
    public class User {
        
        @XlsColumn(columnName="名前")
        private String name;
        
        // レコードが空と判定するためのメソッド
        @XlsIsEmpty
        public boolean isEmpty() {
            
            if(name != null || !name.isEmpty()) {
                return false;
            }
            
            return true;
        }
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsVerticalRecords``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

垂直方向に連続する列をListまたは配列にマッピングします。
要するに ``@XlsHorizontalRecords`` を縦方向にしたものです。
``@XlsHorizontalRecords`` と同じくList型の引数を1つだけ取るsetterメソッドに対して付与します。

.. figure:: ./_static/VerticalRecord.png
   :align: center
   
   VerticalRecords

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        @XlsVerticalRecords(tableLabel="ユーザ一覧")
        private List<Record> records;
    }


* ``@XlsHorizontalRecords`` と同じくterminal属性、およびoptional属性を指定することもできます。
* overRecord、remainedRecord属性で、書き込み時のレコードの操作を指定することができます。
   
   * ただし、 ``@XlsHorizontalRecords`` では、overRecord=OverRecordOperate.Insertはサポートしていません。
   * ただし、 ``@XlsHorizontalRecords`` では、remainedRecord=RemainedRecordOperate.Deleteはサポートしていません。
   * これらの操作をサポートしていない理由は、Apache POIが、一括で列の挿入、削除をサポートしていないためです。



実際に表を作る場合、垂直方向ですが、タイトルは上方に設定することが一般的です。
その場合、属性 ``tableLabelAbove=true`` を付与すると表のタイトルが上方に位置するとして処理します。(ver1.0から使用可能)

.. figure:: ./_static/VerticalRecord2.png
   :align: center
   
   VerticalRecords（表のタイトルが上方の場合）

.. sourcecode:: java
    
    @XlsSheet(name="Users")
    public class SheetObject {
        @XlsVerticalRecords(tableLabel="ユーザ一覧", tableLabelAbove=true)
        private List<Record> records;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsColumn``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``@XlsHorizontalRecords`` または ``@XlsVerticalRecords`` で指定されたクラスのプロパティをカラム名にマッピングします。
フィールドやメソッドに対して付与します。

.. sourcecode:: java
    
    public class Record {
        @XlsColumn(columnName="ID")
        private String id;
    }


同じ値がグループごとに結合されているカラムの場合はmerged属性をtrueに設定します。
こうしておくと、前の列の値が引き継がれて設定されます。

.. sourcecode:: java
    
    public class Record {
        @XlsColumn(columnName="Gender", merged=true)
        private String id;
    }


.. note::
    
    書き込みにおいては、merged属性の値がtrueであっても、上部または左側のセルと値が同じでも結合は基本的に行いません。
    ただし、システム設定 ``XlsMapperConfig`` の項目「mergeCellOnSave」の値をtrueにすると結合されます。
 

見出し行が結合され、1つの見出しに対して複数の列が存在する場合はheaderMergedプロパティを使用します。
headerMergedの値には列見出しから何セル分離れているかを指定します。

.. figure:: ./_static/Column_headerMerged.png
   :align: center
   
   Column
   

.. sourcecode:: java
    
    public class User {
        
        @XlsColumn(columnName="連絡先")
        private String mailAddress;
        
        @XlsColumn(columnName="連絡先", headerMerged=1)
        private String mailAddress;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsMapColumns``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``@XlsHorizontalRecords`` もしくは ``@XlsVerticalRecords`` でカラム数が可変の場合に、
それらのカラムをMapとして設定します。BeanにはMapを引数に取るフィールドまたはメソッドを用意し、このアノテーションを記述します。

.. figure:: ./_static/MapColumns.png
   :align: center
   
   MapColumns

.. sourcecode:: java
    
    public class User {
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attributes;
    }


previousColumnName属性で指定された次のカラム以降、カラム名をキーとしたMapが生成され、Beanにセットされます。

``@XlsConverter`` などで型変換を適用するときは、マップの値が変換対象となります。
マップのキーは必ずString型を指定してください。



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsIterateTables``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

同一の構造の表がシート内で繰り返し出現する場合に使用します。

tableLabelプロパティで繰り返し部分の見出しラベルを指定します。

また、bottomプロパティは@XlsIterateTables内で ``@XlsHorizontalRecords`` を使用する場合に、

テーブルの開始位置が ``@XlsIterateTables`` の見出しセルからどれだけ離れているかを指定します。

.. figure:: ./_static/IterateTables.png
   :align: center
   
   IterateTables

.. sourcecode:: java
    
    @XlsSheet(name="シート名")
    public class SheetObject {
        @XlsIterateTables(tableLabel="部門情報", bottom=2)
        private List<Unit> units;
    }


繰り返し部分に対応するJavaBeanでは以下のように ``@XlsLabelledCell`` や ``@XlsHorizontalRecords`` などのアノテーションを使用することができます。

``@XlsHorizontalRecords`` を使用する場合、tableLabelプロパティには、``@XlsIterateTables`` のtableLabelプロパティで指定したラベルと同じラベルを指定する必要がある点に注意してください。

.. sourcecode:: java
    
    public class Unit {
        @XlsLabelledCell(label="部門名", type=LabelledCellType.Right)
        private String deptName;
        
        @XlsHorizontalRecords(tableLabel="部門情報")
        private List<UnitUser> unitUsers;
    }


繰り返し部分に対応するJavaBeanで ``@XlsHorizontalRecords`` を使用した場合、通常の場合と同じく ``@XlsColumn`` で列とのマッピングを行います。

.. sourcecode:: java
    
    public class UnitUser {
        @XlsColumn(columnName="ID")
        private String id;
        
        @XlsColumn(columnName="名前")
        private String name;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsHint``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時に、``@XlsHoriontalRecords`` を使用して行の挿入や削除を行う設定を行っている場合、
フィールドの処理順序によって、``Map<String, Point> positions`` フィールドで座標がずれる場合があります。

このようなときに、``@XlsHint`` で書き込む処理順序を指定し一定に保つことができます。

``@XlsHint`` を付与しないフィールドは、付与しているフィールドよりも後から処理が実行されます。
order属性が同じ値を設定されているときは、 フィールド名の昇順で優先度を決めて処理されます。


.. figure:: ./_static/Hint.png
   :align: center
   
   Hint

.. sourcecode:: java
    
    public class UnitUser {
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        @XlsHint(order=1)
        private List<User> users;
        
        @XlsLabelledCell(label="更新日", type=LabelledCellType.Right)
        @XlsHint(order=2)
       private String updateTime;
        
    }


.. note::
    
    ソースコード上で定義した順番は、実行時には保証されないため、``@XlsHint`` で順番を指定し、処理順序を一定にすることができます。
    
    ``@XlsHint`` を付与すると、書き込み時だけでなく読み込み時にも処理順序が一定になります。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsIsEmpty``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``@XlsHorizontalRecords/@XlsVerticalRecords`` を使用して、読み込む際に、空のレコードを読み飛ばしたい場合、
レコードが空と判定するためのメソッドに付与します。

* このアノテーションを使用する場合は、``@XlsHorizontalRecords`` の属性「skipEmptyRecord=true」を設定する必要があります。
* ``@XlsIsEmpty`` を付与したメソッドは、publicかつ引数なしの戻り値がboolean形式の書式にする必要があります。
* ``@XlsVertialRecords`` でも同様に使用できます。

また、この機能は読み込み時のみに有効です。書き込み時は、空のレコードでもそのまま出力されます。

.. sourcecode:: java
    
    // ルートのオブジェクト
    @XlsSheet(name="シート名")
    public class UnitUser {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", skipEmptyRecord=true)
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
        @XlsIsEmpty
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



``IsEmptyBuilder`` (ver.0.5から追加)を利用することで、より簡潔に記述することも可能です。

* ``IsEmptyBuilder#reflectionIsEmpty(...)`` を利用して判定する場合、位置情報を保持するフィールド ``Map<String, Point> positions`` などは除外対象とする必要があります。
* 独自に判定する場合、``IsEmptyBuilder#append(...)`` を利用します。
* さらに、 ``IsEmptyBuilder#compare(IsEmptyComparator)`` を利用することで独自の判定をすることができます。その際に、Lambda式を利用すると簡潔に記載できます。

.. sourcecode:: java
    
    // ルートのオブジェクト
    @XlsSheet(name="シート名")
    public class UnitUser {
    
        @XlsHorizontalRecords(tableLabel="ユーザ一覧", skipEmptyRecord=true)
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
        @XlsIsEmpty
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


