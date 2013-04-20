package com.kikbak.client.service.impl;


import java.util.Date;

import com.kikbak.client.service.impl.types.TransactionType;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadWriteKikbakDAO;
import com.kikbak.dao.ReadWriteTransactionDAO;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Transaction;

public class KikbakManager {

	private final ReadOnlyKikbakDAO roKikbakDao;
	private final ReadWriteKikbakDAO rwKikbakDao;
	private final ReadWriteTransactionDAO rwTransactionDao;
	private final ReadOnlyOfferDAO roOfferDao;
	
	public KikbakManager(ReadOnlyOfferDAO roOfferDao, ReadOnlyKikbakDAO roKikbakDao, 
						ReadWriteKikbakDAO rwKikbakDao, ReadWriteTransactionDAO rwTransactionDao){
		
		this.roKikbakDao = roKikbakDao;
		this.roOfferDao = roOfferDao;
		this.rwKikbakDao = rwKikbakDao;
		this.rwTransactionDao = rwTransactionDao;
	}
	
	public void manageKikbak(final Long userId, final Long offerId, final Long merchantId, final Long locationId){
		Kikbak kikbak = roKikbakDao.findByUserIdAndOfferId(userId, offerId);
		if( kikbak == null){
			createKikbak(userId, offerId, merchantId, locationId);
		}
		else{
			updateKikbak(kikbak, offerId, merchantId, locationId);
		}
	}
	
	protected void updateKikbak(Kikbak kikbak, final Long offerId, final Long merchantId, final Long locationId){
		Offer offer = roOfferDao.findById(offerId);
		kikbak.setValue(offer.getKikbakValue() + kikbak.getValue());
		
		Transaction txn = new Transaction();
		txn.setAmount(offer.getKikbakValue());
		txn.setAuthorizationCode("newshare");
		txn.setLocationId(locationId);
		txn.setMerchantId(merchantId);
		txn.setOfferId(offerId);
		txn.setTransactionType((short)TransactionType.Deposit.ordinal());
		txn.setVerificationCode("newshare");
		txn.setUserId(kikbak.getUserId());
		txn.setDate(new Date());
		
		rwKikbakDao.makePersistent(kikbak);
		rwTransactionDao.makePersistent(txn);
	}
	
	protected void createKikbak(final Long userId, final Long offerId, final Long merchantId, final Long locationId){
		Offer offer = roOfferDao.findById(offerId);
		Kikbak kikbak = new Kikbak();
		kikbak.setBeginDate(offer.getBeginDate());
		kikbak.setEndDate(offer.getEndDate());
		kikbak.setLocationId(locationId);
		kikbak.setMerchantId(offer.getMerchantId());
		kikbak.setOfferId(offerId);
		kikbak.setUserId(userId);
		kikbak.setValue(offer.getKikbakValue());
		
		Transaction txn = new Transaction();
		txn.setAmount(kikbak.getValue());
		txn.setAuthorizationCode("created");
		txn.setLocationId(locationId);
		txn.setMerchantId(merchantId);
		txn.setOfferId(offerId);
		txn.setTransactionType((short)TransactionType.Deposit.ordinal());
		txn.setVerificationCode("created");
		txn.setUserId(userId);
		txn.setDate(new Date());
		
		rwKikbakDao.makePersistent(kikbak);
		rwTransactionDao.makePersistent(txn);
	}
}
