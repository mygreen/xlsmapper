======================================
値の検証方法
======================================

--------------------------------------------------------
検証の基本
--------------------------------------------------------


 XlsMapperの検証は、SpringFrameworkのValidation機構に似た方法をとります。
 
* エラー情報は、 ``SheetBindingErrors`` クラスに格納します。

  * SheetBindingErrorsのインスタンスは、メソッド ``XlsMapper#loadDetail(...)`` のように、XlsMapperのメソッド 'xxxDetail()' から取得できます。

* セルの値をJavaオブジェクトへ変換の失敗情報は、読み込み時に自動的に作成され、格納されます。

  * 1つのセルの型変換に失敗しても処理を続行するよう、 :doc:`システム設定 <configuration>` のプロパティ ``continueTypeBindFailure`` を 'true' に設定します。

* 型変換以外の検証は、独自に実装したBeanに対するValidatorにより、値を検証します。

  * 通常は、抽象クラス ``ObjectValidatorSupport`` を継承して作成します。
  * ``@XlsHorizontalRecords`` のようにネストしたBeanの場合、リストの要素のBeanのValidatorを別途用意します。

* エラー情報は、SheetBindingErrorsに、エラーオブジェクト ``ObjectError`` として格納されます。

  * ObjectErrorには、メッセージコードや引数が保持されているため、``SheetErrorFormatter`` を使用して、エラーオブジェクトを文字列に変換します。


.. sourcecode:: java
    :linenos:
    
    XlsMapper xlsMapper = new XlsMapper();
    
    // 型変換エラーが起きても処理を続行するよう設定
    xlsMapper.getConiguration().setContinueTypeBindFailure(true);
    
    // エラー情報を含んだ詳細な戻り値を取得します。
    SheetBindingErrors<Employer> bindingResult = xlsMapper.loadDetail(xlsIn, Employer.class);
    
    // マッピングしたオブジェクトを取得します。
    Employer bean = bindingResult.getTarget();
    
    // BeanのValidatorを実行します。
    EmployerValidator validator = new EmployerValidator();
    validator.validate(bean, errors);
    
    // グループを指定する場合(クラスで指定する)
    // validator.validate(bean, errors, Hint.class);
    
    // 値の検証結果を文字列に変換します。
    if(errors.hasErrors()) {
        SheetErrorFormatter errorFormatter = new SheetErrorFormatter();
        for(ObjectError error : errors.getAllErrors()) {
            String message = errorFormatter.format(error);
        }
    }


--------------------------------------------------------
独自の入力値検証
--------------------------------------------------------

Validatorは、 ``ObjectValidatorSupport`` を継承して作成します。

* Genericsで検証対象のBeanクラスを指定し、validateメソッド内で検証処理の実装を行います。
* 検証対象のフィールドにエラーがあるかどうかは、 `SheetBindingErrors#hasFieldErrors` でチェックできます。
    
  * 型変換エラーがある場合、Beanのプロパティに値がマッピングされていないため、エラーがあるかどうかをチェックします。
    
* フィールドに対するエラーを設定する場合、`SheetBindingErrors#createFieldError("フィールド名", "エラーコード")` で設定します。
  
  * ビルダクラス ``InternalFieldErrorBuilder`` を使って組み立てます。
  
  * Map<String, Points>フィールドでセルのアドレスを保持している場合は、`InternalFieldErrorBuilder#address(...)` でセルのアドレスを指定できます。
  
  * エラー引数は、インデックス形式の配列型と名前付きのマップ型のどちらでも指定できます。名前付きのマップ型の利用をお勧めします。

* Validatoinの実行時には、BeanValdiation(JSR-303)のように、ヒントとなるグループ情報を渡すことがき、もし、値が渡されたら、特定の判定を行ったりもできます。


.. sourcecode:: java
    :linenos:
    
    public class EmployerValidator extends ObjectValidatorSupport<Employer> {
        
        // ネストしたBeanのValidator
        private EmployerHistoryValidator historyValidator;
        
        public EmployerValidator() {
            this.historyValidator = new EmployerHistoryValidator();
        }
        
        @Override
        public void validate(Employer targetObj SheetBindingErrors errors, Class<?>... groups) {
            
            // 型変換などのエラーがない場合、文字長のチェックを行う
            // チェック対象のフィールド名を指定します。
            if(!errors.hasFieldErrors("name")) {
                if(targetObj.getName().length() > 10) {
                
                    // 名前付きの引数、セルのアドレスを渡す場合の指定
                    errors.createFieldError("name", "error.maxLength")
                        .address(targetObj.positions("name"))
                        .variables("max", 10)
                        .buildAndAddError();
                }
            }
            
            // レコードの要素の値の検証
            for(int i=0; i < targetObj.getHistory().size(); i++) {
                // ネストしたBeanの検証の実行
                // パスをネストする。リストの場合はインデックスを指定する。
                errors.pushNestedPath("history", i);
                historyValidator.validate(targetObj.getHistory().get(i), errors);
                // 検証後は、パスを戻す
                errors.popNestedPath();
                
                // パスのネストと戻しは、invokeNestedValidatorで自動的にもできます。
                // invokeNestedValidator(historyValidator, targetObj.getHistory().get(i), errors, "history", i);
            }
            
        }
    }



--------------------------------------------------------
フィールド（プロパティ）の入力値検証
--------------------------------------------------------

フィールドに対する値の検証は、 ``CellField`` クラスを使用することでもできます。

* コンストラクタに検証対象のプロパティ名を指定します。プロパティ名には、ネストしたもの、配列・リストやマップの要素の指定もできます。

  * ドット(.)で繋げることで、階層指定ができます（例: ``person.name`` ）。
  * 括弧([数値])を指定することで、配列またはリストの要素が指定できます(例: ``list[0]`` )。
  * 括弧([キー名])を指定することで、マップの値が指定できます(例: ``map[abc]`` )。
  * 組み合わせることもできます（例: ``data[0][abc].name`` ）。
  
* フィールドに対する検証を `CellField#add(...)` で追加することで複数の検証を設定できます。
* 値の件所を行う場合は、 `CellField#validate(errors)` で実行します。

  * SheetBindingErrorsに対してエラーオブジェクトが自動的に設定されます。
   
* フィールドに対してエラーがある場合、 `CellField#hasErrors(...)/hasNotErrors(...)` で検証できます。
 

.. sourcecode:: java
    :linenos:
    
    public class EmployerHistoryValidator extends ObjectValidatorSupport<EmployerHistory> {
        
        @Override
        public void validate(EmployerHistory targetObj, SheetBindingErrors errors, Class<?>... groups) {
            
            // プロパティ historyDate に対するフィールドの組み立てと値の検証
            final CellField<Date> historyDateField = new CellField<Date>("historyDate", errors);
            historyDateField.setRequired(true)
                .add(new MinValidator<Date>(new Date(), "yyyy-MM-dd"))
                .validate(groups);
            
            
            // プロパティ comment に対するフィールドの組み立てと値の検証
            final CellField<String> commentField = new CellField<String>("comment", errors);
            commentField.setRequired(false)
                .add(StringValidator.maxLength(5))
                .validate(groups);
            
            // 
            if(historyDateField.hasNotErrors() && commentField.hasNotErrors()) {
                // 項目間のチェックなど
                if(commentField.isInputEmpty()) {
                    errors.createGlobalError("error.01").buildAndAddError();
                }
            }
            
        }
    }


.. note::
    
    アノテーション @XlsLabelledArray や @XlsArrayColumns などを使ってフィールドが配列やリストへにマッピングした値を検証する場合、 
    ``ArrayCellField`` を使用します。 `[ver.2.0+]`
    
    使用方法は、CellFieldと変わりません。


--------------------------------------------------------
メッセージファイルの定義
--------------------------------------------------------


メッセージファイルは、クラスパスのルートに ``SheetValidationMessages.properties`` というプロパティファイルを配置しておくと、自動的に読み込まれます。

* プロパティファイルは、文字コードをUTF-8に設定し、asciiコードへの変換は不要です。 `[ver.2.0+]`

* エラーメッセージは、下記の表「エラーメッセージの一致順」に従い一致したものが用いれます。
  
  * 型変換エラーは、読み込み時に自動的にチェックされ、エラーコードは、 ``cellTypeMismatch`` と決まっています。

* メッセージ中ではEL式を利用することができます。
* メッセージ中の通常の変数は、``{変数名}`` で定義し、EL式は ``${EL式}`` で定義します。
  
  * ただし、EL式のライブラリを依存関係に追加しておく必要があります。
  

.. sourcecode:: properties
    :linenos:
    
    ## メッセージの定義
    ## SheetValidationMessages.properties
    
    # 共通変数
    # {sheetName} : シート名
    # {cellAddress} : セルのアドレス。'A1'などの形式。
    # {label} : フィールドの見出し。
    
    # フィールドエラー
    cellFieldError.patern==[{sheetName}]:${empty label ? '' : label} - {cellAddress}は'書式に一致しませんでした。
    
    # 型変換エラー
    cellTypeMismatch=[{sheetName}]:${empty label ? '' : label} - {cellAddress}の型変換に失敗しました。
    
    # クラスタイプで指定する場合
    cellTypeMismatch.int=[{sheetName}]:${empty label ? '' : label} - {cellAddress}は数値型で指定してください。
    cellTypeMismatch.java.util.Date=[{sheetName}]:${empty label ? '' : label} - {cellAddress}は日付型で指定してください。
    
    # フィールド名で指定する場合
    cellTypeMismatch.updateTime=[{sheetName}]:${empty label ? '' : label} - {cellAddress}は'yyyy/MM/dd'の書式で指定してください。



.. list-table:: エラーメッセージの一致順
   :widths: 10 40 50
   :header-rows: 1
   
   * - 優先順位
     - エラーコードの形式
     - サンプル
   
   * - 1
     - `\<エラーコード\>.\<完全オブジェクト名\>.\<完全パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.com.sample.SampleBean.list[1].address`
   
   * - 2
     - `\<エラーコード\>.\<完全オブジェクト名\>.\<パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.com.sample.SampleBean.list.address`
     
   * - 3
     - `\<エラーコード\>.\<完全オブジェクト名\>.\<フィールド名\>`
     - `cellFieldError.pattern.com.sample.SampleBean.address`
   
   * - 4
     - `\<エラーコード\>.\<オブジェクト名\>.\<完全パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.SampleBean.list[1].address`
   
   * - 5
     - `\<エラーコード\>.\<オブジェクト名\>.\<パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.SampleBean.list.address`
   
   * - 5
     - `\<エラーコード\>.\<オブジェクト名\>.\<フィールド名\>`
     - `cellFieldError.pattern.SampleBean.address`
   
   * - 6
     - `\<エラーコード\>.\<完全パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.list[1].address`
   
   * - 7
     - `\<エラーコード\>.\<パス\>.\<フィールド名\>`
     - `cellFieldError.pattern.list.address`
   
   * - 8
     - `\<エラーコード\>.\<フィールド名\>`
     - `cellFieldError.pattern.address`
   
   * - 9
     - `\<エラーコード\>.\<フィールドのクラスタイプ\>`
     - `cellFieldError.pattern.java.lang.String`
   
   * - 10
     - `\<エラーコード\>`
     - `cellFieldError.pattern`

.. note::
    
    メッセージ中で、セルのアドレス（変数{cellAddress}）、ラベル（変数{label}）を利用したい場合は、
    Beanクラスに位置情報を保持するフィールド ``Map<String, Point> positions`` と
    ラベル情報を保持する ``Map<String, String> labels`` を定義しておく必要があります。


--------------------------------------------------------
メッセージファイルの読み込み方法の変更
--------------------------------------------------------

メッセージファイルは、``java.util.ResourceBundle`` や ``java.util.Properties`` 、またSpringの ``org.springframework.context.MessageSource`` からも取得できます。
設定する場合、``SheetErrorFormatter#setMessageResolver(...)`` で対応するクラスを設定します。

.. list-table:: メッセージファイルのブリッジ用クラス
   :widths: 50 50
   :header-rows: 1
   
   * - XlsMapper提供のクラス
     - メッセージ取得元のクラス
   
   * - com.gh.mygreen.xlsmapper.validation.ResourceBundleMessageResolver
     - java.util.ResourceBundle
   
   * - com.gh.mygreen.xlsmapper.validation.PropertiesMessageResolver
     - java.util.Prperties
   
   * - com.gh.mygreen.xlsmapper.validation.SpringMessageResolver
     - org.springframework.context.MessageSource


.. sourcecode:: java
    
    // SpringのMessageSourceからメッセージを取得する場合
    MessageSource messageSource = /*...*/;
    
    SheetErrorFormatter errorFormatter = new SheetErrorFormatter();
    errorFormatter.setMessageResolver(new SpringMessageResolver(messageSource));


--------------------------------------------------------
Bean Validationを使用した入力値検証
--------------------------------------------------------

 BeanValidation JSR-303(ver.1.0)/JSR-349(ver.1.1)/JSR-380(ver.2.0)を利用する場合、ライブラリで用意されている「SheetBeanValidator」を使用します。
 
* BeanValidationの実装として、`Hibernate Validator <http://hibernate.org/validator/>`_ が必要になるため、依存関係に追加します。
  
  * Hibernate Validatorを利用するため、メッセージをカスタマイズしたい場合は、クラスパスのルートに「ValidationMessages.properties」を配置します。
  
* 検証する際には、SheetBeanValidator#validate(...)を実行します。
  
  * Bean Validationの検証結果も、SheetBindingErrorsの形式に変換され格納されます。
  
* メッセー時を出力する場合は、SheetErrorFormatterを使用します。


.. sourcecode:: java
    :linenos:
    
    XlsMapper xlsMapper = new XlsMapper();
    
    // 型変換エラーが起きても処理を続行するよう設定
    xlsMapper.getConiguration().setContinueTypeBindFailure(true);
    
    // シートの読み込み
    SheetBindingErrors<Employer> errors = xlsMapper.loadSheetDetail(new File("./src/test/data/employer.xlsx"), errors);
    
    // Bean Validationによる検証の実行
    SheetBeanValidator validatorAdaptor = new SheetBeanValidator();
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
        <version>6.0.10.Final</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>3.0.1-b10</version>
        <scope>provided</scope>
    </dependency>



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Bean Validationのカスタマイズ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

BeanValidationのメッセージファイルを他のファイルやSpringのMessageSourcesから取得することもできます。

XlsMapperのクラス ``com.gh.mygreen.xlsmapper.validation.beanvalidation.MessageResolverInterpolator`` を利用することで、BeanValidationのメッセージ処理クラスをブリッジすることができます。

上記の「メッセージファイルのブリッジ用クラス」を渡すことができます。

.. sourcecode:: java
    :linenos:
    
    // BeanValidationのValidatorの定義
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.usingContext()
            .messageInterpolator(new MessageInterpolatorAdapter(
                     .new ResourceBundleMessageResolver(), new MessageInterpolator()))
            .getValidator();
    
    // BeanValidationのValidatorを渡す
    SheetBeanValidator sheetValidator = new SheetBeanValidator(validator);
    



Bean Validation1.1から式中にEL式が利用できるようになりましたが、その参照実装であるHibernate Validator5.xでは、EL2.x系を利用し、EL3.xの書式は利用できません。
EL式の処理系をXlsMapperのクラス ``com.gh.mygreen.xlsmapper.validation.MessageInterpolator`` を利用することでEL式の処理系を変更することができます。

XslMapperの ``ExpressionLanguageELImpl`` は、EL3.0のライブラリが読み込まれている場合、3.x系の処理に切り替えます。

.. sourcecode:: java
    :linenos:
    
    // BeanValidatorの式言語の実装を独自のものにする。
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.usingContext()
            .messageInterpolator(new MessageInterpolatorAdapter(
                    // メッセージリソースの取得方法を切り替える
                    new ResourceBundleMessageResolver(ResourceBundle.getBundle("message.OtherElMessages")),
                    
                    // EL式の処理を切り替える
                    new MessageInterpolator(new ExpressionLanguageELImpl())))
            .getValidator();
    
    // BeanValidationのValidatorを渡す
    SheetBeanValidator sheetValidator = new SheetBeanValidator(validator);

