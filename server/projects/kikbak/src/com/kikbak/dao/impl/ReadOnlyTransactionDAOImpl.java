package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyTransactionDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Transaction;

public class ReadOnlyTransactionDAOImpl extends ReadOnlyGenericDAOImpl<Transaction, Long> implements ReadOnlyTransactionDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByKikbakId(Long kikbakId) {
		return listByCriteria(Restrictions.eq("kikbakId", kikbakId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByMerchantId(Long merchantId) {
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByLocationId(long locationId) {
		return listByCriteria(Restrictions.eq("locationId", locationId));
	}

}
