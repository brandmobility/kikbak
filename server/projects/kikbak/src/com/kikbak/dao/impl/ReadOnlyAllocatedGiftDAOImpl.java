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

	private static final String gift_offer_ids = "select offer_id from allocatedgift where user_id=?";
	
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
	public Collection<Long> listOfferIdsForUser(Long userId) {
		return sessionFactory.getCurrentSession().createSQLQuery(gift_offer_ids).addScalar("offer_id",  StandardBasicTypes.LONG ).setLong(0, userId).list();
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Allocatedgift> listByFriendUserId(Long friendId) {
		return listByCriteria(Restrictions.eq("friendUserId", friendId));
	}

	
}
