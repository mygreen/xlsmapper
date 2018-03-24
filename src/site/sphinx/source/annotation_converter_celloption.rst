
.. _annotationXlsCellOption:

----------------------------------
``@XlsCellOption``
----------------------------------

書き込み時のセルの配置位置やインデントなどの書式を指定します。

書き込み時は、テンプレート側のシートでセルの位置などを指定しておけば、それらの設定が引き継がれます。

ただし、アノテーションを付与することで、テンプレート側のシートと異なる設定に変更することもできます。


* 属性 ``wrapText`` の値がtrueの場合、強制的にセルの内の文字表示の設定「折り返して全体を表示する」が有効になります。
   
  * falseの場合、テンプレートとなるセルの設定を引き継ぎます。
   
* 属性 ``shrinkToFit`` の値がtrueの場合、強制的にセル内の文字表示の設定「縮小して全体を表示する」が有効になります。
    
  * falseの場合、テンプレートとなるセルの設定を引き継ぎます。

* 属性 ``indent`` でインデントを指定することができます。

  * インデントが指定可能な横位置(左詰め/右詰め/均等割り付け)のときのみ有効になります。
  * -1 以下の時は、現在の設定を引き継ぎます。

* 属性 ``horizontalAlign`` でセルの横位置を指定することができます。

* 属性 ``verticalAlign`` でセルの縦位置を指定することができます。


.. sourcecode:: java
    :linenos:
    :caption: セルの配置の指定
    
    public class SampleRecord {
    
        // 「縮小して全体を表示する」が有効になる。
        @XlsColumn(columnName="ID")
        @XlsCellOption(wrapText=true)
        private int id;
        
        //「折り返して全体を表示する」が有効になる。
        @XlsColumn(columnName="名前")
        @XlsCellOption(shrinkToFit=true)
        private String name;
        
        // セルの横位置、縦位置の指定
        @XlsColumn(columnName="備考")
        @XlsCellOption(horizontalAlign=HorizontalAlign.Center, verticalAlign=VerticalAlign.Top)
        private String comment;
    }


.. note::
    
    Excelの仕様上、設定「折り返して全体を表示する」と「縮小して全体を表示する」は、二者択一であるため、両方の設定を有効にすることはできません。
    もし、属性wrapTextとshrinkToFitの値をtrueに設定した場合、shrinkToFitの設定が優先され、「縮小して全体を表示する」が有効になります。




