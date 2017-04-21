package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link PositionSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class PositionSetterFactoryTest {
    
    /**
     * 位置情報が無い場合
     *
     */
    public static class NotPosition {
        
        private PositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new PositionSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(SampleRecord.class, "test");
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
        
        private PositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new PositionSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link CellPosition}の場合
         */
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test", position);
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test", position.toPoint());
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.positions)
                    .hasSize(1)
                    .containsEntry("test", position.toCellAddress());
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
            
            Map<String, org.apache.poi.ss.util.CellAddress> positions;
            
        }
        
    }
    
    /**
     * メソッドによる位置情報を格納する場合
     *
     */
    public static class ByMethod {
        
        private PositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new PositionSetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.address).isEqualTo(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.address).isEqualTo(position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithInt() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(IntRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                IntRecord record = new IntRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.address).isEqualTo(position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.address).isEqualTo(position.toCellAddress());
            }
            
        }
        
        private static class CellAddressRecord {
            
            private CellPosition address;
            
            public void setTestPosition(CellPosition address) {
                this.address = address;
            }
            
        }
        
        private static class PointRecord {
            
            private Point address;
            
            void setTestPosition(Point address) {
                this.address = address;
            }
            
        }
        
        private static class IntRecord {
            
            private Point address;
            
            void setTestPosition(int column, int row) {
                this.address = new Point(column, row);
            }
            
        }
        
        private static class PoiCellAddressRecord {
            
            private org.apache.poi.ss.util.CellAddress address;
            
            protected void setTestPosition(org.apache.poi.ss.util.CellAddress address) {
                this.address = address;
            }
            
        }
        
    }
    
    /**
     * フィールドによる位置情報を格納する場合
     *
     */
    public static class ByField {
        
        private PositionSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new PositionSetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.testPosition).isEqualTo(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PointRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.testPosition).isEqualTo(position.toPoint());
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionSetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionSetter accessor = positionSetter.get();
                CellPosition position = CellPosition.of("B24");
                
                accessor.set(record, position);
                
                assertThat(record.testPosition).isEqualTo(position.toCellAddress());
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<PositionSetter> positionSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(positionSetter).isEmpty();
            
        }
        
        private static class CellAddressRecord {
            
            private CellPosition testPosition;
            
            
        }
        
        private static class PointRecord {
            
            private Point testPosition;
            
            
        }
        
        private static class PoiCellAddressRecord {
            
            private org.apache.poi.ss.util.CellAddress testPosition;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell testPosition;
            
            
        }
    }
}
