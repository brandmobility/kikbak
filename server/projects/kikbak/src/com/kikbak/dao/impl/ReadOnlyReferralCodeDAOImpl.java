package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyReferralCodeDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Referralcode;

@Repository
public class ReadOnlyReferralCodeDAOImpl extends ReadOnlyGenericDAOImpl<Referralcode, Long> implements ReadOnlyReferralCodeDAO{

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Referralcode findByCode(String code) {
        return findByCriteria(Restrictions.eq("code", code));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Referralcode findBySharedId(Long sharedId) {
        return findByCriteria(Restrictions.eq("sharedId", sharedId));
    }

}
