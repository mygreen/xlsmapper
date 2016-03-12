--------------------------------------------------------
XlsMapperConfigによる動作のカスタマイズ
--------------------------------------------------------


読み込み時及び書き込み時の動作をXlsMapperConfigクラスでカスタマイズすることができます。

.. sourcecode:: java
    
    // 設定用のオブジェクトXlsMapperConfigの作成
    XlsMapperConfig config = new XlsMapperConfig();
    
    // シートが見つからない場合にエラーにしない。
    config.setIgnoreSheetNotFound(true);
    
    // XlsMapperConfigクラスをXlsMapperに渡す。
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.setConfig(config);
    
    // 設定を変更したXlsMapperでシートの読み込み
    SheetObject sheet = xlsMapper.load(
        new FileInputStream("example.xls"), SheetObject.class);


XlsMapperConfigは、XlsMapperクラスのインスタンスを作成時にも持っているため、次のような変更もできます。

.. sourcecode:: java
    
    XlsMapper xlsMapper = new XlsMapper();
    
    // XlsMapperクラスから直接XlsMapperConfigのインスタンスを取得し変更する。
    xlsMapper.getConfig().setIgnoreSheetNotFound(true);
    
    // 設定を変更したXlsMapperでシートの読み込み
    SheetObject sheet = xlsMapper.load(
        new FileInputStream("example.xls"), SheetObject.class);

XlsMapperConfigでは以下の設定を行うことができます。

.. list-table:: XlsConfigで設定可能な項目
   :widths: 20 30 50
   :header-rows: 1
   
   * - プロパティ名
     - クラス型
     - 説明
   
   * - ``ignoreSheetNotFound``
     - ``boolean``
     - | シートが見つからなくても無視するかどうか。
       | 初期値は'false'です
   
   * - ``normalizeLabelText``
     - ``boolean``
     - | ラベルを正規化（空白、改行、タブを除去）して、マッピングするかどうか
       | 初期値は'false'です。
       | **Ver.1.1以上** から利用可能です。
   
   * - ``regexLabelText``
     - ``boolean``
     - | マッピングするラベルに ``/正規表現/`` と記述しておくと正規表現でマッピングできるかどうか。
       | 初期値は'false'です。
       | **Ver.1.1以上** から利用可能です。
   
   * - ``continueTypeBindFailure``
     - ``boolean``
     - | 型変換エラーが発生しても処理を続けるかどうか。
       | 初期値は'false'です。
       | **Ver1.1以前** は、 ``skipTypeBindFailure`` という名称です。
   
   * - ``mergeCellOnSave``
     - ``boolean``
     - | 書き込み時にセルの結合を行うかどうか。
       | アノテーション :ref:`@XlsColumn <annotationXlsColumn>` の属性mergedを書き込み時も考慮します。
       | 初期値は'false'です。
   
   * - ``correctNameRangeOnSave``
     - ``boolean``
     - | 書き込み時に名前の定義範囲を修正するかどうか。
       | 初期値は'false'です。
       | **Ver.0.3以上** から利用可能です。
   
   * - ``correctCellDataValidationOnSave``
     - ``boolean``
     - | 書き込み時に名前のセルの入力規則を修正するかどうか。
       | 初期値は'false'です。
       | **Ver.0.3以上** から利用可能です。
   
   * - ``correctCellCommentOnSave``
     - ``boolean``
     - | コメント付きのシートに対して列を追加し保存する際にPOIの不良（ `Bug 56017 <https://bz.apache.org/bugzilla/show_bug.cgi?id=56017>`_ ）のため例外が発生する事象を回避するために設定します。
       | 例外は、バージョンPOI-3.10～3.11で発生します。バージョンPOI-3.12で修正されています。
       | 初期値は'false'です。
       | **Ver.0.4以上** から利用可能です。
   
   * - ``cellFormatter``
     - ``CellFormatter``
     - | POIのセルの値をフォーマットして文字列として取得するクラスです。
       | 実装は、Ver.0.4から `Excel-CellFormatter <https://github.com/mygreen/excel-cellformatter>`_ を利用しています。
   
   * - ``fieldProcessorRegistry``
     - ``FieldProcessorRegstry``
     - | フィールドプロセッサーを管理します。
   
   * - ``converterRegistry``
     - ``CellConverterRegistry``
     - | セルの値をJavaオブジェクトに変換するクラスを管理します。
   
   * - ``beanFactory``
     - ``FactoryCallback``
     - | 読み込み時などのJavaBeansオブジェクトのインスタンスを作成すためのコールバック用クラス。
       | 独自の実装を渡すことで、SpringFrameworkなどのDIコンテナで管理しているクラスを使用することができます。
       
   * - ``sheetFinder``
     - ``SheetFinder``
     - | アノテーション :ref:`@XlsSheet <annotationXlsSheet>` に基づき処理対象のシートを抽出するクラス。
       | **Ver.1.1以上** から利用可能です。
   
   * - ``itemConverter``
     - ``ItemConverter``
     - | アノテーション :ref:`@XlsArrayConverter <annotationXlsArrayConverter>` の属性 ``itemConverter`` で指定する、リストなどの要素を変換するための標準の処理クラスです。
       | **Ver.1.1以上** から利用可能です。


