--------------------------------------------------------
CellFormatterの拡張
--------------------------------------------------------

POIによるExcelの値を文字列として取得するには、一般的には難しいです。

POIは、数値と日時型はファイル内部では同じdouble型で保持されているため、書式で判断する必要がります。

書式はカスタマイズ可能であり、様々なものが利用できるため、セルのタイプを単純には判定できません。
そこで、XlsMapperでは、セルの値を取得するために、外部のライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ を利用しています。

セルの値を取得する処理を独自の実装に切り替えることができます。

* セルの実装を切り替えるには、インタフェース ``com.gh.mygreen.xlsmapper.CellFormatter`` を実装したものを、Configuration に渡します。

* 標準では、``com.gh.mygreen.xlsmapper.DefaultCellFormatter`` が設定されています。

.. sourcecode:: java
    
    Configuration config = new Configuration();
    
    // 独自の処理系に変更する。
    config.setCellFormatter(new CustomCellFormatter());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfiguration(config);
    



