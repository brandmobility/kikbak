package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Allocatedgift;

@Repository
public class ReadOnlyAllocatedGiftDAOImpl extends ReadOnlyGenericDAOImpl<Allocatedgift, Long> implements ReadOnlyAllocatedGiftDAO {

	private static final String gift_shared_ids = "select shared_id from allocatedgift where user_id=?";
	private static final String get_valid_offer_ids = "select * from allocatedgift where user_id=? and offer_id not in (select offer_id from allocatedgift where user_id=? and redemption_date is not null)";
	private static final String is_gift_available = "select offer_id from allocatedgift where user_id=? and offer_id=? and redemption_date is not null";
	private static final String get_gift_value = "select value from allocatedgift where user_id=? and offer_id=? limit 1";
	private static final String count_of_redeemed_shares = "select count(*) from allocatedgift where friend_user_id=? and offer_id=? and redemption_date is not null";

        @SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listValidByUserId(Long userId) {
		return sessionFactory.getCurrentSession().createSQLQuery(get_valid_offer_ids).addEntity(Allocatedgift.class).setLong(0, userId).setLong(1, userId).list();
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listByMerchantId(Long merchantId) {
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Long> listSharedIdsForUser(Long userId) {
		return sessionFactory.getCurrentSession().createSQLQuery(gift_shared_ids).addScalar("shared_id",  StandardBasicTypes.LONG ).setLong(0, userId).list();
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listByFriendUserId(Long friendId) {
		return listByCriteria(Restrictions.eq("friendUserId", friendId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listValidByUserIdAndSharedId(Long userId, long sharedId) {
		return listByCriteria(Restrictions.eq("userId", userId), Restrictions.eq("sharedId", sharedId));
	}

	@Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public boolean isGiftAvailable(Long userId, Long offerId) {
	    @SuppressWarnings("unchecked")
        Collection<Long> ids = sessionFactory.getCurrentSession().createSQLQuery(is_gift_available).addEntity(Allocatedgift.class).setLong(0, userId).setLong(1, offerId).list();
	    //if list is empty it means there was a gift redeemed
	    return ids.isEmpty() ? true:false;
	}
	
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Double getAllocatedGiftValue(Long userId, Long offerId) {
        @SuppressWarnings("unchecked")
        Collection<Double> values = sessionFactory.getCurrentSession().createSQLQuery(get_gift_value).setLong(0, userId).setLong(1, offerId).list();
        return values.isEmpty() ? null : values.iterator().next();
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Long countOfRedeemedShares(Long friendId, Long offerId){


    	return l;
    }
}
