====================================================
システム設定
====================================================


読み込み時及び書き込み時の動作をConfigurationクラスでカスタマイズすることができます。

.. sourcecode:: java
    :linenos:
    
    // 設定用のオブジェクトConfigurationの作成
    Configuration config = new Configuration();
    
    // シートが見つからない場合にエラーにしない。
    config.setIgnoreSheetNotFound(true);
    
    // ConfigurationクラスをXlsMapperに渡す。
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.setConfiguration(config);
    
    // 設定を変更したXlsMapperでシートの読み込み
    SheetObject sheet = xlsMapper.load(
        new FileInputStream("example.xls"), SheetObject.class);


Configurationは、XlsMapperクラスのインスタンスを作成時にも持っているため、次のような変更もできます。

.. sourcecode:: java
    :linenos:
    
    XlsMapper xlsMapper = new XlsMapper();
    
    // XlsMapperクラスから直接XlsMapperConfigのインスタンスを取得し変更する。
    xlsMapper.getConfiguration().setIgnoreSheetNotFound(true);
    
    // 設定を変更したXlsMapperでシートの読み込み
    SheetObject sheet = xlsMapper.load(
        new FileInputStream("example.xls"), SheetObject.class);

Configurationでは以下の設定を行うことができます。

.. list-table:: Configurationで設定可能な項目
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
       | アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、:ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` でレコードの追加を行った箇所に名前の定義があるときに考慮します。
       | 初期値は'false'です。
       | **Ver.0.3以上** から利用可能です。
   
   * - ``correctCellDataValidationOnSave``
     - ``boolean``
     - | 書き込み時に名前のセルの入力規則を修正するかどうか。
       | アノテーション :ref:`@XlsHorizontalRecords <annotationXlsHorizontalRecords>` 、:ref:`@XlsVerticalRecords <annotationXlsVerticalRecords>` でレコードの追加を行った箇所に入力規則が設定されているときに考慮します。
       | 初期値は'false'です。
       | **Ver.0.3以上** から利用可能です。
   
   * - ``formulaRecalcurationOnSave``
     - ``boolean``
     - | 書き込み時に式の再計算をするか設定します。
       | 数式を含むシートを出力したファイルを開いた場合、一般的には数式が開いたときに再計算されます。
       | ただし、大量で複雑な数式が記述されていると、パフォーマンスが落ちるため 'false' 設定すると無効にすることもできます。
       | 初期値は'true'です。
       | **Ver.1.5以上** から利用可能です。
   
   * - ``cacheCellValueOnLoad``
     - ``boolean``
     - | 読み込み時にセルの値をキャッシュして処理速度の向上を行うかどうか。書き込み時に名前のセルの入力規則を修正するかどうか。
       | 初期値は'true'です。
       | **Ver.2.0以上** から利用可能です。
   
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
     - ``BeanFactory``
     - | 読み込み時などのJavaBeansオブジェクトのインスタンスを作成すためのコールバック用クラス。
       | 独自の実装を渡すことで、SpringFrameworkなどのDIコンテナで管理しているクラスを使用することができます。
       
   * - ``bindingErrorsFactory``
     - ``SheetBindingErrorsFactory``
     - | マッピング時のエラー情報 ``SheetBindingErrors`` のインスタンスを作成すためのコールバック用クラス。
       | 独自の実装を渡すことで、SpringFrameworkなどのDIコンテナで管理しているクラスを使用することができます。
       | **Ver.2.0以上** から利用可能です。
       
   * - ``sheetFinder``
     - ``SheetFinder``
     - | アノテーション :ref:`@XlsSheet <annotationXlsSheet>` に基づき処理対象のシートを抽出するクラス。
       | **Ver.1.1以上** から利用可能です。
   
   * - ``formulaFormatter``
     - ``MessageInterpolator``
     - | アノテーション :ref:`@XlsFormula <annotationFormula>` の属性 ``value`` で指定した数式を独自の変数やEL式をフォーマットする際に利用します。
       | **Ver.1.5以上** から利用可能です。

   * - ``annotationMapping``
     - ``AnnotationMappingInfo``
     - | XMLなどによるアノテーションのマッピング情報を設定します。
       | 詳細は、:doc:`xmlmapping` のを参照してください。
       | **Ver.2.0以上** から利用可能です。

   * - ``commentOperator``
     - ``CellCommentOperator``
     - | セルのコメント情報をマッピングするデフォルトの処理を設定します。
       | 詳細は、:doc:`sheetinfo_comment` や :ref:`@XlsCommentOption <annotationXlsCommentOption>` を参照してください。
       | **Ver.2.1以上** から利用可能です。


