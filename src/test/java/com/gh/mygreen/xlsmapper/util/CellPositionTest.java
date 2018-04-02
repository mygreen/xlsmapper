package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * {@link CellPosition}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellPositionTest {
    
    @Test
    public void testOfString() {
        
        {
            CellPosition address = CellPosition.of("A1");
            assertThat(0).isEqualTo(address.getRow());
            assertThat(0).isEqualTo(address.getColumn());
        }
        
        {
            CellPosition address =  CellPosition.of("AX232");
            assertThat(231).isEqualTo(address.getRow());
            assertThat(49).isEqualTo(address.getColumn());
        }
        
        {
            assertThatThrownBy(() -> CellPosition.of("a32A132"))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
    }
}
