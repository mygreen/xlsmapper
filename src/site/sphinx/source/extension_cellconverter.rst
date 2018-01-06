--------------------------------------------------------
CellConverterの拡張
--------------------------------------------------------

Excelのセルの値をJavaの任意のクラスにマッピングするには、CellConveterを実装します。

* インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverter`` を実装します。

  * 実際には、ユーティリティメソッドがそろっている、``AbstractCellConverter`` を継承して実装します。
  * 読み込み時、書き込み時のそれぞれのメソッドを実装します。
  * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.cellconvert.converter`` 以下に格納されているクラスを参照してください。

.. sourcecode:: java
    
    public class LocaleDateConverter extends AbstractCellConverter<LocaleDate> {
        
        // シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
        @Override
        public LocaleDate toObject(final Cell cell, final FieldAdaptor adaptor, final Configuration config)
            throws XlsMapperException {
            //TODO: 実装する
        }
        
        //シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
        @Override
        Cell toCell(FieldAdaptor adaptor, LocaleDate targetValue, Sheet sheet, int column, int row, Configuration config) throws XlsMapperException;
            //TODO: 実装する
        }
    }



* よく利用するConverterの場合は、 ``CellConverterRegstry`` に登録します。

  * システム標準の ``CellConverterRegstry`` は、``XlsMapperConfg#getConverterRegistry()`` から取得できます。

.. sourcecode:: java
    
    // 独自のCellConveterの登録
    Configuration config = new Configuration();
    config.getConverterRegistry().registerProcessor(LocaleDate.class, new LocaleDateConverter());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfiguration(config);
    

* 1つのセル専用のConverterの場合は、アノテーションを用いて ``@XlsConverter(converterClass=SampleConverter.class)`` のように指定します。

  * Converterのクラスのインスタンスは、 ``Configuration#createBean(...)`` 経由で作成されます。
  * インスタンスを生成する際に、Spring経由などで作成したい場合は、下記の「BeanFactoryの拡張」を参照してください。

.. sourcecode:: java
    
    public class SampleRecord {
         
         // フィールド独自のConveterの設定
         @XlsColumn(columnName="更新日付")
         @XlsConverter(converterClass=LocaleDateConvereter.class)
         private LocaleDate localeDate;
    }



