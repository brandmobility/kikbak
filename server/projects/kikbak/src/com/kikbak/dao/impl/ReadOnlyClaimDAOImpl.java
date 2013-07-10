package com.kikbak.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyClaimDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Claim;

@Repository
public class ReadOnlyClaimDAOImpl extends ReadOnlyGenericDAOImpl<Claim, Long> implements ReadOnlyClaimDAO{

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public List<Claim> listByMerchant(Long merchandId) {
        return (List<Claim>) listByCriteria(Restrictions.eq("merchantId", merchandId));
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public List<Claim> listByOfferId(Long offerId) {
        return (List<Claim>) listByCriteria(Restrictions.eq("offerId", offerId));
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public List<Claim> listByUserid(Long userId) {
        return (List<Claim>) listByCriteria(Restrictions.eq("userId", userId));
    }

}
