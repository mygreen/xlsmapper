====================================================
XMLによるマッピング方法
====================================================


アノテーションだけではなく、外部XMLファイルでマッピングを行うことも可能です。
これはダイナミック・アノテーションという、アノテーションと同様の情報をXMLファイルで定義することで行います。


以下にクラスに対してアノテーションを付与するXMLファイルの例を示します。

.. sourcecode:: xml
    :linenos:
    :caption: クラスに対するアノテーションのXMLでの定義方法
    
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
    :linenos:
    :caption: メソッドに対するアノテーションのXMLでの定義方法
    
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


フィールドにアノテーションを付与することも可能です。

.. sourcecode:: xml
    :linenos:
    :caption: フィールドに対するアノテーションのXMLでの定義方法
    
    <?xml version="1.0" encoding="utf-8"?>
    <annotations>
        <class name="com.gh.mygreen.xlsmapper.example.SheetObject">
            <field name="title">
              <annotation name="...">
                  ...
              </annotation>
            </field>
        </class>
    </annotations>


外部XMLファイルを使う場合、ハードコードされたアノテーションを外部XMLファイルの内容でオーバーライドすることが可能です。
XML情報は ``AnnotationMappingInfo`` として読み込み、 ``Configuration#setAnnotationMapping(..)`` メソッドに渡します。

.. sourcecode:: java
    :linenos:
    :caption: XMLによるマッピングの指定方法
    
    // XMLファイルに定義したマッピング情報の読み込み
    AnnotationMappingInfo annotaionMapping = XmlIO.load(new File("example.xml"), "UTF-8");
    
    // システム情報に設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfiguration.setAnnotationMapping(annotaionMapping);
    
    // マッピング
    SheetObject sheet = xlsMapper.load(new FileInputStream("example.xls"), SheetObject.class);


ClassやMethod、Fieldオブジェクトから直接アノテーションを取得する代わりに ``AnnotationReader`` を使えば、
XMLで宣言されたアノテーションと、クラスに埋め込まれているアノテーションを区別せずに取得することができます。
``AnnotationReader`` にはこの他にもメソッド、フィールドに付与されたアノテーションを取得するためのメソッドも用意されています。


----------------------------------------------
アノテーションをXMLで上書きする場合
----------------------------------------------

XMLに定義していないメソッドなどは、Javaのソースコードの定義が有効になります。
しかし、XMLにメソッドを定義すると、そのメソッドに対してはXMLの定義が優先されます。

例えば、1つのメソッドにアノテーションを3つ定義していた場合、1つのアノテーションの定義を変更したい場合でも、XMLでは3つのアノテーションの定義を行う必要があります。

このように、一部のアノテーションのみを書き換えたい場合、属性 ``override=true`` を付与すると、差分が反映されます。

.. note::
   
   * 属性 ``override`` は、ver1.0から有効です。
   * 属性 ``override=true`` の場合は、Javaのソースコードの定義に定義している一部のアノテーションを書き換えるために利用します。
   * Javaのソースコード側の定義を削除する場合は、従来通り、属性 ``override`` を定義しない、または ``orverride=false`` を定義し、必要なアノテーションの定義をします。


.. sourcecode:: xml
    :linenos:
    
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
    :linenos:
    
    @XlsSheet(name="テスト")  // <== 上書きされる
    private static class SheetObject {
        
        @XlsSheetName
        private String sheetName;
        
        @XlsOrder(1)
        @XlsTrim
        @XlsLabelledCell(label="名称", type=LabelledCellType.Right)  // <== 上書きされる
        private String name;
        
        private List<NormalRecord> records;
        
        public List<NormalRecord> getRecords() {
            return records;
        }
        
        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="クラス名", terminal=RecordTerminal.Empty)  // <== 上書きされる
        public void setRecords(List<NormalRecord> records) {
            this.records = records;
        }
        
    }

.. _xml-build:

----------------------------------------------
XMLを動的に組み立てる場合
----------------------------------------------

アノテーション用のXMLを記述する際に、クラス名やアノテーション名は、FQCN（完全修飾クラス名）で記述する必要があり、間違えることがあります。

また、アノテーションの値はOGNL形式で記述する必要があるため、書式を知らない場合はわざわざ調べる必要があります。

このような時は、XMLをJavaにて動的に組み立てる方法を取ることができます。

XMLを動的に組み立てるには、 各XMLのオブジェクトのビルダクラスである ``XmlInfo.Builder`` などを利用します。
さらに、ヘルパクラスである ``com.gh.mygreen.xlsmapper.xml.XmlBuilder`` を利用すると、より直感的に作成することができます。

* XmlBuilderを、**static import** するとより使い安くなります。
* AnnotationMappingInfoオブジェクトは、``com.gh.mygreen.xlsmapper.xml.XmlIO#save(...)`` メソッドでファイルに保存します。
  
  * 作成した XmlInfoオブジェクトは、JAXBのアノテーションが付与されているため、 **JAXBの機能を使ってXMLに変換** することもできます。
  
* アノテーションの属性値は、``AttributeInfo.Builder#attribute(...)`` メソッドで自動的にOGNLの書式に変換されます。
  
  * OGNL式に変換するクラスは、 ``com.gh.mygreen.xlsmapper.xml.OgnlValueFormatter`` クラスで処理されます。
  
  * 独自にカスタマイズしたクラスで処理したい場合は、予め ``XmlBuilder#setValueFormatter(...)`` メソッドで変更することが可能です。
  
  * 直接OGNLの値を設定したい場合は、``AttributeInfo.Builder#attributeWithNative(...)`` メソッドで設定することもできます。

.. note::
   
   * XmlBuilderクラスなどの、XMLを動的に組み立てる機能は、ver.1.1から追加されたものです。


.. sourcecode:: java
    :linenos:
    
    // XmlBuilder.createXXX() メソッドを簡単に呼ぶために、static import します。
    import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
    
    public void sample() {
        
        AnnotationMappingInfo annotationMapping = createXml()         // ルートオブジェクトのXmlInfo(<annotations>タグ)を組み立てるビルダクラスを作成します。
                .classInfo(createClass(SimpleSheet.class)      // クラス「SimpleSheet」に対するXML情報の組み立てを開始します。
                        .annotation(createAnnotation(XlsSheet.class)  // クラスのアノテーション「@XlsSheet」情報の組み立てを開始します。
                                .attribute("name", "単純なシート")  // アノテーションの属性「name」を設定します。自動的にOGNL形式に変換されます。
                                .buildAnnotation())  // 組み立てたアノテーション情報のオブジェクトを取得します。
                        .field(createField("sheetName")  // フィールド「sheetName」情報の組み立てを開始します。
                                .annotation(createAnnotation(XlsSheetName.class) // フィールドのアノテーション「@XlsSheetName」情報の組み立てを開始します。
                                        .buildAnnotation())
                                .buildField())       // 組み立てたフィールド情報のオブジェクトを取得します。
                        .field(createField("name")
                                .annotation(createAnnotation(XlsLabelledCell.class)
                                        .attribute("label", "名称")
                                        .attributeWithNative("type", "@com.gh.mygreen.xlsmapper.annotation.LabelledCellType@Right") // 直接OGNL式で設定することもできます。
                                        .buildAnnotation())
                                .annotation(createAnnotation(XlsTrim.class)
                                        .buildAnnotation())
                                .annotation(createAnnotation(XlsDefaultValue.class)
                                        .attribute("value", "－")
                                        .buildAnnotation())
                                .buildField())
                        .method(createMethod("setRecords")  // メソッド「setRecords」情報の組み立てを開始します。
                                .annotation(createAnnotation(XlsHorizontalRecords.class)   // メソッドのアノテーションを設定します。
                                        .attribute("tableLabel", "名簿一覧")
                                        .attribute("terminal", RecordTerminal.Border)
                                        .buildAnnotation())
                                .buildMethod())        // 組み立てたメソッド情報のオブジェクトを取得します。
                        .buildClass())  // 組み立てたクラス情報のオブジェクトを取得します。
                .buildXml();  // 組み立てたXML情報のオブジェクトを取得します。
        
        // XMLをファイルに保存します。
        XmlIO.save(annotationMapping, new File("anno_simple.xml"), "UTF-8");
        
    }
    

組み立てたXMLは、下記のようになります。

.. sourcecode:: xml
    :linenos:
    
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <annotations>
        <class name="com.gh.mygreen.xlsmapper.example.SimpleSheet" override="false">
            <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsSheet">
                <attribute name="name">"単純なシート"</attribute>
            </annotation>
            <field name="sheetName" override="false">
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsSheetName"/>
            </field>
            <field name="name" override="false">
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell">
                    <attribute name="label">"名称"</attribute>
                    <attribute name="type">@com.gh.mygreen.xlsmapper.annotation.LabelledCellType@Right</attribute>
                </annotation>
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsTrim" />
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue">
                    <attribute name="value">"－"</attribute>
                </annotation>
            </field>
            <method name="setRecords" override="false">
                <annotation name="com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords">
                    <attribute name="tableLabel">"名簿一覧"</attribute>
                    <attribute name="terminal">@com.gh.mygreen.xlsmapper.annotation.RecordTerminal@Border</attribute>
                </annotation>
            </method>
        </class>
    </annotations>



XMLに変換しないで、直接AnnotationMappingInfoをシステム設定クラスConfigurationに渡すことで、
シート名を設定するアノテーション ``@XlsSheet(name="<シート名>")`` の値を動的に書き換えることが容易にできるようになります。

.. sourcecode:: java
    :linenos:
    
    // XmlBuilder.createXXX() メソッドを簡単に呼ぶために、static import します。
    import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
    
    public void sample() {
        
        AnnotationMappingInfo annotationMapping = createXml()
                .classInfo(createClass(SimpleSheet.class)
                        .override(true)   // アノテーションを差分だけ反映する設定を有効にします。
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "サンプル")
                                .buildAnnotation())
                        .buildClass())
                .buildXml();
        
        // システム設定のConfirgurationに直接渡すこともできます。
        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.getConfiguration.setAnnotationMapping(annotaionMapping);
    
    }



