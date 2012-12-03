package com.kikbak.client.service.impl;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.client.service.impl.types.TransactionType;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadWriteKikbakDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dao.ReadWriteTransactionDAO;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Shared;
import com.kikbak.dto.Transaction;
import com.kikbak.jaxb.SharedType;

public class SharedExperienceServiceImpl implements SharedExperienceService {

	@Autowired
	ReadOnlySharedDAO roSharedDao;
	
	@Autowired
	ReadWriteSharedDAO rwSharedDao;
	
	@Autowired
	ReadOnlyKikbakDAO roKikbakDAO;
	
	@Autowired
	ReadWriteKikbakDAO rwKikbakDAO;
		
	@Autowired
	ReadWriteTransactionDAO rwTransactionDAO;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDAO;
	
	@Autowired
	ReadOnlyUser2FriendDAO roU2FDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void registerSharing(final Long userId, SharedType experience) {
		//persist share
		persistExperience(userId, experience);
		
		//award the latest friend who shared the award with you.
		Collection<Long> friends = roU2FDao.listFriendsForUser(userId);
		if( friends != null){
			Long friendToAwardId = null;
			Date latest = null;
			for( Long friendId : friends){
				Collection<Shared> shares = roSharedDao.listByUserIdAndOfferId(userId, experience.getOfferId());
				if( shares != null ){
					for(Shared share : shares){
						//if nothing to share
						if( latest == null ){
							latest = share.getSharedDate();
							friendToAwardId = friendId;
						}
						else if( latest.getTime() < share.getSharedDate().getTime()){
							latest = share.getSharedDate();
							friendToAwardId = friendId;
						}
					}
				}
			}
			
			if( friendToAwardId != null){
				manageKikbak(friendToAwardId, experience);
			}
		}
	}

	protected void persistExperience(final Long userId, SharedType experience){
		Shared shared = new Shared();
		shared.setLocationId(experience.getLocationId());
		shared.setMerchantId(experience.getMerchantId());
		shared.setOfferId(experience.getOfferId());
		shared.setUserId(userId);
		shared.setSharedDate(new Date());
		
		rwSharedDao.makePersistent(shared);
	}
	
	protected void manageKikbak(final Long userId, SharedType experience){
		Kikbak kikbak = roKikbakDAO.findByUserIdAndOfferId(userId, experience.getOfferId());
		if( kikbak == null){
			createKikbak(userId, experience);
		}
		else{
			updateKikbak(kikbak, experience);
		}
	}
	
	protected void updateKikbak(Kikbak kikbak, SharedType experience){
		Offer offer = roOfferDAO.findById(kikbak.getOfferId());
		kikbak.setValue(offer.getKikbakValue() + kikbak.getValue());
		
		Transaction txn = new Transaction();
		txn.setAmount(offer.getKikbakValue());
		txn.setAuthorizationCode("newshare");
		txn.setLocationId(experience.getLocationId());
		txn.setMerchantId(experience.getMerchantId());
		txn.setOfferId(experience.getOfferId());
		txn.setTransactionType((short)TransactionType.Deposit.ordinal());
		txn.setVerificationCode("newshare");
		txn.setUserId(kikbak.getUserId());
		
		rwKikbakDAO.makePersistent(kikbak);
		rwTransactionDAO.makePersistent(txn);
	}
	
	protected void createKikbak(final Long userId, SharedType experience){
		Offer offer = roOfferDAO.findById(experience.getOfferId());
		Kikbak kikbak = new Kikbak();
		kikbak.setBeginDate(offer.getBeginDate());
		kikbak.setEndDate(offer.getEndDate());
		kikbak.setLocationId(0);
		kikbak.setMerchantId(offer.getMerchantId());
		kikbak.setValue(offer.getKikbakValue());
		
		Transaction txn = new Transaction();
		txn.setAmount(kikbak.getValue());
		txn.setAuthorizationCode("created");
		txn.setLocationId(experience.getLocationId());
		txn.setMerchantId(experience.getMerchantId());
		txn.setOfferId(experience.getOfferId());
		txn.setTransactionType((short)TransactionType.Deposit.ordinal());
		txn.setVerificationCode("created");
		txn.setUserId(userId);
		
		rwKikbakDAO.makePersistent(kikbak);
		rwTransactionDAO.makePersistent(txn);
	}
}
