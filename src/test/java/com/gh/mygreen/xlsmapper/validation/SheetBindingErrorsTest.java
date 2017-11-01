package com.gh.mygreen.xlsmapper.validation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link SheetBindingErrors}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrorsTest {
    
    /**
     * テスト用のオブジェクト
     *
     */
    private static class SampleSheet {
        
        List<Nested> list;
        
        Map<String, Nested> map;
        
        private static class Nested {
            
            Integer f01;
            
            String f02;
            
            Nested(Integer f01, String f02) {
                this.f01 = f01;
                this.f02 = f02;
            }
        }
    }
    
    
    @Test
    public void test_setNestedPath() throws Exception {
        
        SampleSheet sheet = new SampleSheet();
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        
        errors.setNestedPath("sheet");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        errors.setNestedPath("sheet.list");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet.list");
        
        errors.setRootPath();
        assertThat(errors.getCurrentPath()).isEqualTo("");
        
        // 特殊なパターン
        errors.setNestedPath(".");
        assertThat(errors.getCurrentPath()).isEqualTo("");
        
        errors.setNestedPath("");
        assertThat(errors.getCurrentPath()).isEqualTo("");
        
        errors.setNestedPath("sheet.");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        errors.setNestedPath("sheet.list.");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet.list");
        
        
    }
    
    @Test
    public void test_pushNestedPath_and_popNestedPath() throws Exception {
        
        SampleSheet sheet = new SampleSheet();
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        
        // 通常の場合
        errors.pushNestedPath("sheet");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        errors.pushNestedPath("list");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet.list");
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath()).isEqualTo("");
        
        // インデックス指定の場合
        errors.pushNestedPath("sheet");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        errors.pushNestedPath("list", 0);
        assertThat(errors.getCurrentPath()).isEqualTo("sheet.list[0]");
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath()).isEqualTo("sheet");
        
        // キー指定の場合
        errors.pushNestedPath("map", "code01");
        assertThat(errors.getCurrentPath()).isEqualTo("sheet.map[code01]");
        
        // エラーの場合 - ネストするパスの指定が空の場合
        assertThatThrownBy(() -> {
            errors.setRootPath();
            errors.pushNestedPath(".");
            
        }).isInstanceOf(IllegalArgumentException.class);
        
        // エラーの場合 - ネストするパスの指定が空の場合
        assertThatThrownBy(() -> {
            errors.setRootPath();
            errors.pushNestedPath(null);
            
        }).isInstanceOf(IllegalArgumentException.class);
        
        // エラーの場合 - 取り出すパスがそれ以上内場合
        assertThatThrownBy(() -> {
            errors.setRootPath();
            errors.popNestedPath();
            
        }).isInstanceOf(IllegalStateException.class);
                
    }
    
    @Test
    public void test_addError_and_getError() {
        
        // オブジェクトデータの作成
        SampleSheet sheet = new SampleSheet();
        sheet.list = new ArrayList<>();
        sheet.list.add(new SampleSheet.Nested(1, "list01"));
        sheet.list.add(new SampleSheet.Nested(2, "list02"));
        
        sheet.map = new HashMap<>();
        sheet.map.put("a01", new SampleSheet.Nested(1, "map01"));
        sheet.map.put("a02", new SampleSheet.Nested(2, "map02"));
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        
        assertThat(errors.getObjectName()).isEqualTo(SampleSheet.class.getCanonicalName());
        assertThat(errors.hasErrors()).isFalse();
        assertThat(errors.getFirstGlobalError()).isEmpty();
        assertThat(errors.getFirstFieldError()).isEmpty();
        
        errors.addError(new ObjectErrorBuilder(errors.getObjectName(), new String[]{"obj01"}).build());
        errors.addError(new ObjectErrorBuilder(errors.getObjectName(), new String[]{"obj02"}).build());
        
        errors.addError(new FieldErrorBuilder(errors.getObjectName(), "list[0].f01", new String[]{"obj03"}).build());
        errors.addError(new FieldErrorBuilder(errors.getObjectName(), "list[0].f02", new String[]{"obj03"}).build());
        errors.addError(new FieldErrorBuilder(errors.getObjectName(), "list[1].f01", new String[]{"obj03"}).build());
        errors.addError(new FieldErrorBuilder(errors.getObjectName(), "map[a01].f01", new String[]{"obj04"}).build());
        
        assertThat(errors.hasGlobalErrors()).isTrue();
        assertThat(errors.getGlobalErrorCount()).isEqualTo(2);
        
        {
            ObjectError objError = errors.getFirstGlobalError().get();
            assertThat(objError.getObjectName()).isEqualTo(SampleSheet.class.getCanonicalName());
        }
        
        assertThat(errors.hasFieldErrors()).isTrue();
        assertThat(errors.getFieldErrorCount()).isEqualTo(4);
        
        assertThat(errors.hasFieldErrors("list[0].f01")).isTrue();
        assertThat(errors.getFieldErrorCount("list[0].f01")).isEqualTo(1);
        
        assertThat(errors.hasFieldErrors("list[0].*")).isTrue();
        assertThat(errors.getFieldErrorCount("list[0].*")).isEqualTo(2);
        
        {
            FieldError fieldError = errors.getFirstFieldError("list[0].*").get();
            assertThat(fieldError.getField()).isEqualTo("list[0].f01");
        }
        
        {
            FieldError fieldError = errors.getFirstFieldError().get();
            assertThat(fieldError.getField()).isEqualTo("list[0].f01");
        
        }
        
        assertThat(errors.hasFieldErrors("list*")).isTrue();
        assertThat(errors.getFieldErrorCount("list*")).isEqualTo(3);
        
        // リセット
        errors.clearAllErrors();
        assertThat(errors.hasErrors()).isFalse();
        

    }
    
    /**
     * シート、セルなどの情報のエラーのテスト
     */
    @Test
    public void test_Sheet_addError_and_getError() {
        
        String sheetName = "名簿シート";
        
        // オブジェクトデータの作成
        SampleSheet sheet = new SampleSheet();
        sheet.list = new ArrayList<>();
        sheet.list.add(new SampleSheet.Nested(1, "list01"));
        sheet.list.add(new SampleSheet.Nested(2, "list02"));
        
        sheet.map = new HashMap<>();
        sheet.map.put("a01", new SampleSheet.Nested(1, "map01"));
        sheet.map.put("a02", new SampleSheet.Nested(2, "map02"));
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName(sheetName);
        
        assertThat(errors.getObjectName()).isEqualTo(SampleSheet.class.getCanonicalName());
        assertThat(errors.hasErrors()).isFalse();
        assertThat(errors.getFirstGlobalError()).isEmpty();
        assertThat(errors.getFirstFieldError()).isEmpty();
        assertThat(errors.getSheetName()).isEqualTo(sheetName);
        
        errors.createGlobalError("obj01")
            .buildAndAddError()
            .createGlobalError("obj02")
            .buildAndAddError();
        
        errors.createFieldError("list[0].f01", "obj03").address(CellPosition.of("A2"))
            .buildAndAddError()
            .createFieldError("list[0].f02", "obj03").address(CellPosition.of("B2"))
            .buildAndAddError()
            .createFieldError("list[1].f01", "obj03").address(CellPosition.of("A3"))
            .buildAndAddError()
            .createFieldError("map[a01].f01", "obj04").address(CellPosition.of("D3"))
            .buildAndAddError()
            ;
        
        assertThat(errors.hasGlobalErrors()).isTrue();
        assertThat(errors.getGlobalErrorCount()).isEqualTo(2);
        
        
        {
            ObjectError objError = errors.getFirstGlobalError().get();
            assertThat(objError.getObjectName()).isEqualTo(SampleSheet.class.getCanonicalName());
            assertThat(objError.getSheetName().get()).isEqualTo(sheetName);
        }
        
        assertThat(errors.hasFieldErrors()).isTrue();
        assertThat(errors.getFieldErrorCount()).isEqualTo(4);
        
        assertThat(errors.hasFieldErrors("list[0].f01")).isTrue();
        assertThat(errors.getFieldErrorCount("list[0].f01")).isEqualTo(1);
        
        {
            // フィールドパスを指定してエラーを取得
            FieldError fieldError = errors.getFirstFieldError("list[0].f01").get();
            assertThat(fieldError.getObjectName()).isEqualTo(SampleSheet.class.getCanonicalName());
            assertThat(fieldError.getSheetName().get()).isEqualTo(sheetName);
            assertThat(fieldError.getField()).isEqualTo("list[0].f01");
            assertThat(fieldError.getAddress().toString()).isEqualTo("A2");
            
            assertThat(fieldError.getRejectedValue()).isEqualTo(1);
        }
        
        assertThat(errors.hasFieldErrors("list[0].*")).isTrue();
        assertThat(errors.getFieldErrorCount("list[0].*")).isEqualTo(2);
        
        {
            // 先頭のフィールド情報を取得
            FieldError fieldError = errors.getFirstFieldError("list[0].*").get();
            assertThat(fieldError.getField()).isEqualTo("list[0].f01");
        }
        
        assertThat(errors.hasFieldErrors("list*")).isTrue();
        assertThat(errors.getFieldErrorCount("list*")).isEqualTo(3);
        
        // リセット
        errors.clearAllErrors();
        assertThat(errors.hasErrors()).isFalse();
        
    }
    
//    
//    
//    /**
//     * フィールドの指定
//     * ・バインドエラー
//     */
//    @Test
//    public void test_rejectTypeBind() {
//        
//        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
//        errors.setSheetName("名簿用シート");
//        
//        errors.rejectTypeBind("name01[0]", "山田太郎", String.class);
//        errors.rejectTypeBind("name01[2]", "山田次郎", String.class, new HashMap<String, Object>());
//        
//        assertThat(errors.hasGlobalErrors(), is(false));
//        
//        assertThat(errors.hasFieldErrors(), is(true));
//        assertThat(errors.getFieldErrorCount(), is(2));
//        
//        FieldError fieldError001 = errors.getFirstFieldError("name01*");
//        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
//        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
//        assertThat(fieldError001.getCodes(), is(hasItemInArray("cellTypeMismatch")));
//        assertThat(fieldError001.getFieldValue(), is((Object)"山田太郎"));
//        assertThat(fieldError001.getFieldType(), is(typeCompatibleWith(String.class)));
//    }
//    
//    /**
//     * シートフィールドの指定
//     * ・バインドエラー
//     */
//    @Test
//    public void test_rejectSheetTypeBind() {
//        
//        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
//        errors.setSheetName("名簿用シート");
//        
//        errors.rejectSheetTypeBind("name01[0]", "山田太郎", String.class, CellPosition.of("A2"), "名前01");
//        errors.rejectSheetTypeBind("name01[2]", "山田次郎", String.class, new HashMap<String, Object>(), CellPosition.of("A3"), "名前02");
//        
//        assertThat(errors.hasGlobalErrors(), is(false));
//        
//        assertThat(errors.hasFieldErrors(), is(true));
//        assertThat(errors.getFieldErrorCount(), is(2));
//        
//        assertThat(errors.hasCellFieldErrors(), is(true));
//        assertThat(errors.getCellFieldErrorCount(), is(2));
//        
//        CellFieldError fieldError001 = errors.getFirstCellFieldError("name01*");
//        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
//        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
//        assertThat(fieldError001.getSheetName(), is("名簿用シート"));
//        assertThat(fieldError001.getCellAddress().toPoint(), is(toPointAddress("A2")));
//        assertThat(fieldError001.getLabel(), is("名前01"));
//        assertThat(fieldError001.getCodes(), is(hasItemInArray("cellTypeMismatch")));
//        assertThat(fieldError001.getFieldValue(), is((Object)"山田太郎"));
//        assertThat(fieldError001.getFieldType(), is(typeCompatibleWith(String.class)));
//    }
    
    
}
