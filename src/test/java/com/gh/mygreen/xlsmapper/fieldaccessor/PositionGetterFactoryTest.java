package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.util.HashMap;
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
 * {@link PositionGetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class PositionGetterFactoryTest {
    
    /**
     * 位置情報が無い場合
     *
     */
    public static class NotPosition {
        
        private PositionGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new PositionGetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(SampleRecord.class, "test");
            assertThat(positionGetter).isEmpty();
            
        }
        
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドの位置情報の場合
     *
     */
    public static class ByMapField {
        
        private PositionGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new PositionGetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(NoMapRecord.class, "test");
            assertThat(positionGetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(positionGetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link CellPosition}の場合
         */
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // マップのインスタンスがない場合
                CellAddressRecord record = new CellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                
                Optional<CellPosition> actual = accessor.get(record);
                assertThat(actual).isEmpty();
            }
            
            {
                // 位置情報を取得する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                
                record.positions = new HashMap<>();
                record.positions.put("test", position);
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
            {
                // 位置情報を取得する - 該当するフィールドの値が存在しない
                CellAddressRecord record = new CellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                record.positions = new HashMap<>();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).isEmpty();
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PointRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // マップのインスタンスがない場合
                PointRecord record = new PointRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                
                Optional<CellPosition> actual = accessor.get(record);
                assertThat(actual).isEmpty();
            }
            
            {
                // 位置情報を取得する
                PointRecord record = new PointRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                
                record.positions = new HashMap<>();
                record.positions.put("test", position.toPoint());
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
            {
                // 位置情報を取得する - 該当するフィールドの値が存在しない
                PointRecord record = new PointRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                record.positions = new HashMap<>();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).isEmpty();
            }
            
        }
        
        /**
         * マップのタイプが{@link Point}の場合
         */
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // マップのインスタンスがない場合
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                
                Optional<CellPosition> actual = accessor.get(record);
                assertThat(actual).isEmpty();
            }
            
            {
                // 位置情報を取得する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                
                record.positions = new HashMap<>();
                record.positions.put("test", position.toCellAddress());
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
            {
                // 位置情報を取得する - 該当するフィールドの値が存在しない
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                
                record.positions = new HashMap<>();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).isEmpty();
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
        
        private PositionGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new PositionGetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を取得する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.address = position;
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PointRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.address = position.toPoint();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.address = position.toCellAddress();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(positionGetter).isEmpty();
            
            
        }
        
        private static class CellAddressRecord {
            
            private CellPosition address;
            
            public CellPosition getTestPosition() {
                return address;
            }
            
        }
        
        private static class PointRecord {
            
            private Point address;
            
            Point getTestPosition() {
                return address;
            }
            
        }
        
        private static class PoiCellAddressRecord {
            
            private org.apache.poi.ss.util.CellAddress address;
            
            protected org.apache.poi.ss.util.CellAddress getTestPosition() {
                return address;
            }
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell address;
            
            Cell getTestPosition() {
                return address;
            }
            
        }
        
    }
    
    /**
     * フィールドによる位置情報を格納する場合
     *
     */
    public static class ByField {
        
        private PositionGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new PositionGetterFactory();
        }
        
        @Test
        public void testCreateWithCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(CellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                CellAddressRecord record = new CellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.testPosition = position;
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoint() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PointRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PointRecord record = new PointRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.testPosition = position.toPoint();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithPoiCellAddress() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(PoiCellAddressRecord.class, "test");
            assertThat(positionGetter).isNotEmpty();
            
            {
                // 位置情報を設定する
                PoiCellAddressRecord record = new PoiCellAddressRecord();
                
                PositionGetter accessor = positionGetter.get();
                CellPosition position = CellPosition.of("B24");
                record.testPosition = position.toCellAddress();
                
                Optional<CellPosition> actual = accessor.get(record);
                
                assertThat(actual).contains(position);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<PositionGetter> positionGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(positionGetter).isEmpty();
            
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
