
.. _annotationXlsArrayConverter:

---------------------------------------
``@XlsArrayConverter``
---------------------------------------

配列またはCollection型（List, Set）の変換規則の設定を行います。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
基本的な使い方
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Collection型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。

* ``java.util.List`` の場合、``java.util.ArrayList`` がインスタンスのクラスとなります。
* ``java.util.Set`` の場合、``java.util.LinkedHashSet`` がインスタンスのクラスとなります。


配列、またはCollection型の要素で指定可能なクラス型は、次の通りです。
任意のクラス型に対応する場合は、属性 ``elementConverter`` で変換処理クラスを指定してください。

* String型
* プリミティブ型「boolean/char/byte/short/int/long/float/double」と、そのラッパークラス。
* ``java.math.BigDecimal`` / ``java.math.BigInteger`` 


文字列のセルに対して、任意の区切り文字を指定し、配列やListに対してマッピングします。

* 属性 ``separator`` で区切り文字を指定します。

  * 区切り文字の初期値は、半角カンマ(,)です。
  
* トリム用アノテーション ``@XlsTrim`` を付与し、トリム処理を有効にしている設定の場合、区切った項目にもトリム処理が適用されます。 `[ver0.5+]` 
  
* 属性 ``ignoreEmptyElement`` の値をtrueに設定していると、トリム処理によって項目が空文字となった場合、その項目は無視されます。


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 区切り文字の指定
        @XlsColumn(columnName="リスト")
        @XlsArrayConverter(separator="\n")
        private List<String> list;
        
        // 要素のトリム処理を指定する
        @XlsColumn(columnName="配列")
        @XlsTrim    // 区切った配列の要素にもトリムが適用されます。
        @XlsArrayConverter(separator=",")
        private int[] array;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
空の要素を無視する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``ignoreEmptyElement`` で、区切った項目の値が空文字の場合、無視するか指定します。
    
例えば、区切り文字","のとき、セルの値が ``"a,,b"`` の場合、trueを設定すると ``["a", "b"]`` として読み込みます。

書き込み時も同様に、値が空またはnullの項目を無視します。


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 空の要素を無視する場合
        @XlsColumn(columnName="集合")
        @XlsArrayConverter(ignoreEmptyElement=true)
        private Set<Integer> set;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
要素の値を変換するクラスを指定する
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``elementConverter`` で要素の値を変換するクラスを指定することができます。 `[ver1.1+]`

変換するクラスは、インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.ElementConverter`` を実装している必要があります。
標準では、``com.gh.mygreen.xlsmapper.cellconvert.DefaultElementConverter`` が使用され、基本的な型のみサポートしています。

インスタンスは、システム設定「beanFactory」経由で作成されるため、:doc:`SpringFrameworkのコンテナからインスタンスを取得 <spring>` することもできます。

.. sourcecode:: java
    :linenos:
    
    // 変換用クラス
    public class CustomElementConverter implements ElementConverter<User> {
        
        @Override
        public User convertToObject(final String str, final Class<User> targetClass) throws ConversionException {
            //TODO: 文字列 => オブジェクトに変換する処理
        }
        
        @Override
        public String convertToString(final User value) {
            //TODO: オブジェクト => 文字列に変換する処理
        }
        
    }
    
    // レコード用クラス
    public class SampleRecord {
        
        // 任意のクラス型の要素の値を変換するElementConverterを指定します。
        @XlsColumn(columnName="リスト")
        @XlsArrayConverter(elementConverter=CustomElementConverter.class)
        private List<User> list;
        
    }


