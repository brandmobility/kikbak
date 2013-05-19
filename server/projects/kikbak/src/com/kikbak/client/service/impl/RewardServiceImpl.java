package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.RedemptionException;
import com.kikbak.client.service.RewardService;
import com.kikbak.client.service.impl.types.TransactionType;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteGiftDAO;
import com.kikbak.dao.ReadWriteKikbakDAO;
import com.kikbak.dao.ReadWriteTransactionDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Shared;
import com.kikbak.dto.Transaction;
import com.kikbak.dto.User;
import com.kikbak.jaxb.ClientLocationType;
import com.kikbak.jaxb.ClientMerchantType;
import com.kikbak.jaxb.GiftRedemptionType;
import com.kikbak.jaxb.GiftType;
import com.kikbak.jaxb.KikbakRedemptionType;
import com.kikbak.jaxb.KikbakType;
import com.kikbak.push.service.ApsNotifier;

@Service
public class RewardServiceImpl implements RewardService{

	@Autowired
	ReadOnlyGiftDAO roGiftDao;
	
	@Autowired
	ReadWriteGiftDAO rwGiftDao;
	
	@Autowired
	ReadOnlyKikbakDAO roKikbakDao;
	
	@Autowired
	ReadWriteKikbakDAO rwKikbakDao;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDao;
	
	@Autowired
	ReadOnlyUserDAO roUserDao;
	
	@Autowired
	ReadOnlyMerchantDAO roMerchantDao;
	
	@Autowired
	ReadWriteTransactionDAO rwTxnDao;
	
	@Autowired
	ReadOnlySharedDAO roSharedDao;
	
	@Autowired
	ReadOnlyLocationDAO roLocationDao;
	
	@Autowired
	ApsNotifier apsNotifier;
	
	@Autowired
	ReadOnlyDeviceTokenDAO roDeviceToken;
	
	
	private final SecureRandom random = new SecureRandom();
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Collection<GiftType> getGifts(Long userId) {
		createGifts(userId);
		Collection<Gift> gifts = new ArrayList<Gift>();
		gifts.addAll(roGiftDao.listValidByUserId(userId));
		Collection<GiftType> gts = new ArrayList<GiftType>();
		
		for( Gift gift: gifts){
			GiftType gt = new GiftType();
			gt.setId(gift.getId());
			
			Merchant merchant = roMerchantDao.findById(gift.getMerchantId());
			ClientMerchantType cmt = fillClientMerchantType(merchant);
			gt.setMerchant(cmt);
			
			Offer offer = roOfferDao.findById(gift.getOfferId());
			gt.setDescription(offer.getGiftDescription());
			gt.setName(offer.getGiftName());
			gt.setFriendUserId(gift.getFriendUserId());
			gt.setFbFriendId(roUserDao.findById(gift.getFriendUserId()).getFacebookId());
			Shared shared = roSharedDao.findById(gift.getId());
			gt.setFbImageId(shared.getFbImageId());
			
			gts.add(gt);
		}
		
		return gts;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<KikbakType> getKikbaks(Long userId) {
		Collection<Kikbak> kikbaks = roKikbakDao.listKikbaksWithValue(userId);
		Collection<KikbakType> kts = new ArrayList<KikbakType>();
		
		for( Kikbak kikbak : kikbaks){
			KikbakType kt = new KikbakType();
			kt.setValue(kikbak.getValue());
			kt.setId(kikbak.getId());
			
			Merchant merchant = roMerchantDao.findById(kikbak.getMerchantId());
			ClientMerchantType cmt = fillClientMerchantType(merchant);
			kt.setMerchant(cmt);
			
			Offer offer = roOfferDao.findById(kikbak.getOfferId());
			kt.setDescription(offer.getKikbakDescription());
			kt.setName(offer.getKikbakName());
			
			kts.add(kt);
		}
		
		return kts;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String registerGiftRedemption(final Long userId, GiftRedemptionType giftType) throws Exception {
		
		Location location = roLocationDao.findById(giftType.getLocationId());
		if( !location.getVerificationCode().equals(giftType.getVerificationCode())){
			throw new RedemptionException("Invalid verifcation code");
		}
		
		Gift gift = roGiftDao.findById(giftType.getId());
		gift.setRedemptionDate(new Date());
		gift.setValue(0.0);
		
		rwGiftDao.makePersistent(gift);
		
		KikbakManager km = new KikbakManager(roOfferDao, roKikbakDao, rwKikbakDao, rwTxnDao);
		km.manageKikbak(giftType.getFriendUserId(), gift.getOfferId(), gift.getMerchantId(), giftType.getLocationId());
		
		//send notification for kikbak when gift is redeemed
		Devicetoken token = roDeviceToken.findByUserId(userId);
		if( token != null){
			Offer offer = roOfferDao.findById(gift.getOfferId());
			apsNotifier.sendNotification(token, offer.getKikbakNotificationText());
		}
		
		return generateAuthorizationCode();
	}

	@Override
	public String registerKikbakRedemption(final Long userId, KikbakRedemptionType kikbakType) throws Exception {
		
		Location location = roLocationDao.findById(kikbakType.getLocationId());
		if( !location.getVerificationCode().equals(kikbakType.getVerificationCode())){
			throw new RedemptionException("Invalid verifcation code");
		}
		
		Kikbak kikbak = roKikbakDao.findById(kikbakType.getId());
		if( kikbakType.getAmount() > kikbak.getValue() ){
			throw new RedemptionException("Value of attempted redemption is greater then remaining value");
		}
		
		kikbak.setValue(kikbak.getValue() - kikbakType.getAmount());
		
		Transaction txn = new Transaction();
		txn.setAmount(kikbakType.getAmount());
		txn.setVerificationCode(kikbakType.getVerificationCode());
		txn.setMerchantId(kikbak.getMerchantId());
		txn.setLocationId(txn.getLocationId());
		txn.setUserId(userId);
		txn.setTransactionType((short)TransactionType.Credit.ordinal());
		txn.setOfferId(kikbak.getOfferId());
		txn.setKikbakId(kikbak.getId());
		txn.setDate(new Date());
		txn.setAuthorizationCode(generateAuthorizationCode());
		
		rwKikbakDao.makePersistent(kikbak);
		rwTxnDao.makePersistent(txn);
		
		return txn.getAuthorizationCode();
	}
	
	
	protected void createGifts(Long userId){

		Collection<Long> offerIds = roGiftDao.listOfferIdsForUser(userId);
		User user = roUserDao.findById(userId);
		Collection<Shared> shareds = roSharedDao.listAvailableForGifting(user.getFacebookId());
		Collection<Gift> newGifts = new ArrayList<Gift>();
		for(Shared shared : shareds){
			if(!offerIds.contains(shared.getOfferId())){
				Offer offer = roOfferDao.findById(shared.getOfferId());
				Gift gift = new Gift();
				gift.setExperirationDate(offer.getEndDate());
				gift.setFriendUserId(shared.getUserId());
				gift.setMerchantId(shared.getMerchantId());
				gift.setOfferId(shared.getOfferId());
				gift.setUserId(userId);
				gift.setSharedId(shared.getId());
				gift.setValue(offer.getGiftValue());

				rwGiftDao.makePersistent(gift);
				newGifts.add(gift);
				
				offerIds.add(shared.getOfferId());
			}
		}
	}

	
	protected String generateAuthorizationCode(){
		return new BigInteger(16,random).toString(16);
	}
	
	protected ClientMerchantType fillClientMerchantType(Merchant merchant){
		ClientMerchantType cmt = new ClientMerchantType();
		cmt.setId(merchant.getId());
		cmt.setName(merchant.getName());
		cmt.setUrl(merchant.getUrl());

		Collection<Location> locations = roLocationDao.listByMerchant(merchant.getId());
		for(Location location: locations){
			ClientLocationType clt = new ClientLocationType();
			clt.setLocationId(location.getId());
			clt.setLatitude(location.getLatitude());
			clt.setLongitude(location.getLongitude());
			clt.setPhoneNumber(location.getPhoneNumber());
			cmt.getLocations().add(clt);
		}
		return cmt;
	}
}
