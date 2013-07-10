package com.kikbak.dao;

import com.kikbak.dto.Kikbak;

public interface ReadWriteKikbakDAO {
    
    public void makePersistent(Kikbak kikbak);
    public void makeTransient(Kikbak kikbak);
}
