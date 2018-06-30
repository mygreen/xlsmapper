

.. _annotationXlsNumberConverter:

------------------------------------
``@XlsNumberConverter``
------------------------------------

数値型に対する変換規則を指定する際に利用するアノテーションです。

対応するJavaの型は次の通り。

* byte/short/int/long/float/doubleのプリミティブ型とそのラッパークラス。
* ``java.math.BigDecimal`` / ``java.math.BigInteger`` 。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
読み込み時の書式の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

読み込み時にセルの種類が数値（通貨、会計、パーセンテージ、分数、指数）ではない場合、
文字列として値を取得し、その値を属性 ``javaPattern`` で指定した書式に従いパースし、Javaの数値型に変換します。

* 属性 ``javaPattern`` で読み込み時の書式を指定します。 `[ver1.1+]`
    
  * Javaのクラス ``java.text.DecimalFormat`` で解釈可能な書式を設定します。
  
  * ver1.0以前は、属性patternを使っていましたが、廃止になりました。
  
* 属性 ``locale`` でロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性 ``currency`` で通貨コード（ISO-4217コード）を指定します。
    
  * Javaのクラス ``java.util.Currency`` で解釈可能なコードを指定します。

* 書式に合わない値をパースした場合、例外 ``TypeBindException`` が発生します。

.. sourcecode:: java
    :linenos:
    :caption: 読み込み時の指定方法
    
    public class SampleRecord {
        
        @XlsColumn(columnName="給与")
        @XlsNumberConverter(javaPattern="\u00A4\u00A4 #,##0.0000", locale="ja_JP", currency="USD")
        private double salary;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時の書式の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時の書式は、基本的にテンプレートファイルに設定してある値を使用します。
また、アノテーションでも直接指定することができます。

* 属性 ``excelPattern`` で書き込み時の書式を指定します。 `[ver1.1+]`

  * Excelの書式を指定する場合は、 `ユーザ定義 <http://mygreen.github.io/excel-cellformatter/sphinx/format_basic.html>`_ の形式で指定する必要があります。


.. sourcecode:: java
    :linenos:
    :caption: 書き込み時の指定方法
    
    public class SampleRecord {
        
        @XlsColumn(columnName="給与")
        @XlsNumberConverter(javaPattern="[$-411]\"￥\"#,##0.0000")
        private double salary;
        
    }


.. note::
    
    テンプレートファイルのセルの書式を「標準」に設定している場合に書き込むと、
    書式が「標準」設定の全てのセルの書式が書き換わってしまいます。
    
    そのため、日付や数値などの書式が必要な場合は、テンプレートファイルで予め書式を設定しておくか、
    アノテーションの属性excelPatternで書式を指定しておいてください。



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
有効桁数の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Excel内部とJavaの数値は、表現可能な有効桁数が異なります。

そのため、特に読み込み時などExcelの仕様に合わせてJavaのクラスに指定することが可能です。


* 属性 ``precision`` で有効桁数を指定します。 `[ver0.5+]`
   
  * 0以下の値を設定すると、桁数の指定を省略したことになり、無制限になります。

.. note::
   
   Excelでは有効桁数が15桁であるため、Javaのlong型など15桁を超える表現が可能な数値を書き込んだ場合、数値が丸められるため注意してください。
   
   * 例えば、long型の19桁の数値 ``1234567890123456789`` を書き込んだ場合、16桁以降の値が丸められ ``1234567890123450000`` として書き込まれます。
   * Excelの仕様については、`Excel の仕様と制限 <https://support.office.com/ja-jp/article/Excel-%E3%81%AE%E4%BB%95%E6%A7%98%E3%81%A8%E5%88%B6%E9%99%90-1672b34d-7043-467e-8e27-269d656771c3?ui=ja-JP&rs=ja-JP&ad=JP>`_ を参照してください。



