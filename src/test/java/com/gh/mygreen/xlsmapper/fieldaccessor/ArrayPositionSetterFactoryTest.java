package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link ArrayPositionSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ArrayPositionSetterFactoryTest {
    
    /**
     * 位置情報が無い場合
     *
     */
    public static class NotPosition {
        
        private ArrayPositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayPositionSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(SampleRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドの位置情報の場合
     *
     */
    public static class ByMapField {
        
        private ArrayPositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayPositionSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link CellPosition}の場合
         */
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test[1]", position);
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoint() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test[1]", position.toPoint());
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test[1]", position.toCellAddress());
            }
            
        }
        
        /**
         * Map形式ではない場合
         *
         */
        private static class NoMapRecord {
            
            CellPosition positions;
            
        }
        
        /**
         * 値がサポートしていないクラスタイプの場合
         *
         */
        private static class NoSupportTypeRecord {
            
            Map<String, CellReference> positions;
            
        }
        
        private static class CellAddressRecord {
            
            Map<String, CellPosition> positions;
            
        }
        
        private static class PointRecord {
            
            Map<String, Point> positions;
            
        }
        
        private static class PoiCellAddressRecord {
            
            Map<String, CellAddress> positions;
            
        }
        
    }
    
    /**
     * メソッドによる位置情報を格納する場合
     *
     */
    public static class ByMethod {
        
        private ArrayPositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayPositionSetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.addressList)
                    .hasSize(2)
                    .containsExactly(null, position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.addressList)
                    .hasSize(2)
                    .containsExactly(null, position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithInt() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(IntRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                IntRecord record = new IntRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.addressList)
                    .hasSize(2)
                    .containsExactly(null, position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.addressList)
                    .hasSize(2)
                    .containsExactly(null, position.toCellAddress());
            }
            
        }
        
        private static class CellAddressRecord {
            
            private List<CellPosition> addressList = new ArrayList<>();
            
            public void setTestPosition(int index, CellPosition address) {
                Utils.addListWithIndex(addressList, address, index);
            }
            
        }
        
        private static class PointRecord {
            
            private List<Point> addressList = new ArrayList<>();
            
            void setTestPosition(int index, Point address) {
                Utils.addListWithIndex(addressList, address, index);
            }
            
        }
        
        private static class IntRecord {
            
            private List<Point> addressList = new ArrayList<>();
            
            void setTestPosition(int index, int column, int row) {
                Utils.addListWithIndex(addressList, new Point(column, row), index);
            }
            
        }
        
        private static class PoiCellAddressRecord {
            
            private List<CellAddress> addressList = new ArrayList<>();
            
            protected void setTestPosition(int index, CellAddress address) {
                Utils.addListWithIndex(addressList, address, index);
            }
            
        }
        
    }
    
    /**
     * フィールドによる位置情報を格納する場合
     *
     */
    public static class ByField {
        
        private ArrayPositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayPositionSetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.testPosition)
                    .hasSize(2)
                    .containsExactly(null, position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.testPosition)
                    .hasSize(2)
                    .containsExactly(null, position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                ArrayPositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position, 1);
                
                assertThat(record.testPosition)
                    .hasSize(2)
                    .containsExactly(null, position.toCellAddress());
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<ArrayPositionSetter> positionSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        private static class CellAddressRecord {
            
            private List<CellPosition> testPosition;
            
            
        }
        
        private static class PointRecord {
            
            private List<Point> testPosition;
            
            
        }
        
        private static class PoiCellAddressRecord {
            
            private List<CellAddress> testPosition;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private List<Cell> testPosition;
            
            
        }
    }
    
}
