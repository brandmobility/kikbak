package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.UserService;
import com.kikbak.client.service.impl.types.GenderType;
import com.kikbak.client.service.impl.types.PlatformType;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteDeviceTokenDAO;
import com.kikbak.dao.ReadWriteUser2FriendDAO;
import com.kikbak.dao.ReadWriteUserDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Location;
import com.kikbak.dto.Offer;
import com.kikbak.dto.User;
import com.kikbak.dto.User2friend;
import com.kikbak.jaxb.DeviceTokenType;
import com.kikbak.jaxb.FriendType;
import com.kikbak.jaxb.OfferLocationType;
import com.kikbak.jaxb.OfferType;
import com.kikbak.jaxb.UserIdType;
import com.kikbak.jaxb.UserLocationType;
import com.kikbak.jaxb.UserType;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	ReadOnlyUserDAO roUserDao;
	
	@Autowired
	ReadWriteUserDAO rwUserDao;
	
	@Autowired
	ReadOnlyUser2FriendDAO roU2FDao;
	
	@Autowired
	ReadWriteUser2FriendDAO rwU2FDao;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDao;
	
	@Autowired
	ReadOnlyLocationDAO roLocationDao;
	
	@Autowired
	ReadOnlyDeviceTokenDAO roDeviceTokenDao;
	
	@Autowired
	ReadWriteDeviceTokenDAO rwDeviceTokenDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserIdType registerUser(UserType userType) {
		User user = findOrCreateUser(userType);
		user.setCreateDate(new Date());
		user.setEmail(userType.getEmail());
		user.setFirstName(userType.getFirstName());
		byte gender = 0;
		if( userType.getGender().compareToIgnoreCase(GenderType.female.name()) == 0 ){
			gender = 1;
		}
		user.setGender(gender);
		user.setLastName(userType.getLastName());
		user.setUpdateDate(null);
		user.setUsername(userType.getUsername());
		user.setFacebookId(userType.getId());
		
		
		rwUserDao.makePersistent(user);
		UserIdType userId = new UserIdType();
		userId.setUserId(user.getId());
		return userId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateFriends(final long userId, final Collection<FriendType> friends) {
		
		//delete old
		Collection<Long> currentFriendIds = new ArrayList<Long>();
		for(FriendType ft : friends){
			currentFriendIds.add(ft.getId());
		}
		Collection<Long> friendsToDelete = roU2FDao.listFriendsToDelete(userId, currentFriendIds);
		if( friendsToDelete.size() != 0 ){
			rwU2FDao.batchDelete(userId, friendsToDelete);
		}
		
		//update new
		Collection<Long> fbIds = roU2FDao.listFriendsForUser(userId);
		Collection<User2friend> friendAssociations = new ArrayList<User2friend>();
		for( FriendType ft : friends){
			BigInteger fbId = new BigInteger(((Long)ft.getId()).toString());
			if( !fbIds.contains( fbId ) ){
				User2friend u2f = new User2friend();
				u2f.setFacebookFriendId(ft.getId());
				u2f.setUserId(userId);
				friendAssociations.add(u2f);
			}
		}
		
		if( friendAssociations.size() != 0 ){
			rwU2FDao.batchInsert(friendAssociations);
		}
		
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<OfferType> getOffers(Long userId,
			UserLocationType userLocation) {
		
		Collection<Offer> offers = roOfferDao.listValidOffers();
		Collection<OfferType> ots = new ArrayList<OfferType>();
		for(Offer offer : offers){
			OfferType ot = new OfferType();
			ot.setBeginDate(offer.getBeginDate().getTime());
			ot.setDefaultText(offer.getDefaultText());
			ot.setDescription(offer.getDescription());
			ot.setEndDate(offer.getEndDate().getTime());
			ot.setId(offer.getId());
			ot.setName(offer.getName());
			ot.setGiftValue(offer.getGiftValue());
			ot.setKikbakValue(offer.getKikbakValue());
			ot.setMerchantId(offer.getMerchantId());
			
			Collection<Location> locations = roLocationDao.listByMerchant(offer.getMerchantId());
			for( Location location: locations){
				OfferLocationType otl = new OfferLocationType();
				otl.setLocationId(location.getId());
				otl.setLatitude(location.getLatitude());
				otl.setLongitude(location.getLongitude());
				ot.getLocations().add(otl);
			}
			
			ots.add(ot);
		}
		
		return ots;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void persistDeviceToken(Long userId, DeviceTokenType deviceToken) {
		Devicetoken token = roDeviceTokenDao.findByUserId(userId);
		if( token == null ){
			token = new Devicetoken();
		}
		
		token.setToken(deviceToken.getToken());
		token.setPlatformType((short)PlatformType.iOS.ordinal());
		token.setLastUpdateTime(new Date());
		token.setUserId(userId);
		
		rwDeviceTokenDao.makePersistent(token);
	}
	
	protected User findOrCreateUser(UserType userType){
		User user = roUserDao.findByFacebookId(userType.getId());
		if( user == null ){
			user = new User();
		}
		
		return user;
	}
}
