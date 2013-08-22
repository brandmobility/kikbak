package com.kikbak.dao.impl;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Allocatedgift;

@Repository
public class ReadOnlyAllocatedGiftDAOImpl extends ReadOnlyGenericDAOImpl<Allocatedgift, Long> implements ReadOnlyAllocatedGiftDAO {

	private static final String gift_shared_ids = "select shared_id from allocatedgift where user_id=?";
	private static final String gift_offer_ids = "select offer_id, friend_user_id from allocatedgift where user_id=?";
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listValidByUserId(Long userId) {
		return listByCriteria(Restrictions.and(Restrictions.eq("userId", userId), Restrictions.isNull("redemptionDate")));
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
	public Multimap<Long,Long> listOfferIdsByFriendsForUser(Long userId){
	    @SuppressWarnings("rawtypes")
        List results = sessionFactory.getCurrentSession().createSQLQuery(gift_offer_ids).addScalar("offer_id",  StandardBasicTypes.LONG ).addScalar("friend_user_id",  StandardBasicTypes.LONG ).setLong(0, userId).list();
	    Multimap<Long, Long> mmap = HashMultimap.create();
	    for( Object obj : results) {
	        Object[] arr = (Object[])obj;
	        mmap.put((Long)arr[0], (Long)arr[1]);
	    }
	    return mmap;
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
	
}
