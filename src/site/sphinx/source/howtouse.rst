======================================
基本的な使い方
======================================

----------------------------
ダウンロード
----------------------------

Mavenを使用する場合は *pom.xml* に以下の記述を追加してください。

.. sourcecode:: xml
    
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>xlsmapper</artifactId>
        <version>1.0</version>
    </dependency>


----------------------------
読み込み方
----------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
単一のシートを読み込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

XlsMapperはアノテーションを付与してJavaBeansとExcelをマッピングするライブラリです。
アノテーション ``@XlsSheet`` を付与したJavaBeanを作成したうえで以下のようにして読み込みを行います。

.. sourcecode:: java
    
    SheetObject sheet = new XlsMapper().load(
        new FileInputStream("example.xls"), // 読み込むExcelファイル
        SheetObject.class                   // アノテーションを付与したクラス。
        );

なお、``@XlsCell``、 ``@XlsLabelledCell``、 ``@XlsColumn`` アノテーションでマッピングするプロパティに関しては、
現時点ではString型、プリミティブ型、プリミティブ型のラッパー型のいずれかである必要があります。

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
    
    Object[] sheets = new XlsMapper().loadMultiple(
        new FileInputStream("example.xls"),                  // 読み込むExcelファイル
        new Class[]{SheetObject1.class, SheetObject2.class}  // アノテーションを付与したクラス。
        );

アノテーション ``@XlsSheet(regex="正規表現+")`` のように、シート名を正規表現で指定した場合、
シート内の表の形式は同じですが、名前が異なる複数のシートとしを読み込むことができます。


.. sourcecode:: java
    
    SheetObject[] sheets = new XlsMapper().loadMultiple(
        new FileInputStream("example.xls"),  // 読み込むExcelファイル
        SheetObject.class                    // アノテーションを付与したクラス。
        );

----------------------------
書き込み方
----------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
単一のシートの書き込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込む際には、``@XlsSheet`` アノテーションを付与したJavaBeansのクラスのインスタンスを渡します。
また、雛形となるテンプレートのシートを記述しているExcelファイルを引数に渡します。

.. sourcecode:: java
    
    SheetObject sheet = //... POJOのインスタンス。
    new XlsMapper().save(
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
    public class SheetObject {
      @XlsSheetName
      public String sheetName;
    }
    
    SheetObject1 sheet = //... POJOのインスタンス。
    sheet.sheetName = "Sheet_1"; // 予めシート名を設定しておく必要があります。
    
    new XlsMapper().save(
        new FileInputStream("template.xls"), // テンプレートのExcelファイル
        new FileOutputStream("example.xls"), // 書き込むExcelファイル
        sheet         // JavaBeansのインスタンスの配列
        );

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
複数のシートを書き込む場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

複数のシートを読み込む場合、``XlsMapper#saveMultplue(...)`` を使用します。
書き込むJavaBeansのクラスのインスタンスは、アノテーション ``@XlsSheet`` を付与する必要があります。
シートのオブジェクトは配列として渡します。

.. sourcecode:: java
    
    SheetObject1 sheet1 = //... POJOのインスタンス。
    SheetObject2 sheet2 = //... POJOのインスタンス。
    
    new XlsMapper().saveMultiple(
        new FileInputStream("template.xls"), // テンプレートのExcelファイル
        new FileOutputStream("example.xls"), // 書き込むExcelファイル
        new Object[]{sheet1, sheet2}         // JavaBeansのインスタンスの配列
        );


.. note::
    アノテーション ``@XlsSheet(regexp="正規表現*")`` のようにシート名を正規表現で定義している場合、
    書き込み先のシート名はアノテーション@XlsSheetNameを付与したフィールドを元に決定します。
    
テンプレートのExcelファイル中にシートが1つしかない場合、書き込む個数分コピーしておく必要があります。
このような場合、書き込み対象のテンプレートファイルを事前に処理しておきます。


