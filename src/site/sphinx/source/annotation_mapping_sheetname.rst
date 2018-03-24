

.. _annotationXlsSheetName:

--------------------------------
``@XlsSheetName``
--------------------------------

シート名をString型のプロパティにマッピングします。

.. sourcecode:: java
    :linenos:
    :caption: 基本的な使い方
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsSheetName
        private String sheetName;
    }



.. note:: 
    
    
    書き込み時に、アノテーション :ref:`@XlsSheet(regex="\<シート名\>") <annotationXlsSheet>` にて、
    シート名を正規表現で指定している場合は、 ``@XlsSheetName`` を付与しているフィールドで書き込むシートを決定します。
    
    そのため書き込む前に、シート名を指定する必要があります。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
メソッドにアノテーションを付与する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーションをメソッドに付与する場合、書き込み時はgetterメソッドメソッドの付与が必要になります。

さらに、アノテーションは付与しなくてもよいですが、setterメソッドの定義が必要になります。

そのため、 ``@XlsSheetName`` を指定する際にはフィールドに付与することをお薦めします。

.. sourcecode:: java
    :linenos:
    :caption: メソッドにアノテーションを付与する場合（読み込み時）
    
    // 読み込み時は、setterメソッドに付与する。
    @XlsSheet(name="Users")
    public class SheetObject {
        
        private String sheetName;
        
        // 読み込み時は、setterメソッドにアノテーションの付与が必要。
        @XlsSheetName
        public void setSheetName(String sheetName) {
            return sheetName;
        }
        
    }



.. sourcecode:: java
    :linenos:
    :caption: メソッドにアノテーションを付与する場合（書き込み時）
    
    // 書き込み時は、getterメソッドに付与し、かつsetterメソッドの定義が必要。
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        private String sheetName;
        
        // 書き込み時は、getterメソッドにアノテーションの付与が必要。
        @XlsSheetName
        public String getSheetName() {
            return sheetName;
        }
        
        // アノテーションの付与は必要ないが、定義が必要。
        public void setSheetName(String sheetName) {
            return sheetName;
        }
        
    }


