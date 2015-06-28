package com.gh.mygreen.xlsmapper.validation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link SheetBindingErrors}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrorsTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_setNestedPath() throws Exception {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        
        errors.setNestedPath("sheet");
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        errors.setNestedPath("sheet.list");
        assertThat(errors.getCurrentPath(), is("sheet.list"));
        
        errors.setRootPath();
        assertThat(errors.getCurrentPath(), is(""));
        
        // 特殊なパターン
        errors.setNestedPath(".");
        assertThat(errors.getCurrentPath(), is(""));
        
        errors.setNestedPath("");
        assertThat(errors.getCurrentPath(), is(""));
        
        errors.setNestedPath("sheet.");
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        errors.setNestedPath("sheet.list.");
        assertThat(errors.getCurrentPath(), is("sheet.list"));
        
        
    }
    
    @Test
    public void test_pushNestedPath_and_popNestedPath() throws Exception {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        
        // 通常の場合
        errors.pushNestedPath("sheet");
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        errors.pushNestedPath("list");
        assertThat(errors.getCurrentPath(), is("sheet.list"));
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath(), is(""));
        
        // インデックス指定の場合
        errors.pushNestedPath("sheet");
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        errors.pushNestedPath("list", 0);
        assertThat(errors.getCurrentPath(), is("sheet.list[0]"));
        
        errors.popNestedPath();
        assertThat(errors.getCurrentPath(), is("sheet"));
        
        // キー指定の場合
        errors.pushNestedPath("map", "code01");
        assertThat(errors.getCurrentPath(), is("sheet.map[code01]"));
        
        // エラーの場合 - ネストするパスの指定が空の場合
        try {
            errors.setRootPath();
            errors.pushNestedPath(".");
            fail();
            
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
        }
        
        // エラーの場合 - ネストするパスの指定が空の場合
        try {
            errors.setRootPath();
            errors.pushNestedPath(null);
            fail();
            
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
        }
        
        // エラーの場合 - 取り出すパスがそれ以上内場合
        try {
            errors.setRootPath();
            errors.popNestedPath();
            fail();
            
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalStateException.class));
        }
        
    }
    
    @Test
    public void test_addError_and_getError() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleObj");
        
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getFirstGlobalError(), is(nullValue()));
        assertThat(errors.getFirstFieldError(), is(nullValue()));
        
        errors.addError(new ObjectError("obj01"));
        errors.addError(new ObjectError("obj02"));
        errors.addError(new FieldError("obj03", "list[0].f01"));
        errors.addError(new FieldError("obj03", "list[0].f02"));
        errors.addError(new FieldError("obj03", "list[1].f01"));
        errors.addError(new FieldError("obj04", "map[a01].f01"));
        
        assertThat(errors.hasGlobalErrors(), is(true));
        assertThat(errors.getGlobalErrorCount(), is(2));
        
        assertThat(errors.hasSheetGlobalErrors(), is(false));
        
        ObjectError objError01 = errors.getFirstGlobalError();
        assertThat(objError01.getObjectName(), is("obj01"));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(4));
        
        assertThat(errors.hasCellFieldErrors(), is(false));
        
        assertThat(errors.hasFieldErrors("list[0].f01"), is(true));
        assertThat(errors.getFieldErrorCount("list[0].f01"), is(1));
        
        assertThat(errors.hasFieldErrors("list[0].*"), is(true));
        assertThat(errors.getFieldErrorCount("list[0].*"), is(2));
        
        FieldError fieldError01 = errors.getFirstFieldError("list[0].*");
        assertThat(fieldError01.getFieldPath(), is("list[0].f01"));
        
        FieldError fieldError02 = errors.getFirstFieldError();
        assertThat(fieldError02.getFieldPath(), is("list[0].f01"));
        
        assertThat(errors.hasFieldErrors("list*"), is(true));
        assertThat(errors.getFieldErrorCount("list*"), is(3));
        
        // リセット
        errors.clearAllErrors();
        assertThat(errors.hasErrors(), is(false));
        

    }
    
    /**
     * シート、セルなどの情報のエラーのテスト
     */
    @Test
    public void test_Sheet_addError_and_getError() {
        
        String sheetName = "名簿シート";
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName(sheetName);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getFirstSheetGlobalError(), is(nullValue()));
        assertThat(errors.getFirstCellFieldError(), is(nullValue()));
        assertThat(errors.getSheetName(), is(sheetName));
        
        errors.addError(new SheetObjectError("obj01", sheetName));
        errors.addError(new SheetObjectError("obj02", sheetName));
        errors.addError(new CellFieldError("obj03", "list[0].f01", sheetName, toPointAddress("A2")));
        errors.addError(new CellFieldError("obj03", "list[0].f02", sheetName, toPointAddress("B2")));
        errors.addError(new CellFieldError("obj03", "list[1].f01", sheetName, toPointAddress("A3")));
        errors.addError(new CellFieldError("obj04", "map[a01].f01", sheetName, toPointAddress("D3")));
        
        assertThat(errors.hasSheetGlobalErrors(), is(true));
        assertThat(errors.getSheetGlobalErrorCount(), is(2));
        
        SheetObjectError objError01 = errors.getFirstSheetGlobalError();
        assertThat(objError01.getObjectName(), is("obj01"));
        assertThat(objError01.getSheetName(), is(sheetName));
        
        assertThat(errors.hasCellFieldErrors(), is(true));
        assertThat(errors.getCellFieldErrorCount(), is(4));
        
        assertThat(errors.hasCellFieldErrors("list[0].f01"), is(true));
        assertThat(errors.getCellFieldErrorCount("list[0].f01"), is(1));
        
        assertThat(errors.hasCellFieldErrors("list[0].*"), is(true));
        assertThat(errors.getCellFieldErrorCount("list[0].*"), is(2));
        
        CellFieldError fieldError01 = errors.getFirstCellFieldError("list[0].*");
        assertThat(fieldError01.getFieldPath(), is("list[0].f01"));
        assertThat(fieldError01.getSheetName(), is(sheetName));
        assertThat(fieldError01.getCellAddress(), is(toPointAddress("A2")));
        
        CellFieldError fieldError02 = errors.getFirstCellFieldError();
        assertThat(fieldError02.getFieldPath(), is("list[0].f01"));
        assertThat(fieldError01.getSheetName(), is(sheetName));
        assertThat(fieldError01.getCellAddress(), is(toPointAddress("A2")));
        
        assertThat(errors.hasCellFieldErrors("list*"), is(true));
        assertThat(errors.getCellFieldErrorCount("list*"), is(3));
        
        // リセット
        errors.clearAllErrors();
        assertThat(errors.hasErrors(), is(false));
        
    }
    
    /**
     * オブジェクトエラーの追加
     */
    @Test
    public void test_reject() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        
        errors.reject("error001");
        errors.reject("error002", "error02.default");
        
        errors.reject("error003", new HashMap<String, Object>());
        errors.reject("error004", new HashMap<String, Object>(), "error004.default");
        
        errors.reject("error005", new Object[]{});
        errors.reject("error006", new Object[]{}, "error006.default");
        
        assertThat(errors.hasGlobalErrors(), is(true));
        assertThat(errors.getGlobalErrorCount(), is(6));
        
        assertThat(errors.hasSheetGlobalErrors(), is(false));
        assertThat(errors.getSheetGlobalErrorCount(), is(0));
        
        ObjectError objError001 = errors.getFirstGlobalError();
        assertThat(objError001.getObjectName(), is("SampleSheet"));
        assertThat(objError001.getCodes(), is(hasItemInArray("error001")));
        
    }
    
    /**
     * シートのオブジェクトエラーの追加
     */
    @Test
    public void test_rejectSheet() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName("名簿用シート");
        
        errors.rejectSheet("error001");
        errors.rejectSheet("error002", "error02.default");
        
        errors.rejectSheet("error003", new HashMap<String, Object>());
        errors.rejectSheet("error004", new HashMap<String, Object>(), "error004.default");
        
        errors.rejectSheet("error005", new Object[]{});
        errors.rejectSheet("error006", new Object[]{}, "error006.default");
        
        assertThat(errors.hasGlobalErrors(), is(true));
        assertThat(errors.getGlobalErrorCount(), is(6));
        
        assertThat(errors.hasSheetGlobalErrors(), is(true));
        assertThat(errors.getSheetGlobalErrorCount(), is(6));
        
        SheetObjectError objError001 = errors.getFirstSheetGlobalError();
        assertThat(objError001.getObjectName(), is("SampleSheet"));
        assertThat(objError001.getCodes(), is(hasItemInArray("error001")));
        assertThat(objError001.getSheetName(), is("名簿用シート"));        
    }
    
    /**
     * フィールドエラーの追加
     */
    @Test
    public void test_rejectValue() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        
        errors.rejectValue("name01[0]", "error001");
        errors.rejectValue("name01[1]", "error002", "error02.default");
        
        errors.rejectValue("name02[1]", "error003", new HashMap<String, Object>());
        errors.rejectValue("name02[2]", "error004", new HashMap<String, Object>(), "error03.default");
        
        errors.rejectValue("family[mother].age", "error005", new Object[]{});
        errors.rejectValue("family[father].age", "error006", new Object[]{}, "error06.default");
        
        assertThat(errors.hasGlobalErrors(), is(false));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(6));
        
        assertThat(errors.getFieldErrorCount("name*"), is(4));
        assertThat(errors.getFieldErrorCount("name01*"), is(2));
        
        
        FieldError fieldError001 = errors.getFirstFieldError("name01*");
        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
        assertThat(fieldError001.getCodes(), is(hasItemInArray("error001")));
        
        
    }
    
    /**
     * シートのフィールドエラーの追加
     */
    @Test
    public void test_rejectSheetValue() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName("名簿用シート");
        
        errors.rejectSheetValue("name01[0]", toPointAddress("A2"), "error001");
        errors.rejectSheetValue("name01[1]", toPointAddress("A3"), "error002", "error02.default");
        
        errors.rejectSheetValue("name02[1]", toPointAddress("B2"), "error003", new HashMap<String, Object>());
        errors.rejectSheetValue("name02[2]", toPointAddress("B2"), "error004", new HashMap<String, Object>(), "error03.default");
        
        errors.rejectSheetValue("family[mother].age", toPointAddress("C2"), "error005", new Object[]{});
        errors.rejectSheetValue("family[father].age", toPointAddress("C3"), "error006", new Object[]{}, "error06.default");
        
        assertThat(errors.hasGlobalErrors(), is(false));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(6));
        
        assertThat(errors.hasCellFieldErrors(), is(true));
        assertThat(errors.getCellFieldErrorCount(), is(6));
        
        assertThat(errors.getCellFieldErrorCount("name*"), is(4));
        assertThat(errors.getCellFieldErrorCount("name01*"), is(2));
        
        
        CellFieldError fieldError001 = errors.getFirstCellFieldError("name01*");
        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
        assertThat(fieldError001.getCodes(), is(hasItemInArray("error001")));
        assertThat(fieldError001.getSheetName(), is("名簿用シート"));
        assertThat(fieldError001.getCellAddress(), is(toPointAddress("A2")));
        
        
    }
    
    /**
     * シートのフィールドエラーの追加
     * ・フィールドの値の指定
     */
    @Test
    public void test_rejectSheetValue2() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName("名簿用シート");
        
        errors.rejectSheetValue("name01[0]", "山田太郎", String.class, toPointAddress("A2"), "error001");
        errors.rejectSheetValue("name01[1]", "山田次郎", String.class, toPointAddress("A3"), "error002", "error02.default");
        
        errors.rejectSheetValue("name02[1]", "鈴木一郎", String.class, toPointAddress("B2"), "error003", new HashMap<String, Object>());
        errors.rejectSheetValue("name02[2]", "鈴木次郎", String.class, toPointAddress("B2"), "error004", new HashMap<String, Object>(), "error03.default");
        
        errors.rejectSheetValue("family[mother].age", 40, Integer.class, toPointAddress("C2"), "error005", new Object[]{});
        errors.rejectSheetValue("family[father].age", 50, Integer.class, toPointAddress("C3"), "error006", new Object[]{}, "error06.default");
        
        assertThat(errors.hasGlobalErrors(), is(false));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(6));
        
        assertThat(errors.hasCellFieldErrors(), is(true));
        assertThat(errors.getCellFieldErrorCount(), is(6));
        
        assertThat(errors.getCellFieldErrorCount("name*"), is(4));
        assertThat(errors.getCellFieldErrorCount("name01*"), is(2));
        
        
        CellFieldError fieldError001 = errors.getFirstCellFieldError("name01*");
        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
        assertThat(fieldError001.getCodes(), is(hasItemInArray("error001")));
        assertThat(fieldError001.getSheetName(), is("名簿用シート"));
        assertThat(fieldError001.getCellAddress(), is(toPointAddress("A2")));
        assertThat(fieldError001.getFieldValue(), is((Object)"山田太郎"));
        assertThat(fieldError001.getFieldType(), is(typeCompatibleWith(String.class)));
        
        
    }
    
    /**
     * フィールドの指定
     * ・バインドエラー
     */
    @Test
    public void test_rejectTypeBind() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName("名簿用シート");
        
        errors.rejectTypeBind("name01[0]", "山田太郎", String.class);
        errors.rejectTypeBind("name01[2]", "山田次郎", String.class, new HashMap<String, Object>());
        
        assertThat(errors.hasGlobalErrors(), is(false));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(2));
        
        FieldError fieldError001 = errors.getFirstFieldError("name01*");
        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
        assertThat(fieldError001.getCodes(), is(hasItemInArray("cellTypeMismatch")));
        assertThat(fieldError001.getFieldValue(), is((Object)"山田太郎"));
        assertThat(fieldError001.getFieldType(), is(typeCompatibleWith(String.class)));
    }
    
    /**
     * シートフィールドの指定
     * ・バインドエラー
     */
    @Test
    public void test_rejectSheetTypeBind() {
        
        SheetBindingErrors errors = new SheetBindingErrors("SampleSheet");
        errors.setSheetName("名簿用シート");
        
        errors.rejectSheetTypeBind("name01[0]", "山田太郎", String.class, toPointAddress("A2"), "名前01");
        errors.rejectSheetTypeBind("name01[2]", "山田次郎", String.class, new HashMap<String, Object>(), toPointAddress("A3"), "名前02");
        
        assertThat(errors.hasGlobalErrors(), is(false));
        
        assertThat(errors.hasFieldErrors(), is(true));
        assertThat(errors.getFieldErrorCount(), is(2));
        
        assertThat(errors.hasCellFieldErrors(), is(true));
        assertThat(errors.getCellFieldErrorCount(), is(2));
        
        CellFieldError fieldError001 = errors.getFirstCellFieldError("name01*");
        assertThat(fieldError001.getObjectName(), is("SampleSheet"));
        assertThat(fieldError001.getFieldPath(), is("name01[0]"));
        assertThat(fieldError001.getSheetName(), is("名簿用シート"));
        assertThat(fieldError001.getCellAddress(), is(toPointAddress("A2")));
        assertThat(fieldError001.getLabel(), is("名前01"));
        assertThat(fieldError001.getCodes(), is(hasItemInArray("cellTypeMismatch")));
        assertThat(fieldError001.getFieldValue(), is((Object)"山田太郎"));
        assertThat(fieldError001.getFieldType(), is(typeCompatibleWith(String.class)));
    }
    
}
