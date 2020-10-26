package com.es.core.model.entity.phone.jdbcPhoneDao;

import com.es.core.model.DAO.exceptions.IdUniquenessException;
import com.es.core.model.DAO.phone.JdbcPhoneDao;
import com.es.core.model.DAO.phone.PhoneDao;
import com.es.core.model.entity.phone.Color;
import com.es.core.model.entity.phone.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.util.AopTestUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = JdbcPhoneDaoIntTestConfiguration.class)
@ExtendWith(SpringExtension.class)
public class JdbcPhoneDaoIntTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PhoneDao jdbcPhoneDao;


    private static final String SELECT_BY_ID_SQL = "SELECT * FROM phones WHERE phones.id = ?";
    private static final String SELECT_COLORS_BY_PHONE_ID_SQL = "SELECT colors.id, colors.code FROM phone2color " +
            "JOIN colors ON phone2color.colorId = colors.id " +
            "WHERE phoneId = ?";
    private final static String SELECT_CORRESPONDING_COLORS_SQL = "SELECT colors.id, colors.code FROM " +
            "(SELECT * FROM phone2color WHERE phoneId = ?) AS corresponding_phone2color " +
            "JOIN colors " +
            "ON corresponding_phone2color.colorId = colors.id";

    private final List<Long> testIds = Arrays.asList(1000L, 1001L, 1002L, 1003L, 1004L);
    private Map<Long, Phone> testPhones;

    @BeforeEach
    public void setUp() {
        refreshDataSet();
        refreshTestInstances();
    }

    private void refreshDataSet() {
        Resource initSchema = new ClassPathResource("db/test-schema.sql");
        Resource testData = new ClassPathResource("db/testdata-phones.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, testData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    private void refreshTestInstances() {
        testPhones = new LinkedHashMap<>();
        for (var id : testIds) {
            var testPhone = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL,
                    new BeanPropertyRowMapper<>(Phone.class), id);
            var testPhoneColors = jdbcTemplate.query(SELECT_COLORS_BY_PHONE_ID_SQL, new Object[]{id},
                    new BeanPropertyRowMapper<>(Color.class));
            testPhone.setColors(new HashSet<>(testPhoneColors));
            testPhones.put(id, testPhone);
        }
    }

    @Test
    public void getByIdTest() {
        for (var id : testIds) {
            System.out.println(testPhones.get(id));
            System.out.println(jdbcPhoneDao.get(id).get());
            assertEquals(testPhones.get(id), jdbcPhoneDao.get(id).get());
        }
    }

    @Test
    public void getByIdTestWithAmbiguity() {
        assertThrows(IdUniquenessException.class, () -> {
            JdbcTemplate jdbcTemplateMock = Mockito.mock(jdbcTemplate.getClass());
            Field field = JdbcPhoneDao.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(AopTestUtils.getUltimateTargetObject(jdbcPhoneDao), jdbcTemplateMock);

            List<Phone> ambiguousResult = Arrays.asList(new Phone(), new Phone(), new Phone());
            when(jdbcTemplateMock.query(anyString(), any(ResultSetExtractor.class), any(Object.class)))
                    .thenReturn(ambiguousResult);
            jdbcPhoneDao.get(Long.MAX_VALUE);
        });
    }

    @Test
    public void getByNonExistingIdTest() {
        assertAll(
                () -> {
                    Optional<Phone> expectedPhone = Optional.empty();
                    Optional<Phone> actualPhone = jdbcPhoneDao.get(Long.MAX_VALUE);
                    assertEquals(expectedPhone, actualPhone);
                },
                () -> assertThrows(NoSuchElementException.class, () -> jdbcPhoneDao.get(Long.MAX_VALUE).get())
        );
    }

    @Test
    public void findAllPhonesWithColorsTest() {
        assertAll(
                () -> assertPhonesEqualityWithLimitOffset(1, 4),
                () -> assertPhonesEqualityWithLimitOffset(0, 5),
                () -> assertPhonesEqualityWithLimitOffset(0, 0)
        );
        assertEquals(jdbcPhoneDao.findAll(Integer.MAX_VALUE, 9), Collections.emptyList());
    }

    private void assertPhonesEqualityWithLimitOffset(int limit, int offset) {
        List<Phone> actualPhones = jdbcPhoneDao.findAll(offset, limit);
        List<Phone> expectedPhones = testPhones.values().stream()
                .filter(phone -> !phone.getColors().isEmpty())
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
        assertEquals(expectedPhones, actualPhones);
    }

    @Test
    public void savingNewPhoneEqualityTest() {
        String query = "SELECT * FROM phones WHERE brand = 'exBrand' and model = 'exModel'";

        //setuping expected
        Phone expectedPhone = new Phone();
        Color color1 = new Color(1000L, "Black");
        Color color2 = new Color(1001L, "White");

        expectedPhone.setBrand("exBrand");
        expectedPhone.setModel("exModel");
        expectedPhone.setColors(Set.of(color1, color2));

        jdbcPhoneDao.save(expectedPhone);

        //getting actual
        Phone actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class));
        List<Color> actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS_SQL, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));

        //compare
        assertEquals(expectedPhone, actualPhone);
    }

    @Test
    public void savingNewPhoneQuantityTest() {
        Phone actualPhone = new Phone();
        actualPhone.setBrand("exBrand");
        actualPhone.setModel("exModel");
        int quantityBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        jdbcPhoneDao.save(actualPhone);
        int quantityAfterAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        assertEquals(quantityBeforeSaving, quantityAfterAdding - 1);
    }

    @Test
    public void savingExistingPhoneTest() {
        String query = "SELECT * FROM phones WHERE phones.id = ?";

        //checking equality before updating
        Phone expectedPhone = testPhones.get(1000L);
        Phone actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class), 1000L);
        List<Color> actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS_SQL, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));

        assertEquals(expectedPhone, actualPhone);

        //checking inequality after changing of one record
        String updateTestString = "updated";
        expectedPhone.setDescription(updateTestString);

        assertNotEquals(expectedPhone.getDescription(), actualPhone.getDescription());

        //checking the number of records while saving
        int quantityBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        jdbcPhoneDao.save(expectedPhone);
        int quantityAfterAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        assertEquals(quantityBeforeSaving, quantityAfterAdding);

        //checking equality of records after reloading old record
        actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class), 1000L);
        actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS_SQL, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));
        assertEquals(expectedPhone, actualPhone);
    }
}

