
.. _annotationXlsLabelledArrayCells:

--------------------------------------------
``@XlsLabelledArrayCells``
--------------------------------------------

セルの見出し用のラベルセルを指定し、左右もしくは下側に連続し隣接するセルをCollection(List, Set)または配列にマッピングします。 `[ver.2.0+]`

``@XlsArrayCells`` と ``@XlsLabelledCell`` を融合したアノテーションとなります。


* 属性 ``label`` で、見出しとなるセルの値を指定します。
* 属性 ``type`` で、見出しセルから見て値が設定されている位置を指定します。
    
  * 列挙型 ``LabelledCellType`` で、左右もしくは下側のセルを指定できます。
    
* 属性 ``direction`` で、連続する隣接するセルの方向を指定します。

  * 列挙型 ``ArrayDirection`` で、横方向（右方向）もしくは、直方向（下方向）を指定できます。
  * 初期値は、横方向（右方向）です。
  * ただし、セルの位置を左側( ``type=LabelledCellType.Left`` ) とした場合、セルの方向は横方向( ``direction=ArrayDirection.Horizon`` ) は、設定できないため注意してください。

* 属性 ``size`` で、連続するセルの個数を指定します。
  
* 属性 ``optional`` で、見出しとなるセルが見つからない場合に無視するかどうかを指定しできます。

* Collection(List, Set)型または配列のフィールドに付与します。

  * List型などの場合、Genericsのタイプとして、マッピング先のクラスを指定します。
  * 指定しない場合は、アノテーションの属性 ``elementClass`` でクラス型を指定します。



.. figure:: ./_static/LabelledArrayCells.png
   :align: center
   
   LabelledArrayCells


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // ラベルの右側 + 横方向の隣接するセル
        // 属性directionを省略した場合は、ArrayDirection.Horizonを指定したと同じ意味。
        @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=6)
        private List<String> nameKanas1;
        
        // ラベルの下側 + 横方向の隣接するセル
        // 属性optional=trueと設定すると、ラベルセルが見つからなくても処理を続行する
        @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Bottom,
                 direction=ArrayDirection.Horizon, size=6, optional=true)
        private String[] nameKanas2;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
縦方向に隣接したセルをマッピングする場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* 縦方向にマッピングするため、属性 ``direction`` を、``ArrayDirection.Vertical`` に設定します。

.. figure:: ./_static/LabelledArrayCells_direction.png
   :align: center
   
   LabelledArrayCells(direction)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // ラベルの右側 + 縦方向の隣接するセル
        // 属性direction=ArrayDirection.Verticalを指定すると、縦方向にマッピングします。
        @XlsLabelledArrayCells(label="ラベル3", type=LabelledCellType.Right,
                 direction=ArrayDirection.Vertical, size=4)
        private List<String> nameKanas3;
        
        // ラベルの下側 + 縦方向の隣接するセル
        @XlsLabelledArrayCells(label="ラベル4", type=LabelledCellType.Right,
                 direction=ArrayDirection.Vertical, size=4)
        private String[] nameKanas4 nameKanas4;
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルから離れたセルを指定する方法（属性range）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``range`` を指定すると、属性typeの方向に向かって指定した **セル数分を検索** し、最初に発見した空白以外のセルを開始位置としてマッピングします。

* 属性 ``range`` と ``skip`` を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。
* 属性 ``range`` は、 **読み込み時のみ有効** です。書き込み時に指定しても無視されます。
* ラベルセルから離れたセルを指定する場合に使用します。
* ただし、データセルが偶然空白のときは、マッピング対象のセルがずれるため、この属性を使用する場合は注意が必要です。

.. figure:: ./_static/LabelledArrayCells_range.png
   :align: center
   
   LabelledArrayCells(range)

.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, range=4, size=4)
        private List<String> words1;
        
        @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Bottom, range=5, size=3)
        private List<String> words2;
        
    }
    

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルから離れたセルを指定する方法（属性skip）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``skip`` を指定すると、属性typeの方向に向かって、ラベルセルから指定した **セル数分離れた** セルを開始位置としてマッピングします。

* 属性 ``range`` と ``skip`` を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。

.. figure:: ./_static/LabelledArrayCells_skip.png
   :align: center
   
   LabelledArrayCells(skip)

.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=3, skip=2)
        private List<String> words1;
        
         @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Bottom, size=3, skip=3)
        private List<String> words2;
    }
    



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
重複するラベルを指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

同じラベルのセルが複数ある場合は、区別するため見出しを属性 ``headerLabel`` で指定します。 

属性headerLabelで指定したセルから、label属性で指定したセルを下方向に検索し、最初に見つかった一致するセルをラベルセルとして使用します。


.. figure:: ./_static/LabelledArrayCells_headerLabel.png
   :align: center
   
   LabelledArrayCells(headerLabel)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=10
                headerLabel="氏名")
        private List<String> nameRuby;
        
        @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=10
                headerLabel="住所")
        private List<String> addressRuby;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルが結合している場合（属性labelMerged）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* 属性 ``labelMerged`` で、見出しのラベルセルが結合を考慮するか指定します。

  * trueのときは、結合されているセルを1つのラベルセルとしてマッピングします。
  * falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
  
  * 初期値はtrueであるため、特に意識はする必要はありません。

* 属性 ``labelMerged`` の値がfalseのとき、ラベルセルが結合されていると、値が設定されているデータセルまでの距離が変わるため、属性 ``skip`` を併用します。


.. figure:: ./_static/LabelledArrayCells_labelMerged.png
   :align: center
   
   LabelledArrayCells(labelMerged)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // labelMerged=trueは初期値なので、省略可
        @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=6)
        private List<String> name1;
        
        // labelMerged=falseで、ラベルが結合しているときは、skip属性を併用します。
        @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Right, size=6,
                 labelMerged=false, skip=2)
        private List<String> name2;
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
結合したセルをマッピングする場合（属性elementMerged）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* 属性 ``elementMerged`` で、セルの結合を考慮するか指定します。

  * trueのときは、結合されているセルを1つのセルとしてマッピングします。
  * falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
  
    * ただし、falseのときは、書き込む際には結合が解除されます。
  
  * 初期値はtrueであるため、特に意識はする必要はありません。

* セルが結合されている場合は、結合後の個数を指定します。



.. figure:: ./_static/LabelledArrayCells_elementMerged.png
   :align: center
   
   LabelledArrayCells(elementMerged)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // elementMerged=trueは初期値なので、省略可
        @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=3, 
                elementMerged=true)
        private List<String> words;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時に配列・リストのサイズが不足、または余分である場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーション :ref:`@XlsArrayOption <annotationXlsArrayOption>` を指定することで、書き込み時のセルの制御を指定することができます。

* 属性 ``overOperation`` で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 ``size`` の値が小さく、足りない場合の操作を指定します。
* 属性 ``remainedOperation`` で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 ``size`` の値が大きく、余っている場合の操作を指定します。

.. figure:: ./_static/LabelledArrayCells_ArrayOption.png
   :align: center
   
   LabelledArrayCells(ArrayOption)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=6)
        @XlsArrayOption(overOperation=OverOperation.Error, remainedOperation=RemainedOperation.Clear)
        private List<String> nameKana;

    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
位置情報／見出し情報を取得する際の注意事項
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

マッピング対象のセルのアドレスを取得する際に、フィールド ``Map<String, Point> positions`` を定義しておけば、自動的にアドレスがマッピングされます。

通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。

アノテーション ``@XlsLabelledArrayCells`` でマッピングしたセルのキーは、 ``<プロパティ名>[<インデックス>]`` の形式になります。
インデックスは、0から始まります。


同様に、マッピング対象の見出しを取得する、フィールド ``Map<String, String> labels`` へのアクセスも、キーは、 ``<プロパティ名>[<インデックス>]`` の形式になります。
ただし、見出し情報の場合は、全ての要素が同じ値になるため、従来通りの ``<プロパティ名>`` でも取得できます。

.. figure:: ./_static/LabelledArrayCells_positions.png
   :align: center
   
   LabelledArrayCells(positions/labels)


.. sourcecode:: java
    :linenos:
    
    public class SampleRecord {
        
        // 位置情報
        private Map<String, Point> positions;
        
        // 見出し情報
        private Map<String, String> labels;
        
        @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=6)
        private List<String> nameKana;
        
    }
    
    // 位置情報・見出し情報へのアクセス
    SampleRecord record = /* レコードのインスタンスの取得 */;
    
    Point position = record.positions.get("nameKana[2]");
    
    String label = recrod.labeles.get("nameKana[2]");
    
    // 見出し情報の場合、従来通りのインデックスなしでも取得できる
    String label = recrod.labeles.get("nameKana");
    


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルを正規表現、正規化して指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。 `[ver1.1+]`

* 正規表現で指定する場合、アノテーションの属性の値を ``/正規表現/`` のように、スラッシュで囲み指定します。
  
  * スラッシュで囲まない場合、通常の文字列として処理されます。
  
  * 正規表現の指定機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``regexLabelText`` の値を trueに設定します。
  
* ラベセルの値に改行が空白が入っている場合、それらを除去し、正規化してアノテーションの属性値と比較することが可能です。
  
  * 正規化とは、空白、改行、タブを除去することを指します。
   
  * ラベルを正規化する機能を有効にするには、:doc:`システム設定のプロパティ <configuration>` ``normalizeLabelText`` の値を trueに設定します。
  

これらの指定が可能な属性は、``label`` , ``headerLabel`` です。


.. sourcecode:: java
    :linenos:
    
    // システム設定
    XlsMapper xlsMapper = new XlsMapper();
    xlsMapper.getConfiguration()
            .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
            .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
    
    // シート用クラス
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        // 正規表現による指定
        @XlsLabelledArrayCells(label="/名前.+/", type=LabelledCellType.Right, size=10)
        private List<String> names;
        
    }



