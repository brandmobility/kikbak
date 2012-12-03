package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Gift;

public class ReadOnlyGiftDAOImpl extends ReadOnlyGenericDAOImpl<Gift, Long> implements ReadOnlyGiftDAO {

	private static final String gift_offer_ids = "select offer_id from Gift where user_id=?";
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Gift> listByUserId(Long userId) {
		return listByCriteria(Restrictions.eq("userId", userId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Gift> listByMerchantId(Long merchantId) {
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Gift> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> listOfferIdsForUser(Long userId) {
		return sessionFactory.getCurrentSession().createSQLQuery(gift_offer_ids).setLong(0, userId).list();
	}

	
}
