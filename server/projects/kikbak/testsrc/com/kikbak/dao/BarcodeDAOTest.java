package com.kikbak.dao;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Barcode;

public class BarcodeDAOTest extends KikbakBaseTest {
    
    @Autowired
    ReadOnlyBarcodeDAO roBarcode;
    
    @Autowired
    ReadWriteBarcodeDao rwBarcode;
    
    @Test
    public void testBarcodeRW(){
        Barcode barcode = new Barcode();
        barcode.setAllocatedgiftId(12L);
        barcode.setAssociationDate(new Date());
        barcode.setCode("1232");
        barcode.setExpirationDate(new Date());
        barcode.setBeginDate(new Date());
        barcode.setMerchantId(12L);
        
        rwBarcode.makePersistent(barcode);
        
        Barcode found = roBarcode.findByCode("1232");
     
        assertEquals(barcode.getMerchantId(), found.getMerchantId());
    }

}
