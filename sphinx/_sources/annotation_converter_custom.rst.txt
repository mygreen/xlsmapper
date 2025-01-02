------------------------------------------------------------------
独自のクラスタイプの対応方法
------------------------------------------------------------------

Excelのセルの値を任意のJavaのクラス型にマッピングする方法を説明します。

1. CellConverterの実装クラスの作成
2. CellConverterを作成するためのファクトリクラスの作成
3. アノテーション ``@XlsConverter`` による指定


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CellConverterの実装クラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Excelのセルの値をJavaの任意のクラスにマッピングするには、CellConveterを実装します。

次のように、タイトルとリストが組み合わさったテキストをCustomTypeクラスにマッピングするとします。

.. sourcecode:: md
    :caption: マッピング対象の値
    
    タイトル
    - リスト1
    - リスト2

.. sourcecode:: java
   :linenos:
   :caption: マッピングするJavaクラス

   public class CustomType {

        /** タイトル */
        private String title;

        /** リスト */
        private List<String> items;

        /**
         * リストの要素を追加する
         * @param リストの要素
         */
        public void addItem(String item) {
            if(items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
        }
        
        // setter, getterは省略

    }


* インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverter`` を実装します。

  * 実際には、ユーティリティメソッドがそろっている、``com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter`` を継承して実装します。
  * 読み込み時、書き込み時のそれぞれのメソッドを実装します。
    
    * 文字列に対するマッピングの場合は、``TextFormatter`` の ``parse/format`` メソッドに委譲します。
    * ``TextFormatter`` の実装は、後の ``CellConverterFactory`` の作成にて実装します。
    
  * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.cellconvert.impl`` 以下に格納されているクラスを参照してください。

.. sourcecode:: java
    :linenos:
    :caption: CellConverterの実装
    
    public class CustomCellConverter extends BaseCellConverter<CustomType> {
        
        public CustomCellConverter(FieldAccessor field, Configuration config) {
            super(field, config);
        }
        
        /**
         * セルをJavaのオブジェクト型に変換します。
         * @param evaluatedCell 数式を評価済みのセル
         * @param formattedValue フォーマット済みのセルの値。トリミングなど適用済み。
         * @return 変換した値を返す。
         * @throws TypeBindException 変換に失敗した場合
         */
        @Override
        protected CustomType parseCell(Cell evaluatedCell, String formattedValue) throws TypeBindException {
        
            try {
                // 文字列を変換するときには、TextFormatter#parseに委譲します。
                return getTextFormatter().parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        
        }
        
        /**
         * 書き込み時のセルに値と書式を設定します。
         * @param cell 設定対象のセル
         * @param cellValue 設定対象の値。
         * @throws TypeBindException 変換に失敗した場合
         */
        @Override
        protected void setupCell(Cell cell, Optional<CustomType> cellValue) throws TypeBindException {
        
            if(cellValue.isPresent()) {
                // 文字列を変換するときには、TextFormatter#formatに委譲します。
                String text = getTextFormatter().format(cellValue.get());
                cell.setCellValue(text);
            } else {
                cell.setCellType(CellType.BLANK);
            }
        }
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CellConverterFactoryの実装クラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* インタフェース ``com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory`` を実装する。

  * 実際には、サポートメソッドが揃っている ``com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport`` を継承し作成します。
  * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.cellconvert.impl`` 以下に格納されているクラスを参照してください。

* 文字列に対する処理として、 ``TexFormatter`` を実装します。

  * ``TextFromatter#parse`` は、初期値を ``@XlsDefaltValue("<初期値>")`` で与えられているときに、文字列をオブジェクトに変換する際に使用します。
  * ``TextFormatter#format`` は、Validationのエラーメッセージ中で値をフォーマットするときに、オブジェクトを文字列に変換する際に使用します。


.. sourcecode:: java
    :linenos:
    :caption: CellConverterFactoryの実装
    
    /**
     * フィールドに対するセル変換クラスを作成する。
     * @param accessor フィールド情報
     * @param config システム設定
     * @return セルの変換クラス。
     */
    public class CustomCellConverterFactory extends CellConverterFactorySupport<CustomType>
                implements CellConverterFactory<CustomType> {
        
        @Override
        public CustomCellConverter create(FieldAccessor accessor, Configuration config) {
            final CustomCellConverter cellConverter = new CustomCellConverter(accessor, config);
            
            // トリムなどの共通の処理を設定する
            setupCellConverter(cellConverter, accessor, config);
            
            return cellConverter;
        }
        
        @Override
        protected void setupCustom(AbstractCellConverter<CustomType> cellConverter, FieldAccessor field, Configuration config) {
            // 必要があれば実装する。
        }
        
        /**
         * {@link TextFormatter}のインスタンスを作成する。
         * @param field フィールド情報
         * @param config システム情報
         * @return {@link TextFormatter}のインスタンス
         */
        @Override
        protected TextFormatter<CustomType> createTextFormatter(FieldAccessor field, Configuration config) {
        
            return new TextFormatter<CustomType>() {
                
                @Override
                public CustomType parse(String text) throws TextParseException {
                
                    if(StringUtils.isEmpty(text)) {
                        return null;
                    }
                    
                    // 改行で分割する
                    String[] split = text.split("\r\n|\n");
                    
                    if(split.length <= 1) {
                        // 1行以下しかない場合は、例外とする
                        throw new TextParseException(text, CustomType.class);
                    }
                    
                    CustomType data = new CustomType();
                    data.setTitle(split[0]);
                    
                    for(int i=1; i < split.length; i++) {
                        String item = split[i];
                        if(item.startsWith("- ")) {
                            // リストの記号を削除する
                            item = item.substring(2);
                        }
                        data.addItem(item);
                    }
                    
                    return data;
                }
                
                @Override
                public String format(CustomType value) {
                    if(value == null) {
                        return "";
                    }
                    
                    StringBuilder text = new StringBuilder();
                    text.append(value.getTitle())
                        .append("\n");
                    
                    // 改行で繋げる
                    text.append(value.getItems().stream().collect(Collectors.joining("\n")));
                    return text.toString();
                }
                
            };
        }

    }

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
作成したCellConverterの使用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

作成したCellConverterを使用するには、2つの方法があります。

.. _annotationXlsConverter:

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
``@XlsConverter`` を使用する方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

作成した CellConverterFactoryの実装クラスをアノテーション ``@XlsConverter`` に指定します。

.. sourcecode:: java
    :linenos:
    :caption: XlsConverterアノテーションによる指定
    
    
    public class SampleRecord {
         
         // 独自のCellConverterFactoryの指定
         @XlsConverter(CustomCellConverterFactory.class)
         @XlsColumn(columnName="TODOリスト")
         private CustomType value;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CellConverterRegistry を使用する方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

作成した CellConverterFactoryの実装クラスのインスタンスを ``XlsMapperConfg#getConverterRegistry()`` に登録します。


.. sourcecode:: java
    :linenos:
    :caption: CellConverterRegistryへの登録
    
    // 独自のCellConverterFactoryの登録
    XlsMapper mapper = new XlsMapper();
    CellConverterRegistry cellConverterRegistry = mapper.getConiguration().getConverterRegistry();
    cellConverterRegistry.registerConverter(CustomType.class, new CustomCellConverterFactory());
    
    
