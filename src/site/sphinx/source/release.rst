======================================
リリースノート
======================================

--------------------------------------------------------
ver.1.4 - 2016-03-21
--------------------------------------------------------

* `#79 <https://github.com/mygreen/xlsmapper/issues/79>`_ : :ref:`@XlsNestedRecords <annotationXlsNestedRecords>` による、入れ子構造の表をマッピングする機能を追加しました。


--------------------------------------------------------
ver.1.3 - 2016-03-13
--------------------------------------------------------

* `#77 <https://github.com/mygreen/xlsmapper/issues/77>`_ : :ref:`@XlsListener <annotationXlsListener>` による、ライフサイクル・コールバック処理をリスナクラスに別途実装する機能を追加しました。

* `#78 <https://github.com/mygreen/xlsmapper/issues/78>`_ : 複数のアノテーションが設定されている場合、1つしか処理されない事象を改善しました。

--------------------------------------------------------
ver.1.2.1 - 2016-03-12
--------------------------------------------------------

* `#65 <https://github.com/mygreen/xlsmapper/issues/65>`_ : 例外時のメッセージのスペルミス、値の設定間違いを修正しました。


--------------------------------------------------------
ver.1.2 - 2016-03-12
--------------------------------------------------------

* :doc:`システムプロパティ <otheruse_config>` ``skipTypeBindFailure`` の名称を ``continueTypeBindFailure`` に変更し、意味と名称が一致するようにしました。

* `#71 <https://github.com/mygreen/xlsmapper/issues/71>`_ : アノテーション ``@XlsColumn`` などを付与したフィールドが、``java.util.LinkedList`` などの具象クラスの場合をサポートしました。

* `#76 <https://github.com/mygreen/xlsmapper/issues/76>`_ : アノテーション :ref:`@XlsMapColumn <annotationXlsMapColumns>` に属性 ``nextColumnName`` を追加、マッピングの終了条件のセルを指定できるようにしました。


--------------------------------------------------------
ver.1.1 - 2016-03-08
--------------------------------------------------------

* `#3 <https://github.com/mygreen/xlsmapper/issues/3>`_ : :ref:`@XlsArrayConverter <annotationXlsArrayConverter>` に属性 ``itemConverterClass`` を追加し、任意のクラス型を変換できるようにしました。

* `#66 <https://github.com/mygreen/xlsmapper/issues/66>`_ : セルの値を取得する方法を別ライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ の最新版ver.0.6に対応しました。

* `#67 <https://github.com/mygreen/xlsmapper/issues/67>`_ : アノテーション :ref:`@XlsNumberConverter <annotationXlsNumberConverter>` 、 :ref:`@XlsDateConverter <annotationXlsDateConverter>` の属性 ``pattern`` を廃止し、
  読み込み用の書式の属性 ``javaPattern`` と書き込み用の書式の属性 ``excelPattern`` を追加しました。


* `#70 <https://github.com/mygreen/xlsmapper/issues/70>`_ : アノテーションのXMLによるマッピング機能の機能追加として、:ref:`XMLをJavaオブジェクトで組み立てる機能 <xml-build>` を追加しました。
  それに伴い、次の修正も行いました。

  * XML読み込み用のクラス ``XmlLoader`` の名称を ``XmlIO`` に変更し、XMLの書き込み用メソッドを追加しました。
  * 例外クラス ``XmlLoadException`` の名称を ``XmlOperateException`` に変更しました。
  * 読み込み時/書き込み時の処理対象となるシートの抽出処理を、 ``SheetFinder`` クラスに分離しました。
    :doc:`XlsMapperConfigのプロパティ「sheetFinder」<otheruse_config>` でカスタマイズすることができます。

* `#72 <https://github.com/mygreen/xlsmapper/issues/72>`_ : ラベルや見出しを正規表現で指定、正規化してマッピングする機能を追加しました。

  * :doc:`システム設定のプロパティ <otheruse_config>` として、 ``regexLabelText`` , ``normalizeLabelText`` を追加。
  
  * :ref:`@XlsLabelledCell <annotationXlsLabelledCell>` の属性 ``label`` , ``headerLabel`` で有効になります。
  
  * :ref:`@XlsHorizonalRecords <annotationXlsHorizontalRecords>` の属性 ``tableLabel`` , ``terminateLabel`` で有効になります。

  * :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` の属性 ``tableLabel`` , ``terminateLabel`` で有効になります。
  
  * :ref:`@XlsIterateTables <annotationXlsIterateTables>` の属性 ``tableLabel`` で有効になります。


* `#73 <https://github.com/mygreen/xlsmapper/issues/73>`_ : 見出し結合されている場合の属性を追加しました。

  * :ref:`@XlsHorizonalRecords(headerBottom) <annotationXlsHorizontalRecords>` を追加しました。

  * :ref:`@XlsVerticalRecords(headerRight) <annotationXlsVerticalRecords>` を追加しました。

* `#74 <https://github.com/mygreen/xlsmapper/issues/74>`_ : 型変換用のアノテーションのパッケージ ``～.xlsmapper.annotation.converter`` を ``～.xlsmapper.annotation`` に移動しました。

* `#75 <https://github.com/mygreen/xlsmapper/issues/75>`_ : 一部のアノテーションの属性名を変更しました。

  * アノテーション :ref:`@XlsHorizonalRecords <annotationXlsHorizontalRecords>` と :ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` の属性 ``skipEmptyRecord`` を ``ignoreEmptyErecord`` に変更しました。

  * アノテーション :ref:`@XlsConverter <annotationXlsConverter>` の属性 ``forceWrapText`` を ``wrapText`` に、属性 ``forceShrinkToFit`` を ``shrinkToFit`` 変更しました。

--------------------------------------------------------
ver.1.0a - 2015-09-23
--------------------------------------------------------

下記の機能を追加または改善

* `#63 <https://github.com/mygreen/xlsmapper/issues/63>`_ : 実行すると必要のないCoverturaのクラスのエラーが発生する事象を修正しました。
    
    * ビルドをし直しただけで、機能はver.1.0から変更ありません。


--------------------------------------------------------
ver.1.0 - 2015-07-19
--------------------------------------------------------

下記の機能を追加または改善

* `#14 <https://github.com/mygreen/xlsmapper/issues/14>`_ : メッセージ中で利用可能な式言語を EL2.0/3.0、MVELの2つにしました。
    
    * 入力値検証で利用する ``CellField`` クラスでプロパティを指定する際に、独自の実装PropertyNavigationに切り替えました。
      それに伴い、非公開のフィールドへのアクセスも可能になりました。

* `#28 <https://github.com/mygreen/xlsmapper/issues/28>`_ : クラス ``java.util.Calendar`` に対するCellConveterを追加しました。

* `#35 <https://github.com/mygreen/xlsmapper/issues/35>`_ : アノテーション ``@XlsHorizontalRecords`` の付与可能なクラスタイプとして、``java.util.Set`` を追加しました。

    * 実装クラスを指定した場合は、その読み込み時には、インスタンスが設定されます。
    * アノテーション ``@XlsVerticalRecords`` も同様に修正しました。

* `#37 <https://github.com/mygreen/xlsmapper/issues/37>`_ : アノテーション ``@XlsVerticalRecords`` でマッピングする際に、表のタイトル（ラベル）が上にある場合に対応しまいた。

    * 属性 ``tableLabelAbove=true`` を付与すると、表のタイトルの位置が上にあると前提として処理を行います。
    * さらに、表のタイトルから見出しがどれだけ離れているか指定する属性 ``right`` を追加しました。 ``XlsHorizontalRecords`` の属性 ``bottom`` に対応するものです。

* `#50 <https://github.com/mygreen/xlsmapper/issues/50>`_ : クラス ``IsEmptyBuilder`` にて、検証対象のタイプがMap, Collection, 配列の場合、要素をチェックするように機能追加しました。要素の値が全てnullまたは空と判定できた場合は、そのオブジェクトの値が空と判定します。

    * 設定用クラス ``IsEmptyConfig`` で、要素をチェックするかなどを変更することができます。

* `#53 <https://github.com/mygreen/xlsmapper/issues/53>`_ : フィールドの入力値検証を行うためのFieldValidatorの実装である、「MaxValidator/MinValidator/RangeValidator」において、メッセージ表示用に値をフォーマットを ``FieldFormatter`` で行うように機能追加しました。

    * 標準では、``DefaultFieldFormatter`` が設定されていますが、独自の実装に切り替えることができます。

* `#56 <https://github.com/mygreen/xlsmapper/issues/56>`_ : AnnotationReaderで読み込むXMLに属性 ``override=true`` を定義すると、JavaクラスとXMLファイルでそｚれぞれに定義しているアノテーションの差分を考慮するよう機能追加しました。

* `#58 <https://github.com/mygreen/xlsmapper/issues/58>`_ : ドキュメント `拡張方法 <http://mygreen.github.io/xlsmapper/sphinx/extension.html>`_ を記載しました。

* `#59 <https://github.com/mygreen/xlsmapper/issues/59>`_ : アノテーション ``@XlsVerticalRecords/XlsSheetName`` の書き込み時の処理に、読み込み用のアノテーションを取得していたため、getterメソッドにアノテーションを付与していても反映されない事象を修正しました。

    * 各種CellConverterの処理時に、アノテーション ``@XlsConverter`` を付与していた場合も同様の事象を修正しました。

* `#60 <https://github.com/mygreen/xlsmapper/issues/60>`_ : 入力値検証時にメッセージを処する際にエスケープ文字( ``\`` ) が正しく処理されない事象を修正しました。

* `#61 <https://github.com/mygreen/xlsmapper/issues/61>`_ : インタフェース ``CellConverter`` 中の書き込み用のメソッドの使用を整理しました。

    * ``@XlsMapColums`` を付与したフィールドの値を処理するためのメソッド ``toCellWithMap(...)`` を ``toCell()`` に統合しました。
    * ``toCell(...)`` メソッドの第二引数として渡していた処理対象のオブジェクトを、Beanクラスではなく、書き込み対象の値を渡すように変更しました。
    * CellConverterRegistry, FieldProcessorRegistryで使用していないメソッドを削除しました。


--------------------------------------------------------
ver.0.5 - 2015-06-29
--------------------------------------------------------

下記の機能を追加または改善

* `#21 <https://github.com/mygreen/xlsmapper/issues/21>`_ : セルの値を取得する方法を別ライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ の最新版ver.0.4に対応しました。

* `#22 <https://github.com/mygreen/xlsmapper/issues/22>`_ : 内部クラス定義にてクラス定義がprivateなどの非公開の場合ににも対応しました。読み込み時にインスタンスの生成に失敗する事象を改善しました。

* `#23 <https://github.com/mygreen/xlsmapper/issues/23>`_ : 読み込み時に、文字列形式のセルをdoubleなどの数値型のクラスにマッピングする際にエラーが発生する事象を改善しました。

* `#24 <https://github.com/mygreen/xlsmapper/issues/24>`_ : 読み込み時に、Javaクラスの表現可能な値よりも大きい数値をマッピングする際に、オーバーフローではなく、エラーとするよう動作を改善しました。

* `#25 <https://github.com/mygreen/xlsmapper/issues/25>`_ : 日時型をマッピングする際に、日時の型変換用アノテーション ``@XlsDateConverter`` で書式を指定しないとエラーが発生する事象を改善しました。アノテーションを指定しない場合、Javaの各タイプごとにデフォルトの書式が設定されます。

    * ``java.util.Date`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Date`` の場合、デフォルトで `yyyy-MM-dd` の書式が適用されます。
    * ``java.sql.Time`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss` の書式が適用されます。
    * ``java.sql.Timestamp`` の場合、デフォルトで `yyyy-MM-dd HH:mm:ss.SSS` の書式が適用されます。

* `#26 <https://github.com/mygreen/xlsmapper/issues/26>`_ : 空セル（ブランクセル）をString型に読み込む時、型変換世のアノテーション ``@XlsConverter(trim=true)`` を付与してトリムを有効としている場合、空文字を設定するように改善しました。

    * トリムが無効な場合は、nullが設定されます。

* `#27 <https://github.com/mygreen/xlsmapper/issues/27>`_ : 空の項目を無視するリスト型の型変換用アノテーション ``@XlsArrayConverter(ignoreEmptyItem=true)`` と、トリムを有効にするアノテーション ``@XlsConverter(trim=true)`` を組み合わせた場合、トリム処理が無視される事象を改善しました。

    * トリム処理により空の項目となり、空の項目を無視する設定をしている場合、その項目は読み込み、書き込みの対象外となります。


* `#28 <https://github.com/mygreen/xlsmapper/issues/28>`_ : アノテーション ``@XlsLabelledCell(label="XXXX", optional=true)`` と設定し、指定したラベルのセルが見つからない場合に、NullPointerExceptionが発生する事象を修正しました。

* `#31 <https://github.com/mygreen/xlsmapper/issues/31>`_ : アノテーション ``@XlsLabelledCell`` の属性 range、skip、headerLabelを指定した場合の処理を改善しました。

    * 属性headerLabelを指定した場合、Excelのシート上のheaderLabelで指定したセルを取得した後、labelで指定したセルを検索する際に、検索の開始位置が常に0行目から検索し直してしまい、違うセルがヒットしてしまう事象を修正しました。
   
    * 属性skipとrangeを指定していると、NullPointerExceptionが発生する事象を修正しました。

* `#32 <https://github.com/mygreen/xlsmapper/issues/32>`_ : アノテーション ``@XlsLabelledCell`` でセルの値を読み込む時に、``Map<String, Position> positions`` フィールドにてを定義していても、セルのアドレスが正しく取得できない事象を修正しました。

* `#33 <https://github.com/mygreen/xlsmapper/issues/33>`_ : アノテーション ``@XlsSheet(number=2)`` で読み込み／書き込みするシートをシート番号で指定している場合、例外 ``SheetNotFoundException`` がスローされる事象を修正しました。

* `#34 <https://github.com/mygreen/xlsmapper/issues/34>`_ : アノテーション ``@XlsHorizontalRecords`` レコードをマッピングする場合、見出しセルを結合していると正しく、セルの値が取得できない事象を修正しました。``@XlsVerticalRecords`` も同様に修正しました。

* `#38 <https://github.com/mygreen/xlsmapper/issues/38>`_ : 数値型をマッピングする場合、Excelの仕様に合わせて有効桁数を指定するように機能追加しました。

    * 有効桁数は、数値の型変換用アノテーション ``@XlsNumberConverter(precision=15)`` で変更可能です。
    * デフォルトでは、有効桁数はExcelの仕様と同じ15桁です。

* `#39 <https://github.com/mygreen/xlsmapper/issues/39>`_ : 型変換用アノテーション ``@XlsConverter(defaultValue="aaaa")`` デフォルト値を指定しているが、その値自体が不正な場合、ConverterExceptionをスローしているが、その子クラスのTypeBindExceptionをスローするように修正しました。

* `#40 <https://github.com/mygreen/xlsmapper/issues/40>`_ : char型を書き込む時に初期値'\u000'を設定し書き込むとExcel上で文字化けする事象を修正しました。

    * char型を書き込む時に、’\u000’は、空白と判断して、空セルとして書き込むよう修正。
    * char型の場合、書き込む時にデフォト値が2文字以上あってもそのまま書き込まれるため、先頭の1文字のみ書き込むよう修正。

* `#41 <https://github.com/mygreen/xlsmapper/issues/41>`_ : Javaクラス ``java.util.Set`` を書き込む場合、値をnullとしていると、NullPointerExceptionが発生する事象を修正しました。

* `#42 <https://github.com/mygreen/xlsmapper/issues/42>`_ : アノテーション ``@XlsVerticalRecords`` で属性headerAddressを指定していても反映されない事象を修正しました。

* `#44 <https://github.com/mygreen/xlsmapper/issues/44>`_ : アノテーション ``XlsSheet(regexp="Sheet.+")`` 正規表現にてシート名を指定し、書き込む際の改善をしました。
    
    * 正規表現で指定しても、一致するシートが1つの場合は、エラーとしないで、そのシートに書き込む。
    * アノテーション ``@XlsSheetName`` を付与しているフィールドを指定し、その値に一致しなくても、正規表現に一致するシートが1つ一致すれば、そのシートに書き込む。
* `#45 <https://github.com/mygreen/xlsmapper/issues/45>`_ : アノテーション ``@XlsHorizontalRecords(terminal=RecordTerminal.Empty)`` を設定している場合、レコードを設定していても、書き込まれない事象を修正しました。

   * 読み込み時には表の終端を判定する際に、セルの値が空であることに意味があるが、書き込む際にはテンプレート用のセルは空を設定しているため、処理が終了してしまう。そのため、書き込む時に、terminalの値がRecordTerminal.Emptyのとき強制的にRecordTerminal.Borderに補正して処理する。

* `#46 <https://github.com/mygreen/xlsmapper/issues/46>`_ : アノテーション ``@XlsHoritonralRecords`` で書き込む場合、レコードのフィールドにアノテーション `@XlsColumn(merged=true)` を付与し、同じ値のセルを結合する設定をしていると、Excelファイルが壊れる事象を修正しました。

* `#47 <https://github.com/mygreen/xlsmapper/issues/47>`_ : アノテーション ``@XlsHorizontalRecords`` を付与しているフィールド型が配列型の場合、書き込むときにレコードが出力されない事象を修正しました。
  同様に、``@XlsVertiacalRecords``、``@XlsIterateTables`` の処理も修正しました。

* `#48 <https://github.com/mygreen/xlsmapper/issues/48>`_ : アノテ－ション ``@XlsHorizontalRecords(remainedRecord=RemainedRecordOperate.Delete)`` を付与し、書き込む先に余分な行を削除するときに、1回多く削除してしまう事象を修正しました。

* `#49 <https://github.com/mygreen/xlsmapper/issues/49>`_ : アノテーション ``@XlsHorizontalRecords`` を付与し、書き込む際にレコードが追加、削除されるときに、Excelの入力規則の範囲修正が正しくできない事象を修正しました。

* `#51 <https://github.com/mygreen/xlsmapper/issues/51>`_ : アノテーション ``@XlsIterateTables`` を付与し、連結した表を書き込む時に、はみ出したセルがあると、属性orverRecordOperateの処理が実行されない事象を修正しました。

* `#52 <https://github.com/mygreen/xlsmapper/issues/52>`_ : アノテーション ``@XlsHorizontalRecords`` を付与したクラスに、ライフサイクルコールバック用のアノテーション ``@XlsPostSave`` を付与したメソッドが実行されない事象を修正しました。 ``@XlsVerticalRecords`` の場合も同様に修正しました。

* `#54 <https://github.com/mygreen/xlsmapper/issues/54>`_ : メッセージ中などの式言語の処理としてEL2.Xを利用する場合、実装を外部ライブラリ `standalone-el <https://github.com/mygreen/standalone-el/>`_ に変更しました。

* `#57 <https://github.com/mygreen/xlsmapper/issues/57>`_ : メッセージ中などの式言語の処理としてEL3.Xを利用している場合、formatterを利用しているとエラーが発生する事象を修正しました。

    * EL3.xのライブラリのバージョンを3.0から3.0.1-b08に変更しました。

* アノテーション ``@XlsIsEmpty`` を付与してレコードが空かどうか判定するメソッドの実装を容易にするためのクラス ``IsEmptyBuilder`` を追加しました。

* XMLファイルによるマッピング機能において、XMLのパースをJAXPから、JAXBへ変更しました。



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


* アノテーション ``@XlsIsEmpty`` を追加しました。
   
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



