package com.kikbak.dao;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Referralcode;

public class ReferralCodeDAOTest extends KikbakBaseTest {
    
    @Autowired
    ReadOnlyReferralCodeDAO roRCCode;
    
    @Autowired
    ReadWriteReferralCode rwRCCode;
    
    @Test
    public void testReferralCodeRW(){
        Referralcode rc = new Referralcode();
        rc.setCode("test");
        rc.setSharedId(2L);
        
        rwRCCode.makePersistent(rc);
        
        Referralcode found = roRCCode.findByCode("test");
        assertEquals(rc.getSharedId(), found.getSharedId());
   
    }

}
