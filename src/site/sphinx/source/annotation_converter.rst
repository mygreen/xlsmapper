--------------------------------------
型変換用のアノテーション
--------------------------------------

* 読み込み時は、Excelの型とJavaの型が一致する場合、そのままマッピングします。

  * 型が一致しない場合、Excelのセルの値を文字列として取得し、その値をパースしてJavaの型に変換します。
  * 文字列をパースする書式などの指定は、型ごとの専用のアノテーションを付与します。
  * 各型の変換用アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。

* 書き込み時は、Javaの型に合ったExcelのセルの型で処理します。
  
  * テンプレートファイルのセルの型とJavaの型が一致すれば、そのまま書き込みます。
    一致しない場合は、付与されたアノテーションに従って書式などを設定します。
  * 各型の変換用アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。


.. list-table:: ExcelとJavaのマッピング可能な型
   :widths: 30 30 40
   :header-rows: 1
   
   * - Excelの型
     - Javaの型
     - 対応するアノテーション
     
   * - | -(共通)
     - | -(共通)
     - :ref:`@XlsConverter <annotationXlsConverter>`
     
   * - | ブール型
       | （TRUE/FALSE）
     - | boolean/Boolean
     - :ref:`@XlsBooleanConverter <annotationXlsBooleanConverter>`
     
   * - 文字列
     - | String
       | char/Character(先頭の1文字のみがマッピング対象)
     - 
   
   * - | 数値
       | （数値/通貨/会計/パーセンテージ/分数/指数）
     - | byte/short/int/long/float/double/これらのラッパークラス
       | /java.math.BigDecimal/java.math.BigInteger
     - :ref:`@XlsNumberConverter <annotationXlsNumberConverter>`
     
   * - | 日時
       | （日付/時刻）
     - | java.util.Date/java.util.Calendar
       | /java.sql.Date/Time/Timestamp
     - :ref:`@XlsDateConverter <annotationXlsDateConverter>`
   
   * - 文字列
     - | 任意の列挙型
     - :ref:`@XlsEnumConverter <annotationXlsEnumConverter>`
     
   * - 文字列
     - | 配列
       | /Collection(java.util.List/java.util.Set)
     - :ref:`@XlsArrayConverter <annotationXlsArrayConverter>`
     
   * - | ハイパーリンクを設定したセル
     - | java.net.URI
       | /com.gh.mygreen.xlsmapper.cellconvert.CellLink(本ライブラリの独自の型)
     - 

.. 以降は、埋め込んで作成する
.. include::  ./annotation_converter_converter.rst
.. include::  ./annotation_converter_boolean.rst
.. include::  ./annotation_converter_number.rst
.. include::  ./annotation_converter_date.rst
.. include::  ./annotation_converter_enum.rst
.. include::  ./annotation_converter_array.rst


