--------------------------------------
型変換用のアノテーション
--------------------------------------

* 読み込み時は、Excelの型とJavaの型が一致する場合、そのままマッピングします。

  * 型が一致しない場合、Excelのセルの値を文字列として取得し、その値をパースしてJavaの型に変換します。
  * 文字列をパースする書式などの指定は、型ごとの専用のアノテーションを付与します。
  * 型アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。

* 書き込み時は、Javaの型に合ったExcelのセルの型で処理します。
  
  * テンプレートファイルのセルの型とJavaの型が一致すれば、そのまま書き込みます。
    一致しない場合は、付与されたアノテーションに従って書式などを設定します。
  * 型アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。


.. list-table:: ExcelとJavaのマッピング可能な型
   :widths: 30 30 40
   :header-rows: 1
   
   * - Excelの型
     - Javaの型
     - 対応するアノテーション
     
   * - | ブール型
       | （TRUE/FALSE）
     - | boolean/Boolean
     - ``@XlsBooleanConverter``
     
   * - 文字列
     - | String
       | char/Character(先頭の1文字のみがマッピング対象)
     - ``@XlsStringConverter``
   
   * - | 数値
       | （数値/通貨/会計/パーセンテージ/分数/指数）
     - | byte/short/int/long/float/double/これらのラッパークラス
       | /java.math.BigDecimal/java.math.BigInteger
     - ``@XlsNumberConverter``
     
   * - | 日時
       | （日付/時刻）
     - | java.util.Date/java.util.Calendar
       | /java.sql.Date/Time/Timestamp
     - ``@XlsDateConverter``
   
   * - 文字列
     - | 任意の列挙型
     - ``@XlsEnumConverter``
     
   * - 文字列
     - | 配列
       | /Collection(java.util.List/java.util.Set)
     - ``@XlsArrayConverter``
     
   * - | ハイパーリンクを設定したセル
     - | java.net.URI
       | /com.gh.mygreen.xlsmapper.cellconvert.CellLink(本ライブラリの独自の型)
     - 



.. _annoXlsConverter:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

型固有の変換用のアノテーションではなく、共通のアノテーションです。

値がnullや空のときの初期値の定義やトリム処理の設定を定義します。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
初期値の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性defaultValueで読み込み時/書き込み時のデフォルト値を指定します。
  
* 日付などの書式がある場合、専用のアノテーションで指定した書式 ``@XlsDateConverter(pattern="<任意の書式>")`` を元に、文字列をそのオブジェクトに変換し処理します。
  
* デフォルト値を指定しないでプリミティブ型に対して読み込む場合、その型の初期値が設定されます。
    
  * int型は0、double型は0.0、boolean型はfalse。char型の場合は、 '\\u0000' 。
  * プリミティブのラッパークラスや参照型の場合は、nullが設定されます。
    
* 指定したデフォルト値がマッピング先の型として不正な場合は、通常の型変換エラーと同様に、例外 ``com.gh.mygreen.xlsmapper.cellconvert.TypeBindException`` がスローされます。`[ver0.5]`
    
* char型にマッピングする場合、デフォルト値が2文字以上でも、先頭の一文字がマッピングされます。


.. sourcecode:: java
    
    public class SampleRecord {
    
        @XlsColumn(columnName="ID")
        @XlsConverter(defaultValue="-1")
        private int id;
        
        @XlsColumn(columnName="更新日時")
        @XlsConverter(defaultValue="2010/01/01") // 属性patternで指定した書式に沿った値を指定します。
        @XlsDateConverter(pattern="yyyy/MM/dd")
        private Date updateTime;
        
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
トリミングの指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

トリミングを行いたい場合、属性trimの値を 'true' に指定します。

* 属性trimの値をtrueにすると、読み込み時と書き込み時にトリムを行います。
   
* シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
  
  * ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、変換する前にトリム処理を行います。
  
* 値が空のセルをString型に読み込む場合、``trim = false`` のときはnull設定されますが、``trim = true`` のきは、空文字が設定されます。`[ver0.5+]` 


.. sourcecode:: java
    
    public class SampleRecord {
    
        @XlsColumn(columnName="ID")
        @XlsConverter(defaultValue=" 123 ", trim=true) // 属性defaultValueもトリム対象となる。
        private int id;
        
        @XlsColumn(columnName="名前")
        @XlsConverter(trim=true) // 空のセルを読み込むと空文字が設定される。
        private String name;
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
書き込み時のセルの文字の制御の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


書き込み時にセルの折り返し設定や縮小表示設定を強制的に行うこともできます。

書き込み時はテンプレートとなるシートのセルの書式を基本的に使用するので、事前に折り返し設定が有効になって入れば書き込み時もそれらの設定が有効になります。

* 属性forceWrapTextの値がtrueの場合、強制的にセルの内の文字表示の設定「折り返して全体を表示する」が有効になります。
   
  * falseの場合、テンプレートとなるセルの設定を引き継ぎます。
   
* 属性forceShrinkToFitの値がtrueの場合、強制的にセル内の文字表示の設定「縮小して全体を表示する」が有効になります。
    
  * falseの場合、テンプレートとなるセルの設定を引き継ぎます。


.. sourcecode:: java
    
    public class SampleRecord {
    
        @XlsColumn(columnName="ID")
        @XlsConverter(forceWrapText=true) // 「縮小して全体を表示する」が有効になる。
        private int id;
        
        @XlsColumn(columnName="名前")
        @XlsConverter(forceShrinkToFit=true) //「折り返して全体を表示する」が有効になる。
        private String name;
        
        @XlsColumn(columnName="備考")
        @XlsConverter(forceShrinkToFit=false) // 設定しない場合は、テンプレート設定が有効になる。
        private String comment;
    }


.. note::
    
    Excelの仕様上、設定「折り返して全体を表示する」と「縮小して全体を表示する」は、二者択一であるため、両方の設定を有効にすることはできません。
    もし、属性forceWrapTextとforceShrinkToFitの値をtrueに設定した場合、forceShrinkToFitの設定が優先され、「縮小して全体を表示する」が有効になります。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
独自の変換規則を指定する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

本ライブラリで対応していない型に変換したい時など、属性「converterClass」で独自のConverter用クラスを指定します。

Converterクラスは、インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverter`` を実装する必要があります。

詳細は、 :doc:`CellConverterの拡張 <extension_cellconverter>` を参照してください。


.. sourcecode:: java
    
    
    // CellConverterの定義
    public class LocaleDateConverter extends CellConverter<LocaleDate> {
    
        // シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
        @Override
        public LocaleDate toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
            //TODO: 実装する
        }
         
        //シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
        @Override
        Cell toCell(FieldAdaptor adaptor, LocaleDate targetValue, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
            //TODO: 実装する
         }
    }
    
    // 独自CellConverterの指定
    public class SampleRecord {
    
        // フィールド独自のConveterの設定
        @XlsColumn(columnName="更新日付")}
        @XlsConverter(converterClass=LocaleDateConvereter.class)}
        private LocaleDate localeDate;
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsBooleanConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Excelのセルの種類が「ブール型」以外の場合に、Javaの「boolean/Boolean」にマッピング規則を定義します。


単純に「true、false」以外に、「○、×」とのマッピングも可能となります。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性loadForTrue、loadForFalseで読み込み時のtrueまたはfalseと判断するの候補の値を指定します。
   
* 属性loadForTrueとloadForFalseの値に重複がある場合、loadForTrueの定義が優先されまます。
  
* 属性laodForTrueを指定しない場合、デフォルトで「"true", "1", "yes", "on", "y", "t"」が設定されます。
  
* 属性loadForFalseを指定しない場合、デフォルトで「"false", "0", "no", "off", "f", "n"」が設定されます。
    
* 属性ignoreCaseの値をtrueにすると、読み込み時に大文字、小文字の区別なく候補の値と比較します。


.. sourcecode:: java
    
    public class SampleRecord {
        
        // boolean型の読み込み時のtrueとfalseの値の変換規則を指定します。
        @XlsColumn(columnName="ステータス")
        @XlsBooleanConverter(
                loadForTrue={"○", "有効", "レ"},
                loadForFalse={"×", "無効", "-", ""})
        private boolean availaled;
        
        // 読み込み時の大文字・小文字の区別を行わない
        @XlsColumn(columnName="チェック")
        @XlsBooleanConverter(
              loadForTrue={"OK"},
              loadForFalse={"NO"},
              ignoreCase=true)
        private Boolean checked;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
書き込み時の値の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性saveAsTrueとsaveAsFalseで書き込み時のtrueまたはfalse値に該当する文字を指定します。
    
* 属性saveAsTrueを指定しない場合は、デフォルトで"true"が設定され、セルのタイプもブール型になります。
  
* 属性saveAsFalseを指定しない場合は、デフォルトで"false"が設定され、セルのタイプもブール型になります。
    
* 読み込みと書き込みの両方を行う場合、属性loadForTrueとloadForFalseの値に属性saveAsTrueとsaveAsFalseの値を含める必要があります。
    

.. sourcecode:: java
    
    public class SampleRecord {
        
        // boolean型の書き込み時のtrueとfalseの値の変換規則を指定します。
        @XlsColumn(columnName="ステータス")
        @XlsBooleanConverter(
                loadForTrue={"○", "有効", "レ"}, // 読み書きの両方を行う場合、書き込む値を含める必要がある。
                loadForFalse={"×", "無効", "-", ""},
                saveAsTrue="○",
                saveAsFalse="-")
        )
        private boolean availaled;
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
変換に失敗した際の処理
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
読み込み時にtrueまたはfalseに変換できない場合、例外TypeBindExceptionが発生します。

* 属性failToFalseをtrueに設定することで、変換できない場合に強制的に値をfalseとして読み込み、例外を発生しなくできます。

.. sourcecode:: java
    
    public class SampleRecord {
        
        // 読み込み時に変換できない場合に、強制的に値をfalseとして読み込みます。
        @XlsColumn(columnName="ステータス")
        @XlsBooleanConverter(
                loadForTrue={"○", "有効", "レ"},
                loadForFalse={"×", "無効", "-", ""},
                failToFalse=true)
        private boolean availaled;
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsNumberConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

数値型に対する変換規則を指定する際に利用するアノテーションです。

対応するJavaの型は次の通り。

* byte/short/int/long/float/doubleのプリミティブ型とそのラッパークラス。
* ``java.math.BigDecimal`` / ``java.math.BigInteger`` 。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の書式の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時にセルの種類が数値（通貨、会計、パーセンテージ、分数、指数）ではない場合、
文字列として値を取得し、その値を属性patternで指定した書式に従いパースし、Javaの数値型に変換します。

* 属性patternで書式を指定します。
    
  * Javaのクラス ``java.text.DecimalFormat`` で解釈可能な書式を設定します。
    
* 属性localeでロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性currencyで通貨コード（ISO-4217コード）を指定します。
    
  * Javaのクラス ``java.util.Currency`` で解釈可能なコードを指定します。

* 書式に合わない値をパースした場合、例外TypeBindExceptionが発生します。

.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(name="給与")
        @XlsNumberConverter(pattern="#,##0.0000", locale="ja_JP", currency="USD")
        private double salary;
        
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
有効桁数の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Excel内部とJavaの数値は、表現可能な有効桁数が異なります。

そのため、特に読み込み時などExcelの仕様に合わせてJavaのクラスに指定することが可能です。


* 属性precisionで有効桁数を指定します。`[ver0.5+]`
   
  * Excelの仕様上、有効桁数は15桁であるため、デフォルト値は15です。
  * 0以下の値を設定すると、桁数の指定を省略したことになります。


.. note::
   
   Excelでは有効桁数が15桁であるため、Javaのlong型など15桁を超える表現が可能な数値を書き込んだ場合、数値が丸められるため注意してください。
   
   * 例えば、long型の19桁の数値 ``1234567890123456789`` を書き込んだ場合、16桁以降の値が丸められ ``1234567890123450000`` として書き込まれます。
   * Excelの仕様については、`Excel の仕様と制限 <https://support.office.com/ja-jp/article/Excel-%E3%81%AE%E4%BB%95%E6%A7%98%E3%81%A8%E5%88%B6%E9%99%90-1672b34d-7043-467e-8e27-269d656771c3?ui=ja-JP&rs=ja-JP&ad=JP>`_ を参照してください。



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsDateConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

日時型に対する変換規則を指定する際に利用するアノテーションです。

対応するJavaの型は次の通り。

* ``java.util.Date``
* ``java.sql.Date`` / ``java.sql.Timestamp`` / ``java.sql.Time``
* ``java.util.Calendar``  `[ver0.5]`


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の書式の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時にセルの種類が日付、時刻ではない場合、文字列として値を取得し、
その値を属性patternで指定した書式に従いパースし、Javaの日時型に変換します。

* 属性patternで書式を指定します。
  * Javaのクラス ``java.util.SimpleDateFormat`` で解釈可能な書式を指定します。
    
* 属性localeでロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性lenientで、日付/時刻の解析を厳密に行わないか指定します。
  
  * trueの厳密に解析を行いません。falseの場合厳密に解析を行います。

* 書式に合わない値をパースした場合、例外TypeBindExceptionが発生します。
  

.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(columnName="有効期限")
        @XlsDateConverter(pattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP",
                lenient=true)
        private Date expired;
        
    }


.. note::
    読み込み時のセルの値が属性patternで指定した書式に一致していなくても、セルのタイプが日付または時刻の場合は、例外の発生なく読み込むことができます。
    セルの表示形式の分類が文字列の場合は、アノテーション ``@XlsDateConverter(pattern="<書式>")`` で指定した書式に従い処理されます。
    
    ただし、型変換用のアノテーション ``@XlsDateConverter`` を付与しない場合は、Javaの型ごとに次の書式が標準で適用されます。`[ver0.5+]` 
    
    * ``java.util.Date`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Date`` の場合、デフォルトで `yyyy-MM-dd` の書式が適用されます。
    * ``java.sql.Time`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Timestamp`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss.SSS` の書式が適用されます。
    * ``java.util.Calendar`` の場合、デフォルトで、 `yyyy-MM-dd HH:mm:ss` の書式が適用されます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsEnumConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

列挙型の変換規則の設定を行います。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
基本的な使い方
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

セルの値と列挙型の要素の値をマッピングさせます。

要素の値とは、 ``Enum#name()`` の値です。

* 属性ignoreCaseの値をtrueにすると、読み込み時に大文字/小文字の区別なく変換します。

.. sourcecode:: java
    
    public class SampleRecord {
        
        // 列挙型のマッピング
        @XlsColumn(columnName="権限")
        @XlsEnumConverter(ignoreCase=true)
        private RoleType role;
        
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal, Admin;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
別名でマッピングする場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

別名をマッピングする場合、属性valueMethodNameで列挙型の要素の別名を取得するメソッド名を指定します。

.. sourcecode:: java
    
    public class SampleRecord {
        
        // 別名による列挙型のマッピング
        @XlsColumn(columnName="権限")
        @XlsEnumConverter(valueMethodName="localeName")
        private RoleType role;
        
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal("一般権限"), Admin("管理者権限");
        
        // 別名の設定
        private String localeName;
        
        private RoleType(String localeName) {
            this.localeName = localeName;
        }
      
        // 別名の取得用メソッド
        public String localeName() {
            return this.localeName;
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsArrayConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

配列またはCollection型（List, Set）の変換規則の設定を行います。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
基本的な使い方
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Collection型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。

* ``java.util.List`` の場合、``java.util.ArrayList`` がインスタンスのクラスとなります。
* ``java.util.Set`` の場合、``java.util.LinkedHashSet`` がインスタンスのクラスとなります。


配列、またはCollection型の要素で指定可能なクラスタイプは、次の通りです。

* String型
* プリミティブ型「boolean/char/byte/short/int/long/float/double」と、そのラッパークラス。
* {@link java.math.BigDecimal}/{@link java.math.BigInteger}


文字列のセルに対して、任意の区切り文字を指定し、配列やListに対してマッピングします。

* 属性separatorで区切り文字を指定します。

  * 区切り文字の初期値は、半角カンマ(,)です。
  
* 型変換アノテーション ``@XlsConverter(trim=true)`` を付与し、トリム処理を有効にしている設定の場合、区切った項目にもトリム処理が適用されます。 `[ver0.5+]` 
  
  * 属性ignoreEmptyItemの値をtrueに設定していると、トリム処理によって項目が空文字となった場合、その項目は無視されます。


.. sourcecode:: java
    
    public class SampleRecord {
        
        // 区切り文字の指定
        @XlsColumn(columnName="リスト")
        @XlsArrayConverter(separator="\n")
        private List<String> list;
        
        // 要素のトリム処理を指定する
        @XlsColumn(columnName="配列")
        @literal @XlsConverter(trim=true)    // 区切った配列の要素にもトリムが適用されます。
        @XlsArrayConverter(separator=",")
        private int[] array;
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
空の要素を無視する場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性ignoreEmptyItemで、区切った項目の値が空文字の場合、無視するか指定します。
    
例えば、区切り文字","のとき、セルの値が ``"a,,b"`` の場合、trueを設定すると ``["a", "b"]`` として読み込みます。

書き込み時も同様に、値が空またはnullの項目を無視します。


.. sourcecode:: java
    
    public class SampleRecord {
        
        // 空の要素を無視する場合
        @XlsColumn(columnName="集合")
        @XlsArrayConverter(ignoreEmptyItem=true)
        private Set<Integer> set;
        
    }



