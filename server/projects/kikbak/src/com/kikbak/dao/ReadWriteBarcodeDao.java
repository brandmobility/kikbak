package com.kikbak.dao;

import com.kikbak.dto.Barcode;

public interface ReadWriteBarcodeDao {
    public void makePersistent(Barcode kikbak);
    public void makeTransient(Barcode kikbak);
    
    public Barcode allocateBarcode(final Long userId, final Long giftId, final Long allocatedGiftId);
}
