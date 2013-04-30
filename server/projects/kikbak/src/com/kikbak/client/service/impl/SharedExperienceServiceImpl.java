package com.kikbak.client.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dto.Shared;
import com.kikbak.jaxb.SharedType;


@Service
public class SharedExperienceServiceImpl implements SharedExperienceService {

	@Autowired
	ReadOnlySharedDAO roSharedDao;
	
	@Autowired
	ReadWriteSharedDAO rwSharedDao;
	
	
	@Autowired
	ReadOnlyUser2FriendDAO roU2FDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void registerSharing(final Long userId, SharedType experience) {
		//persist share
		persistExperience(userId, experience);
		
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

	protected void persistExperience(final Long userId, SharedType experience){
		Shared shared = new Shared();
		shared.setLocationId(experience.getLocationId());
		shared.setMerchantId(experience.getMerchantId());
		shared.setOfferId(experience.getOfferId());
		shared.setFbImageId(experience.getFbImageId());
		shared.setUserId(userId);
		shared.setSharedDate(new Date());
		
		rwSharedDao.makePersistent(shared);
	}
	
	
}
