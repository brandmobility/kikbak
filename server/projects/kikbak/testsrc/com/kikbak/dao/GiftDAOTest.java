package com.kikbak.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Gift;

public class GiftDAOTest extends KikbakBaseTest{

    @Autowired
    ReadOnlyGiftDAO roGift;
    
    @Autowired
    ReadWriteGiftDAO rwGift;
    
    @Test
    public void testRWGift(){
        Gift gift = new Gift();
        gift.setDescription("desc");
        gift.setDetailedDesc("gift");
        gift.setDiscountType("amount");
        gift.setImageUrl("image");
        gift.setNotificationText("notification");
        gift.setOfferId(12);
        gift.setRedemptionLocationType("store");
        gift.setValidationType("qr");
        gift.setValue(12.3);
        
        rwGift.makePersistent(gift);
        
        Gift found = roGift.findByOfferId(gift.getOfferId());
        assertEquals(found.getOfferId(), gift.getOfferId());
    }
}
