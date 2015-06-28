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
                <attribute name="type">@Xlscom.gh.mygreen.xlsmapper.annotation.XlsLabelledCellType@Right</attribute>
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
        new FileInputStream("example.xml"), SheetObject.class);


なお、``AnnotationReader`` クラスを使用することで、XlsMapperのダイナミック・アノテーション機能を別のプログラムでも利用することが可能です。

.. sourcecode:: java
    
    // XMLファイルの読み込み
    XMLInfo info = XMLLoader.load(new FileInputStream("example.xml"));

    // AnnotationReaderのインスタンスを作成
    AnnotationReader reader = new AnnotationReader(info);

    // SheetObjectクラスに付与されたSheetアノテーションを取得
    Sheet sheet = reader.getAnnotation(SheetObject.class, Sheet.class);


ClassやMethod、Fieldオブジェクトから直接アノテーションを取得する代わりに ``AnnotationReader`` を使えば、
XMLで宣言されたアノテーションと、クラスに埋め込まれているアノテーションを区別せずに取得することができます。
``AnnotationReader`` にはこの他にもメソッド、フィールドに付与されたアノテーションを取得するためのメソッドも用意されています。

