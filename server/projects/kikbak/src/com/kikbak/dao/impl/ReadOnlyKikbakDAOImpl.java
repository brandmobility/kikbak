package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Kikbak;

@Repository
public class ReadOnlyKikbakDAOImpl extends ReadOnlyGenericDAOImpl<Kikbak, Long> implements ReadOnlyKikbakDAO{

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Kikbak findByOfferId(Long offerId) {
        return findByCriteria(Restrictions.eq("offerId", offerId));
    }

}
