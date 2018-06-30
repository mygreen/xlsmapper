
.. _annotationXlsEnumConverter:

----------------------------------------------
``@XlsEnumConverter``
----------------------------------------------


列挙型の変換規則の設定を行います。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
基本的な使い方
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

セルの値と列挙型の要素の値をマッピングさせます。

要素の値とは、 ``Enum#name()`` の値です。

* 属性 ``ignoreCase`` の値をtrueにすると、読み込み時に大文字/小文字の区別なく変換します。

.. sourcecode:: java
    :linenos:
    :caption: 列挙型の変換規則の指定方法
    
    public class SampleRecord {
        
        // 列挙型のマッピング
        @XlsColumn(columnName="権限")
        @XlsEnumConverter(ignoreCase=true)
        private RoleType role;
        
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal, Admin;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
別名でマッピングする場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

別名でマッピングする場合、属性 ``aliasMethod`` で列挙型の要素の別名を取得するメソッド名を指定します。

.. sourcecode:: java
    :linenos:
    :caption: 列挙型を別名でマッピングする方法
        
    public class SampleRecord {
        
        // 別名による列挙型のマッピング
        @XlsColumn(columnName="権限")
        @XlsEnumConverter(aliasMethod="localeName")
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
      
        // 別名の取得用メソッド
        public String localeName() {
            return this.localeName;
        }
        
    }



