======================================
表・セルのマッピング方法
======================================

表やセルをマッピングするためには様々なアノテーションが用意されています。

様々な表に対応するため、マッピング方法を指定するためのアノテーションと補助的に詳細なオプションを指定するためのアノテーションがあります。

もし、独自の構造の表をマッピングするならば、「:doc:`独自の表・セルのマッピング方法 <fieldprocessor>`」を参照してください。


.. list-table:: 基本的なマッピング用のアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
     
   * - :doc:`@XlsSheet <annotation_mapping_sheet>`
     - シートをマッピングするためにクラスに付与し使用します。

   * - :doc:`@XlsSheetName <annotation_mapping_sheetname>`
     - シート名をマッピングします。

   * - :doc:`@XlsCell <annotation_mapping_cell>`
     - セルの座標を直接指定してマッピングします。

   * - :doc:`@XlsLabelledCell <annotation_mapping_labelledcell>`
     - 見出し付きのセルをマッピングします。

   * - :doc:`@XlsArrayCells <annotation_mapping_arraycells>`
     - 連続し隣接するセルを配列またはリストにマッピングします。

   * - :doc:`@XlsLabelledArrayCells <annotation_mapping_labelledarraycells>`
     - 見出し付きの連続し隣接するセルを配列またはリストにマッピングします。

   * - :doc:`@XlsHorizontalRecords <annotation_mapping_horizontalrecords>`
     - 水平方向に連続する行のレコードを持つ表をマッピングします。

   * - :doc:`@XlsVerticalRecords <annotation_mapping_verticalrecords>`
     - 垂直方向に連続する列のレコードを持つ表をマッピングします。

   * - :doc:`@XlsColumn <annotation_mapping_column>`
     - レコードのカラムをマッピングします。

   * - :doc:`@XlsMapColumns <annotation_mapping_mapcolumns>`
     - レコードの可変長のカラムをマッピングします。

   * - :doc:`@XlsArrayColumns <annotation_mapping_arraycolumns>`
     - レコードの隣接するカラムをマッピングします。

   * - :doc:`@XlsNestedRecords <annotation_mapping_nestedrecords>`
     - 入れ子構造のレコードをマッピングします。

   * - :doc:`@XlsIterateTables <annotation_mapping_iteratetables>`
     - シート内で繰り返される同一構造の表をマッピングします。

   * - :doc:`@XlsComment <annotation_mapping_comment>`
     - セルの座標を直接指定して、セルのコメントをマッピングします。

   * - :doc:`@XlsLablledComment <annotation_mapping_labelledcomment>`
     - 指定したラベルセルのコメント情報をマッピングします。


.. list-table:: 補助的なマッピング用のアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
     
   * - :doc:`@XlsOrder <annotation_mapping_order>`
     - フィールドの処理順序を指定するために使用します。

   * - :doc:`@XlsIgnorable <annotation_mapping_ignorable>`
     - レコードを読み飛ばすことが可能か判定するためのメソッドに使用します。

   * - :doc:`@XlsArrayOption <annotation_mapping_arrayoption>`
     - 連続し隣接するセルを配列またはリストを書き込む際の制御を行います。

   * - :doc:`@XlsRecordOption <annotation_mapping_recordoption>`
     - レコードを書き込む際の制御を行います。

   * - :doc:`@XlsRecordFinder <annotation_mapping_recordfinder>`
     - レコードの開始位置をプログラマティックに指定します。

   * - :doc:`@XlsCommentOption <annotation_mapping_commentoption>`
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

