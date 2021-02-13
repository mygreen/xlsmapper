
.. _annotationXlsLabelledComment:

------------------------------------
``@XlsLabelledComment``
------------------------------------

セルの見出し用のラベルセルを指定し、セルのコメントをマッピングします。 `[ver.2.1+]`

フィールドまたはメソッドに対して付与します。

書込み時のコメントの書式の制御は、アノテーション :ref:`@XlsCommentOption <annotationXlsCommentOption>` で指定します。
 
* 属性 ``label`` で、見出しとなるセルの値を指定します。
* 属性 ``optional`` で、見出しとなるセルが見つからない場合に無視するかどうかを指定しできます。


.. figure:: ./_static/LabelledComment.png
   :align: center
   
   LabelledComment


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
    
        @XlsLabelledComment(label="ラベル1")
        private String titleComment;
        
        // ラベルセルが見つからなくても処理を続行する
        @XlsLabelledComment(label="ラベル2"optional=true)
        private String summaryComment;
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルが重複するセルを指定する方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

同じラベルのセルが複数ある場合は、区別するための見出しを属性 ``headerLabel`` で指定します。

属性headerLabelで指定したセルから、label属性で指定したセルを下方向に検索し、最初に見つかった一致するセルをラベルセルとして使用します。


.. figure:: ./_static/LabelledComment_headerLabel.png
   :align: center
   
   LabelledComment(headerLabel)


.. sourcecode:: java
    :linenos:
    
    @XlsSheet(name="Users")
    public class SampleSheet {
        
        @XlsLabelledComment(label="クラス名", type=LabelledCellType.Right,
                headerLabel="アクション")
        private String actionClassNameComment;
        
        @XlsLabelledComment(label="クラス名", type=LabelledCellType.Right,
                headerLabel="アクションフォーム")
        private String formClassNameComment;
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ラベルセルを正規表現、正規化して指定する場合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
また、空白や改行を除去してラベルセルを比較するように設定することも可能です。

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
        @XlsLabelledComment(label="/名前.+/")
        private String classNameComment;
        
    }


