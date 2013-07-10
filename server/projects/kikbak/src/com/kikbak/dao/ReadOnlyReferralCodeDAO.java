package com.kikbak.dao;

import com.kikbak.dto.Referralcode;

public interface ReadOnlyReferralCodeDAO {

    public Referralcode findById(Long id);
    public Referralcode findByCode(String code);
    public Referralcode findBySharedId(Long sharedId);
}
