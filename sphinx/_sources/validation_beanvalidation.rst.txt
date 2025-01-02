--------------------------------------------------------
Bean Validationを使用した入力値検証
--------------------------------------------------------

Bean Validationを利用する際には、ライブリを追加します。 
Mavenを利用している場合は、``pom.xml`` にライブラリを追加します。

* 本ライブラリは、Bean Validation 1.0/1.1/2.0及び、Jakarta Bean Validaiton 3.0/3.1に対応してします。

  * Bean Validation 1.0/1.1/2.0では、 ``com.gh.mygreen.xlsmapper.validation.beanvalidation.SheetBeanValidator`` を使用して値の検証します。
  * Jakarta Bean Validation 3.xでは、 ``com.gh.mygreen.xlsmapper.validation.beanvalidation.JakartaSheetBeanValidator`` を使用して値の検証します。

* Bean Validationの実装として、`Hibernate Validator <http://hibernate.org/validator/>`_ が必要になるため、依存関係に追加します。
  
  * Hibernate Validatorを利用するため、メッセージをカスタマイズしたい場合は、クラスパスのルートに「``ValidationMessages.properties``」を配置します。
  
* 検証する際には、 ``SheetBeanValidator#validate(...)`` / ``JakartaSheetBeanValidator#validate(...)`` を実行します。
  
  * Bean Validationの検証結果も、``SheetBindingErrors`` の形式に変換され格納されます。
  
* メッセー時を出力する場合は、``SheetErrorFormatter`` を使用します。


Hibernate Validatorは対応するBean Validaitonのバージョンが決まっているため、対応したライブラリのバージョンを追加する必要があります。

.. list-table:: Bean ValidationとHibernate Validatorの対応バージョン
   :widths: 20 20 60
   :header-rows: 1
   
   * - Bean Validation
     - Hibernate Validator
     - 備考
     
   * - ver.1.0 (JSR-303)
     - ver.4.x
     - Java8以上で利用可能です。
     
   * - ver.1.1 (JSR-349)
     - ver.5.x
     - Java8以上で利用可能です。

   * - ver.2.0 (JSR-380)
     - ver.6.x
     - Java8以上で利用可能です。

   * - ver.3.0/3.1
     - ver.8.x
     - | **XlsMapper 2.3+** から対応しています。
       | Hibernate Validator v8.xから、**Java11以上** が必須になります。
       | Bean Validaitonから、Jakarta Bean Validationに名称が変更されパッケージも変更されています。


.. sourcecode:: java
    :linenos:
    
    XlsMapper xlsMapper = new XlsMapper();
    
    // 型変換エラーが起きても処理を続行するよう設定
    xlsMapper.getConiguration().setContinueTypeBindFailure(true);
    
    // シートの読み込み
    SheetBindingErrors<Employer> errors = xlsMapper.loadSheetDetail(new File("./src/test/data/employer.xlsx"), errors);
    
    // Bean Validation による検証の実行
    SheetBeanValidator validatorAdaptor = new SheetBeanValidator();

    // Jakarta Bean Validation による検証の実行
    //JakartaSheetBeanValidator validatorAdaptor = new JakartaSheetBeanValidator();

    validatorAdaptor.validate(beanObj, errors);
    
    // 値の検証結果を文字列に変換します。
    if(errors.hasErrors()) {
        SheetErrorFormatter errorFormatter = new SheetErrorFormatter();
        for(ObjectError error : errors.getAllErrors()) {
            String message = errorFormatter.format(error);
        }
    }

.. sourcecode:: xml
    :caption:  Bean Validation 1.1 の依存ライブラリ
    :linenos:
    
    <!-- ====================== Bean Validationのライブラリ ===============-->
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.1.0.Final</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
    <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>5.3.3.Final</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>3.0.1-b10</version>
        <scope>provided</scope>
    </dependency>

.. sourcecode:: xml
    :caption:  Bean Validation 2.0 の依存ライブラリ
    :linenos:
    
    <!-- ====================== Bean Validationのライブラリ ===============-->
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>2.0.1.Final</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
    <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.0.20.Final</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>3.0.1-b10</version>
        <scope>provided</scope>
    </dependency>


Jakarta Bean Validation 3.1を利用する場合は、Hibernate Validator8.x系を利用します。
さらに、メッセージ中にJakarta EEのEL式が利用可能となっているため、その実装であるライブリを追加します。

.. sourcecode:: xml
    :caption: Jakarta Bean Validation 3.1 の依存ライブラリ
    :linenos:
    
    <!-- ====================== Jakarta Bean Validationのライブラリ ===============-->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>8.0.2.Final</version>
    </dependency>
    
    <!-- EL式のライブラリが必要であれば追加します -->
    <dependency>
        <groupId>org.glassfish.expressly</groupId>
        <artifactId>expressly</artifactId>
        <version>5.0.0</version>
    </dependency>


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Bean Validationのカスタマイズ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

BeanValidationのメッセージファイルを他のファイルやSpringのMessageSourcesから取得することもできます。

XlsMapperのクラス ``com.gh.mygreen.xlsmapper.validation.beanvalidation.MessageInterpolatorAdapter`` を利用することで、BeanValidationのメッセージ処理クラスをブリッジできます。

* Jakarta Bean Validaitonの場合は、``com.gh.mygreen.xlsmapper.validation.beanvalidation.JakartaMessageInterpolatorAdapter`` を使用してください。

上記の「メッセージファイルのブリッジ用クラス」を渡すことができます。

.. sourcecode:: java
    :caption:  Bean Validation によるメッセージのカスタマイズ
    :linenos:
    
    // BeanValidationのValidatorの定義
    ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MessageInterpolatorAdapter(
                    .new ResourceBundleMessageResolver(), new MessageInterpolator()))
            .buildValidatorFactory();
    Validator validator = validatorFactory.usingContext()
            .getValidator();
    
    // Bean ValidationのValidatorを渡す
    SheetBeanValidator sheetValidator = new SheetBeanValidator(validator);

Bean Validation1.1から式中にEL式が利用できるようになりましたが、その参照実装であるHibernate Validator5.xでは、EL2.x系を利用し、EL3.xの書式は利用できません。
EL式の処理系をXlsMapperのクラス ``com.gh.mygreen.xlsmapper.validation.MessageInterpolator`` を利用することでEL式の処理系を変更できます。

XslMapperの ``ExpressionLanguageELImpl`` は、EL3.0のライブラリが読み込まれている場合、3.x系の処理に切り替えます。

.. sourcecode:: java
    :linenos:
    
    // BeanValidatorの式言語の実装を独自のものにする。
    ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MessageInterpolatorAdapter(
                    // メッセージリソースの取得方法を切り替える
                    new ResourceBundleMessageResolver(ResourceBundle.getBundle("message.OtherElMessages")),
                    
                    // EL式の処理を切り替える
                    new MessageInterpolator(new ExpressionLanguageELImpl())))
            .buildValidatorFactory();
    Validator validator = validatorFactory.usingContext()
            .getValidator();
    
    // BeanValidationのValidatorを渡す
    SheetBeanValidator sheetValidator = new SheetBeanValidator(validator);

