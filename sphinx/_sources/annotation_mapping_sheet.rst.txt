
.. _annotationXlsSheet:

--------------------------
``@XlsSheet``
--------------------------

マッピング対象のシートを「シート番号」「シート名」「シート名に対する正規表現」のいずれかで指定します。

クラスに付与します。

.. sourcecode:: java
    :linenos:
    :caption: シート番号で指定する場合
    
    @XlsSheet(number=0)
    public class SampleSheet {
        ...
    }


.. sourcecode:: java
    :linenos:
    :caption: シート名で指定する場合
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        ...
    }


.. sourcecode:: java
    :linenos:
    :caption: シート名を正規表現で指定する場合
    
    @XlsSheet(regex="Users.+")
    public class SampleSheet {
        ...
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
シート名を正規表現で指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

正規表現で指定する場合は、 ``XlsMapper#loadMultiple(...)`` メソッドを用いることで、同じ形式の一致した複数シートの情報を一度に取得することができます。

書き込み時は、複数のシートが一致する可能性があり、1つに特定できない場合があるため注意が必要です。

* 正規表現に一致するシートが1つしかない場合は、そのまま書き込みます。`[ver0.5+]`
* 正規表現に一致するシートが複数ある場合、アノテーション :ref:`@XlsSheetName <annotationXlsSheetName>` を付与したフィールドの値を元に決定します。
  そのため、予めフィールドに設定しておく必要があります。
* アノテーション :ref:`@XlsSheetName <annotationXlsSheetName>` を付与しているフィールドを指定し、その値に一致しなくても、正規表現に一致するシートが1つ一致すれば、そのシートに書き込まれます。`[ver0.5+]`


.. sourcecode:: java
    :linenos:
    
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
    
    // 複数のシートの書き込み
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.saveMultiple(new FileInputStream("template.xls"),
        new FileOutputStream("out.xls"),
        new Object[]{sheet1, sheet2, sheet3}
    );


動的に書き込むシート数が変わるような場合は、下記のように、テンプレート用のシートをコピーしてから処理を行います。

.. sourcecode:: java
    :linenos:
    
    
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
    


