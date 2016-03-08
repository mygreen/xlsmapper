--------------------------------------------------------
FieldProcessorの拡張
--------------------------------------------------------

Excelのシートを独自の基準で走査して、Javaクラスにマッピングする、 FieldProcessor を実装することができます。

* 読み込み時用のインタフェース ``com.gh.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor`` と、書き込み時のインタフェース ``com.gh.mygreen.xlsmapper.fieldprocessor.SavingFieldProcessor`` が分かれており、必要なものを実装します。

  * 読み込みと書き込み処理を同時に実装する際には、抽象クラス「com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor」を継承すると便利です。
  * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.fieldprocessor.processor`` 以下に格納されているクラスを参照してください。。


.. sourcecode:: java
    
    public class CellProcessor extends AbstractFieldProcessor<SampleAnno> {
    
        // シートの読み込み時の処理
        @Override
        public void loadProcess(final Sheet sheet, final Object beansObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
        }
        
        // シートの書き込み時の処理
        @Override
        public void saveProcess(final Sheet sheet, final Object targetObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
            
        }
    }


* 作成した FieldProcessorは、 ``FieldProcessorRegistry#registerProcessor(...)`` にて登録します。

  * システム標準の ``FieldProcessorRegistry`` は、``XlsMapperConfg#getFieldProcessorRegistry()`` から取得できます。


.. sourcecode:: java
    
    // 独自のFieldProcessorの登録
    XlsMapperConfig config = new XlsMapperConfig();
    config.getFieldProcessorRegistry().registerProcessor(SampleAnno.class, new SampleFieldProcessor());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfig(config);
    


