========================================================
セルの書式の指定方法
========================================================

セルの書式を指定してマッピングする方法を説明します。

* 読み込み時は、Excelの型とJavaの型が一致する場合、そのままマッピングします。

  * 型が一致しない場合、Excelのセルの値を文字列として取得し、その値をパースしてJavaの型に変換します。
  * 文字列をパースする書式などの指定は、型ごとの専用のアノテーションを付与します。
  * 各型の変換用アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。

* 書き込み時は、Javaの型に合ったExcelのセルの型で処理します。
  
  * テンプレートファイルのセルの型とJavaの型が一致すれば、そのまま書き込みます。
    一致しない場合は、付与されたアノテーションに従って書式などを設定します。
  * 各型の変換用アノテーションを付与しない場合は、アノテーションの初期値の設定を元に変換されます。

* 本ライブラリで対応していないクラスタイプの場合は、変換処理を実装すれば対応できます。

  * 詳細は、 :doc:`独自のクラスタイプの対応方法 <annotation_converter_custom>` を参照してください。


.. list-table:: ExcelとJavaのマッピング可能な型
   :widths: 30 70
   :header-rows: 1
   
   * - Excelの型
     - Javaの型
     
   * - | ブール
       | （TRUE/FALSE）
     - | boolean/Boolean
     
   * - 文字列
     - | String
       | 全ての型（ブール/数値/日時/Collection・配列/URL）
   
   * - | 数値
       | （数値/通貨/会計
       |  /パーセンテージ/分数/指数）
     - | byte/short/int/long/float/double/これらのラッパークラス
       | /java.math.BigDecimal/java.math.BigInteger
     
   * - | 日時
       | （日付/時刻）
     - | java.util.Date/java.util.Calendar
       | /java.sql.Date/Time/Timestamp
       | /java.time.LocalDateTime/LocalDate/LocalTime
     
   * - 文字列
     - | 配列
       | /Collection(java.util.List/java.util.Set)
     
   * - | ハイパーリンクを設定したセル
     - | java.net.URI
       | /com.gh.mygreen.xlsmapper.cellconvert.CellLink(本ライブラリの独自の型)



.. list-table:: 書式を指定するアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
   
   * - :ref:`@XlsBooleanConverter <annotationXlsBooleanConverter>`
     - ブール型に対する書式を指定します。

   * - :ref:`@XlsNumberConverter <annotationXlsNumberConverter>`
     - 数値型に対する書式を指定します。

   * - :ref:`@XlsDateTimeConverter <annotationXlsDateTimeConverter>`
     - 日時型に対する書式を指定します。

   * - :ref:`@XlsEnumConverter <annotationXlsEnumConverter>`
     - 列挙型に対する書式を指定します。

   * - :ref:`@XlsArrayConverter <annotationXlsArrayConverter>`
     - Collection型や配列型に対する書式を指定します。

   * - :ref:`@XlsCellOption <annotationXlsCellOption>`
     - 書き込み時のセルの配置位置やインデントなどの書式を指定します。


.. note::

   書式指定用のアノテーションは、セルをマッピングするアノテーション :ref:`@XlsCell <annotationXlsCell>` 、 :ref:`@XlsLabelledCell <annotationXlsLabelledCell>`、 :ref:`@XlsArrayCells <annotationXlsArrayCells>`、 :ref:`@XlsLabelledArrayCells <annotationXlsLabelledArrayCells>`、 :ref:`@XlsColumn <annotationXlsColumn>`、 :ref:`@XlsMapColumns <annotationXlsMapColumns>`、 :ref:`@XlsArrayColumns <annotationXlsArrayColumns>`  を付与しているプロパティに対して有効になります。


.. 以降は、埋め込んで作成する
.. include::  ./annotation_converter_boolean.rst
.. include::  ./annotation_converter_number.rst
.. include::  ./annotation_converter_datetime.rst
.. include::  ./annotation_converter_enum.rst
.. include::  ./annotation_converter_array.rst
.. include::  ./annotation_converter_celloption.rst
.. include::  ./annotation_converter_custom.rst


