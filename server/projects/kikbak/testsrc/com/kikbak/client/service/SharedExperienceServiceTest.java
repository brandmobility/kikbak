package com.kikbak.client.service;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kikbak.KikbakTest;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dto.Shared;
import com.kikbak.jaxb.v1.share.SharedType;

public class SharedExperienceServiceTest extends KikbakTest {

    @Autowired
    SharedExperienceService service;

    @Autowired
    @Qualifier("ReadOnlySharedDAO")
    ReadOnlySharedDAO roSharedDao;

    @Before
    public void setUp() throws Exception {
        setupDbFromRes("spa.xml");
        setupDbFromRes("user.xml");
        setupDb("<dataset><shared/></dataset>");
    }

    @Test
    public void testRegisteringSharing() {

        SharedType st = new SharedType();
        st.setLocationId(1L);
        st.setMerchantId(1);
        st.setOfferId(1);
        st.setType("email");

        String referralCode = service.registerSharing(1L, st);

        Collection<Shared> shares = roSharedDao.listByUserId(1L);
        assertEquals(1, shares.size());
        
        Shared shared = roSharedDao.findByReferralCode(referralCode);
        assertNotNull(shared);
    }
}
