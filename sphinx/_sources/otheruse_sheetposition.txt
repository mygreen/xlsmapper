------------------------------------------
シート上の位置情報の取得
------------------------------------------

読み込み時、書き込み時にマッピングしたセルのアドレス取得することができます。

取得方法は複数ありますが、``Map<String, Point> positions`` フィールドを用いるのが記述量が少なく簡単だと思います。
 
 
 1. `Map\<String, Point\> positions` というフィールドを定義しておくとプロパティ名をキーにセルの位置がセットされるようになっています。
 
   * アノテーション ``@XlsMapColumns`` のセルの位置情報のキーは、 *プロパティ名+['セルの見出し']* としてセットされます。
 
 2. アノテーションを付与した *setterメソッド名+Position* というメソッドを用意しておくと、引数にセルの位置が渡されます。
 
   * 位置情報を取得用のsetterメソッドは以下のいずれかの引数を取る必要があります。
    
     * int x, int y
     
     * java.awt.Point
     
   * ただし、``@XlsMapColumns`` に対するsetterメソッドは、第一引数にセルの見出しが必要になります。
   
     * String key, int x, int y
     
     * String key, java.awt.Point
     
 3. アノテーションを付与した *フィールド名+Position* という ``java.awt.Point`` 型のフィールドを用意しておくと、セルの位置が渡されます。
 
   * ただし、``@XlsMapColumns`` に対するフィールドは、``Map<String, Point>`` 型にする必要があります。キーには見出しが入ります。
 
.. sourcecode:: java
    
    @XlsLabelledCell(label="Name")
    public void setName(String name){
        ...
    }
    
    // 汎用的な位置定義
    public Map<String, Point> positions;
    
    // positionsフィールドが定義されている場合は、setter メソッドは必要ありません。
    public void setNamePosition(int x, int y){
        ...
    }


.. note::
   
   フィールド ``Map<String, Point> positions`` と対応するsetterメソッドやフィールドをそれぞれ定義していた場合、
   優先度 *positions > setterメソッド > フィールド* に従い設定されます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時の位置情報取得の注意点
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時は、行を追加/削除する処理がある場合、アノテーションを付与したプロパティの処理順序によって、セルの位置が正しく取得できない場合があります。

例えば、既に処理して取得したセルの位置情報よりも、上方の表で後から行を削除、追加したとき、取得済みのセルの位置情報が不正になります。

このような場合、アノテーション ``@XlsHint`` をプロパティに付与して、処理順序を一定に保ちます。


.. sourcecode:: java
    
    @XlsSheet(name="情報")
    public Employee {
        // セルの位置情報
        public Map<String, Point> positions;
        
        @XlsHorizontalRecords(tableLabel="履歴", terminal=RecordTerminal.Border,
           overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete // データによって行の追加、削除する設定
        )
        @XlsHint(order=1) // プロパティの処理順序を指定します。
        private List<EmployeeHistory> histories;
        
        @XlsLabelledCell(label="名前")
        @XlsHint(order=2) // プロパティの処理順序を指定します。
        private String name;
        
    }


