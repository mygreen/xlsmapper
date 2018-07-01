======================================
マイグレーション
======================================

バージョン間で互換正がなくなった設定や機能などを説明します。
バージョンアップする際の参考にしてください。

:doc:`リリースノート <release>` も参考にしてください。

--------------------------------------------------------
To ver.2.0
--------------------------------------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
前提環境の変更
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* Java7を使用している場合、Java8に変更します。
* POI-3.16以下を使用してる場合、POI-3.17に変更します。

  * POI-3.17から、セルのタイプ/罫線などの定数値に対応する列挙型が追加されてました。
    今後、定数値は削除され列挙型が推奨されます。
    ただし、開発中(2018年6月時点)の次バージョンのPOI-4.0でメソッド名が変わるため注意が必要です。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
クラス名などの変更
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* 日時型のマッピング時の書式を指定するアノテーション ``@XlsDateConverter`` の名称を ``@XlsDateTimeConverter`` に変更します。
* 列挙型のマッピング時の書式を指定するアノテーション ``@XlsEnumConverter`` において、属性 ``valueMethodName`` の名称を ``aliasName`` に変更します。
* 配列／リスト型にマッピング時の書式を指定するアノテーション ``@XlsArrayConverter`` において、属性の名称を以下に変更します。

  * 属性の名称を ``itemConverterClass`` → ``elementConverterClass`` に変更します。
  * 属性の名称を ``ignoreEmptyItem`` → ``ignoreEmptyElement`` に変更します。
  * 属性の名称を ``itemClass`` → ``elementClass`` に変更します。
  * 要素をパース/フォーマットするクラス ``ItemConverter`` の名称を ``ElementConverter`` に変更します。
    デフォルト実装クラスの名称も ``DefaultItemConverter`` → ``DefaultElementConverter`` に変更します。

* マッピングの順番を指定するアノテーション ``@XlsHint(order=1)`` の名称を :ref:`@XlsOrder <annotationXlsOrder>` に変更します。
* レコードをスキップするかどうか判定用のメソッドを指定するアノテーション ``@XlsIsEmpty`` の名称を :ref:`@XlsIgnorable <annotationXlsIgnorable>` に変更します。
* リスナーを指定するアノテーション ``@XlsListener`` の属性 ``listenerClass`` の名称を ``value`` に変更するか、省略します。
* :doc:`システム設定のプロパティ<configuration>` を指定するクラス ``XlsConfig`` の名称を ``Configuration`` に変更します。
* 複数のシートをマッピングした結果を格納するクラス ``SheetBingingErrorsContainer`` の名称を ``MultipleSheetBindingErrors`` に変更します。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
アノテーションの定義の変更
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* トリムをするアノテーションを ``@XlsConverter(trim=true)`` を、 :ref:`@XlsTrim <annotationXlsTrim>` に変更します。
* 初期値を指摘するアノテーション ``@XlsConverter(defaultValue="初期値")`` を、 :ref:`@XlsDefaultValue("初期値") <annotationXlsDefaultValue>` に変更します。
* セルの書き込み時の制御設定を行うアノテーション ``@XlsConverter(wrapText=true, shrinkToFit=false)`` を、 `@XlsCellOption(wrapText=true, shrinkToFit=false) <annotationXlsCellOption>` に変更します。
* レコードの書き込み時のオプションを指定するアノテーション ``@XlsHorizontalRecords(overRecord=..., remainedRecord=...)`` を、 :ref:`@XlsRecordOption(overOperation=..., remainedOperation=...) <annotationXlsRecordOption>` に変更します。 
    
    * アノテーション ``@XlsVerticalRecords(overRecord=..., remainedRecord=...)`` も同様に変更します。

* レコードを除外する条件として、 ``@XlsHorizontalRecords(ignoreEmptyRecord=true)`` の属性を削除します。
  
  * 属性の値がfalseのときは、レコードのメソッドに付与しているアノテーション ``@XlsIgnorable(以前は、@XlsIsEmpty)`` を削除します。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
その他の変更
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* メッセージ定義のプロパティファイル ``SheetValidationMessages.properties`` をasciiコードの変換を止め、さらに、文字コードをUTF-8に変更します。
* XMLによるマッピングの指定方法を、システム設定のプロパティで設定するよう変更します。詳細は、:doc:`xmlmapping` を参照してください。


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

* :doc:`システムプロパティ <configuration>` ``skipTypeBindFailure`` の名称を ``continueTypeBindFailure`` に変更します。


