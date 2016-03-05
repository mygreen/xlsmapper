
.. _annotationXlsDateConverter:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsDateConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

日時型に対する変換規則を指定する際に利用するアノテーションです。

対応するJavaの型は次の通り。

* ``java.util.Date``
* ``java.sql.Date`` / ``java.sql.Timestamp`` / ``java.sql.Time``
* ``java.util.Calendar``  `[ver0.5+]`


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の書式の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時にセルの種類が日付、時刻ではない場合、文字列として値を取得し、
その値を属性javaPatternで指定した書式に従いパースし、Javaの日時型に変換します。

* 属性javaPatternで書式を指定します。 `[ver1.1+]`
  
  * Javaのクラス ``java.util.SimpleDateFormat`` で解釈可能な書式を指定します。
  
  * ver1.0以前は、属性patternを使っていましたが、廃止になりました。
  
* 属性localeでロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性lenientで、日付/時刻の解析を厳密に行わないか指定します。
  
  * trueの厳密に解析を行いません。falseの場合厳密に解析を行います。

* 書式に合わない値をパースした場合、例外TypeBindExceptionが発生します。



.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(columnName="有効期限")
        @XlsDateConverter(javaPattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP",
                lenient=true)
        private Date expired;
        
    }


.. note::
    読み込み時のセルの値が属性javaPatternで指定した書式に一致していなくても、セルのタイプが日付または時刻の場合は、例外の発生なく読み込むことができます。
    セルの表示形式の分類が文字列の場合は、アノテーション ``@XlsDateConverter(javaPattern="<書式>")`` で指定した書式に従い処理されます。
    
    ただし、型変換用のアノテーション ``@XlsDateConverter`` を付与しない場合は、Javaの型ごとに次の書式が標準で適用されます。`[ver0.5+]` 
    
    * ``java.util.Date`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Date`` の場合、デフォルトで `yyyy-MM-dd` の書式が適用されます。
    * ``java.sql.Time`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Timestamp`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss.SSS` の書式が適用されます。
    * ``java.util.Calendar`` の場合、デフォルトで、 `yyyy-MM-dd HH:mm:ss` の書式が適用されます。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
書き込み時の書式の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

書き込み時の書式は、基本的にテンプレートファイルに設定してある値を使用します。
また、アノテーションでも直接指定することができます。

* 属性excelPatternで書き込み時の書式を指定します。 `[ver1.1+]`

  * Excelの書式を指定する場合は、 `ユーザ定義 <http://mygreen.github.io/excel-cellformatter/sphinx/format_basic.html>`_ の形式で指定する必要があります。


.. sourcecode:: java
    
    public class SampleRecord {
        
        @XlsColumn(columnName="有効期限")
        @XlsDateConverter(excelPattern="[$-411]yyyy\"年\"mm\"月\"dd\"日\" hh\"時\"mm\"分\"ss\"秒\"")
        private Date expired;
        
    }


.. note::
    
    テンプレートファイルのセルの書式を「標準」に設定している場合に書き込むと、
    書式が「標準」設定の全てのセルの書式が書き換わってしまいます。
    
    そのため、日付や数値などの書式が必要な場合は、テンプレートファイルで予め書式を設定しておくか、
    アノテーションの属性excelPatternで書式を指定しておいてください。



