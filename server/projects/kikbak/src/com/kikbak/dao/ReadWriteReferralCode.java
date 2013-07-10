package com.kikbak.dao;

import com.kikbak.dto.Referralcode;

public interface ReadWriteReferralCode {

    public void makePersistent(Referralcode offer);
    public void makeTransient(Referralcode offer);

}
