=========================================
数式の指定方法
=========================================

.. _annotationFormula:

----------------------------------------------
``@XlsFormula``
----------------------------------------------

書き込み時にセルの数式を指定するためのアノテーションです。 `[ver.1.5+]`

.. note::

   数式指定用のアノテーションは、セルをマッピングするアノテーション :ref:`@XlsCell <annotationXlsCell>` 、 :ref:`@XlsLabelledCell <annotationXlsLabelledCell>`、 :ref:`@XlsArrayCells <annotationXlsArrayCells>`、 :ref:`@XlsLabelledArrayCells <annotationXlsLabelledArrayCells>`、 :ref:`@XlsColumn <annotationXlsColumn>`、 :ref:`@XlsMapColumns <annotationXlsMapColumns>`、 :ref:`@XlsArrayColumns <annotationXlsArrayColumns>`  を付与しているプロパティに対して有効になります。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
数式を直接指定する場合（属性value）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

数式を直接指定する場合は、属性 ``value`` で指定します。
Javaのアノテーションの仕様上、属性valueのみを指定する時には、属性名の省略可能がです。

数式を指定する際に、メッセージファイルと同様に、変数やEL式が利用可能です。

* 変数は ``{変数名}`` で定義します。
* EL式は ``${EL式}`` で定義します。

  * EL式は、 `JEXL(Java Expression Language) <http://commons.apache.org/proper/commons-jexl/>`_ の形式で指定します。
  * JEXLの仕様は、 `JEXL Reference <http://commons.apache.org/proper/commons-jexl/reference/syntax.html>`_ を参照してください。


変数、EL式中では、予め次の変数が登録されており、セルの値ごとに変わります。

.. list-table:: 予め登録されている変数
   :widths: 30 70
   :header-rows: 1
   
   * - 変数名
     - 説明
   
   * - ``rowIndex``
     - 処理対象のセルの行のインデックス。0から始まります。
   
   * - ``columnIndex``
     - 処理対象のセルの列のインデックス。0から始まります。
     
   * - ``rowNumber``
     - 処理対象のセルの行番号。1から始まります。
   
   * - ``columnNumber``
     - 処理対象のセルの列番号。1から始まります。
     
   * - ``columnAlpha``
     - 処理対象のセルの列の名前。Aから始まります。
   
   * - ``address``
     - 処理対象のセルのアドレス。 ``A1`` の形式です。
   
   * - ``targetBean``
     - 処理対象のプロパティが定義されているJavaBeanのオブジェクトです。
   
   * - ``cell``
     - 処理対象のセルのオブジェクトです。POIのクラス ``org.apache.poi.ss.usermodel.Cell`` のオブジェクトです。


さらに、よく使う関数が登録されており、呼び出すことができます。
関数の実態は、 ``com.gh.mygreen.xlsmapper.expression.CustomFunctions`` です。

.. list-table:: 予め登録されている関数
   :widths: 50 50
   :header-rows: 1
   
   * - 関数の形式
     - 説明
   
   * - ``x:colToAlpha(<列番号>)``
     - 1から始まる列番号を英字名に変換します。


自身のJavaBeanも変数 ``targetBean`` として登録されているため、任意のメソッドを呼び出すこともできます。

.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="サンプル")
    public class SampleSheet {
    
        // 数式の指定
        @XlsOrder(1)
        @XlsLabelledCell(label="更新日付", type=LabelledCellType.Right)
        @XlsFormula("TODAY()")
        private Date date;
        
        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="レコード", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SampleRecord> records;
    }
    
    public class SampleRecord {
        
        // マッピングした位置情報
        private Map<String, CellPosition> positions;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="国語")
        private int kokugo;
        
        @XlsColumn(columnName="算数")
        private int sansu;
        
        // 数式の指定（変数、EL式を使用して指定）
        @XlsColumn(columnName="合計")
        @XlsFormula(value="SUM(${x:colToAlpha(targetBean.kokugoColNum)}{rowNumber}:${x:colToAlpha(targetBean.sansuColNum)}{rowNumber})", primary=true)
        private int sum;
        
        // プロパティ「kokugo」の列番号を返す。
        public String getKokugoColNum() {
            CellPosition position = positions.get("kokugo");
            return position.addRow(1);
        
        }
        
        // プロパティ「sansu」の列番号を返す。
        public String getSansuColNum() {
            CellPosition position = positions.get("sansu");
            return position.addRow(1);
        }

    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
数式を組み立てるメソッドを指定する場合（属性methodName）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


条件により数式を変更するような場合や、複雑な数式を組み立てる場合、数式を組み立てるメソッドを指定することができます。

メソッドの条件は次のようになります。

* 定義位置は、プロパティが定義してあるJavaBeanのクラスと同じ箇所。
* 修飾子は、public/private/protected などなんでもよい。
* 引数は、指定しないか、または次の値が指定可能。順番は任意。

  * セルのオブジェクト ``org.apache.poi.ss.usermodel.Cell`` 。
  * シートのオブジェクト ``org.apache.poi.ss.usermodel.Sheet`` 。
  * セルの座標 ``com.gh.mygreen.xlsmapper.util.CellAddress`` 。
  
    * 0から始まります。
    * 同じ座標を示すクラスとして、 ``java.awt.Point`` 、``org.apache.poi.ss.util.CellAddress`` が使用可能です。
  
  * システム設定 ``com.gh.mygreen.xlsmapper.Configuration`` 。
  
* 戻り値は、String型。
  
  * nullまたは空文字を返すと、ブランクセルとして出力されます。

.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="サンプル")
    public class SampleSheet {
    
        // 数式のメソッドの指定
        @XlsOrder(1)
        @XlsLabelledCell(label="更新日付", type=LabelledCellType.Right)
        @XlsFormula(methodName="getDateFormula")
        private Date date;
        
        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="レコード", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SampleRecord> records;
        
        // 数式を組み立てるメソッド
        public String getDateFormula() {
            return "TODAY()"
        }
    }
    
    public class SampleRecord {
        
        // マッピングした位置情報
        private Map<String, CellPosition> positions;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="国語")
        private int kokugo;
        
        @XlsColumn(columnName="算数")
        private int sansu;
        
        // 数式の指定（メソッドを指定）
        @XlsColumn(columnName="合計")
        @XlsFormula(methodName="getSumFormula", primary=true)
        private int sum;
        
        // 数式を組み立てるメソッド
        private String getSumFormula(CellPosition position) {
            
            int rowNumber = position.addRow(1);
            String colKokugo = CellReference.convertNumToColString(positions.get("kokugo").y);
            String colSansu = CellReference.convertNumToColString(positions.get("sansu").y);
            
            return String.format("SUM(%s%d:%s%d)", colKokugo, rowNumber, colSansu, rowNumber);
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
数式を優先する場合（属性primary）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

出力するオブジェクトのプロパティに値が設定されている場合、アノテーション ``@XlsFormula`` を指定していても、デフォルトでは値が出力されます。

数式を優先して出力する場合、 属性 ``primary=true`` を指定すると数式が優先されます。
特に、プリミティブ型など初期値が入っている場合や、 アノテーション ``@XlsConverter(defaultValue="<初期値>")`` で初期値を指定している場合には、注意が必要です。

.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // マッピングした位置情報
        private Map<String, CellAddress> positions;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="国語")
        private int kokugo;
        
        @XlsColumn(columnName="算数")
        private int sansu;
        
        // 数式の指定（数式を優先する場合）
        @XlsColumn(columnName="合計")
        @XlsFormula(value="SUM(B{rowNumber}:C{rowNumber})", primary=true)
        private int sum;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
式言語処理のカスタマイズ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


数式を直接指定する場合は、EL式の1つの実装である `JEXL <http://commons.apache.org/proper/commons-jexl/>`_ が利用できますが、実装を切り替えたり、デフォルトの関数を登録したりとカスタマイズができます。


設定を変更したい場合は、 ``Configuration#formulaFormatter()`` の値を変更します。

.. sourcecode:: java
    :linenos:
    
    // 数式をフォーマットする際のEL関数を登録する。
    ExpressionLanguageJEXLImpl formulaEL = new ExpressionLanguageJEXLImpl();
    Map<String, Object> funcs = new HashMap<>(); 
    funcs.put("x", CustomFunctions.class);
    formulaEL.getJexlEngine().setFunctions(funcs);
    
    // 数式をフォーマットするEL式の実装を変更する
    XlsMapper mapper = new XlsMapper();
    mapper.getConiguration().getFormulaFormatter().setExpressionLanguage(formulaEL);




^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
数式を設定する際のポイント
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
処理順序を一定にすることによる数式中の座標のずれを防ぐ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

数式にセルの座標を含む場合、セルを書き込んだ後に行を追加すると、セルの位置がずれる場合があります。
これは、内部で使用しているExcelのライブラリ「Apaceh POI」は、行を追加しても数式中の座標は不変であるためです。

このような場合、 アノテーション :ref:`@XlsOrder <annotationXlsOrder>` を使い、処理順序を指定することで回避することができます。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
EL式中でプロパティを参照する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

どのようなEL式の処理系もプロパティの値を参照する場合、基本的にはJavaBeanの規約に基づくpublicなgetterメソッド経由でアクセスすることになります。
ただし、JEXLは、publicフィールドも参照できます。

getter/stterのアクセッサメソッドの定義が面倒な場合は、動的に生成する `Lombok <http://projectlombok.org/>`_ を利用することをお薦めします。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
複雑な表を作成する
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

最後のレコードの値が数式でデザインが異なるような表を作成する場合を例に説明します。

.. figure:: ./_static/Formula_sample.png
   :align: center
   
   Formula(sample)



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
JavaBeanの定義
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 平均値(AVERAGE関数)の数式を組み立てるには、レコードの件数が取得が必要です。

  * このようなときは、レコードのインスタンスを作成するときに、親のインスタンスを設定し、たどれるようにします。

* プロパティの値が設定されている場合はプロパティの値を出力し、値がnullのときには数式を出力するようにするように、属性 ``primary=false`` を設定します。

  * 数値などの場合、プリミティブ型だと初期値が設定されてしまうため、ラッパー型を使います。

* レコードの色を変えたい場合は、:doc:`ライフサイクル・コールバック用 <annotation_lifecycle>` のアノテーションを使います。

  * ``@XlsPostSave`` で書き込んだ後に実行されるメソッドに付与し、その実装を行います。


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="成績表")
    public class SampleSheet {
        
        // マッピングした位置情報
        private Map<String, Point> positions;
        
        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SampleRecord> records;
        
        // レコードを追加する
        public void add(SampleRecord record) {
            if(records == null) {
                this.records = new ArrayList<>();
            }
            
            // 自身のインスタンスを渡す
            record.setParent(this); 
            
            // No.を自動的に振る
            record.setNo(records.size()+1);
            
            this.records.add(record);
        }
        
        public List<SampleRecord> getRecords() {
            return records;
        }
    }
    
    public class SampleRecord {
        
        // マッピングした位置情報
        private Map<String, CellPosition> positions;
        
        // 親のBean情報
        private SampleSheet parent;
        
        @XlsColumn(columnName="名前")
        private String name;
        
        @XlsColumn(columnName="国語")
        @XlsFormula(methodName="getKyokaAvgFormula", primary=false)
        private Integer kokugo;
        
        @XlsColumn(columnName="算数")
        @XlsFormula(methodName="getKyokaAvgFormula", primary=false)
        private Integer sansu;
        
        @XlsColumn(columnName="合計")
        @XlsFormula(value="SUM(C{rowNumber}:D{rowNumber})", primary=true)
        private Integer sum;
        
        // 各教科の平均の数式を組み立てる
        public String getKyokaAvgFormula(Point point) {
        
            // レコード名が平均のときのみ数式を出力する
            if(!name.equals("平均")) {
                return null;
            }
            
            // レコードのサイズ（平均用のレコード行を覗いた値）
            final int dataSize = parent.getRecords().size() -1;
            
            // 列名
            final String colAlpha = CellReference.convertNumToColString(point.x);
            
            // 平均値の開始/終了の行番号
            final int startRowNumber = point.y - dataSize +1;
            final int endRowNumber = point.y;
            
            return String.format("AVERAGE(%s%d:%s%d)", colAlpha, startRowNumber, colAlpha, endRowNumber);
        
        }
        
        // 最後のレコードのときにセルの色を変更
        @XlsPostSave
        public void handlePostSave(final Sheet sheet) {
            
            if(!name.equals("平均")) {
                return;
            }
            
            final Workbook book = sheet.getWorkbook();
            
            for(Point address : positions.values()) {
                Cell cell = POIUtils.getCell(sheet, address);
                
                CellStyle style = book.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());
                
                // 塗りつぶし
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                
                // 罫線の上部を変更
                style.setBorderTop(CellStyle.BORDER_DOUBLE);
                
                cell.setCellStyle(style);
            }
            
        }
        
        public void setParent(SampleSheet parent) {
            this.parent = parent;
        }
        
        public void setNo(int no) {
            this.no = no;
        }
        
        public SampleRecord name(final String name) {
            this.name = name;
            return this;
        }
        
        public SampleRecord kokugo(final Integer kokugo) {
            this.kokugo = kokugo;
            return this;
        }
        
        public SampleRecord sansu(final Integer sansu) {
            this.sansu = sansu;
            return this;
        }
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
データの作成とファイルの出力
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 計算式を出力するプロパティの値は、省略します。
* 特に、平均値を設定するレコードは、名前以外の値は省略します。

.. sourcecode:: java
    :linenos:
    
    // データの作成
    final SampleSheet outSheet = new SampleSheet();
    
    // 各人のレコードの作成（合計値の設定は行わない。）
    outSheet.add(new SampleRecord().name("山田太郎").kokugo(90).sansu(85));
    outSheet.add(new SampleRecord().name("鈴木一郎").kokugo(85).sansu(80));
    outSheet.add(new SampleRecord().name("林三郎").kokugo(80).sansu(60));
    
    // 平均値用のレコード(点数などのデータ部分はなし)
    outSheet.add(new SampleRecord().name("平均"));
    
    // ファイルへの書き込み
    XlsMapper mapper = new XlsMapper();
    mapper.getConiguration().setContinueTypeBindFailure(true);
    
    File outFile = new File("seiseki.xlsx");
    try(InputStream template = new FileInputStream("template.xlsx");
            OutputStream out = new FileOutputStream(outFile)) {
        
        mapper.save(template, out, outSheet);
    }


