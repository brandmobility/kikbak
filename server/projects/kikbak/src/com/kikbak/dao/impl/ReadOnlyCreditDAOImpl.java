package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyCreditDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Credit;

public class ReadOnlyCreditDAOImpl extends ReadOnlyGenericDAOImpl<Credit, Long> implements ReadOnlyCreditDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Credit> listCreditsWithBalance(Long userId) {
		return listByCriteria(Restrictions.and(Restrictions.eq("userId", userId), Restrictions.gt("value", 0.0)));
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Credit> listByMerchantId(Long merchantId) {
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Credit> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Credit findByUserIdAndOfferId(Long userId, Long offerId) {
		return findByCriteria(Restrictions.and(Restrictions.eq("userId", userId), Restrictions.eq("offerId", offerId)));
	}

}
