
.. _annotationXlsConverter:

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``@XlsConverter``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

独自のクラスタイプの変換処理を指定するためのアノテーションです。

本ライブラリで対応していない型に変換したい時に、属性 ``value`` で独自のセルの値を変換するクラスのファクトリクラスを指定します。

* ファクトリクラスは、インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverterFactory`` を実装します。
* セルの変換用クラスは、インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverter`` を実装する必要があります。

詳細は、 :doc:`CellConverterの拡張 <extension_cellconverter>` を参照してください。


.. sourcecode:: java
    :linenos:
    :caption: 独自のCellConverterの作成
    
    // CellConverterFactoryの定義
    public class CustomCellConverterFactory implements CellConverterFactory<LocalDate> {
    
        @Override
        CustomCellConverter create(FieldAccessor field, Configuration config) {
            // CellConverterのインスタンスの作成
            return new CustomCellConverter();
        }
    
    }
    
    // CellConverterの定義
    public class CustomCellConverter implements CellConverter<LocaleDate> {
    
        // シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
        @Override
        public LocaleDate toObject(final Cell cell)
            throws XlsMapperException {
            //TODO: 実装する
        }
         
        //シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
        @Override
        Cell toCell(LocalDate targetValue, Object targetBean, Sheet sheet, CellPosition address) throws XlsMapperException;
            //TODO: 実装する
         }
    }
    
    // 独自CellConverterの指定
    public class SampleRecord {
    
        // フィールド独自のConveterの設定
        @XlsColumn(columnName="更新日付")}
        @XlsConverter(CustomCellConverterFactory.class)}
        private LocaleDate localeDate;
        
    }


