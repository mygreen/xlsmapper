package com.gh.mygreen.xlsmapper.spring;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gh.mygreen.xlsmapper.SpringBeanFactory;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * Springを使ったテスト
 * <ul>
 *  <li>Bean作成</li>
 *  <li>メッセージング</li>
 * </ul>
 *
 * @since 1.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:TextContext.xml")
public class SpringTest {

    @Autowired
    private SpringBeanFactory springBeanFactory;

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
     * Springコンテナ管理のBeanから作成する。
     */
    @Test
    public void test_injection_bean() throws Exception {

        Configuration config = new Configuration();
        config.setBeanFactory(springBeanFactory);

        XlsMapper mapper = new XlsMapper();
        mapper.setConfiguration(config);

        try(InputStream in = new FileInputStream("src/test/data/spring.xlsx")) {
            SheetBindingErrors<SampleSheet> errors = mapper.loadDetail(in, SampleSheet.class);

            SampleSheet sheet = errors.getTarget();

            assertThat(sheet.getSampleService(), is(not(nullValue())));

            assertThat(sheet.getRecords(), hasSize(2));
            for(SampleRecord record : sheet.getRecords()) {
                assertThat(record.getSampleService(), is(not(nullValue())));

            }

        }

    }

    /**
     * Springコンテナ管理外のクラスにインジェクションする。
     */
    @Test
    public void test_injection_not_bean() throws Exception {

        Configuration config = new Configuration();
        config.setBeanFactory(springBeanFactory);

        XlsMapper mapper = new XlsMapper();
        mapper.setConfiguration(config);

        try(InputStream in = new FileInputStream("src/test/data/spring.xlsx")) {
            SheetBindingErrors<NotSpringBeanSheet> errors = mapper.loadDetail(in, NotSpringBeanSheet.class);

            NotSpringBeanSheet sheet = errors.getTarget();

            assertThat(sheet.sampleService, is(not(nullValue())));

            assertThat(sheet.records, hasSize(2));
            for(NotSpringBeanRecord record : sheet.records) {
                assertThat(record.sampleService, is(not(nullValue())));

            }

        }

    }

    /**
     * Spring管理外のクラス
     *
     */
    @XlsSheet(name="Spring管理のBean")
    public static class NotSpringBeanSheet {

        /** SpringBeanをインジェクションする */
        @Autowired
        private SampleService sampleService;

        @XlsHorizontalRecords(tableLabel="一覧")
        private List<NotSpringBeanRecord> records;

        /** 読み込み後に処理を実行する */
        @XlsPostLoad
        public void onLoad() {

            sampleService.doService();

        }

    }

    /**
     * Spring管理外のクラス
     */
    public static class NotSpringBeanRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @Autowired
        private SampleService sampleService;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="名称")
        private String name;

        @XlsColumn(columnName="値")
        private Double value;

        @XlsPostLoad
        public void onLoad() {

            sampleService.doService();

        }

    }

}
