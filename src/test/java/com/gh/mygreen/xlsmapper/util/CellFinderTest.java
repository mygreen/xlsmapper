package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;



import org.junit.Test;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;

/**
 * {@link CellFinder}のテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellFinderTest {
    
    private Sheet sheet;
    
    private Configuration config;
    
    @Before
    public void setupBefore() throws Exception {
         Workbook workbook = WorkbookFactory.create(new FileInputStream(new File("src/test/data/utils.xlsx")));
         this.sheet = workbook.getSheet("CellFinder");
         
         this.config = new Configuration();
    }
    
    @Test
    public void testFind() {
        
        Optional<Cell> cell = CellFinder.query(sheet, "テスト", config).findOptional();
        
        assertThat(cell).isNotEmpty();
        assertThat(CellPosition.of(cell.get()).toString()).isEqualTo("B4");
        assertThat(getCellContents(cell.get())).isEqualTo("テスト");
        
    }
    
    /**
     * 開始位置の指定
     */
    @Test
    public void testFind_fromPosition() {
        
        {
            // 開始位置を含む場合（デフォルト値）
            Optional<Cell> cell = CellFinder.query(sheet, "テスト", config)
                    .startPosition(CellPosition.of("B4"))
                    .findOptional();
            
            assertThat(cell).isNotEmpty();
            assertThat(CellPosition.of(cell.get()).toString()).isEqualTo("B4");
            assertThat(getCellContents(cell.get())).isEqualTo("テスト");
        
        }
        
        {
            // 開始位置を含まない場合
            Optional<Cell> cell = CellFinder.query(sheet, "テスト", config)
                    .startPosition(CellPosition.of("B4"))
                    .excludeStartPosition(true)
                    .findOptional();
            
            assertThat(cell).isEmpty();
        }
        
        
    }
    
    /**
     * セルが見つからない場合
     */
    @Test
    public void testFind_whenNotFoundException() {
        
        {
            Cell cell = CellFinder.query(sheet, "テスト", config).findWhenNotFoundException();
            
            assertThat(cell).isNotNull();
            assertThat(CellPosition.of(cell).toString()).isEqualTo("B4");
            assertThat(getCellContents(cell)).isEqualTo("テスト");
        }
        
        {
            // セルが見つからない場合
            assertThatThrownBy(() -> CellFinder.query(sheet, "あいう", config).findWhenNotFoundException())
                .isInstanceOf(CellNotFoundException.class);
        }
        
    }
    
    /**
     * 正規表現による検索が有効
     */
    @Test
    public void testFind_regex() {
        
        config.setRegexLabelText(true);
        Optional<Cell> cell = CellFinder.query(sheet, "/テスト.+/", config).findOptional();
        
        assertThat(cell).isNotEmpty();
        assertThat(CellPosition.of(cell.get()).toString()).isEqualTo("C5");
        assertThat(getCellContents(cell.get())).isEqualTo("テスト（1）");
        
    }
    
    /**
     * 正規化による検索が有効
     */
    @Test
    public void testFind_normalized() {
        
        config.setNormalizeLabelText(true);
        Optional<Cell> cell = CellFinder.query(sheet, "ABCefg", config).findOptional();
        
        assertThat(cell).isNotEmpty();
        assertThat(CellPosition.of(cell.get()).toString()).isEqualTo("B8");
        assertThat(getCellContents(cell.get())).isEqualTo("  ABC\nefg ");
        
    }
    
    /**
     * 正規化+正規表現による検索が有効
     */
    @Test
    public void testFind_regex_normalized() {
        
        config.setNormalizeLabelText(true)
            .setRegexLabelText(true);
        Optional<Cell> cell = CellFinder.query(sheet, "/テスト.+/", config)
                .startPosition(CellPosition.of("C8"))
                .findOptional();
        
        assertThat(cell).isNotEmpty();
        assertThat(CellPosition.of(cell.get()).toString()).isEqualTo("C9");
        assertThat(getCellContents(cell.get())).isEqualTo("  テスト (3) ");
        
    }
    
    private String getCellContents(final Cell cell) {
        return POIUtils.getCellContents(cell, config.getCellFormatter());
    }
}
