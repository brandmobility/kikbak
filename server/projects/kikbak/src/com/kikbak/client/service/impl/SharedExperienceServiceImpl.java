package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.ReferralCodeUniqueException;
import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dto.Shared;
import com.kikbak.jaxb.share.SharedType;


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
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String registerSharing(final Long userId, SharedType experience) {
		//persist share
        int maxLength = config.getInt(RANDOM_SECRET_LENGTH, DEFAULT_RANDOM_SECRET_LENGTH);
        String referralCode = null;
        while (true) {
            try {
                referralCode = generateReferralCode(userId, experience, maxLength);
                persistExperience(userId, experience, referralCode);
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

	private String generateReferralCode(Long userId, SharedType experience, int maxLength) {
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
		shared.setFbImageId(experience.getFbImageId());
		shared.setUserId(userId);
		shared.setCaption(experience.getCaption());
		shared.setType(experience.getType());
		shared.setSharedDate(new Date());
		shared.setReferralCode(referralCode);
		
		rwSharedDao.saveShared(shared);
	}
	
	
}
