package com.kikbak.client.service.impl;


import java.util.Date;

import com.kikbak.client.service.impl.types.TransactionType;
import com.kikbak.dao.ReadOnlyCreditDAO;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadWriteCreditDAO;
import com.kikbak.dao.ReadWriteTransactionDAO;
import com.kikbak.dto.Credit;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Transaction;

public class CreditManager {

	private final ReadOnlyCreditDAO roCreditDao;
	private final ReadWriteCreditDAO rwCreditDao;
	private final ReadWriteTransactionDAO rwTransactionDao;
	private final ReadOnlyKikbakDAO roKikbakDao;
	private final ReadOnlyOfferDAO roOfferDao;
	
	public CreditManager(ReadOnlyOfferDAO roOfferDao, ReadOnlyKikbakDAO roKikbakDao, ReadOnlyCreditDAO roCreditDao, 
						ReadWriteCreditDAO rwCreditDao, ReadWriteTransactionDAO rwTransactionDao){
		
		this.roCreditDao = roCreditDao;
		this.roOfferDao = roOfferDao;
		this.roKikbakDao = roKikbakDao;
		this.rwCreditDao = rwCreditDao;
		this.rwTransactionDao = rwTransactionDao;
	}
	
	public Long manageCredit(final Long userId, final Long offerId, final Long merchantId, final Long locationId){
		Credit credit = roCreditDao.findByUserIdAndOfferId(userId, offerId);
		if( credit == null){
			return createCredit(userId, offerId, merchantId, locationId);
		}
		else{
			return updateCreditAvailable(credit, offerId, merchantId, locationId);
		}
	}
	
	protected Long updateCreditAvailable(Credit credit, final Long offerId, final Long merchantId, final Long locationId){
        Offer offer = roOfferDao.findById(offerId);
		if (credit.getRedeemCount() + 1 > offer.getRedeemLimit()) {
		    // exceed limit, no transaction and credit update
		    return null;
		}
	    
	    Kikbak kikbak = roKikbakDao.findByOfferId(offerId);
		credit.setValue(kikbak.getValue() + credit.getValue());
		
		Transaction txn = new Transaction();
		txn.setAmount(kikbak.getValue());
		txn.setAuthorizationCode("newshare");
		txn.setLocationId(locationId);
		txn.setMerchantId(merchantId);
		txn.setOfferId(offerId);
		txn.setTransactionType((short)TransactionType.Credit.ordinal());
		txn.setVerificationCode("newshare");
		txn.setUserId(credit.getUserId());
		txn.setDate(new Date());
		
		rwCreditDao.makePersistent(credit);
		rwTransactionDao.makePersistent(txn);
		
		return credit.getId();
	}
	
	protected Long createCredit(final Long userId, final Long offerId, final Long merchantId, final Long locationId){
	    Kikbak kikbak = roKikbakDao.findByOfferId(offerId);
	    Offer offer = roOfferDao.findById(offerId);
	    Credit credit = new Credit();
		credit.setBeginDate(offer.getBeginDate());
		credit.setEndDate(offer.getEndDate());
		credit.setLocationId(locationId);
		credit.setMerchantId(offer.getMerchantId());
		credit.setOfferId(offerId);
		credit.setUserId(userId);
		credit.setValue(kikbak.getValue());
		credit.setRewardType(kikbak.getRewardType());
		credit.setKikbakId(kikbak.getId());
		credit.setRedeemCount(1);
		credit.setCreatedDate(new Date());
		
		Transaction txn = new Transaction();
		txn.setAmount(credit.getValue());
		txn.setAuthorizationCode("created");
		txn.setLocationId(locationId);
		txn.setMerchantId(merchantId);
		txn.setOfferId(offerId);
		txn.setTransactionType((short)TransactionType.Credit.ordinal());
		txn.setVerificationCode("created");
		txn.setUserId(userId);
		txn.setDate(new Date());
		
		rwCreditDao.makePersistent(credit);
		rwTransactionDao.makePersistent(txn);
		
		return credit.getId();
	}
}
