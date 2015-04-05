--------------------------------------
型変換用のアノテーション
--------------------------------------


数値や日時、列挙型にマッピングする際の変換規則の設定を行います。

型変換用のアノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

値がnullや空のときの初期値の定義やトリム処理の設定を定義します。

* 属性defaultValueで読み込み時/書き込み時のデフォルト値を指定します。
    
    * デフォルト値を指定しないでプリミティブ型に対して読み込む場合、数値型の場合は0や0.0、booleanの場合はfalseが設定されます。
      プリミティブのラッパークラスやオブジェクト型の場合は、nullが設定されます。
      
* 属性trimの値をtruにすると、読み込み時と書き込み時にトリムを行います。
   
    * シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
      ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、変換する前にトリム処理を行います。


.. sourcecode:: java
    
    public class UnitUser {
        
        // 読み込み時や書き込み時の初期値、トリム処理の有効を設定できます。
        @XlsConverter(defaultValue="10", trim=true)
        private int age;
        
    }



書き込み時にセルの折り返し設定や縮小表示設定を強制的に行うこともできます。

書き込み時はテンプレートとなるシートのセルの書式を基本的に使用するので、事前に折り返し設定が有効になって入れば書き込み時もそれらの設定が有効になります。

* 属性forceWrapTextの値がtrueの場合、強制的にセルの内の文字表示の設定「折り返して全体を表示する」が有効になります。
   
    * falseの場合、テンプレートとなるセルの設定を引き継ぎます。
   
 * 属性forceShrinkToFitの値がtrueの場合、強制的にセル内の文字表示の設定「縮小して全体を表示する」が有効になります。
    
    * falseの場合、テンプレートとなるセルの設定を引き継ぎます。


.. sourcecode:: java
    
    public class UnitUser {
      
        // 書き込み時のセルの文字表示の折り返し設定などが指定できます。
        @XlsConverter(forceWrapText=false, forceShrinkToFit=true)
        private int age;
        
    }


.. note::
    
    Excelの仕様上、設定「折り返して全体を表示する」と「縮小して全体を表示する」は、二者択一であるため、両方の設定を有効にすることはできません。
    もし、属性forceWrapTextとforceShrinkToFitの値をtrueに設定した場合、forceShrinkToFitの設定が優先され、「縮小して全体を表示する」が有効になります。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsBooleanConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

boolean、Boolean型の変換規則の設定を行います。

単純に、「true、false」以外に、「○、×」とのマッピングも可能となります。
 
* 属性loadForTrue、loadForFalseで読み込み時のtrueまたはfalseと判断するの候補の値を指定します。
   
    * 属性loadForTrueとloadForFalseの値に重複がある場合、loadForTrueの定義が優先されまます。
    
    * 属性laodForTrueを指定しない場合、デフォルトで「"true", "1", "yes", "on", "y", "t"」が設定されます。
    
    * 属性loadForFalseを指定しない場合、デフォルトで「"false", "0", "no", "off", "f", "n"」が設定されます。
    
* 属性saveAsTrueとsaveAsFalseで書き込み時のtrueまたはfalse値に該当する文字を指定します。
    
    * 属性saveAsTrueを指定しない場合は、デフォルトで"true"が設定され、セルのタイプもBoolean型になります。
    
    * 属性saveAsFalseを指定しない場合は、デフォルトで"false"が設定され、セルのタイプもBoolean型になります。
    
    * 読み込みと書き込みの両方を行うプログラムの場合、loadForTrueとloadForFalseの値にsaveAsTrueとsaveAsFalseの値を含める必要があります。
    
* 属性ignoreCaseの値をtrueにすると、読み込み時に大文字、小文字の区別なく候補の値と比較します。


.. sourcecode:: java
    
    public class UnitUser {
        
        // boolean型の読み込み時と書き込み時のtrueとfalseの値の変換規則を指定します。
        @XlsBooleanConverter(
          loadForTrue={"○", "有効", "レ"}, loadForFalse={"×", "無効", "-", ""},
          saveAsTrue="○", saveAsFalse="-",
          ignoreCase=true
        )
        private boolean availale;
        
    }


 
読み込み時にtrueまたはfalseに変換できない場合、例外TypeBindExceptionが発生します。

* 属性failToFalseをtrueに設定することで、変換できない場合に強制的に値をfalseとして読み込み、例外を発生しなくできます。

 
.. sourcecode:: java
    
    public class UnitUser {
        
        // 読み込み時に変換できない場合に、強制的に値をfalseとして読み込みます。
        @XlsBooleanConverter(failToFalse=true)
        private boolean availale;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsNumberConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


数値型（short、int、long、float、double、プリミティブのラッパークラス、BigDecimal、BigInteger）の書式などの設定を行います。
 
* 属性patternで書式を指定します。
    
    * Javaのクラスjava.text.DecimalFormatで解釈可能な書式を設定します。
    
* 属性localeでロケールを指定します。
    
    * 言語コードのみを指定する場合、'ja'の2桁で指定します。
    * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性currencyで通貨コード（ISO-4217コード）を指定します。
    
    * Javaのクラスjava.util.Currencyで解釈可能なコードを指定します。


.. sourcecode:: java
    
    public class UnitUser {
      
        @XlsNumberConverter(pattern="#,##0.0000", locale="ja_JP", currency="USD")
        private double salary;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsDateConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

日付型（java.util.Date、java.sql.Date, java.sql.Timestamp, java.sql.Time）の書式などの設定を行います。

* 属性patternで書式を指定します。
    * Javaのクラスjava.util.SimpleDateFormatで解釈可能な書式を指定します。
    
* 属性localeでロケールを指定します。
    
    * 言語コードのみを指定する場合、'ja'の2桁で指定します。
    * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性lenientで、日付/時刻の解析を厳密に行わないか指定します。
    
    * trueの厳密に解析を行いません。falseの場合厳密に解析を行います。
    * 読み込み時に書式に合わないセルの値を読み込んだ場合、例外TypeBindExceptionが発生します。
    

.. sourcecode:: java
    
    public class UnitUser {
        
        @XlsDateConverter(pattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP", lenient=true)
        private Date expired;
        
    }


.. note::
    読み込み時のセルの値が属性patternで指定した書式に一致していなくても、セルのタイプが日付または時刻の場合は、例外の発生なく読み込むことができます。
 


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsEnumConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

列挙型の変換規則の設定を行います。

* 属性ignoreCaseの値をtrueにすると、読み込み時に大文字/小文字の区別なく変換します。
* 属性valueMethodNameで列挙型の項目の値を取得するメソッド名を指定します。
    
    * 指定しない場合、Enum#name()のメソッドの値が使用されます。


.. sourcecode:: java
    
    public class UnitUser {
        
        // 列挙型のマッピング
        @XlsEnumConverter(ignoreCase=true, valueMethodName="localeName")
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
      
        // 別名の取得
        public String localeName() {
            return this.localeName;
        }
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsArrayConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

配列またはCollection型（List, Set）の変換規則の設定を行います。

配列またはCollectoinの要素のクラス型は、String型、プリミティブ型またはそのラッパークラスをとることができます。

* 属性separatorで区切り文字を指定します。
* 属性ignoreEmptyItemで、区切った項目の値が空文字の場合、無視するか指定します。
    
    * 例えば、区切り文字","のとき、セルの値が"a,,b"の場合、trueを設定すると\["a", "b"\]として読み込みます。
    * 書き込み時も同様に、値が空またはnullの項目を無視します。


.. sourcecode:: java
    
    public class UnitUser {
      
        @XlsArrayConverter(separator=",", ignoreEmptyItem=true)
        private String[] arrays;
        
        @XlsArrayConverter(separator=";")
        private List<Integer> list;
        
    }



読み込む際に各要素の値をトリミングしたい場合は、アノテーション ``@XlsConverter(trim=true)`` を付与します。

.. sourcecode:: java
    
    public class UnitUser {
        
        // 要素のトリム処理を指定する
        @XlsConverter(trim=true)
        @XlsArrayConverter(separator=";")
        private Set<Integer> set;
        
    }


基本的に、Genericsの型パラメータから要素のクラス型を自動的に判断しますが、属性itemClassで直接指定することもできます。

.. sourcecode:: java
    
    public class UnitUser {
        // 要素のクラス型を指定する
        @XlsArrayConverter(itemClass=Integer.class)
        private List list;
        
    }



