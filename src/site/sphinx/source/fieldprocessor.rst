=========================================================
独自の表・セルのマッピング方法
=========================================================

Excelのシートを独自の基準で走査して、Javaクラスにマッピングする、 FieldProcessor を実装することができます。

* 読み込み時用と書き込み時用のメソッドがあり、それぞれ実装します。
* 抽象クラス「com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor」を継承すると便利です。
* 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.fieldprocessor.impl`` 以下に格納されているクラスを参照してください。。

.. sourcecode:: java
    :linenos:
    :caption: FieldProcessorの作成
    
    public class SampleFieldProcessor extends AbstractFieldProcessor<SampleAnno> {
    
        // シートの読み込み時の処理
        @Override
        public void loadProcess(final Sheet sheet, final Object beansObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final Configuration config, final LoadingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
        }
        
        // シートの書き込み時の処理
        @Override
        public void saveProcess(final Sheet sheet, final Object targetObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final Configuration config, final SavingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
            
        }
    }


* 作成した FieldProcessorは、 ``FieldProcessorRegistry#registerProcessor(...)`` にて登録します。

  * システム標準の ``FieldProcessorRegistry`` は、``XlsMapperConfg#getFieldProcessorRegistry()`` から取得できます。


.. sourcecode:: java
    :linenos:
    :caption: FieldProcessorの登録
    
    // 独自のFieldProcessorの登録
    Configuration config = new Configuration();
    config.getFieldProcessorRegistry().registerProcessor(SampleAnno.class, new SampleFieldProcessor());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfiguration(config);
    


また、アノテーションを作成するう際に、メタアノテーション ``@XlsFieldProcessor`` でFieldProcessorを指定することもできます。 `[ver2.0+]` 

.. sourcecode:: java
    :linenos:
    :caption: メタアノテーションを使ったFieldProcessorの指定
    
    // 独自のマッピング用のアノテーションの作成
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @XlsFieldProcessor(SampleFieldProcessor.class)  // 対応するFieldProcessorの指定
    public @interface XlsSampleAnno {
        // ・・・属性の定義
    }

