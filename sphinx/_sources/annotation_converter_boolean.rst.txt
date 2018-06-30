
.. _annotationXlsBooleanConverter:

--------------------------------------
``@XlsBooleanConverter``
--------------------------------------

Excelのセルの種類が「ブール型」以外の場合に、Javaの「boolean/Boolean」にマッピング規則を定義します。


単純に「true、false」以外に、「○、×」とのマッピングも可能となります。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
読み込み時の値の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``loadForTrue`` 、 ``loadForFalse`` で読み込み時のtrueまたはfalseと判断するの候補の値を指定します。
   
* 属性loadForTrueとloadForFalseの値に重複がある場合、loadForTrueの定義が優先されまます。
  
* 属性laodForTrueを指定しない場合、デフォルトで「"true", "1", "yes", "on", "y", "t"」が設定されます。
  
* 属性loadForFalseを指定しない場合、デフォルトで「"false", "0", "no", "off", "f", "n"」が設定されます。
    
* 属性 ``ignoreCase`` の値をtrueにすると、読み込み時に大文字、小文字の区別なく候補の値と比較します。


.. sourcecode:: java
    :linenos:
    
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


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時の値の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``saveAsTrue`` と ``saveAsFalse`` で書き込み時のtrueまたはfalse値に該当する文字を指定します。
    
* 属性saveAsTrueを指定しない場合は、デフォルトで"true"が設定され、セルのタイプもブール型になります。
  
* 属性saveAsFalseを指定しない場合は、デフォルトで"false"が設定され、セルのタイプもブール型になります。
    
* 読み込みと書き込みの両方を行う場合、属性loadForTrueとloadForFalseの値に属性saveAsTrueとsaveAsFalseの値を含める必要があります。
    

.. sourcecode:: java
    :linenos:
    
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


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
変換に失敗した際の処理
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 
読み込み時にtrueまたはfalseに変換できない場合、例外TypeBindExceptionが発生します。

* 属性 ``failToFalse`` をtrueに設定することで、変換できない場合に強制的に値をfalseとして読み込み、例外を発生しなくできます。

.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 読み込み時に変換できない場合に、強制的に値をfalseとして読み込みます。
        @XlsColumn(columnName="ステータス")
        @XlsBooleanConverter(
                loadForTrue={"○", "有効", "レ"},
                loadForFalse={"×", "無効", "-", ""},
                failToFalse=true)
        private boolean availaled;
        
    }



