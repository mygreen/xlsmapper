
.. _annotationXlsCell:

---------------------------------
``@XlsCell``
---------------------------------

セルの列と行を指定してBeanのプロパティにマッピングします。

フィールドまたはメソッドに対して付与します。

* 属性 ``column`` 、 ``row`` で、インデックスを指定します。
   
  * columnは列番号で、0から始まります。
  * rowは行番号で、0から始まります。
    
* 属性 ``address`` で、 'B3' のようにシートのアドレス形式で指定もできます。
   
  * 属性addressを指定する場合は、column, rowは指定しないでください。
  * 属性addressの両方を指定した場合、addressの値が優先されます。

.. figure:: ./_static/Cell.png
   :align: center
   
   Cell



.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // インデックス形式で指定する場合
        @XlsCell(column=0, row=0)
        private String title;
        
        // アドレス形式で指定する場合
        @XlsCell(address="B3")
        private String name;
        
    }


