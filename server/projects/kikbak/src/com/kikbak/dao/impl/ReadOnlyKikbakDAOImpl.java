package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Kikbak;

public class ReadOnlyKikbakDAOImpl extends ReadOnlyGenericDAOImpl<Kikbak, Long> implements ReadOnlyKikbakDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Kikbak> listByUserId(Long userId) {
		return listByCriteria(Restrictions.eq("userId", userId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Kikbak> listByMerchantId(Long merchantId) {
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Kikbak> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}

	@Override
	public Kikbak findByUserIdAndOfferId(Long userId, Long offerId) {
		return findByCriteria(Restrictions.and(Restrictions.eq("userId", userId), Restrictions.eq("offerId", offerId)));
	}

}
