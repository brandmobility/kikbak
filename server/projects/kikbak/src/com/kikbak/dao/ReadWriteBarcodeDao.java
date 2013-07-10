package com.kikbak.dao;

import com.kikbak.dto.Barcode;

public interface ReadWriteBarcodeDao {
    public void makePersistent(Barcode kikbak);
    public void makeTransient(Barcode kikbak);
}
