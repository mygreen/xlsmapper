
.. _annotationXlsDateTimeConverter:

-----------------------------------------------
``@XlsDateTimeConverter``
-----------------------------------------------

日時型に対する変換規則を指定する際に利用するアノテーションです。

対応するJavaのクラスタイプと、アノテーションを付与していないときに適用されるの標準の書式は以下の通りです。

.. list-table:: 対応する日時のクラスタイプと標準の書式
   :widths: 50 50
   :header-rows: 1
   
   * - クラスタイプ
     - 標準の書式
     
   * - ``java.util.Date``
     - *yyyy-MM-dd HH:mm:ss*
     
   * - ``java.util.Calendar``
     - *yyyy-MM-dd HH:mm:ss*
     
   * - ``java.sql.Date``
     - *yyyy-MM-dd*
     
   * - ``java.sql.Time``
     - *HH:mm:ss*
     
   * - ``java.sql.Timestamp``
     - *yyyy-MM-dd HH:mm:ss.SSS*
     
   * - ``java.time.LocalDateTime``
     - *uuuu-MM-dd HH:mm:ss*
     
   * - ``java.time.LocalDate``
     - *uuuu-MM-dd*
     
   * - ``java.time.LocalTime``
     - *HH:mm:ss*
     


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
読み込み時の書式の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

読み込み時にセルの種類が日付、時刻ではない場合、文字列として値を取得し、
その値を属性 ``javaPattern`` で指定した書式に従いパースし、Javaの日時型に変換します。

* 属性 ``javaPattern`` で書式を指定します。 `[ver1.1+]`
  
  * java.util.Date/java.util.Calendar/java.sql.XXXX系のクラスの場合、 `java.text.SimpleDateFormat <https://docs.oracle.com/javase/jp/8/docs/api/java/text/SimpleDateFormat.html>`_ で解釈可能な書式を設定します。
  
  * java.time.XXX系のクラスの場合、 `java.time.format.DateTimeFormatter <https://docs.oracle.com/javase/jp/8/docs/api/java/time/format/DateTimeFormatter.html>`_ で解釈可能な書式を設定します。
  
* 属性 ``locale`` でロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性 ``lenient`` で、日付/時刻の解析を厳密に行わないか指定します。
  
  * trueの厳密に解析を行いません。falseの場合厳密に解析を行います。

* 属性 ``timezone`` でタイムゾーンを指定します。
  
  * Asia/Tokyo, GMT, GMT+09:00などの値を指定します。  
  * ただし、オフセットを持たないクラスタイプ「LocalDateTime, LocalDate, LocalTime」の時は、指定しても意味がありません。


* 書式に合わない値をパースした場合、例外 ``TypeBindException`` が発生します。



.. sourcecode:: java
    :linenos:
    :caption: 読み込み時の書式の指定
    
    public class SampleRecord {
        
        @XlsColumn(columnName="有効期限")
        @XlsDateTimeConverter(javaPattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP",
                lenient=true)
        private Date expired;
        
    }


.. note::
    読み込み時のセルの値が属性javaPatternで指定した書式に一致していなくても、セルのタイプが日付または時刻の場合は、例外の発生なく読み込むことができます。
    セルの表示形式の分類が文字列の場合は、アノテーション ``@XlsDateTimeConverter(javaPattern="<書式>")`` で指定した書式に従い処理されます。
    
    ただし、型変換用のアノテーション ``@XlsDateTimeConverter`` を付与しない場合は、Javaの型ごとに上記の書式が標準で適用されます。`[ver0.5+]` 
    


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時の書式の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時の書式は、基本的にテンプレートファイルに設定してある値を使用します。
また、アノテーションでも直接指定することができます。

* 属性 ``excelPattern`` で書き込み時の書式を指定します。 `[ver1.1+]`

  * Excelの書式を指定する場合は、 `ユーザ定義 <http://mygreen.github.io/excel-cellformatter/sphinx/format_basic.html>`_ の形式で指定する必要があります。


.. sourcecode:: java
    :linenos:
    :caption: 書き込み時の書式の指定
    
    public class SampleRecord {
        
        @XlsColumn(columnName="有効期限")
        @XlsDateTimeConverter(excelPattern="[$-411]yyyy\"年\"mm\"月\"dd\"日\" hh\"時\"mm\"分\"ss\"秒\"")
        private Date expired;
        
    }


.. note::
    
    テンプレートファイルのセルの書式を「標準」に設定している場合に書き込むと、
    書式が「標準」設定の全てのセルの書式が書き換わってしまいます。
    
    そのため、日付や数値などの書式が必要な場合は、テンプレートファイルで予め書式を設定しておくか、
    アノテーションの属性excelPatternで書式を指定しておいてください。



