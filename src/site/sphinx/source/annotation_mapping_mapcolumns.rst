

.. _annotationXlsMapColumns:

------------------------------------
``@XlsMapColumns``
------------------------------------


アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` もしくは :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` において、
指定されたレコード用クラスのカラム数が可変の場合に、それらのカラムを ``java.util.Map`` として設定します。

* BeanにはMapを引数に取るフィールドまたはメソッドを用意し、このアノテーションを記述します。
* 属性 ``previousColumnName`` で、指定された次のカラム以降、カラム名をキーとしたMapが生成され、Beanにセットされます。
* 属性 ``optional`` で、見出しとなるセルが見つからない場合に無視するかどうかを指定しできます。 `[ver2.0+]`

.. figure:: ./_static/MapColumns.png
   :align: center
   
   MapColumns


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
終了条件のセルを指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``nextColumnName`` で、指定した前のカラムまでが処理対象となり、マッピングの終了条件を指定することができます。 `[ver1.2+]`

* 属性 ``optional`` で、見出しとなるセルが見つからない場合に無視するかどうかを指定しできます。 `[ver2.0+]`


.. figure:: ./_static/MapColumns_nextColumnName.png
   :align: center
   
   MapColumns(nextColumnName)


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsMapColumns(previousColumnName="名前", nextColumnName="備考")
        private Map<String, String> attendedMap;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
型変換する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーション :ref:`@XlsConverter <annotationXlsConverter>` などで型変換を適用するときは、Mapの値が変換対象となります。
マップのキーは必ずString型を指定してください。

.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        // 型変換用のアノテーションを指定した場合、Mapの値に適用されます。
        @XlsMapColumns(previousColumnName="名前")
        @XlsBooleanConverter(loadForTrue={"出席"}, loadForFalse={"欠席"},
                saveAsTrue="出席", saveAsFalse"欠席"
                failToFalse=true)
        private Map<String, Boolean> attendedMap;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
位置情報／見出し情報を取得する際の注意事項
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

マッピング対象のセルのアドレスを取得する際に、フィールド ``Map<String, Point> positions`` を定義しておけば、自動的にアドレスがマッピングされます。

通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。

アノテーション ``@XlsMapColumns`` でマッピングしたセルのキーは、 ``<プロパティ名>[<セルの見出し>]`` の形式になります。


同様に、マッピング対象の見出しを取得する、フィールド ``Map<String, String> labels`` へのアクセスも、
キーは、 ``<プロパティ名>[<セルの見出し>]`` の形式になります。


.. figure:: ./_static/MapColumns_positions.png
   :align: center
   
   MapColumns(positions/labels)


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 位置情報
        private Map<String, Point> positions;
        
        // 見出し情報
        private Map<String, String> labels;
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
    }
    
    // 位置情報・見出し情報へのアクセス
    SampleRecord record = /* レコードのインスタンスの取得 */;
    
    Point position = record.positions.get("attendedMap[4月2日]");
    
    String label = recrod.labeles.get("attendedMap[4月2日]");
    


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
見出しを正規表現、正規化して指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。 `[ver1.1+]`

* 正規表現で指定する場合、アノテーションの属性の値を ``/正規表現/`` のように、スラッシュで囲み指定します。
  
  * スラッシュで囲まない場合、通常の文字列として処理されます。
  
  * 正規表現の指定機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``regexLabelText`` の値を trueに設定します。
  
* ラベセルの値に改行が空白が入っている場合、それらを除去し、正規化してアノテーションの属性値と比較することが可能です。
  
  * 正規化とは、空白、改行、タブを除去することを指します。
   
  * ラベルを正規化する機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``normalizeLabelText`` の値を trueに設定します。
  

これらの指定が可能な属性は、``previousColumnName`` 、``nextColumnName`` です。


.. sourcecode:: java
    :linenos:
    
    // システム設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfiguration()
            .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
            .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
    
    // レコード用クラス
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        // 正規表現による指定
        @XlsColumn(columnName="/名前.+/")
        private String name;
        
        // 正規表現による指定
        @XlsMapColumns(previousColumnName="/名前.+/", nextColumnName="/備考.+/")
        private Map<String, String> attendedMap;
        
        @XlsColumn(columnName="/備考.+/")
        private String comment;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み前に動的にテンプレートファイルを書き換える
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み処理の場合、マップのキーがデータごとに異なり、テンプレートのフォーマットと合わない場合があります。

そのような場合、テンプレートファイルを書き込むデータに合わせて書き換えます。
その際には、 :doc:`ライフサイクル・コールバック用のアノテーション <annotation_lifecycle>` ``@XlsPreSave`` で、実装を行うことができます。

実装処理は、Apache POIのAPIを使って行います。。

.. figure:: ./_static/MapColumns_preSave.png
   :align: center
   
   MapColumns(preSave)


.. sourcecode:: java
    :linenos:
    
    // シート用クラス
    @XlsSheet(name="List")
    public class SampleSheet {
        
        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        @XlsRecordOption(overOperation=OverOperation.Insert)
        List<SampleRecord> records;
        
        // XlsMapColumnsのマッピング用のセルを作成する
        @XlsPreSave
        public void onPreSave(final Sheet sheet, final Configuration config) {
            
            try {
                final Workbook workbook = sheet.getWorkbook();
                
                // 基準となる日付のセル[日付]を取得する
                Cell baseHeaderCell = Utils.getCell(sheet, "[日付]", 0, 0, config);
                
                // 書き換えるための見出しの値の取得
                List<String> dateHeaders = new ArrayList<>(records.get(0).attendedMap.keySet());
                
                // 1つ目の見出しの書き換え
                baseHeaderCell.setCellValue(dateHeaders.get(0));
                
                // ２つ目以降の見出し列の追加
                Row headerRow = baseHeaderCell.getRow();
                for(int i=1; i < dateHeaders.size(); i++) {
                    Cell headerCell = headerRow.createCell(baseHeaderCell.getColumnIndex() + i);
                    
                    CellStyle style = workbook.createCellStyle();
                    style.cloneStyleFrom(baseHeaderCell.getCellStyle());
                    headerCell.setCellStyle(style);
                    headerCell.setCellValue(dateHeaders.get(i));
                    
                }
                
                // 2つめ以降のデータ行の列の追加
                Row valueRow = sheet.getRow(baseHeaderCell.getRowIndex() + 1);
                Cell baseValueCell = valueRow.getCell(baseHeaderCell.getColumnIndex());
                for(int i=1; + i < dateHeaders.size(); i++) {
                    Cell valueCell = valueRow.createCell(baseValueCell.getColumnIndex() + i);
                    
                    CellStyle style = workbook.createCellStyle();
                    style.cloneStyleFrom(baseValueCell.getCellStyle());
                    valueCell.setCellStyle(style);
                    
                }
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        }
    
    }
    
    // レコード用クラス
    public class SampleRecord {
        
        @XlsColumn(columnName="ID")
        private int id;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        // 可変長のセルのマッピング
        @XlsMapColumns(previousColumnName="名前")
        private Map<String, String> attendedMap;
        
    }
    



