package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.v1.RateLimitException;
import com.kikbak.client.service.v1.ReferralCodeUniqueException;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Shared;
import com.kikbak.jaxb.v1.share.SharedType;
import com.kikbak.jaxb.v2.share.ChannelType;
import com.kikbak.jaxb.v2.share.StoriesResponse;
import com.kikbak.jaxb.v2.share.StoryType;
import com.kikbak.push.service.PushNotifier;


@Service
public class SharedExperienceServiceImpl implements SharedExperienceService {

	private static final int DEFAULT_RANDOM_SECRET_LENGTH = 6;

    private static final String RANDOM_SECRET_LENGTH = "gift.redeem.code.length";

    @Autowired
    private PropertiesConfiguration config;

    @Autowired
    @Qualifier("ReadOnlySharedDAO")
	ReadOnlySharedDAO roSharedDao;
	
	@Autowired
	@Qualifier("ReadWriteSharedDAO")
	ReadWriteSharedDAO rwSharedDao;
	
	
	@Autowired
	ReadOnlyUser2FriendDAO roU2FDao;
	
    @Autowired
    PushNotifier pushNotifier;
	
    @Autowired
    ReadOnlyGiftDAO roGiftDAO;    
    
    @Autowired
    private ReadOnlyUserDAO readOnlyUserDAO;
    
    @Autowired 
    private ReadOnlyMerchantDAO roMerchantDAO;

    @Autowired
    private ReadOnlyOfferDAO roOfferDAO;
    
    @Autowired
    private ReadOnlyAllocatedGiftDAO roAllocatedGiftDAO;
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void getShareStories(Long userId, Long offerId, String imageUrl, String platform, 
    					String email, String phonenumber, String caption, String employeeId, StoriesResponse response) throws ReferralCodeUniqueException, RateLimitException{
    	
    	checkMaxRedeemCountReached(userId, offerId);
    	
    	int maxLength = config.getInt(RANDOM_SECRET_LENGTH, DEFAULT_RANDOM_SECRET_LENGTH);
    	String referralCode = generateReferralCode(maxLength);
    	response.setCode(referralCode);
    	persistStory(userId, offerId, imageUrl, email, phonenumber, caption, employeeId, referralCode);
    	for( ChannelType type : ChannelType.values()){
    		StoryType story = new StoryType();
    		fillInStory(story, type, platform, referralCode, offerId, imageUrl);
    		response.getStories().add(story);
    	}
    }
    
    protected void checkMaxRedeemCountReached(Long userId, Long offerId) throws RateLimitException{
    	
    	Long count = roAllocatedGiftDAO.countOfRedeemedShares(userId, offerId);
    	Offer offer = roOfferDAO.findById(offerId);
    	if( count >= offer.getRedeemLimit() ){
    		throw new RateLimitException("User can no longer share!");
    	}
    }
    
    protected void fillInStory(StoryType story, ChannelType type, String platform, 
    		String code, Long offerId, String imageUrl){
    	String titleKey = "share.template.title." + type.name().toLowerCase() + "."
                + platform.toLowerCase();
        String bodyKey = "share.template.body." + type.name().toLowerCase() + "."
                + platform.toLowerCase();
        String loginUrl = config.getString("share.template.login.url").replace("%CODE%", code);

        String titleTemplate = config.getString(titleKey, "");
        String bodyTemplate = config.getString(bodyKey, "");

        
        if (StringUtils.isNotBlank(bodyTemplate)) {
            Offer offer = roOfferDAO.findById(offerId);
        	
            Merchant merchant = roMerchantDAO.findById(offer.getMerchantId());
            Gift gift = roGiftDAO.findByOfferId(offerId);
            if (StringUtils.isNotBlank(titleTemplate)) {
                String title = fillTemplate(loginUrl, titleTemplate, merchant, gift, imageUrl);
                story.setSubject(title);
            }
            if (StringUtils.isNotBlank(bodyTemplate)) {
                String body = fillTemplate(loginUrl, bodyTemplate, merchant, gift, imageUrl);
                story.setBody(body);
            }
        }
        story.setLandingUrl(loginUrl);
        story.setType(type);
    }
    
    
    private String fillTemplate(String loginUrl, String template,
            Merchant merchant, Gift gift, String imageUrl) {
        String result;
        result = template.replace("%IMAGE_URL%", StringUtils.trimToEmpty(imageUrl));
        result = result.replace("%DESC%", StringUtils.trimToEmpty(gift.getDescription()));
        result = result.replace("%DESC_DETAIL%", StringUtils.trimToEmpty(gift.getDetailedDesc()));
        result = result.replace("%LOGIN_URL%", loginUrl);
        result = result.replace("%MERCHANT%", StringUtils.trimToEmpty(merchant.getName()));
        
//        result = result
//                .replace(
//                        "%NAME%",
//                        StringUtils.trimToEmpty(user.getFirstName())
//                                + (StringUtils.isBlank(user.getFirstName()) ? StringUtils.trimToEmpty(user
//                                        .getLastName()) : ""));
        
        return result;
    }
    
    
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String registerSharing(final Long userId, SharedType experience) {
		//persist share
        int maxLength = config.getInt(RANDOM_SECRET_LENGTH, DEFAULT_RANDOM_SECRET_LENGTH);
        String referralCode = null;
        while (true) {
            try {
                referralCode = generateReferralCode(maxLength);
                persistExperience(userId, experience, referralCode);
                Gift gift = roGiftDAO.findByOfferId(experience.getOfferId());
                pushNotifier.sendGiftNotification(userId, gift);
                break;
            } catch (ReferralCodeUniqueException e) {
                ++maxLength;
            }
            if (maxLength > 64) {
                throw new RuntimeException("cannot find a unique referral code");
            }
        }
		return referralCode;
		
		//TODO: De we award for sharing
//		//award the latest friend who shared the award with you.
//		Collection<Long> friends = roU2FDao.listFriendsForUser(userId);
//		if( friends != null){
//			Long friendToAwardId = null;
//			Date latest = null;
//			for( Long friendId : friends){
//				Collection<Shared> shares = roSharedDao.listByUserIdAndOfferId(userId, experience.getOfferId());
//				if( shares != null ){
//					for(Shared share : shares){
//						//if nothing to share
//						if( latest == null ){
//							latest = share.getSharedDate();
//							friendToAwardId = friendId;
//						}
//						else if( latest.getTime() < share.getSharedDate().getTime()){
//							latest = share.getSharedDate();
//							friendToAwardId = friendId;
//						}
//					}
//				}
//			}
//			
//			if( friendToAwardId != null){
//				manageKikbak(friendToAwardId, experience);
//			}
//		}
	}

	private String generateReferralCode(int maxLength) {
        String randomStr = new BigInteger(30, new SecureRandom()).toString(Character.MAX_RADIX);
        if (randomStr.length() < maxLength) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < maxLength - randomStr.length(); i++) {
                sb.append("0");
            }
            randomStr = sb.toString() + randomStr;
        }
        return randomStr;
    }

    protected void persistExperience(final Long userId, SharedType experience, String referralCode) 
            throws ReferralCodeUniqueException {
		Shared shared = new Shared();
		shared.setLocationId(experience.getLocationId());
		shared.setMerchantId(experience.getMerchantId());
		shared.setOfferId(experience.getOfferId());
		shared.setEmployeeId(experience.getEmployeeId());
//		shared.setFbImageId(experience.getFbImageId());
		shared.setImageUrl(experience.getImageUrl());
		shared.setUserId(userId);
		shared.setCaption(experience.getCaption());
		shared.setType(experience.getType());
		shared.setSharedDate(new Date());
		shared.setReferralCode(referralCode);
		
		rwSharedDao.save(shared);
	}
    
    protected void persistStory(Long userId, Long offerId, String imageUrl, String email, 
    		String phonenumber, String caption, String employeeId, String referralCode)
    		throws ReferralCodeUniqueException {
    	
    	Offer offer = roOfferDAO.findById(offerId);
    	
    	Shared shared = new Shared();
		shared.setMerchantId(offer.getMerchantId());
		shared.setOfferId(offerId);
		shared.setImageUrl(imageUrl);
		shared.setUserId(userId);
		shared.setType("web");
		shared.setEmail(email);
		shared.setPhonenumber(phonenumber);
		shared.setCaption(caption);
		shared.setEmployeeId(employeeId);
		shared.setSharedDate(new Date());
		shared.setReferralCode(referralCode);
		
		rwSharedDao.save(shared);
    }


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addShareType(String code, String type) throws ReferralCodeUniqueException {
		// TODO Auto-generated method stub
		Shared shared = roSharedDao.findByReferralCode(code);
		StringBuffer sb = new StringBuffer(shared.getType());
		sb.append(";");
		sb.append(type);
		shared.setType(sb.toString());
		
		rwSharedDao.save(shared);
	}

}
