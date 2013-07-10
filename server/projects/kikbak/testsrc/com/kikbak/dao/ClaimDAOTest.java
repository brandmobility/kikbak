package com.kikbak.dao;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Claim;

public class ClaimDAOTest extends KikbakBaseTest {
    
    @Autowired
    ReadOnlyClaimDAO roClaim;
    
    @Autowired
    ReadWriteClaimDAO rwClaim;
    
    @Test
    public void testClaimRW(){
        Claim claim = new Claim();
        claim.setApt("12");
        claim.setCity("city");
        claim.setKikbakId(12);
        claim.setName("bob");
        claim.setOfferId(12);
        claim.setPhoneNumber("number");
        claim.setState("CA");
        claim.setStreet("123 filmore");
        claim.setUserId(19);
        claim.setZipcode("12345");
        claim.setRequestDate(new Date());
        
        rwClaim.makePersistent(claim);
        List<Claim> found = roClaim.listByUserid(claim.getUserId());
        
        assertEquals(found.size(), 1);
    }

}
