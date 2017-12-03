======================================
マイグレーション
======================================

バージョン間で互換正がなくなった設定や機能などを説明します。
バージョンアップする際の参考にしてください。

:doc:`リリースノート <release>` も参考にしてください。

--------------------------------------------------------
To ver.1.1
--------------------------------------------------------

* 型変換用のアノテーションのパッケージのimportを、 ``～.xlsmapper.annotation.converter`` から ``～.xlsmapper.annotation`` に変更します。

  * 該当するアノテーションは、``@XlsConverter/@XlsBooleanConverer/@XlsNumberConverter/@XlsDateConverter/XlsEnumConverer/@XlsArrayConverter`` です。

* アノテーション :ref:`@XlsHorizonalRecords <annotationXlsHorizontalRecords>` と :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` の属性 ``skipEmptyRecord`` を ``ignoreEmptyErecord`` に変更します。

* アノテーション :ref:`@XlsConverter <annotationXlsConverter>` の属性 ``forceWrapText`` を ``wrapText`` に、属性 ``forceShrinkToFit`` を ``shrinkToFit`` 変更します。

* アノテーション :ref:`@XlsNumberConverter <annotationXlsNumberConverter>` 、 :ref:`@XlsDateConverter <annotationXlsDateTimeConverter>` の属性 ``pattern`` を ``javaPattern`` に変更します。さらに、属性 ``excelPattern`` で、書き込み時のExcelの書式を指定します。


* XML読み込み用のクラス ``XmlLoader`` を ``XmlIO`` に変更します。

* 例外クラス ``XmlLoadException`` の名称を ``XmlOperateException`` に変更します。


--------------------------------------------------------
To ver.1.2
--------------------------------------------------------

* :doc:`システムプロパティ <otheruse_config>` ``skipTypeBindFailure`` の名称を ``continueTypeBindFailure`` に変更します。


