======================================
表・セルのマッピング方法
======================================

表やセルをマッピングするためには様々なアノテーションが用意されています。

様々な表に対応するため、マッピング方法を指定するためのアノテーションと補助的に詳細なオプションを指定するためのアノテーションがあります。

もし、独自の構造の表をマッピングするならば、「 :doc:`独自の表・セルのマッピング方法 <fieldprocessor>` 」を参照してください。


.. list-table:: 基本的なマッピング用のアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
     
   * - :ref:`@XlsSheet <annotationXlsSheet>`
     - シートをマッピングするためにクラスに付与し使用します。

   * - :ref:`@XlsSheetName <annotationXlsSheetName>`
     - シート名をマッピングします。

   * - :ref:`@XlsCell <annotationXlsCell>`
     - セルの座標を直接指定してマッピングします。

   * - :ref:`@XlsLabelledCell <annotationXlsLabelledCell>`
     - 見出し付きのセルをマッピングします。

   * - :ref:`@XlsArrayCells <annotationXlsArrayCells>`
     - 連続し隣接するセルを配列またはリストにマッピングします。

   * - :ref:`@XlsLabelledArrayCells <annotationXlsLabelledArrayCells>`
     - 見出し付きの連続し隣接するセルを配列またはリストにマッピングします。

   * - :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>`
     - 水平方向に連続する行のレコードを持つ表をマッピングします。

   * - :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>`
     - 垂直方向に連続する列のレコードを持つ表をマッピングします。

   * - :ref:`@XlsColumn <annotationXlsColumn>`
     - レコードのカラムをマッピングします。

   * - :ref:`@XlsMapColumns <annotationXlsMapColumns>`
     - レコードの可変長のカラムをマッピングします。

   * - :ref:`@XlsArrayColumns <annotationXlsArrayColumns>`
     - レコードの隣接するカラムをマッピングします。

   * - :ref:`@XlsNestedRecords <annotationXlsNestedRecords>`
     - 入れ子構造のレコードをマッピングします。

   * - :ref:`@XlsIterateTables <annotationXlsIterateTables>`
     - シート内で繰り返される同一構造の表をマッピングします。

   * - :ref:`@XlsComment <annotationXlsComment>`
     - セルの座標を直接指定して、セルのコメントをマッピングします。

   * - :ref:`@XlsLablledComment <annotationXlsLabelledComment>`
     - 指定したラベルセルのコメント情報をマッピングします。


.. list-table:: 補助的なマッピング用のアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
     
   * - :ref:`@XlsOrder <annotationXlsOrder>`
     - フィールドの処理順序を指定するために使用します。

   * - :ref:`@XlsIgnorable <annotationXlsIgnorable>`
     - レコードを読み飛ばすことが可能か判定するためのメソッドに使用します。

   * - :ref:`@XlsArrayOption <annotationXlsArrayOption>`
     - 連続し隣接するセルを配列またはリストを書き込む際の制御を行います。

   * - :ref:`@XlsRecordOption <annotationXlsRecordOption>`
     - レコードを書き込む際の制御を行います。

   * - :ref:`@XlsRecordFinder <annotationXlsRecordFinder>`
     - レコードの開始位置をプログラマティックに指定します。

   * - :ref:`@XlsCommentOption <annotationXlsCommentOption>`
     - 書き込み時のセルのコメントのサイズなどの制御を指定します。

.. 以降は、埋め込んで作成する
.. include::  ./annotation_mapping_sheet.rst
.. include::  ./annotation_mapping_sheetname.rst
.. include::  ./annotation_mapping_cell.rst
.. include::  ./annotation_mapping_labelledcell.rst
.. include::  ./annotation_mapping_arraycells.rst
.. include::  ./annotation_mapping_labelledarraycells.rst
.. include::  ./annotation_mapping_horizontalrecords.rst
.. include::  ./annotation_mapping_verticalrecords.rst
.. include::  ./annotation_mapping_column.rst
.. include::  ./annotation_mapping_mapcolumns.rst
.. include::  ./annotation_mapping_arraycolumns.rst
.. include::  ./annotation_mapping_nestedrecords.rst
.. include::  ./annotation_mapping_iteratetables.rst
.. include::  ./annotation_mapping_comment.rst
.. include::  ./annotation_mapping_labelledcomment.rst
..
.. include::  ./annotation_mapping_order.rst
.. include::  ./annotation_mapping_ignorable.rst
.. include::  ./annotation_mapping_arrayoption.rst
.. include::  ./annotation_mapping_recordoption.rst
.. include::  ./annotation_mapping_recordfinder.rst
.. include::  ./annotation_mapping_commentoption.rst

