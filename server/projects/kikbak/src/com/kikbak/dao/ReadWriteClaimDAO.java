package com.kikbak.dao;

import com.kikbak.dto.Claim;

public interface ReadWriteClaimDAO {
    public void makePersistent(Claim kikbak);
    public void makeTransient(Claim kikbak);
}
