
.. _annotationXlsDefaultValue:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsDefaultValue``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

読み込み時、または書き込み時に値がnullの時に、代わりにとなる値を指定します。

* 属性 ``value`` で読み込み時/書き込み時のデフォルト値を指定します。

* 属性 ``cases`` で、読み込み時か書き込み時の処理ケースに限定することができます。

  * 何も指定しない場合、読み込み時と書き込み時の両方に適用されます。

* 日付などの書式がある場合、専用のアノテーションで指定した書式 ``@XlsDateTimeConverter(javaPattern="<任意の書式>")`` を元に、文字列をそのオブジェクトに変換し処理します。
  
* デフォルト値を指定しないでプリミティブ型に対して読み込む場合、その型の初期値が設定されます。
    
  * int型は0、double型は0.0、boolean型はfalse。char型の場合は、 '\\u0000' 。
  * プリミティブのラッパークラスや参照型の場合は、nullが設定されます。
    
* 指定したデフォルト値がマッピング先の型として不正な場合は、通常の型変換エラーと同様に、例外 ``com.gh.mygreen.xlsmapper.cellconverter.TypeBindException`` がスローされます。`[ver0.5]`
    
* char型にマッピングする場合、デフォルト値が2文字以上でも、先頭の一文字がマッピングされます。


.. sourcecode:: java
    :linenos:
    :caption: 初期値の指定
    
    public class SampleRecord {
    
        @XlsColumn(columnName="ID")
        @XlsDefaultValue("-1")
        private int id;
        
        @XlsColumn(columnName="更新日時")
        @XlsDefaultValue("2010/01/01") // 属性javaPatternで指定した書式に沿った値を指定します。
        @XlsDateTimeConverter(javaPattern="yyyy/MM/dd")
        private Date updateTime;
        
        @XlsColumn(columnName="備考")
        @XlsDefaultValue(value="-", cases=ProcessCase.Write) // 処理ケースを指定します。
        private String comment;
        
    }


