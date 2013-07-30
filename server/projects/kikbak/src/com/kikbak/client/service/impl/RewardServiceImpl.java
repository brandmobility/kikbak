package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.RateLimitException;
import com.kikbak.client.service.RedemptionException;
import com.kikbak.client.service.RewardException;
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
import com.kikbak.dao.ReadWriteSharedDAO;
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
import com.kikbak.jaxb.merchantlocation.MerchantLocationType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionResponseType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.rewards.AvailableCreditType;
import com.kikbak.jaxb.rewards.ClaimStatusType;
import com.kikbak.jaxb.rewards.ClientMerchantType;
import com.kikbak.jaxb.rewards.GiftType;
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
    @Qualifier("ReadOnlySharedDAO")
    ReadOnlySharedDAO roSharedDao;

    @Autowired
    @Qualifier("ReadWriteSharedDAO")
    ReadWriteSharedDAO rwSharedDao;

    @Autowired
    ReadOnlyLocationDAO roLocationDao;

    @Autowired
    ApsNotifier apsNotifier;

    @Autowired
    ReadOnlyDeviceTokenDAO roDeviceToken;
    
    @Autowired
    ReadWriteClaimDAO rwClaimDao;
    
    @Autowired
    ReadOnlyBarcodeDAO roBarcodeDao;
    
    @Autowired
    ReadWriteBarcodeDao rwBarcodeDao;


    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor=RewardException.class)
    public Collection<GiftType> getGifts(Long userId) throws RewardException {
        createGifts(userId);
        Collection<Allocatedgift> gifts = new ArrayList<Allocatedgift>();
        gifts.addAll(roAllocatedGiftDao.listValidByUserId(userId));
        Collection<GiftType> gts = new ArrayList<GiftType>();

        for( Allocatedgift ag: gifts){
            Merchant merchant = roMerchantDao.findById(ag.getMerchantId());
            Shared shared = roSharedDao.findById(ag.getSharedId());
            Gift gift = roGiftDao.findById(ag.getGiftId());
            User friend = roUserDao.findById(ag.getFriendUserId());
            Offer offer = roOfferDao.findById(gift.getOfferId());

            GiftType gt = createGiftType(shared, ag, merchant, gift, friend, offer);

            gts.add(gt);
        }

        return gts;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor=RewardException.class)
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
            ac.setRedeemedGiftsCount(roTxnDao.countOfGiftsRedeemedByUserByMerchant(userId, credit.getMerchantId()));

            acts.add(ac);
        }

        return acts;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor=RedemptionException.class)
    public String registerGiftRedemption(final Long userId, GiftRedemptionType giftType) 
    throws RedemptionException, RateLimitException {

    Location location = roLocationDao.findById(giftType.getLocationId());
    if( !location.getVerificationCode().equals(giftType.getVerificationCode())){
        throw new RedemptionException("Invalid verifcation code");
    }

    Allocatedgift gift = roAllocatedGiftDao.findById(giftType.getId());
    gift.setRedemptionDate(new Date());
    gift.setValue(0.0);

    rwGiftDao.makePersistent(gift);

    Shared shared = roSharedDao.findById(gift.getSharedId());
    if (shared == null) {
        throw new RedemptionException("Invalid verifcation code");
    }
    Offer offer = roOfferDao.findById(gift.getOfferId());
    if (offer == null) {
        throw new RedemptionException("Invalid verifcation code");
    }
    
    CreditManager km = new CreditManager(roOfferDao, roKikbakDAO, roCreditDao, rwKikbakDao, rwTxnDao);
    km.manageCredit(giftType.getFriendUserId(), gift.getOfferId(), gift.getMerchantId(), giftType.getLocationId());

    //send notification for kikbak when gift is redeemed
    Devicetoken token = roDeviceToken.findByUserId(userId);
    if( token != null){
        Kikbak kikbak = roKikbakDAO.findByOfferId(gift.getOfferId());
        try {
            apsNotifier.sendNotification(token, kikbak.getNotificationText());
        } catch (Exception e) {
            throw new RuntimeException("failed to notify gift redemption to user " + userId, e);
        }
    }

    return generateAuthorizationCode();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor=RedemptionException.class)
    public CreditRedemptionResponseType redeemCredit(final Long userId, CreditRedemptionType creditType) throws RedemptionException {

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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor=RewardException.class)
    public ClaimStatusType claimGift(Long userId, String referralCode, List<GiftType> gifts) throws RewardException {
        ClaimStatusType status = ClaimStatusType.INVALID_CODE;
        if (userId == null || StringUtils.isBlank(referralCode)) {
            throw new RewardException("userId or referralCode cannot be empty");
        }

        Collection<Long> offerIds = roAllocatedGiftDao.listOfferIdsForUser(userId);
        User user = roUserDao.findById(userId);
        if (user == null) {
            throw new RewardException("user id " + userId + " doesn't exist");
        }

        Collection<Shared> shareds = roSharedDao.listAvailableForGiftingByReferralCode(referralCode);

        Collection<Allocatedgift> newGifts = new ArrayList<Allocatedgift>();
        for(Shared shared : shareds){
            if(!offerIds.contains(shared.getOfferId())){
                Offer offer = roOfferDao.findById(shared.getOfferId());
                Date now = new Date();
                if (now.before(offer.getBeginDate()) || now.after(offer.getEndDate())) {
                    status = ClaimStatusType.EXPIRED;
                    continue;
                } else {
                    Allocatedgift ag = createAllocateOffer(userId, shared, offer);
                    newGifts.add(ag);
                    offerIds.add(shared.getOfferId());
                    status = ClaimStatusType.OK;

                    Merchant merchant = roMerchantDao.findById(ag.getMerchantId());
                    Gift gift = roGiftDao.findById(ag.getGiftId());
                    User friend = roUserDao.findById(ag.getFriendUserId());

                    GiftType gt = createGiftType(shared, ag, merchant, gift,
                            friend, offer);
                    gifts.add(gt);
                }
            }
        }

        return gifts.isEmpty() ? status : ClaimStatusType.OK;
    }

    private GiftType createGiftType(Shared shared, Allocatedgift ag,
            Merchant merchant, Gift gift, User friend, Offer offer) {
        GiftType gt = new GiftType();
        gt.setId(ag.getId());
        ClientMerchantType cmt = fillClientMerchantType(merchant);
        gt.setMerchant(cmt);
        gt.setOfferId(shared.getOfferId());
        gt.setDesc(gift.getDescription());
        gt.setDetailedDesc(gift.getDetailedDesc());
        gt.setValue(gift.getValue());
        gt.setDiscountType(gift.getDiscountType());
        gt.setValidationType(gift.getValidationType());
        gt.setRedemptionLocationType(gift.getRedemptionLocationType());
        gt.setImageUrl(gift.getDefaultGiveImageUrl());
        gt.setDefaultGiveImageUrl(gift.getDefaultGiveImageUrl());
        gt.setTosUrl(offer.getTosUrl());
        gt.setFriendUserId(ag.getFriendUserId());
        gt.setFbFriendId(friend.getFacebookId());
        gt.setFriendName(friend.getFirstName() + " " + friend.getLastName());
        gt.setCaption(shared.getCaption());
        gt.setFbImageId(shared.getFbImageId());

        return gt;
    }

    private Allocatedgift createAllocateOffer(Long userId, Shared shared, Offer offer) {
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
        return ag;
    }


    protected void createGifts(Long userId) throws RewardException {

        User user = roUserDao.findById(userId);
        if (user == null) {
            throw new RewardException("user id " + userId + " doesn't exist");
        }
        Collection<Shared> shareds = roSharedDao.listAvailableForGifting(user.getFacebookId());

        Collection<Long> sharedIds = roAllocatedGiftDao.listSharedIdsForUser(userId);
        Collection<Allocatedgift> newGifts = new ArrayList<Allocatedgift>();
        for(Shared shared : shareds){
            if(!sharedIds.contains(shared.getId())){
                Offer offer = roOfferDao.findById(shared.getOfferId());
                Allocatedgift ag = createAllocateOffer(userId, shared, offer);
                newGifts.add(ag);
                sharedIds.add(shared.getOfferId());
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
            MerchantLocationType clt = new MerchantLocationType();
            clt.setLocationId(location.getId());
            clt.setSiteName(location.getSiteName());
            clt.setAddress1(location.getAddress1());
            clt.setAddress2(location.getAddress2());
            clt.setCity(location.getCity());
            clt.setState(location.getState());
            clt.setZipcode(String.valueOf(location.getZipcode()));
            clt.setZip4(location.getZipPlusFour());
            clt.setLatitude(location.getLatitude());
            clt.setLongitude(location.getLongitude());
            clt.setPhoneNumber(location.getPhoneNumber());
            cmt.getLocations().add(clt);
        }
        return cmt;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void claimCredit(Long userId, ClaimType ct) throws Exception {
        Credit credit = roCreditDao.findById(ct.getCreditId());
        Claim claim = new Claim();
        claim.setKikbakId(credit.getKikbakId());
        claim.setOfferId(credit.getOfferId());
        claim.setName(ct.getName());
        claim.setStreet(ct.getStreet());
        claim.setApt(ct.getApt());
        claim.setCity(ct.getCity());
        claim.setState(ct.getState());
        claim.setPhoneNumber(ct.getPhoneNumber());
        claim.setZipcode(ct.getZipcode());
        claim.setUserId(userId);
        claim.setRequestDate(new Date());
        
        rwClaimDao.makePersistent(claim);
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
            String code = rwBarcodeDao.allocateBarcode(userId, ag.getGiftId(), allocatedGiftId).getCode();
            return validateAndUpdateChecksum(code);
        }
        return validateAndUpdateChecksum(barcode.getCode());
    }
    
    protected String validateAndUpdateChecksum(String code){
        
        int odd = 0;
        int even = 0;
        String[] split = code.split("(?<=\\d)"); 
        for(int i =0; i < split.length-1; ++i) {
            if( (i%2) == 0) {
                odd += Integer.parseInt(split[i]);
            }
            else{
                even += Integer.parseInt(split[i]);
            }
        }
        
        int checkdigit = (odd*3)+even;
        checkdigit = (checkdigit%10);
        if(checkdigit != 0) {
            checkdigit = 10 - checkdigit;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(code.substring(0, code.length()-1));
        buffer.append(String.valueOf(checkdigit));
        return buffer.toString();
    }
}
