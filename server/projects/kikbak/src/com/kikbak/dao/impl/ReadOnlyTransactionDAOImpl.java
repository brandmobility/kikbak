package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.impl.types.TransactionType;
import com.kikbak.dao.ReadOnlyTransactionDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Transaction;

public class ReadOnlyTransactionDAOImpl extends ReadOnlyGenericDAOImpl<Transaction, Long> implements ReadOnlyTransactionDAO{

	private static final String count_of_gift_redemptions = "select count(id) from transaction where user_id=? and merchant_id=? and transaction_type=?";
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByOfferId(Long offerId) {
		return listByCriteria(Restrictions.eq("offerId", offerId));
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Transaction> listByUserId(Long userId){
		return listByCriteria(Restrictions.eq("userId", userId));
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
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Integer countOfGiftsRedeemedByUserByMerchant(Long userId, Long merchantId){
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createSQLQuery(count_of_gift_redemptions).addScalar("count(id)",  StandardBasicTypes.LONG )
        															    .setLong(0, userId)
        															   .setLong(1, merchantId)
        															   .setShort(2, (short)TransactionType.Credit.ordinal());
        return ((Long) query.uniqueResult()).intValue();
	}

}
