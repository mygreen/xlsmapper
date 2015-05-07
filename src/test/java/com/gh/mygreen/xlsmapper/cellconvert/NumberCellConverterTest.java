package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 数値型の変換のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class NumberCellConverterTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * 数値型の読み込みテスト
     * ・変換用アノテーションなし。
     */
    @Test
    public void test_load_number() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NumberSheet.class);
            NumberSheet sheet = mapper.load(in, NumberSheet.class, errors);
            
            if(sheet.primitiveRecords != null) {
                for(PrimitiveRecord record : sheet.primitiveRecords) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.wrapperRecords != null) {
                for(WrapperRecord record : sheet.wrapperRecords) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.otherRecords != null) {
                for(OtherRecord record : sheet.otherRecords) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.formattedRecords != null) {
                for(FormattedRecord record : sheet.formattedRecords) {
                    assertRecord(record, errors);
                }
            }
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    /**
     * セルのアドレスを指定してエラーを取得する。
     * @param errors
     * @param address
     * @return 見つからない場合はnullを返す。
     */
    private CellFieldError getCellFieldError(final SheetBindingErrors errors, final String address) {
        for(CellFieldError error : errors.getCellFieldErrors()) {
            if(error.getFormattedCellAddress().equalsIgnoreCase(address)) {
                return error;
            }
        }
        
        return null;
    }
    
    /**
     * プリミティブ型の値のチェック
     * @param record
     * @param errors
     */
    private void assertRecord(final PrimitiveRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)0L));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)0.0));
            
        } else if(record.no == 2) {
            // ゼロ（標準型）
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)0L));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)0.0));
            
        } else if(record.no == 3) {
            // ゼロ（数値型）
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)0L));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)0.0));
            
        } else if(record.no == 4) {
            // 正の数（標準）
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)12L));
            assertThat(record.f, is((float)12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 5) {
            // 正の数（数値型）
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)12L));
            assertThat(record.f, is((float)12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 6) {
            // 負の数（標準）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)-12.3));
            
        } else if(record.no == 7) {
            // 負の数（数値型）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)-12.3));
            
        } else if(record.no == 8) {
            // 数値（△ 1234）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 9) {
            // 通貨
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)1234567L));
            assertThat(record.f, is((float)12345.67));
            assertThat(record.d, is((double)-12345.67));
            
        } else if(record.no == 10) {
            // 会計
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)12346L));
            assertThat(record.f, is((float)12345.67));
            assertThat(record.d, is((double)-12345.67));
            
        } else if(record.no == 11) {
            // パーセント
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)1));
            assertThat(record.l, is((long)-12));
            assertThat(record.f, is((float)0.1234));
            assertThat(record.d, is((double)-1.234567));
            
        } else if(record.no == 12) {
            // 分数
            assertThat(record.b, is((byte)1));
            assertThat(record.s, is((short)1));
            assertThat(record.i, is((int)-1));
            assertThat(record.l, is((long)123));
            assertThat(record.f, is((float)0.1234));
            assertThat(record.d, is((double)-1.234567));
            
        } else if(record.no == 13) {
            // 指数
            assertThat(record.b, is((byte)1));
            assertThat(record.s, is((short)1));
            assertThat(record.i, is((int)-1));
            assertThat(record.l, is((long)0));
            assertThat(record.f, is((float)0.0001));
            assertThat(record.d, is((double)1000.234));
            
        } else if(record.no == 14) {
            // 指数
            assertThat(record.b, is((byte)123));
            assertThat(record.s, is((short)123));
            assertThat(record.i, is((int)-123));
            assertThat(record.l, is((long)1234567));
            assertThat(record.f, is((float)123.456));
            assertThat(record.d, is((double)-123.456));
            
        } else if(record.no == 15) {
            // 文字列型
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)12));
            assertThat(record.f, is((float)12.34));
            assertThat(record.d, is((double)-12.34));
            
        } else if(record.no == 16) {
            // 最大値（文字列型）
            assertThat(record.b, is((byte)127));
            assertThat(record.s, is((short)32767));
            assertThat(record.i, is((int)2147483647));
            assertThat(record.l, is((long)9223372036854775807L));
            assertThat(record.f, is(Float.valueOf("3.4028234663852886E38")));
            assertThat(record.d, is(Double.valueOf("1.7976931348623157E308")));
            
        } else if(record.no == 17) {
            // 最大置+1（文字列型）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
            assertThat(record.f, is(Float.valueOf("3.4028234663852887E38")));
            assertThat(record.d, is(Double.valueOf("1.7976931348623158E308")));
            
        } else if(record.no == 18) {
            // 最小値（文字列型）
            assertThat(record.b, is((byte)-128));
            assertThat(record.s, is((short)-32768));
            assertThat(record.i, is((int)-2147483648));
            assertThat(record.l, is((long)-9223372036854775808L));
            assertThat(record.f, is(Float.valueOf("-3.40282346638528E38")));
            assertThat(record.d, is(Double.valueOf("-1.7976931348623157E308")));
            
        } else if(record.no == 19) {
            // 最小置-1（文字列型）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
            assertThat(record.f, is(Float.valueOf("-3.40282346638528E38")));
            assertThat(record.d, is(Double.valueOf("-1.7976931348623158E308")));
            
        } else if(record.no == 20) {
            // 最大置+1（数値型）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
//            assertThat(record.f, is(Float.valueOf("3.4028234663852887E38")));
//            assertThat(record.d, is(Double.valueOf("1.7976931348623158E308")));
            
        } else if(record.no == 21) {
            // 最小置-1（数値型）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
//            assertThat(record.f, is(Float.valueOf("-3.40282346638528E38")));
//            assertThat(record.d, is(Double.valueOf("-1.7976931348623158E308")));
            
        } else if(record.no == 22) {
            // 数式
            assertThat(record.b, is((byte)2));
            assertThat(record.s, is((short)3));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)2.4));
            assertThat(record.f, is(Float.valueOf("4.85")));
            assertThat(record.d, is(Double.valueOf("24.25")));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    /**
     * ラッパークラスの値のチェック
     * @param record
     * @param errors
     */
    private void assertRecord(final WrapperRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.b, is(nullValue()));
            assertThat(record.s, is(nullValue()));
            assertThat(record.i, is(nullValue()));
            assertThat(record.l, is(nullValue()));
            assertThat(record.f, is(nullValue()));
            assertThat(record.d, is(nullValue()));
            
        } else if(record.no == 2) {
            // ゼロ（標準型）
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)0L));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)0.0));
            
        } else if(record.no == 3) {
            // ゼロ（数値型）
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)0L));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)0.0));
            
        } else if(record.no == 4) {
            // 正の数（標準）
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)12L));
            assertThat(record.f, is((float)12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 5) {
            // 正の数（数値型）
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)12L));
            assertThat(record.f, is((float)12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 6) {
            // 負の数（標準）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)-12.3));
            
        } else if(record.no == 7) {
            // 負の数（数値型）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)-12.3));
            
        } else if(record.no == 8) {
            // 数値（△ 1234）
            assertThat(record.b, is((byte)-12));
            assertThat(record.s, is((short)-12));
            assertThat(record.i, is((int)12));
            assertThat(record.l, is((long)-12L));
            assertThat(record.f, is((float)-12.3));
            assertThat(record.d, is((double)12.3));
            
        } else if(record.no == 9) {
            // 通貨
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)1234567L));
            assertThat(record.f, is((float)12345.67));
            assertThat(record.d, is((double)-12345.67));
            
        } else if(record.no == 10) {
            // 会計
            assertThat(record.b, is((byte)12));
            assertThat(record.s, is((short)12));
            assertThat(record.i, is((int)-12));
            assertThat(record.l, is((long)12346L));
            assertThat(record.f, is((float)12345.67));
            assertThat(record.d, is((double)-12345.67));
            
        } else if(record.no == 11) {
            // パーセント
            assertThat(record.b, is((byte)0));
            assertThat(record.s, is((short)0));
            assertThat(record.i, is((int)1));
            assertThat(record.l, is((long)-12));
            assertThat(record.f, is((float)0.1234));
            assertThat(record.d, is((double)-1.234567));
            
        } else if(record.no == 12) {
            // 分数
            assertThat(record.b, is((byte)1));
            assertThat(record.s, is((short)1));
            assertThat(record.i, is((int)-1));
            assertThat(record.l, is((long)123));
            assertThat(record.f, is((float)0.1234));
            assertThat(record.d, is((double)-1.234567));
            
        } else if(record.no == 13) {
            // 指数
            assertThat(record.b, is((byte)1));
            assertThat(record.s, is((short)1));
            assertThat(record.i, is((int)-1));
            assertThat(record.l, is((long)0));
            assertThat(record.f, is((float)0.0001));
            assertThat(record.d, is((double)1000.234));
            
        } else if(record.no == 14) {
            // 指数
            assertThat(record.b, is((byte)123));
            assertThat(record.s, is((short)123));
            assertThat(record.i, is((int)-123));
            assertThat(record.l, is((long)1234567));
            assertThat(record.f, is((float)123.456));
            assertThat(record.d, is((double)-123.456));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    /**
     * その他のクラスの値のチェック
     * @param record
     * @param errors
     */
    private void assertRecord(final OtherRecord record, final SheetBindingErrors errors) {
        if(record.no == 1) {
            // 空文字
            assertThat(record.bd, is(nullValue()));
            assertThat(record.bi, is(nullValue()));
            
        } else if(record.no == 2) {
            // ゼロ
            assertThat(record.bd, is(new BigDecimal("0")));
            assertThat(record.bi, is(new BigInteger("0")));
            
        } else if(record.no == 3) {
            // 正の数
            assertThat(record.bd, is(new BigDecimal("12")));
            assertThat(record.bi, is(new BigInteger("12")));
            
        } else if(record.no == 4) {
            // 負の数
            assertThat(record.bd, is(new BigDecimal("-12")));
            assertThat(record.bi, is(new BigInteger("-12")));
            
        } else if(record.no == 5) {
            // 小数
            assertThat(record.bd, is(new BigDecimal(Double.valueOf("12.345"))));
            assertThat(record.bi, is(new BigInteger("12")));
            
        } else if(record.no == 6) {
            // 文字列
            assertThat(record.bd, is(new BigDecimal(Double.parseDouble("12.345")).setScale(3, RoundingMode.HALF_UP)));
            assertThat(record.bi, is(new BigInteger("12")));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    /**
     * 初期値、書式指定の値のチェック
     * @param record
     * @param errors
     */
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        if(record.no == 1) {
            // 空文字
            assertThat(record.b, is((byte)1));
            assertThat(record.s, is((short)1));
            assertThat(record.i, is((int)0));
            assertThat(record.l, is((long)-1));
            assertThat(record.f, is((float)0.0));
            assertThat(record.d, is((double)10000.0));
            
        } else if(record.no == 2) {
            // 正の数
            assertThat(record.b, is((byte)123));
            assertThat(record.s, is((short)123));
            assertThat(record.i, is((int)1234));
            assertThat(record.l, is((long)1234567));
            assertThat(record.f, is((float)0.1234));
            assertThat(record.d, is((double)12345.67));
            
        } else if(record.no == 3) {
            // 負の数
            assertThat(record.b, is((byte)-123));
            assertThat(record.s, is((short)-123));
            assertThat(record.i, is((int)-1234));
            assertThat(record.l, is((long)-1234567));
            assertThat(record.f, is((float)-0.1234));
            assertThat(record.d, is((double)-12345.67));
            
        } else if(record.no == 4) {
            // 最大値
            assertThat(record.b, is((byte)127));
            assertThat(record.s, is((short)32767));
            assertThat(record.i, is((int)2147483647));
            assertThat(record.l, is((long)9223372036854775807L));
            
//            assertThat(record.f, is(Float.valueOf("3.4028234663852886E38")));
//            assertThat(record.d, is(Double.valueOf("1.7976931348623157E308")));
            
        } else if(record.no == 5) {
            // 最大置+1
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
//            assertThat(record.f, is(Float.valueOf("3.4028234663852887E38")));
//            assertThat(record.d, is(Double.valueOf("1.7976931348623158E308")));
            
        } else if(record.no == 6) {
            // 最小値
            assertThat(record.b, is((byte)-128));
            assertThat(record.s, is((short)-32768));
            assertThat(record.i, is((int)-2147483648));
            assertThat(record.l, is((long)-9223372036854775808L));
            
//            assertThat(record.f, is(Float.valueOf("-3.40282346638528E38")));
//            assertThat(record.d, is(Double.valueOf("-1.7976931348623157E308")));
            
        } else if(record.no == 7) {
            // 最小置-1
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            
//            assertThat(record.f, is(Float.valueOf("-3.40282346638528E38")));
//            assertThat(record.d, is(Double.valueOf("-1.7976931348623158E308")));
            
        } else if(record.no == 8) {
            // 不正な値（数値以外）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("s"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("i"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("l"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("f"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("d"))).isTypeBindFailure(), is(true));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    @XlsSheet(name="数値型")
    private static class NumberSheet {
        
        @XlsHorizontalRecords(tableLabel="プリミティブ型", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<PrimitiveRecord> primitiveRecords;
        
        @XlsHorizontalRecords(tableLabel="ラッパークラス", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<WrapperRecord> wrapperRecords;
        
        @XlsHorizontalRecords(tableLabel="その他のクラス", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<OtherRecord> otherRecords;
        
        @XlsHorizontalRecords(tableLabel="初期値、書式指定", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
    }
    
    /**
     * 数値型 - プリミティブ型
     * 
     */
    private static class PrimitiveRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="byte型")
        private byte b;
        
        @XlsColumn(columnName="short型")
        private short s;
        
        @XlsColumn(columnName="int型")
        private int i;
        
        @XlsColumn(columnName="long型")
        private long l;
        
        @XlsColumn(columnName="float型")
        private float f;
        
        @XlsColumn(columnName="double型")
        private double d;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
    
    /**
     * 数値型 - ラッパークラス
     *
     */
    private static class WrapperRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="Byteクラス")
        private Byte b;
        
        @XlsColumn(columnName="Shortクラス")
        private Short s;
        
        @XlsColumn(columnName="Integerクラス")
        private Integer i;
        
        @XlsColumn(columnName="Longクラス")
        private Long l;
        
        @XlsColumn(columnName="Floatクラス")
        private Float f;
        
        @XlsColumn(columnName="Doubleクラス")
        private Double d;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
    
    /**
     * 数値型  - その他のクラス
     *
     */
    private static class OtherRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="BigDecimalクラス")
        private BigDecimal bd;
        
        @XlsColumn(columnName="BigIntegerクラス")
        private BigInteger bi;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
    
    /**
     * 数値型 - 初期値、書式指定
     *
     */
    private static class FormattedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        /** 初期値 */
        @XlsConverter(defaultValue="1")
        @XlsColumn(columnName="byte型")
        private byte b;
        
        /** 初期値 */
        @XlsConverter(defaultValue="1")
        @XlsColumn(columnName="short型")
        private short s;
        
        /** 通貨 */
        @XlsNumberConverter(pattern="", currency="JPY", locale="ja_JP")
        @XlsColumn(columnName="int型")
        private int i;
        
        /** トリム */
        @XlsConverter(defaultValue="-1", trim=true)
        @XlsNumberConverter(pattern="#,###,##0")
        @XlsColumn(columnName="long型")
        private long l;
        
        /** パーセント */
        @XlsNumberConverter(pattern="##.00%")
        @XlsColumn(columnName="float型")
        private float f;
        
        /** 正と負の数 */
        @XlsConverter(defaultValue="10,000.00")
        @XlsNumberConverter(pattern="#,##0.00;(#,##0.00)")
        @XlsColumn(columnName="double型")
        private double d;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
