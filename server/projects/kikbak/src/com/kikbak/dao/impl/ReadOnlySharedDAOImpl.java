package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Shared;

@Repository
public class ReadOnlySharedDAOImpl extends ReadOnlyGenericDAOImpl<Shared, Long> implements ReadOnlySharedDAO{

    static final String SHARES_FOR_NEW_GIFTS = "select * from shared where "
            // not self shared
            + "user_id != :userId AND "
            // offer is still active
            + "offer_id in (select id from offer where begin_date < now() and end_date > now()) AND "
            // user has not redeemed that gift yet
            + "offer_id not in (select distinct offer_id from allocatedgift where user_id = :userId and redemption_date is not null) AND "
            // user is actually a friend of person who shared
            + "user_id in (select user2friend.user_id from user,user2friend where user.id = :userId and user.facebook_id = user2friend.facebook_friend_id) AND "
            // gift was not created yet
            + "id in (select shared.id from shared left join allocatedgift on :userId = allocatedgift.user_id and shared.offer_id = allocatedgift.offer_id and shared.user_id = allocatedgift.friend_user_id where allocatedgift.offer_id is null and shared.user_id != :userId) "
            // then group to filter multiple shares
            + "group by offer_id, user_id";

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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Shared> listSharesForNewGifts(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Shared> shared = session.createSQLQuery(SHARES_FOR_NEW_GIFTS).addEntity(Shared.class)
                .setLong("userId", userId).list();
        return shared;
    }

}
