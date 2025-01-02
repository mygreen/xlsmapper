--------------------------------------
セルの値の変換方法
--------------------------------------

セルの値を加工するためのアノテーションです。

例えば、トリミングや初期値を指定できます。

.. list-table:: 値を変換するアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
   
   * - :doc:`@XlsTrim <annotation_valueconverter_trim>`
     - トリミングを行いたい場合に指定します。

   * - :doc:`@XlsDefaultValue <annotation_valueconverter_defaultvalue>`
     - 初期値を設定したい場合に指定します。


.. note::

   値の変換用のアノテーションは、セルをマッピングするアノテーション :doc:`@XlsCell <annotation_mapping_cell>` 、 :doc:`@XlsLabelledCell <annotation_mapping_labelledcell>`、 :doc:`@XlsArrayCells <annotation_mapping_arraycells>`、 :doc:`@XlsLabelledArrayCells <annotation_mapping_labelledarraycells>`、 :doc:`@XlsColumn <annotation_mapping_column>`、 :doc:`@XlsMapColumns <annotation_mapping_mapcolumns>`、 :doc:`@XlsArrayColumns <annotation_mapping_arraycolumns>`  を付与しているプロパティに対して有効になります。


.. 以降は、埋め込んで作成する
.. include::  ./annotation_valueconverter_trim.rst
.. include::  ./annotation_valueconverter_defaultvalue.rst

