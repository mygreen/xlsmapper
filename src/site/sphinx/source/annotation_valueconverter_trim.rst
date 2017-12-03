
.. _annotationXlsTrim:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsTrim``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

トリミングを行いたい場合、アノテーション ``@XlsTrim`` を付与します。

* アノテーションを付与すると、読み込み時と書き込み時にトリムを行います。
   
* シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
  
  * ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、変換する前にトリム処理を行います。
  
* 値が空のセルをString型に読み込む場合、アノテーションを付与しないときはnull設定されますが、アノテーションを付与するときは空文字が設定されます。`[ver0.5+]` 


.. sourcecode:: java
    :linenos:
    :caption: トリムの指定
    
    public class SampleRecord {
    
        @XlsColumn(columnName="ID")
        @XlsTrim
        @XlsDefaultValue(" 123 ") // 初期値もトリム対象となる。
        private int id;
        
        @XlsColumn(columnName="名前")
        @XlsConverter             // 空のセルを読み込むと空文字が設定される。
        private String name;
        
    }




