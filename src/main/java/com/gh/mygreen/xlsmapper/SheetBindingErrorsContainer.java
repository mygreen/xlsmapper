package com.gh.mygreen.xlsmapper;

import java.util.ArrayList;
import java.util.List;

import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * 複数のシートを読み込む場合に、{@link SheetBindingErrors}を格納するクラス。
 *
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrorsContainer {
    
    private List<SheetBindingErrors> list;
    
    final String objectName;
    
    final String[] objectNames;
    
    public SheetBindingErrorsContainer(final String objectName) {
        ArgUtils.notEmpty(objectName, "objectName");
        this.objectName = objectName;
        this.objectNames = null;
        this.list = new ArrayList<SheetBindingErrors>();
    }
    
    public SheetBindingErrorsContainer(final Class<?> clazz) {
        this(clazz.getCanonicalName());
    }
    
    public SheetBindingErrorsContainer(final String[] objectNames) {
        ArgUtils.notEmpty(objectNames, "objectNames");
        this.objectName = null;
        this.objectNames = objectNames;
        this.list = new ArrayList<SheetBindingErrors>();
    }
    
    public SheetBindingErrorsContainer(final Class<?>[] classes) {
        ArgUtils.notEmpty(classes, "classes");
        this.objectName = null;
        this.objectNames = new String[classes.length];
        this.list = new ArrayList<SheetBindingErrors>();
        
        for(int i=0; i < classes.length; i++) {
            this.objectNames[i] = classes[i].getCanonicalName();
        }
    }
    
    /**
     * 引数listの中のindexのインデックス番号の要素を取得する。
     * <p>indexで指定した要素が存在しない場合、インスタンスを作成しリストに追加する。
     * @param index
     * @return
     */
    SheetBindingErrors findBindingResult(final int index) {
        ArgUtils.notMin(index, 0, "index");
        
        if(list.size() > index) {
            return list.get(index);
        }
        
        final SheetBindingErrors errors = new SheetBindingErrors(getObjectName(index));
        list.add(errors);
        
        return errors;
        
    }
    
    private String getObjectName(final int index) {
        if(objectName != null) {
            return objectName;
        } else {
            return objectNames[index];
        }
    }
    
    /**
     * シート名を指定してエラー情報を取得する。
     * @param sheetName  シート名
     * @return
     * @throws IllegalArgumentException {@literal sheetName == null || sheetName.isEmpty() == true}
     * @throws IllegalArgumentException {@literal sheetName not contain.}
     */
    public SheetBindingErrors getBindingResult(final String sheetName) {
        ArgUtils.notEmpty(sheetName, "sheetName");
        
        for(SheetBindingErrors item : list) {
            if(sheetName.equals(item.getSheetName())) {
                return item;
            }
        }
        
        throw new IllegalArgumentException(String.format("not found errors for sheet '%s'.", sheetName));
    }
    
    /**
     * 指定したシート名の情報が存在するかチェックする。
     * @param sheetName シート名
     * @return true: 存在する場合
     * @throws IllegalArgumentException {@literal sheetName == null || sheetName.isEmpty() == true}
     * @throws IllegalArgumentException {@literal sheetName not contain.}
     */
    public boolean containsBindingResult(final String sheetName) {
        ArgUtils.notEmpty(sheetName, "sheetName");
        
        for(SheetBindingErrors item : list) {
            if(sheetName.equals(item.getSheetName())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * シート番号を指定してエラー情報を取得する。
     * @param index シートのインデックス番号(0から始まる)。
     * @return エラー情報。
     * @throws IllegalArgumentException {@literal index < 0}.
     * @throws IllegalArgumentException {@literal index not contain.}
     */
    public SheetBindingErrors getBindingResult(final int index) {
        ArgUtils.notMin(index, 0, "index");
        
        if(list.size() > index) {
            return list.get(index);
        }
        throw new IllegalArgumentException(String.format("out of index number '%d' for errors.", index));
    }
    
    /**
     * 指定したシート番号の情報が存在するかチェックする。
     * @param index シートのインデックス番号(0から始まる)。
     * @return true: 存在する場合
     * @throws IllegalArgumentException {@literal index < 0}.
     * @throws IllegalArgumentException {@literal index not contain.}
     */
    public boolean containsBindingResult(final int index) {
        ArgUtils.notMin(index, 0, "index");
        
        return list.size() > index;
    }
    
}
