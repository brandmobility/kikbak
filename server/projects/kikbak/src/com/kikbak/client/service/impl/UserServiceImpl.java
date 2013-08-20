package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.UserService;
import com.kikbak.client.service.impl.types.GenderType;
import com.kikbak.config.ContextUtil;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyKikbakDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadOnlyUserTokenDAO;
import com.kikbak.dao.ReadWriteDeviceTokenDAO;
import com.kikbak.dao.ReadWriteUser2FriendDAO;
import com.kikbak.dao.ReadWriteUserDAO;
import com.kikbak.dao.ReadWriteUserTokenDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.dto.User;
import com.kikbak.dto.UserToken;
import com.kikbak.dto.User2friend;
import com.kikbak.jaxb.devicetoken.DeviceTokenType;
import com.kikbak.jaxb.friends.FriendType;
import com.kikbak.jaxb.merchantlocation.MerchantLocationType;
import com.kikbak.jaxb.offer.ClientOfferType;
import com.kikbak.jaxb.register.UserIdType;
import com.kikbak.jaxb.register.UserType;
import com.kikbak.jaxb.userlocation.UserLocationType;
import com.kikbak.location.Coordinate;
import com.kikbak.location.GeoBoundaries;
import com.kikbak.location.GeoFence;

@Service
public class UserServiceImpl implements UserService {

	private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
	
//	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Autowired
	ReadOnlyUserDAO roUserDao;
	
	@Autowired
	ReadWriteUserDAO rwUserDao;
	
	@Autowired
	ReadOnlyUserTokenDAO roUserTokenDao;

	@Autowired
	ReadWriteUserTokenDAO rwUserTokenDao;
	
	@Autowired
	ReadOnlyUser2FriendDAO roU2FDao;
	
	@Autowired
	ReadWriteUser2FriendDAO rwU2FDao;
	
	@Autowired
	ReadOnlyGiftDAO roGiftDao;
	
	@Autowired
	ReadOnlyKikbakDAO roKikbakDao;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDao;
	
	@Autowired
	ReadOnlyLocationDAO roLocationDao;
	
	@Autowired
	ReadOnlyDeviceTokenDAO roDeviceTokenDao;
	
	@Autowired
	ReadWriteDeviceTokenDAO rwDeviceTokenDao;
	
	@Autowired
	ReadOnlyMerchantDAO roMerchantDao;
	
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
		user.setFacebookId(userType.getId());
		
		
		rwUserDao.makePersistent(user);
		UserIdType userId = new UserIdType();
		userId.setUserId(user.getId());
		
		return userId;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getUserToken(long userId) {
		UserToken token = roUserTokenDao.findByUserId(userId);
		if( token == null ){
			token = new UserToken();
			token.setUserId(userId);
			token.setToken(generateRandomToken());
			rwUserTokenDao.makePersistent(token);
		}
		return token.getToken();
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean verifyUserToken(long userId, String token) {
		UserToken userToken = roUserTokenDao.findByUserId(userId);
		if( token != null ){
			if (StringUtils.equals(token, userToken.getToken())) {
				return true;
			}
		}
		return false;
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
	public Collection<ClientOfferType> getOffers(Long userId,
			UserLocationType userLocation) {
		
		Coordinate origin = new Coordinate(userLocation.getLatitude(), userLocation.getLongitude());
		GeoFence fence = GeoBoundaries.getGeoFence(origin, config.getDouble("geo.fence.distance"));
		Collection<Offer> offers = roOfferDao.listValidOffersInGeoFence(fence);
		Collection<ClientOfferType> ots = new ArrayList<ClientOfferType>();
		for(Offer offer : offers){
		    Gift gift = roGiftDao.findByOfferId(offer.getId());
		    Kikbak kikbak = roKikbakDao.findByOfferId(offer.getId());
			ClientOfferType ot = new ClientOfferType();
			ot.setBeginDate(offer.getBeginDate().getTime());
			ot.setEndDate(offer.getEndDate().getTime());
			ot.setId(offer.getId());
			ot.setName(offer.getName());
			ot.setTosUrl(offer.getTosUrl());
			ot.setGiftDesc(gift.getDescription());
			ot.setGiftDetailedDesc(gift.getDetailedDesc());
			ot.setGiftValue(gift.getValue());
			ot.setGiftDiscountType(gift.getDiscountType());
			ot.setKikbakDesc(kikbak.getDescription());
			ot.setKikbakDetailedDesc(kikbak.getDetailedDesc());
			ot.setKikbakValue(kikbak.getValue());
			ot.setOfferImageUrl(offer.getImageUrl());
			ot.setMerchantId(offer.getMerchantId());
			ot.setGiveImageUrl(gift.getImageUrl());
			
			Merchant merchant = roMerchantDao.findById(offer.getMerchantId());
			ot.setMerchantName(merchant.getName());
			ot.setMerchantUrl(merchant.getUrl());
			
			Collection<Location> locations = roLocationDao.listForMerchantInGeoFence(offer.getMerchantId(), fence);
			for( Location location: locations){
			    MerchantLocationType ml = new MerchantLocationType();
				ml.setLocationId(location.getId());
				ml.setSiteName(location.getSiteName());
				ml.setAddress1(location.getAddress1());
				ml.setAddress2(location.getAddress2());
				ml.setCity(location.getCity());
				ml.setState(location.getState());
				ml.setZipcode(String.valueOf(location.getZipcode()));
				ml.setZip4(location.getZipPlusFour());
				ml.setLatitude(location.getLatitude());
				ml.setLongitude(location.getLongitude());
				ml.setPhoneNumber(location.getPhoneNumber());
				ot.getLocations().add(ml);
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
		token.setPlatformType(deviceToken.getPlatformId());
		token.setLastUpdateTime(new Date());
		token.setUserId(userId);
		
		rwDeviceTokenDao.makePersistent(token);
	}
	
	private User findOrCreateUser(UserType userType){
		User user = roUserDao.findByFacebookId(userType.getId());
		if( user == null ){
			user = new User();
		}
		
		return user;
	}
	
	private String generateRandomToken() {
        return new BigInteger(130, new SecureRandom()).toString(Character.MAX_RADIX);
	}
}
