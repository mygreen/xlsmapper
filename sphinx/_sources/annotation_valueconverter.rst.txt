--------------------------------------
セルの値の変換方法
--------------------------------------

セルの値を加工するためのアノテーションです。

例えば、トリミングや初期値を指定することができます。

.. list-table:: 値を変換するアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
   
   * - :ref:`@XlsTrim <annotationXlsTrim>`
     - トリミングを行いたい場合に指定します。

   * - :ref:`@XlsDefaultValue <annotationXlsDefaultValue>`
     - 初期値を設定したい場合に指定します。


.. note::

   値の変換用のアノテーションは、セルをマッピングするアノテーション :ref:`@XlsCell <annotationXlsCell>` 、 :ref:`@XlsLabelledCell <annotationXlsLabelledCell>`、 :ref:`@XlsArrayCells <annotationXlsArrayCells>`、 :ref:`@XlsLabelledArrayCells <annotationXlsLabelledArrayCells>`、 :ref:`@XlsColumn <annotationXlsColumn>`、 :ref:`@XlsMapColumns <annotationXlsMapColumns>`、 :ref:`@XlsArrayColumns <annotationXlsArrayColumns>`  を付与しているプロパティに対して有効になります。


.. 以降は、埋め込んで作成する
.. include::  ./annotation_valueconverter_trim.rst
.. include::  ./annotation_valueconverter_defaultvalue.rst

