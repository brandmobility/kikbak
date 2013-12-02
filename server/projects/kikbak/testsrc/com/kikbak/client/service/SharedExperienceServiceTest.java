package com.kikbak.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kikbak.KikbakTest;
import com.kikbak.client.service.v1.RateLimitException;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.enums.Channel;
import com.kikbak.dto.Shared;

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
    public void testRegisteringSharing() throws RateLimitException {

        SharedExperienceService.ShareInfo share = new SharedExperienceService.ShareInfo();
        share.userId = 1L;
        share.offerId = 1L;
        share.channel = Channel.fb;

        String referralCode = service.registerSharing(share);

        Collection<Shared> shares = roSharedDao.listByUserId(1L);
        assertEquals(1, shares.size());

        Shared shared = roSharedDao.findByReferralCode(referralCode);
        assertNotNull(shared);
    }
}
