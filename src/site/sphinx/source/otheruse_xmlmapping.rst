--------------------------------------------------------
XMLファイルによるマッピング
--------------------------------------------------------


アノテーションだけではなく、外部XMLファイルでマッピングを行うことも可能です。
これはダイナミック・アノテーションという、アノテーションと同様の情報をXMLファイルで定義することで行います。

以下にクラスに対してアノテーションを付与するXMLファイルの例を示します。

.. sourcecode:: xml
    
    <?xml version="1.0" encoding="utf-8"?>
    <annotations>
        <class name="com.gh.mygreen.xlsmapper.example.SheetObject">
            <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsSheet">
                <attribute name="name">'Users'</attribute>
            </annotation>
        </class>
    </annotations>


アノテーションの属性値の指定にはOGNL式を使用します。メソッドにアノテーションを付与する場合は次のようになります。

.. sourcecode:: xml
    
    <?xml version="1.0" encoding="utf-8"?>
    <annotations>
        <class name="com.gh.mygreen.xlsmapper.example.SheetObject">
            <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsSheet">
                <attribute name="name">'Users'</attribute>
            </annotation>
            <method name="setTitle">
              <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell">
                  <attribute name="label">'Title'</attribute>
                <attribute name="type">@com.gh.mygreen.xlsmapper.annotation.XlsLabelledCellType@Right</attribute>
              </annotation>
            </method>
        </class>
    </annotations>



XlsMapperでは使用しませんが、フィールドにアノテーションを付与することも可能です。

.. sourcecode:: xml
    
    <?xml version="1.0" encoding="utf-8"?>
    <annotations>
        <class name="com.gh.mygreen.xlsmapper.example.SheetObject">
            <field name="setTitle">
              <annotation name="...">
                  ...
              </annotation>
            </field>
        </class>
    </annotations>


外部XMLファイルを使う場合、ハードコードされたアノテーションを外部XMLファイルの内容でオーバーライドすることが可能です。
読み込み時は以下のようにExcelファイルとXMLファイルの両方を ``XMLBeans#load(..)`` メソッドに渡します。

.. sourcecode:: java
    
    SheetObject sheet = new XlsMapper().load(
        new FileInputStream("example.xls"),
        SheetObject.class,
        new FileInputStream("example.xml"));


なお、``AnnotationReader`` クラスを使用することで、XlsMapperのダイナミック・アノテーション機能を別のプログラムでも利用することが可能です。

.. sourcecode:: java
    
    // XMLファイルの読み込み
    XMLInfo xmlInfo = XMLLoader.load(new File("example.xml"), "UTF-8");
    
    // AnnotationReaderのインスタンスを作成
    AnnotationReader reader = new AnnotationReader(xmlInfo);
    
    // SheetObjectクラスに付与されたSheetアノテーションを取得
    Sheet sheet = reader.getAnnotation(SheetObject.class, Sheet.class);


ClassやMethod、Fieldオブジェクトから直接アノテーションを取得する代わりに ``AnnotationReader`` を使えば、
XMLで宣言されたアノテーションと、クラスに埋め込まれているアノテーションを区別せずに取得することができます。
``AnnotationReader`` にはこの他にもメソッド、フィールドに付与されたアノテーションを取得するためのメソッドも用意されています。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
アノテーションをXMLで上書きする場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

XMLに定義していないメソッドなどは、Javaのソースコードの定義が有効になります。
しかし、XMLにメソッドを定義すると、そのメソッドに対してはXMLの定義が優先されます。

例えば、1つのメソッドにアノテーションを3つ定義していた場合、1つのアノテーションの定義を変更したい場合でも、XMLでは3つのアノテーションの定義を行う必要があります。

このように、一部のアノテーションのみを書き換えたい場合、属性 ``override=true`` を付与すると、差分が反映されます。

.. note::
   
   * 属性 ``override`` は、ver1.0から有効です。
   * 属性 ``override=true`` の場合は、Javaのソースコードの定義に定義している一部のアノテーションを書き換えるために利用します。
   * Javaのソースコード側の定義を削除する場合は、従来通り、属性 ``override`` を定義しない、または ``orverride=false`` を定義し、必要なアノテーションの定義をします。


.. sourcecode:: xml
    
    <?xml version="1.0" encoding="UTF-8"?>
    <annotations>
        
        <!-- クラスに定義したアノテーションを上書きする場合 -->
        <class name="com.gh.mygreen.xlsmapper.example.SheetObject" override="true">
            <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsSheet">
                <attribute name="name">''</attribute>
                <attribute name="regex">'リスト.+'</attribute>
            </annotation>
            
            <!-- フィールドに定義したアノテーションを一部、上書きする場合 -->
            <field name="name" override="true">
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell">
                    <attribute name="label">'クラス名'</attribute>
                    <attribute name="type">@com.gh.mygreen.xlsmapper.annotation.LabelledCellType@Bottom</attribute>
                </annotation>
            </field>
            
            <!-- メソッドに定義したアノテーションを一部、上書きする場合 -->
            <method name="setRecords" override="true">
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords">
                    <attribute name="tableLabel">'名簿一覧'</attribute>
                    <attribute name="terminal">@com.gh.mygreen.xlsmapper.annotation.RecordTerminal@Border</attribute>
                </annotation>
            </method>
            
        </class>
        
    </annotations>


.. sourcecode:: java
    
    @XlsSheet(name="テスト")  // <== 上書きされる
    private static class SheetObject {
        
        @XlsSheetName
        private String sheetName;
        
        @XlsHint(order=1)
        @XlsConverter(trim=true, forceShrinkToFit=true, defaultValue="－")
        @XlsLabelledCell(label="名称", type=LabelledCellType.Right)  // <== 上書きされる
        private String name;
        
        private List<NormalRecord> records;
        
        public List<NormalRecord> getRecords() {
            return records;
        }
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="クラス名", terminal=RecordTerminal.Empty)  // <== 上書きされる
        public void setRecords(List<NormalRecord> records) {
            this.records = records;
        }
        
    }

