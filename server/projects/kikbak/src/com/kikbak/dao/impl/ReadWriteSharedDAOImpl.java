package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.ReferralCodeUniqueException;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Shared;

@Repository
public class ReadWriteSharedDAOImpl extends GenericDAOImpl<Shared, Long> implements ReadWriteSharedDAO {

    private static final String find_gifts="select shared.* from offer, shared where begin_date < now() and end_date > now() " + 
            "and offer.id=shared.offer_id and offer.id in ( select offer_id from shared where user_id in " +
            "(select user_id from user2friend where facebook_friend_id=?) group by offer_id) " +
            "and user_id in (select user_id from user2friend where facebook_friend_id=?)";

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
    public Collection<Shared> listAvailableForGifting(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Shared> shared = session.createSQLQuery(find_gifts).addEntity(Shared.class).setLong(0, userId).setLong(1, userId).list();
        return shared;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Collection<Shared> listAvailableForGiftingByReferralCode(String referralCode) {
        return listByCriteria(Restrictions.eq("referralCode", referralCode));
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
}
