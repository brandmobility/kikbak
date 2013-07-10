package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Gift;

@Repository
public class ReadOnlyGiftDAOImpl extends ReadOnlyGenericDAOImpl<Gift, Long> implements ReadOnlyGiftDAO{

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Gift findByOfferId(Long offerId) {
        return findByCriteria(Restrictions.eq("offerId", offerId));
    }

}
