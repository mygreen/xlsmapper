
.. _annotationXlsConverter:

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

トリミングを行いたい場合、属性 ``trim`` の値をtrueに指定します。

* 属性trimの値をtrueにすると、読み込み時と書き込み時にトリムを行います。
   
* シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
  
  * ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、変換する前にトリム処理を行います。
  
* 値が空のセルをString型に読み込む場合、``trim=false`` のときはnull設定されますが、``trim=true`` のきは、空文字が設定されます。`[ver0.5+]` 


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


