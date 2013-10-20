package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.v1.ReferralCodeUniqueException;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Shared;

@Repository
public class ReadWriteSharedDAOImpl extends GenericDAOImpl<Shared, Long> implements ReadWriteSharedDAO {

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Collection<Shared> listByUserId(Long userId) {
        return listByCriteria(Restrictions.eq("userId", userId));
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Collection<Shared> listByLocationId(Long locationId) {
        return listByCriteria(Restrictions.eq("locationId", locationId));
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Collection<Shared> listByUserIdAndOfferId(Long userId, Long offerId) {
        return listByCriteria(Restrictions.and(Restrictions.eq("userId", userId),Restrictions.eq("offerId", offerId)));
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Shared findAvailableForGiftingByReferralCode(String referralCode) {
        return findByCriteria(Restrictions.eq("referralCode", referralCode));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void saveShared(Shared shared) throws ReferralCodeUniqueException {
        Session session = getSessionFactory().getCurrentSession();
        try {
            session.saveOrUpdate(shared);
        } catch (ConstraintViolationException e) {
            session.clear();
            throw new ReferralCodeUniqueException("violite unique constraint");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Shared> listSharesForNewGifts(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Shared> shared = session.createSQLQuery(ReadOnlySharedDAOImpl.SHARES_FOR_NEW_GIFTS)
                .addEntity(Shared.class).setLong("userId", userId).list();
        return shared;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Shared findLastShareByUserAndOffer(long userId, long offerId) {
    	
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(getPersistentClass());
        crit.add(Restrictions.and(Restrictions.eq("userId", userId),Restrictions.eq("offerId", offerId)));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("sharedDate"));
        @SuppressWarnings("unchecked")
		Collection<Shared> shareds = crit.list();
        
        return shareds == null ? null : shareds.isEmpty() ? null : shareds.iterator().next();
    }
}
