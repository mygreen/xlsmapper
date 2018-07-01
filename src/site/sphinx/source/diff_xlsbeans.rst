======================================
XLSBeansとの違い
======================================

XlsMapperは、XLSBeans ver.1.1 のソースを元に開発を行っています。

そのため、仕様、特にアノテーションの使い方はXLSBeansと共通しています。
XLSBeansとの違いを以下に示します。

--------------------------------------------------------
改善機能
--------------------------------------------------------

* アノテーションの名称を ``@Xls～`` （例. ``@Column => @XlsColumn`` ） として、他のライブラリのアノテーションと区別がつきやすいようにしています。

* アノテーション ``@XlsCell`` などセルの座標を指定する際に、Excel形式 `A3` のように指定する :ref:`属性address <annotationXlsCell>` を追加し、指定しやすいようにしています。

* エラーメッセージの内容を詳細に表示し、設定間違いの対応をしやすくしています。

* Generics対応として、``@XlsMapColums、@XlsIterateTables`` にも対応しており、マッピング先のClassを指定する必要がありません。

* :doc:`シートの座標位置の取得 <sheetinfo_sheetposition>` が、``@XlsMapColumn`` を付与したフィールドにも対応しています。

  * フィールド ``Map<String, String> positions`` を定義しておけば、キーに見出し名が入ります。

* アノテーションを付与するフィールドの修飾子として、public以外のprivate/protectedにも対応しています。


--------------------------------------------------------
追加機能
--------------------------------------------------------

* :ref:`書き込み機能 <howtouseSheetSave>` があります。
  
  * アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、 :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` に書き込み用の属性を追加し、行が余ったときや足りないときの動作をカスタマイズできます。
                
* 読み込み時、書き込み時の :doc:`コールバック用メソッドを定義するためのアノテーション <annotation_lifecycle>` ``@XlsPreLoad/@XlsPostLoad/@XlsPreSave/@XlsPostLoad`` が使用できます。
  
* 型変換機能として、 ``@XlsConverter`` などの :doc:`型変換用アノテーション <annotation_converter>` を利用して各フィールドごとに細かく設定できます。

* 読み込んだシートの内容をチェックする :doc:`Validation機能 <validation>` があります。

  * 独自にValidatorを実装する方式の他、Bean Validation 1.0/1.1に対応しています。

* セルの座標位置のように、 :doc:`マッピング対象の見出し名を取得 <sheetinfo_caption>` するこができます。


--------------------------------------------------------
削除機能
--------------------------------------------------------

* コードをシンプルにするため、JExcelAPI対応を排除し、Apache POIのみに対応しています。
  
  * XlsMapperのプログラム中で利用する機能でJExcelAPIに優位であったユーザ定義などの書式対応は、外部ライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ を利用することでJExcelAPIとApache POIの差をなくしています。
  
* アノテーション ``@PostProcessor`` を削除しました。

  * 代わりに、``@XlsPreLoad/@XlsPostLoad/@XlsPreSave/@XlsPostLoad`` が使用できます。
 
* 独自実装のProcessorを、プロパティファイル ``xlsbeans.properties`` で設定できる機能を削除しました。

  * 代わりに、システム設定用の ``Configuration#getConverterRegistry()#registerProcessor(...)`` から登録できます。

