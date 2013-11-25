package com.kikbak.dao;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.KikbakTest;
import com.kikbak.client.service.v1.ReferralCodeUniqueException;
import com.kikbak.dto.Shared;

public class SharedDAOTest extends KikbakTest {

    @Autowired
    @Qualifier("ReadOnlySharedDAO")
    ReadOnlySharedDAO roDao;

    @Autowired
    @Qualifier("ReadWriteSharedDAO")
    ReadWriteSharedDAO rwDao;

    @Before
    public void setUp() throws Exception {
        setupDbFromRes("ShareDaoTest.xml");
    }

    @Test
    public void testFindById() {
        Shared sharedA = roDao.findById(1L);
        assertNotNull(sharedA);
        assertEquals(1, sharedA.getUserId());

        Shared sharedB = roDao.findById(2L);
        assertNotNull(sharedB);
        assertEquals(2, sharedB.getUserId());
    }

    @Test
    public void testListByUserId() {
        Collection<Shared> sharedsA = roDao.listByUserId(1L);
        assertEquals(1, sharedsA.size());

        Collection<Shared> sharedsB = roDao.listByUserId(2L);
        assertEquals(2, sharedsB.size());
    }

    @Test
    public void testListByLocationId() {
        Collection<Shared> shareds = roDao.listByLocationId(1L);
        assertEquals(3, shareds.size());
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testWriteShared() throws Exception {
        Shared shared = new Shared();
        shared.setMerchantId(99);
        shared.setOfferId(99);
        shared.setLocationId(99L);
        shared.setUserId(99L);
        shared.setSharedDate(new Date());
        shared.setType("email");
        shared.setReferralCode("code99");
        rwDao.save(shared);
        Shared s = roDao.findById(shared.getId());
        assertEquals(99, s.getLocationId().longValue());
        assertEquals("code99", s.getReferralCode());
    }

    @Test(expected = ReferralCodeUniqueException.class)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testWriteSharedConstrain() throws Exception {
        Shared shared = new Shared();
        shared.setMerchantId(99);
        shared.setOfferId(99);
        shared.setLocationId(99L);
        shared.setUserId(99L);
        shared.setSharedDate(new Date());
        shared.setType("email");
        shared.setReferralCode("code1");
        // "code1" already exists, should throw an exception
        rwDao.save(shared);
    }

    @Test
    public void testListUserAndOfferId() throws Exception {
        Collection<Shared> sharedsA = roDao.listByUserIdAndOfferId(1L, 1L);
        assertEquals(1, sharedsA.size());

        Collection<Shared> sharedsB = roDao.listByUserIdAndOfferId(2L, 1L);
        assertEquals(2, sharedsB.size());
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testLastSharedByUserAndOffer() throws Exception {
        Shared shared = rwDao.findLastShareByUserAndOffer(2L, 1L);
        assertEquals(3, shared.getId());

        shared.setSharedDate(new Date(0));
        rwDao.save(shared);

        shared = rwDao.findLastShareByUserAndOffer(2L, 1L);
        assertEquals(2, shared.getId());
    }
}
