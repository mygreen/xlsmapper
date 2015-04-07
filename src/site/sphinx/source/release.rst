======================================
リリースノート
======================================

--------------------------------------------------------
ver.0.4 - 2015-04-05
--------------------------------------------------------

下記の機能を追加または改善

* `#15 <https://github.com/mygreen/xlsmapper/issues/15>`_ : セルの値を取得する方法を別ライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ を利用するよう変更。

    * XlsConfigのプロパティ ``POICellFormatter`` のクラス名を ``CellFormatter`` に変更。
    * この対策により、`#19 <https://github.com/mygreen/xlsmapper/issues/19>`_ も改善される
    
* `#17 <https://github.com/mygreen/xlsmapper/issues/17>`_ : ハイパーリンクを書き込む処理を改善し、内部的に二重にリンクが設定される事象を修正。
* `#18 <https://github.com/mygreen/xlsmapper/issues/18>`_ : コメントを含むシートをテンプレートして出力し、それをExcelで開くと警告メッセージが表示される事象を修正。
    
    * これは、POI-3.11の不良であり、POI-3.10～POI-3.11のみで発生する。
    * この事象を回避するために、 XlsMapperConfigのプロパティとして、「correctCellCommentOnSave」を追加。


--------------------------------------------------------
ver.0.3 - 2015-01-11
--------------------------------------------------------

下記の機能を追加または改善

* `#4 <https://github.com/mygreen/xlsmapper/issues/4>`_ : 書き込み時にレコードの追加・削除を行った際に入力規則と名前の定義を自動的に修正する機能を追加。
    
    * XlsMapperConfigのプロパティとして、「correctNameRangeOnSave」「correctCellDataValidationOnSave」を追加。
    * ただし、データの入力規則を自動的に修正する機能を利用する場合は、POI-3.11が必要となります。

* `#13 <https://github.com/mygreen/xlsmapper/issues/13>`_: 読み込み時のエラーメッセージの改善。型変換時エラー時にセルの値'validatedValue'を追加。
    
    * さらに、CellFieldを使用した値の検証のエラーメッセージの候補に、クラスタイプを指定できるよう改善。

--------------------------------------------------------
ver.0.2.3 - 2015-01-01
--------------------------------------------------------

下記の機能を追加または改善

* `#7 <https://github.com/mygreen/xlsmapper/issues/7>`_ : Excel関数が設定されているセルを読み込んだときに例外が発生する事象を修正。
 
* `#8 <https://github.com/mygreen/xlsmapper/issues/8>`_ : 書き込み時のセルの「縮小して表示」の処理を効率化。

* `#9 <https://github.com/mygreen/xlsmapper/issues/9>`_ : CellFieldクラスで属性エラーがある場合でも必須チェックが実行される事象を修正。

* `#10 <https://github.com/mygreen/xlsmapper/issues/10>`_ : 列挙型に対して入力値検証する際にエラーコード「typeMismatch.java.lang.Enum」を追加。

* `#11 <https://github.com/mygreen/xlsmapper/issues/11>`_ : isから始まるboolean型のgetterメソッドにアノテーションを付与しても無視される事象を修正。

* `#12 <https://github.com/mygreen/xlsmapper/issues/12>`_ : EL3.0で追加されたラムダ式を利用できるよう改善。

--------------------------------------------------------
ver.0.2.2 - 2014-12-01
--------------------------------------------------------

下記の不良を修正。
 
* `#5 <https://github.com/mygreen/xlsmapper/issues/5>`_  : 書き込み時に、リストのトリムが有効にならない。

* `#6 <https://github.com/mygreen/xlsmapper/issues/6>`_  : 入力値検証の際に変数の値がnullにしているとNPEが発生する。


--------------------------------------------------------
ver.0.2.1 - 2014-11-25
--------------------------------------------------------

下記の不良を修正。

* `#1 <https://github.com/mygreen/xlsmapper/issues/1>`_ - @XlsHorizontalRecordsに、Set型を使用すると例外が発生する。

* `#2 <https://github.com/mygreen/xlsmapper/issues/2>`_ - ExpressionLanguageELImplが、Spring-expression依存になっている。


--------------------------------------------------------
ver.0.2 - 2014-11-24
--------------------------------------------------------


* アノテーション @XlsIsEmptyを追加しました。
   
    * ``@XlsHorizontalRecords`` 、``@XlsVertialRecords`` の属性skipEmptyRecordで'true'を指定した場合、レコードが空の場合、そのレコードの読み込みをスキップします。
    * アノテーション @XlsIsEmptyは、引数なしで、戻り値がtrueのメソッドに付与する必要がります。
   
* ``MessageInterpolator`` を改善し、メッセージ中に定義した変数をメッセージコードとして処理する機能を追加しました。
    
    * メッセージをフォーマットする際に、引数で渡した変数用オブジェクトに存在しない変数名がメッセージに存在する場合、MessageResolverから値を取得します。
   
* SheetBindingErrors中のフィールドエラーにアクセスするメソッドにおいて、現在の位置を考慮するように改善しました。

* ``@XlsHorzontalRecords(remainedRecord=RemainedRecordOperate.Clear)`` を指定指示に書き込んだ場合、書き込むレコードの件数が0件の場合、出力したシートがヘッダーのスタイルになる現象を修正しました。
 
* ``@XlsHorzontalRecords(remainedRecord=RemainedRecordOperate.Delete)`` を指定指示に書き込んだ場合、書き込むレコードの件数が0件の場合、見出し行を除く行が全て削除される現象を改善しまいた。1件のみ残すよう修正しました。


--------------------------------------------------------
ver.0.1 - 2014-10-30
--------------------------------------------------------

初期リリース。



