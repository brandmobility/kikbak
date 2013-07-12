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
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.ReadOnlyBarcodeDAO;
import com.kikbak.dao.ReadOnlyCreditDAO;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyTransactionDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteAllocatedGiftDAO;
import com.kikbak.dao.ReadWriteBarcodeDao;
import com.kikbak.dao.ReadWriteClaimDAO;
import com.kikbak.dao.ReadWriteCreditDAO;
import com.kikbak.dao.ReadWriteTransactionDAO;
import com.kikbak.dto.Allocatedgift;
import com.kikbak.dto.Barcode;
import com.kikbak.dto.Claim;
import com.kikbak.dto.Credit;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Shared;
import com.kikbak.dto.Transaction;
import com.kikbak.dto.User;
import com.kikbak.jaxb.claim.ClaimType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionResponseType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.rewards.AvailableCreditType;
import com.kikbak.jaxb.rewards.ClientMerchantType;
import com.kikbak.jaxb.rewards.GiftType;
import com.kikbak.jaxb.userlocation.UserLocationType;
import com.kikbak.push.service.ApsNotifier;

@Service
public class RewardServiceImpl implements RewardService{

    @Autowired
    ReadOnlyKikbakDAO roKikbakDAO;
    
	@Autowired
	ReadOnlyAllocatedGiftDAO roAllocatedGiftDao;
	
	@Autowired
	ReadOnlyGiftDAO roGiftDao;
	
	@Autowired
	ReadWriteAllocatedGiftDAO rwGiftDao;
	
	@Autowired
	ReadOnlyCreditDAO roCreditDao;
	
	@Autowired
	ReadWriteCreditDAO rwKikbakDao;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDao;
	
	@Autowired
	ReadOnlyUserDAO roUserDao;
	
	@Autowired
	ReadOnlyMerchantDAO roMerchantDao;
	
	@Autowired
	ReadOnlyTransactionDAO roTxnDao;
	
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
	
	@Autowired
	ReadWriteClaimDAO rwClaimDao;
	
	@Autowired
	ReadWriteBarcodeDao rwBarcodeDao;
	
	@Autowired 
	ReadOnlyBarcodeDAO roBarcodeDao;
	
	private final SecureRandom random = new SecureRandom();
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Collection<GiftType> getGifts(Long userId) {
		createGifts(userId);
		Collection<Allocatedgift> gifts = new ArrayList<Allocatedgift>();
		gifts.addAll(roAllocatedGiftDao.listValidByUserId(userId));
		Collection<GiftType> gts = new ArrayList<GiftType>();
		
		for( Allocatedgift ag: gifts){
			GiftType gt = new GiftType();
			gt.setId(ag.getId());
			
			Merchant merchant = roMerchantDao.findById(ag.getMerchantId());
			ClientMerchantType cmt = fillClientMerchantType(merchant);
			gt.setMerchant(cmt);
			gt.setFriendUserId(ag.getFriendUserId());
			
			Gift gift = roGiftDao.findById(ag.getGiftId());
			gt.setDesc(gift.getDescription());
			gt.setDetailedDesc(gift.getDetailedDesc());
			gt.setValue(gift.getValue());
			gt.setDiscountType(gift.getDiscountType());
			gt.setValidationType(gift.getValidationType());
			gt.setRedemptionLocationType(gift.getRedemptionLocationType());
			gt.setImageUrl(gift.getDefaultGiveImageUrl());
			gt.setDefaultGiveImageUrl(gift.getDefaultGiveImageUrl());
			
			Offer offer = roOfferDao.findById(gift.getOfferId());
			gt.setTosUrl(offer.getTosUrl());
			
			User friend = roUserDao.findById(ag.getFriendUserId());
			gt.setFbFriendId(friend.getFacebookId());
			gt.setFriendName(friend.getFirstName() + " " + friend.getLastName());
			Shared shared = roSharedDao.findById(ag.getSharedId());
			gt.setCaption(shared.getCaption());
			gt.setFbImageId(shared.getFbImageId());
			
			gts.add(gt);
		}
		
		return gts;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<AvailableCreditType> getCredits(Long userId) {
		Collection<Credit> credits = roCreditDao.listCreditsWithBalance(userId);
		Collection<AvailableCreditType> acts = new ArrayList<AvailableCreditType>();
		
		for( Credit credit : credits){
		    AvailableCreditType ac = new AvailableCreditType();
			ac.setValue(credit.getValue());
			ac.setId(credit.getId());
			
			Merchant merchant = roMerchantDao.findById(credit.getMerchantId());
			ClientMerchantType cmt = fillClientMerchantType(merchant);
			ac.setMerchant(cmt);
			
			Kikbak kikbak = roKikbakDAO.findByOfferId(credit.getOfferId());
			Offer offer = roOfferDao.findById(credit.getOfferId());
			ac.setDesc(kikbak.getDescription());
			ac.setDetailedDesc(kikbak.getDetailedDesc());
			ac.setRewardType(kikbak.getRewardType());
			ac.setValidationType(kikbak.getValidationType());
			ac.setTosUrl(offer.getTosUrl());
			ac.setImageUrl(kikbak.getImageUrl());
			ac.setRedeeemedGiftsCount(roTxnDao.countOfGiftsRedeemedByUserByMerchant(userId, credit.getMerchantId()));
			
			acts.add(ac);
		}
		
		return acts;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String registerGiftRedemption(final Long userId, GiftRedemptionType giftType) throws Exception {
		
		Location location = roLocationDao.findById(giftType.getLocationId());
		if( !location.getVerificationCode().equals(giftType.getVerificationCode())){
			throw new RedemptionException("Invalid verifcation code");
		}
		
		Allocatedgift gift = roAllocatedGiftDao.findById(giftType.getId());
		gift.setRedemptionDate(new Date());
		gift.setValue(0.0);
		
		rwGiftDao.makePersistent(gift);
		
		CreditManager km = new CreditManager(roOfferDao, roKikbakDAO, roCreditDao, rwKikbakDao, rwTxnDao);
		km.manageCredit(giftType.getFriendUserId(), gift.getOfferId(), gift.getMerchantId(), giftType.getLocationId());
		
		//send notification for kikbak when gift is redeemed
		Devicetoken token = roDeviceToken.findByUserId(userId);
		if( token != null){
			Kikbak kikbak = roKikbakDAO.findByOfferId(gift.getOfferId());
			apsNotifier.sendNotification(token, kikbak.getNotificationText());
		}
		
		return generateAuthorizationCode();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CreditRedemptionResponseType redeemCredit(final Long userId, CreditRedemptionType creditType) throws Exception {
		
		Location location = roLocationDao.findById(creditType.getLocationId());
		if( !location.getVerificationCode().equals(creditType.getVerificationCode())){
			throw new RedemptionException("Invalid verifcation code");
		}
		
		Credit credit = roCreditDao.findById(creditType.getId());
		if( creditType.getAmount() > credit.getValue() ){
			throw new RedemptionException("Value of attempted redemption is greater then remaining value");
		}
		
		credit.setValue(credit.getValue() - creditType.getAmount());
		
		Transaction txn = new Transaction();
		txn.setAmount(creditType.getAmount());
		txn.setVerificationCode(creditType.getVerificationCode());
		txn.setMerchantId(credit.getMerchantId());
		txn.setLocationId(txn.getLocationId());
		txn.setUserId(userId);
		txn.setTransactionType((short)TransactionType.Debit.ordinal());
		txn.setOfferId(credit.getOfferId());
		txn.setCreditId(credit.getId());
		txn.setDate(new Date());
		txn.setAuthorizationCode(generateAuthorizationCode());
		
		rwKikbakDao.makePersistent(credit);
		rwTxnDao.makePersistent(txn);
		
		CreditRedemptionResponseType response = new CreditRedemptionResponseType();
		response.setBalance(credit.getValue());
		response.setAuthorizationCode(txn.getAuthorizationCode());
		return response;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void claimCredit(Long userId, ClaimType ct) throws Exception {
        Credit credit = roCreditDao.findById(ct.getCreditId());
        Claim claim = new Claim();
        claim.setKikbakId(credit.getKikbakId());
        claim.setOfferId(credit.getOfferId());
        claim.setApt(ct.getApt());
        claim.setCity(ct.getCity());
        claim.setState(ct.getState());
        claim.setPhoneNumber(ct.getPhoneNumber());
        claim.setZipcode(ct.getZipcode());
        claim.setUserId(userId);
        
        rwClaimDao.makePersistent(claim);
    }
	
	protected void createGifts(Long userId){

		Collection<Long> offerIds = roAllocatedGiftDao.listOfferIdsForUser(userId);
		User user = roUserDao.findById(userId);
		Collection<Shared> shareds = roSharedDao.listAvailableForGifting(user.getFacebookId());
		Collection<Allocatedgift> newGifts = new ArrayList<Allocatedgift>();
		for(Shared shared : shareds){
			if(!offerIds.contains(shared.getOfferId())){
				Offer offer = roOfferDao.findById(shared.getOfferId());
				Gift gift = roGiftDao.findByOfferId(shared.getOfferId());
				Allocatedgift ag = new Allocatedgift();
				ag.setExpirationDate(offer.getEndDate());
				ag.setFriendUserId(shared.getUserId());
				ag.setMerchantId(shared.getMerchantId());
				ag.setOfferId(shared.getOfferId());
				ag.setUserId(userId);
				ag.setGiftId(gift.getId());
				ag.setSharedId(shared.getId());
				ag.setValue(gift.getValue());

				rwGiftDao.makePersistent(ag);
				newGifts.add(ag);
				
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
			UserLocationType clt = new UserLocationType();
			clt.setLocationId(location.getId());
			clt.setLatitude(location.getLatitude());
			clt.setLongitude(location.getLongitude());
			clt.setPhoneNumber(location.getPhoneNumber());
			cmt.getLocations().add(clt);
		}
		return cmt;
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String getBarcode(Long userId, Long allocatedGiftId) throws Exception {

        Barcode barcode = roBarcodeDao.findByUserIdAndAllocatedGift(userId, allocatedGiftId);
        boolean generateBarcode = true;
        Date now = new Date();
        if( barcode != null) {
            Long hrs =  (((now.getTime() - barcode.getAssociationDate().getTime()) / (1000*60*60)) % 24);
            if(hrs < 72) {
                generateBarcode = false;
            }
        }
        
        if(generateBarcode) {
            Allocatedgift ag = roAllocatedGiftDao.findById(allocatedGiftId);
            return rwBarcodeDao.allocateBarcode(userId, ag.getGiftId(), allocatedGiftId).getCode();
        }
        return barcode.getCode();
    }

}
