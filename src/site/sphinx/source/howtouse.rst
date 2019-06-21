======================================
基本的な使い方
======================================

本ライブラリは、Excelファイルをアノテーションを用いてJavaBeansにマッピングするライブラリ「`XLSBeans <http://amateras.osdn.jp/cgi-bin/fswiki/wiki.cgi?page=XLSBeans>`_」を拡張し、機能を追加したものです。

違いの詳細は、「 :doc:`XLSBeansとの違い <diff_xlsbeans>` 」を参照してください。


----------------------------
ダウンロード
----------------------------

Mavenを使用する場合は *pom.xml* に以下の記述を追加してください。

.. sourcecode:: xml
    :linenos:
    :caption: pom.xmlの依存関係
    
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>xlsmapper</artifactId>
        <version>2.1</version>
    </dependency>


本ライブラリは、ロギングライブラリ `SLF4j <https://www.slf4j.org/>`_ を使用しているため、好きな実装を追加してください。

.. sourcecode:: xml
    :linenos:
    :caption: ロギングライブラリの実装の追加（Log4jの場合）
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.1</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.14</version>
    </dependency>


.. _dependencyEnv:

----------------------------------------
前提条件
----------------------------------------

本ライブラリの前提条件を以下に示します。


.. list-table:: 前提条件
   :widths: 50 50
   :header-rows: 1
   
   * - 項目
     - 値
     
   * - Java
     - ver.1.8
     
   * - `Apache POI <https://poi.apache.org/>`_
     - ver.3.17+

   * - `Spring Framework <https://projects.spring.io/spring-framework/>`_ (option)
     - ver.3.0+

   * - | Bean Validation  (option)
       | ( `Hibernate Validator <http://hibernate.org/validator/>`_ )
     - | ver.1.0/1.1/2.0
       | (Hibernate Validator 4.x/5.x/6.x)


.. _howtouseSheetLoad:


----------------------------
マッピングの基本
----------------------------

次のような表のExcelシートをマッピングする例を説明します。

.. figure:: ./_static/howto_load.png
   :align: center
   
   基本的なマッピング



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
読み込み方の基本
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


まず、シート1つに対して、POJOクラスを作成します。

* シート名を指定するために、アノテーション :ref:`@XlsSheet <annotationXlsSheet>` をクラスに付与します。
* 見出し付きのセル「Date」をマッピングするフィールドに、アノテーション :ref:`@XlsLabelledCell <annotationXlsLabelledCell>` に付与します。
* 表「User List」をマッピングするListのフィールドに、アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` を付与します。

.. sourcecode:: java
    :linenos:
    :caption: シート用のPOJOクラスの定義
    
    @XlsSheet(name="List")
    public class UserSheet {
        
        @XlsLabelledCell(label="Date", type=LabelledCellType.Right)
        Date createDate;
        
        @XlsHorizontalRecords(tableLabel="User List")
        List<UserRecord> users;
        
    }
    


続いて、表「User List」の1レコードをマッピングするための、POJOクラスを作成します。

* レコードの列をマッピングするために、アノテーション :ref:`@XlsColumn <annotationXlsColumn>` をフィールドに付与します。

* フィールドのクラスタイプが、intや列挙型の場合もマッピングできます。

.. sourcecode:: java
    :linenos:
    :caption: レコード用のPOJOクラスの定義
    
    public class UserRecord {
        
        @XlsColumn(columnName="ID")
        int no;
        
        @XlsColumn(columnName="Class", merged=true)
        String className;
        
        @XlsColumn(columnName="Name")
        String name;
        
        @XlsColumn(columnName="Gender")
        Gender gender;
        
    }
    
    // 性別を表す列挙型の定義
    public enum Gender {
        male, female;
    }



作成したPOJOを使ってシートを読み込むときは、 ``XlsMapper#load`` メソッドを利用します。

.. sourcecode:: java
    :linenos:
    :caption: シートの読み込み
    
    XlsMapper xlsMapper = new XlsMapper();
    UserSheet sheet = xlsMapper.load(
        new FileInputStream("example.xlsx"), // 読み込むExcelファイル。
        UserSheet.class                      // シートマッピング用のPOJOクラス。
        );



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み方の基本
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

同じシートの形式を使って、書き込み方を説明します。

まず、書き込み先のテンプレートとなるExcelシートを用意します。
レコードなどは空を設定します。

.. figure:: ./_static/howto_save.png
   :align: center
   
   データが空のテンプレートファイル


続いて、読み込み時に作成したシート用のマッピングクラスに、書き込み時の設定を付け加えるために修正します。

* セル「Date」の書き込み時の書式を指定するために、アノテーション :ref:`@XlsDateTimeConverter <annotationXlsDateTimeConverter>` に付与します。

  * 属性 ``excelPattern`` でExcelのセルの書式を設定します。

* 表「User List」のレコードを追加する操作を指定するために、アノテーション :ref:`@XlsRecordOption <annotationXlsRecordOption>` を付与し、その属性 ``overOperation`` を指定します。
  
  * テンプレート上は、レコードが1行分しかないですが、実際に書き込むレコード数が2つ以上の場合、足りなくなるため、その際のシートの操作方法を指定します。
  
  * 今回の ``OverOperation#Insert`` は、行の挿入を行います。


.. sourcecode:: java
    
    // シート用のPOJOクラスの定義
    @XlsSheet(name="List")
    public class UserSheet {
        
        @XlsLabelledCell(label="Date", type=LabelledCellType.Right)
        @XlsDateTimeConverter(excelPattern="yyyy/m/d")
        Date createDate;
        
        @XlsHorizontalRecords(tableLabel="User List")
        @XlsRecordOption(overOperation=OverOperation.Insert)
        List<UserRecord> users;
        
    }


修正したPOJOを使ってシートを書き込むときは、 ``XlsMapper#save`` メソッドを利用します。

.. sourcecode:: java
    
    // 書き込むシート情報の作成
    UserSheet sheet = new UserSheet();
    sheet.createDate = new Date();
    
    List<UserRecord> users = new ArrayList<>();
    
    // 1レコード分の作成
    UserRecord record1 = new UserRecord();
    record1.no = 1;
    record1.className = "A";
    record1.name = "Ichiro";
    record1.gender = Gender.male;
    users.add(record1);
    
    UserRecord record2 = new UserRecord();
    // ... 省略
    users.add(record2);
    
    sheet.users = users;
    
    // シートの書き込み
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.save(
        new FileInputStream("template.xlsx"), // テンプレートのExcelファイル
        new FileOutputStream("out.xlsx"),     // 書き込むExcelファイル
        sheet                                // 作成したデータ
        );


----------------------------
読み込み方
----------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
単一のシートを読み込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

XlsMapperはアノテーションを付与してJavaBeansとExcelをマッピングするライブラリです。
アノテーション :ref:`@XlsSheet <annotationXlsSheet>` を付与したJavaBeanを作成したうえで以下のようにして読み込みを行います。

.. sourcecode:: java
    
    XlsMapper xlsMapper = new XlsMapper();
    SampleSheet sheet = xlsMapper.load(
        new FileInputStream("example.xls"), // 読み込むExcelファイル
        SampleSheet.class                   // アノテーションを付与したクラス。
        );

なお、:ref:`@XlsCell <annotationXlsCell>`、 :ref:`@XlsLabelledCell <annotationXlsLabelledCell>`、 :ref:`@XlsColumn <annotationXlsColumn>` アノテーションでマッピングするプロパティにおいて、マッピングできる型は、 :doc:`型変換用アノテーション <annotation_converter>` を使用することでカスタマイズできます。

より具体的な使用例はXlsMapperのディストリビューションに同梱されているテストケースのソースコードをご覧ください。


XlsMapperは、Apache POIを使用してExcelのシートの読み込みと書き込みを行います。
拡張子がxlsのExcel2003形式、xlsxのExcel2007形式と特に区別なく読み込むことができます。

Apache POIは、ver.3.5以上に対応しています。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
複数のシートを読み込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


複数のシートを読み込む場合、``XlsMapper#loadMultplue(...)`` を使用します。
 
シートの読み込み先のJavaBeansが異なる場合、クラスタイプを配列として渡します。
戻り値は配列Object[]として返されます。
 
 
.. sourcecode:: java
    
    XlsMapper xlsMapper = new XlsMapper();
    Object[] sheets = xlsMapper.loadMultiple(
        new FileInputStream("example.xls"),                  // 読み込むExcelファイル
        new Class[]{SampleSheet1.class, SampleSheet2.class}  // アノテーションを付与したクラス。
        );

アノテーション ``@XlsSheet(regex="正規表現+")`` のように、シート名を正規表現で指定した場合、
シート内の表の形式は同じですが、名前が異なる複数のシートとしを読み込むことができます。


.. sourcecode:: java
    
    XlsMapper xlsMapper = new XlsMapper();
    SampleSheet[] sheets = new XlsMapper().loadMultiple(
        new FileInputStream("example.xls"),  // 読み込むExcelファイル
        SampleSheet.class                    // アノテーションを付与したクラス。
        );

.. _howtouseSheetSave:

----------------------------
書き込み方
----------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
単一のシートの書き込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込む際には、:ref:`@XlsSheet <annotationXlsSheet>` アノテーションを付与したJavaBeansのクラスのインスタンスを渡します。
また、雛形となるテンプレートのシートを記述しているExcelファイルを引数に渡します。

.. sourcecode:: java
    
    SampleSheet sheet = //... POJOのインスタンス。
    
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.save(
        new FileInputStream("template.xls"), // テンプレートのExcelファイル
        new FileOutputStream("example.xls"), // 書き込むExcelファイル
        sheet                                // JavaBeansのインスタンス
        );


書き込むExcelファイルの形式は、テンプレートとなるExcelファイルと同じ形式になります。
そのため、テンプレートファイルのExcelファイルがxls(Excel2003形式)で、
書き込むExcelファイルの拡張子をxlsx(2007形式)を指定しても、xls(Excel2003形式)となります。

アノテーション ``@XlsSheet(regexp="正規表現*")`` のようにシート名を正規表現で定義している場合、
書き込み先のシート名はアノテーション ``@XlsSheetName`` を付与したフィールドを元に決定します。

そのため、書き込むシート名を予め設定しておく必要があります。

.. sourcecode:: java
    
    /** 正規表現で指定する場合 */
    @XlsSheet(regex="Sheet_[0-9]+")
    public class SampleSheet {
      @XlsSheetName
      public String sheetName;
    }
    
    SampleSheet sheet = //... POJOのインスタンス。
    sheet.sheetName = "Sheet_1"; // 予めシート名を設定しておく必要があります。
    
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.save(
        new FileInputStream("template.xls"), // テンプレートのExcelファイル
        new FileOutputStream("example.xls"), // 書き込むExcelファイル
        sheet         // JavaBeansのインスタンス
        );

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
複数のシートを書き込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

複数のシートを読み込む場合、``XlsMapper#saveMultplue(...)`` を使用します。
書き込むJavaBeansのクラスのインスタンスは、アノテーション :ref:`@XlsSheet <annotationXlsSheet>` を付与する必要があります。
シートのオブジェクトは配列として渡します。

.. sourcecode:: java
    
    SheetSheet1 sheet1 = //... POJOのインスタンス。
    SheetSheet2 sheet2 = //... POJOのインスタンス。
    
    new XlsMapper().saveMultiple(
        new FileInputStream("template.xls"), // テンプレートのExcelファイル
        new FileOutputStream("example.xls"), // 書き込むExcelファイル
        new Object[]{sheet1, sheet2}         // JavaBeansのインスタンスの配列
        );


.. note::
    アノテーション ``@XlsSheet(regexp="正規表現*")`` のようにシート名を正規表現で定義している場合、
    書き込み先のシート名はアノテーション :ref:`@XlsSheetName <annotationXlsSheetName>` を付与したフィールドを元に決定します。
    
テンプレートのExcelファイル中にシートが1つしかない場合、書き込む個数分コピーしておく必要があります。
このような場合、書き込み対象のテンプレートファイルを事前に処理しておきます。

.. sourcecode:: java
    
    // 正規表現で指定する場合
    @XlsSheet(regex="Sheet_[0-9]+")
    public class SampleSheet {
        
        // シート名をマッピングするフィールド
        @XlsSheetName
        private String sheetName;
        ...
    }
    
    
    // 正規表現による複数のシートを出力する場合。
    // 書き込み時に、シート名を設定して、一意に関連づけます。
    SampleSheet sheet1 = new SampleSheet();
    sheet1.sheetName = "Sheet_1"; // シート名の設定
    
    SampleSheet sheet2 = new SampleSheet();
    sheet2.sheetName = "Sheet_2"; // シート名の設定
    
    SampleSheet sheet3 = new SampleSheet();
    sheet3.sheetName = "Sheet_3"; // シート名の設定
    
    SampleSheet[] sheets = new SampleSheet[]{sheet1, sheet2, sheet3};
    
    // シートのクローン
    Workbook workbook = WorkbookFactory.create(new FileInputStream("template.xlsx"));
    Sheet templateSheet = workbook.getSheet("XlsSheet(regexp)");
    for(SampleSheet sheetObj : sheets) {
        int sheetIndex = workbook.getSheetIndex(templateSheet);
        Sheet cloneSheet = workbook.cloneSheet(sheetIndex);
        workbook.setSheetName(workbook.getSheetIndex(cloneSheet), sheetObj.sheetName);
    }
    
    // コピー元のシートを削除する
    workbook.removeSheetAt(workbook.getSheetIndex(templateSheet));
    
    // クローンしたシートファイルを、一時ファイルに一旦出力する。
    File cloneTemplateFile = File.createTempFile("template", ".xlsx");
    workbook.write(new FileOutputStream(cloneTemplateFile));
    
    // 複数のシートの書き込み
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.saveMultiple(
            new FileInputStream(cloneTemplateFile), // クローンしたシートを持つファイルを指定する
            new FileOutputStream("out.xlsx"),
            sheets);



