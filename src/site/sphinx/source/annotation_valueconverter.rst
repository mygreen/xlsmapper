--------------------------------------
値の変換用のアノテーション
--------------------------------------

セルの値を加工するためのアノテーションです。

例えば、トリミングや初期値を指定することができます。

.. list-table:: 値を変換するアノテーション
   :widths: 30 70
   :header-rows: 1
   
   * - アノテーション
     - 概要
   
   * - :ref:`@XlsTrim <annotationXlsTrim>`
     - ブール型に対する書式を指定します。

   * - :ref:`@XlsDefaultValue <annotationXlsDefaultValue>`
     - 数値型に対する書式を指定します。


.. 以降は、埋め込んで作成する
.. include::  ./annotation_valueconverter_trim.rst
.. include::  ./annotation_valueconverter_defaultvalue.rst

