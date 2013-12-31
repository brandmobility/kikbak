package com.kikbak.dao;

public interface ReadOnlyZipcodeDAO {
    public boolean isValidZipcode(long merchantId, String zipCode);
}
