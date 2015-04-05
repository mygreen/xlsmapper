--------------------------------------------------------
XlsMapperConfigによる動作のカスタマイズ
--------------------------------------------------------


読み込み時及び書き込み時の動作をXlsMapperConfigクラスでカスタマイズすることができます。


.. sourcecode:: java
    
    XlsMapper XlsMapper = new XlsMapper();
    
    // シートが見つからない場合にエラーにしない。
    XlsMapperConfig config = new XlsMapperConfig();
    config.config.setIgnoreSheetNotFound(true);
    
    XlsMapper.setConfig(config);
    
    SheetObject sheet = new XlsMapper.load(
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
   
   * - ``skipTypeBindFailure``
     - ``boolean``
     - | 型変換エラーが発生しても処理を続けるかどうか。
       | 初期値は'false'です。
   
   * - ``mergeCellOnSave``
     - ``boolean``
     - | 書き込み時にセルの結合を行うかどうか。
       | アノテーション@XlsColumnの属性mergedを書き込み時も考慮します。
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
       | 例外がPOI-3.10～3.11以上で発生します。POI-3.12で修正されています。
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
       
   

