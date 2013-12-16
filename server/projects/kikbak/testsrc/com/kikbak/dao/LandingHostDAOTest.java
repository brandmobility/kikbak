package com.kikbak.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.LandingHost;

public class LandingHostDAOTest extends KikbakBaseTest {
    
    @Autowired
    ReadWriteLandingHostDAO rwClaim;
    
    @Test
    public void testIncreaseHostCount(){
    	LandingHost host = rwClaim.increaseCount("host1");
        assertEquals(host.getCount().longValue(), 1L);
        host = rwClaim.increaseCount("host1");
        assertEquals(host.getCount().longValue(), 2L);
        host = rwClaim.increaseCount("host1");
        assertEquals(host.getCount().longValue(), 3L);
        host = rwClaim.increaseCount("host2");
        assertEquals(host.getCount().longValue(), 1L);
    }

}
