/*
 *
 */
package fr.explorimmo.poc.persistence;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.ResourceUtils;

import fr.explorimmo.poc.domain.Address;
import fr.explorimmo.poc.domain.Advert;
import fr.explorimmo.poc.test.TestUtils;

/**
 * Advert database integration testing<br/>
 * CRUD operations are tested<br>
 * Finders are tested<br/>
 * 
 * @author louis.gueye@gmail.com
 */
public class AdvertPersistenceTestIT extends BasePersistenceTestIT {

    @Autowired
    private AdvertDao advertDao;

    /**
     * @param longs
     */
    private void assertResultContainsAdvertIds(final List<Advert> result, final Set<Long> ids) {
        if (CollectionUtils.isEmpty(result) && ids == null)
            return;

        final Set<Long> advertIds = new HashSet<Long>();
        for (final Advert advert : result) {
            advertIds.add(advert.getId());
        }

        Assert.assertTrue(advertIds.containsAll(ids));
    }

    @Before
    public void onSetUpInTransaction() throws Exception {
        final Connection con = DataSourceUtils.getConnection(dataSource);
        final IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(ResourceUtils
                .getFile("classpath:dbunit/explorimmo-test-data.xml"));

        try {
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, dataSet);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        Assert.assertEquals(2, baseDao.findAll(Advert.class).size());
    }

    /**
     * Given : a valid advert<br/>
     * When : one persists the above advert<br/>
     * Then : system should retrieve it in database<br/>
     */
    @Test
    public void shouldCreateAdvert() {
        // Given
        final Advert advert = TestUtils.validAdvert();

        // When
        baseDao.persist(advert);
        baseDao.flush();

        // Then
        Assert.assertNotNull(advert.getId());
        Assert.assertEquals(advert, baseDao.get(Advert.class, advert.getId()));
    }

    /**
     * Given : a valid advert<br/>
     * When : one persists the above advert and then delete it<br/>
     * Then : system should not retrieve it in database<br/>
     */
    @Test
    public void shouldDeleteAdvert() {
        // Given
        final Advert advert = TestUtils.validAdvert();

        // When
        baseDao.persist(advert);
        baseDao.flush();
        baseDao.delete(Advert.class, advert.getId());
        baseDao.flush();

        // Then
        final Advert persistedAdvert = baseDao.get(Advert.class, advert.getId());
        Assert.assertNull(persistedAdvert);
    }

    /**
     * Given : one advert with postal code and city properties valued<br/>
     * When : one searches by the above criterion<br/>
     * Then : system should return advert {id = 1} and advert {id = 2}<br/>
     */
    @Test
    public void shouldFindAdvertByCityAndPostalCode() {
        // Given
        final Advert advert = new Advert();
        advert.setAddress(new Address());
        advert.getAddress().setPostalCode("75009");
        advert.getAddress().setCity("paris");

        // When
        final List<Advert> results = advertDao.findByExample(advert);

        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        assertResultContainsAdvertIds(results, new HashSet<Long>(Arrays.asList(1L, 2L)));

    }

    /**
     * Given : one advert with name property valued<br/>
     * When : one searches by the above criterion<br/>
     * Then : system should return advert {id = 2}<br/>
     */
    @Test
    public void shouldFindAdvertByName() {
        // Given
        final Advert advert = new Advert();
        advert.setName("hocquet");

        // When
        final List<Advert> results = advertDao.findByExample(advert);

        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(Long.valueOf(1L), results.get(0).getId());

    }

    /**
     * Given : a valid advert<br/>
     * When : one updates that advert<br/>
     * Then : system should persist changes<br/>
     */
    @Test
    public void shouldUpdateAdvert() {
        // Given
        final Advert advert = TestUtils.validAdvert();
        baseDao.persist(advert);
        baseDao.flush();
        baseDao.evict(advert);
        final String name = RandomStringUtils.random(50);
        advert.setName(name);

        // When
        baseDao.merge(advert);
        baseDao.flush();
        final Advert persistedAdvert = baseDao.get(Advert.class, advert.getId());

        // Then
        Assert.assertEquals(advert.getName(), persistedAdvert.getName());
    }

}
