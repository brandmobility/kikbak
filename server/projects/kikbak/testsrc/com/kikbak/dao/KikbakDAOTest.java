package com.kikbak.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Kikbak;

public class KikbakDAOTest extends KikbakBaseTest{

    @Autowired
    ReadOnlyKikbakDAO roKikbak;
    
    @Autowired 
    ReadWriteKikbakDAO rwKikbak;
    
    @Test
    public void testReadWriteKikbak(){
        Kikbak kikbak = new Kikbak();
        kikbak.setDescription("desc");
        kikbak.setDetailedDesc("detailed");
        kikbak.setImageUrl("url");
        kikbak.setNotificationText("notification");
        kikbak.setOfferId(30);
        kikbak.setRewardType("claim");
        kikbak.setValue(32.42);
        
        rwKikbak.makePersistent(kikbak);
        Kikbak found = roKikbak.findByOfferId(kikbak.getOfferId());
        
        assertTrue(kikbak.getDescription().equals(found.getDescription()));
    }
    
}
