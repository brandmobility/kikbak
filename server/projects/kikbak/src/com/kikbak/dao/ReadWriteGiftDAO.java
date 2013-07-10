package com.kikbak.dao;

import com.kikbak.dto.Gift;

public interface ReadWriteGiftDAO {

    public void makePersistent(Gift gift);
    public void makeTransient(Gift gift);
}
