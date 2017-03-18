package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * {@link CellAddress}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellAddressTest {
    
    @Test
    public void testOfString() {
        
        {
            CellAddress address = CellAddress.of("A1");
            assertThat(0).isEqualTo(address.getRow());
            assertThat(0).isEqualTo(address.getColumn());
        }
        
        {
            CellAddress address =  CellAddress.of("AX232");
            assertThat(231).isEqualTo(address.getRow());
            assertThat(49).isEqualTo(address.getColumn());
        }
        
        {
            assertThatThrownBy(() -> CellAddress.of("a32A132"))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
    }
}
