package com.kikbak.client.service.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.v1.FbLoginException;
import com.kikbak.client.service.v1.FbLoginService;
import com.kikbak.client.service.v1.FbUserLimitException;
import com.kikbak.client.service.v2.UserService2;
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
import com.kikbak.dao.enums.GenderType;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.dto.User;
import com.kikbak.dto.User2friend;
import com.kikbak.dto.UserToken;
import com.kikbak.jaxb.v1.devicetoken.DeviceTokenType;
import com.kikbak.jaxb.v1.merchantlocation.MerchantLocationType;
import com.kikbak.jaxb.v1.register.UserType;
import com.kikbak.jaxb.v1.userlocation.UserLocationType;
import com.kikbak.jaxb.v2.offer.ClientOfferType;
import com.kikbak.location.Coordinate;
import com.kikbak.location.GeoBoundaries;
import com.kikbak.location.GeoFence;

@Service
public class UserServiceImpl implements UserService2 {

    private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration",
            PropertiesConfiguration.class);

    private static final String USER_FRIENDS_MINIMUM_COUNT = "user.friends.minimum.count";

    // private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

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

    @Autowired
    private FbLoginService fbLoginService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long registerFbUser(String accessToken, String phone) throws FbUserLimitException, FbLoginException {
        UserType fbUser = fbLoginService.getUserInfo(accessToken);

        // check if has enough friends
        Collection<Long> friends = fbLoginService.getFriends(accessToken);
        if (friends.size() < config.getInt(USER_FRIENDS_MINIMUM_COUNT)) {
            String msg = "User " + fbUser.getId() + " has too few friends: " + friends.size();
            throw new FbUserLimitException(msg);
        }

        // get existing user or create a new user
        User user = roUserDao.findByFacebookId(fbUser.getId());
        if (user == null) {
            user = new User();
            user.setCreateDate(new Date());
        }

        // update user information
        populate(user, fbUser);
        if (!StringUtils.isEmpty(phone)) {
            user.setManualNumber(phone);
        }
        user.setUpdateDate(new Date());
        rwUserDao.makePersistent(user);

        // update list of friends
        updateFriendsList(user.getId(), friends);

        return user.getId();
    }

    // Copy information from FB to internal database user type
    private void populate(User user, UserType fbUser) {
        user.setFirstName(fbUser.getFirstName());
        user.setLastName(fbUser.getLastName());
        user.setFacebookId(fbUser.getId());
        user.setEmail(fbUser.getEmail());
        user.setGender((byte) (fbUser.getGender().compareToIgnoreCase(GenderType.female.name()) == 0 ? 1 : 0));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long registerWebUser(String name, String email, String phone) {
        User user = roUserDao.findByManualPhoneNotFb(phone);
        if (user == null) {
            user = new User();
            user.setCreateDate(new Date());
        }

        user.setManualName(name);
        user.setManualNumber(phone);
        user.setManualEmail(email);
        user.setUpdateDate(new Date());

        rwUserDao.makePersistent(user);
        return user.getId();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String getUserToken(long userId) {
        UserToken token = roUserTokenDao.findByUserId(userId);
        if (token == null) {
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
        if (token != null) {
            if (StringUtils.equals(token, userToken.getToken())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateFriendsList(long userId, String accessToken) throws FbLoginException {
        Collection<Long> friends = fbLoginService.getFriends(accessToken);
        updateFriendsList(userId, friends);
    }

    private void updateFriendsList(final long userId, final Collection<Long> friends) {
        // delete old
        Collection<Long> friendsToDelete = roU2FDao.listFriendsToDelete(userId, friends);
        if (friendsToDelete.size() != 0) {
            rwU2FDao.batchDelete(userId, friendsToDelete);
        }

        // update new
        Collection<Long> fbIds = roU2FDao.listFriendsForUser(userId);
        Collection<User2friend> friendAssociations = new ArrayList<User2friend>();
        for (Long fid : friends) {
            if (!fbIds.contains(BigInteger.valueOf(fid))) {
                User2friend u2f = new User2friend();
                u2f.setFacebookFriendId(fid);
                u2f.setUserId(userId);
                friendAssociations.add(u2f);
            }
        }

        if (friendAssociations.size() != 0) {
            rwU2FDao.batchInsert(friendAssociations);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<ClientOfferType> hasOffers2(UserLocationType userLocation) {
        Coordinate origin = new Coordinate(userLocation.getLatitude(), userLocation.getLongitude());
        GeoFence fence = GeoBoundaries.getGeoFence(origin, config.getDouble("geo.fence.distance.hasoffer"));
        return getOffersByLocation(fence);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<ClientOfferType> getOffers2(final Long userId, String merchantName) {
        return getOffersByMerchant(merchantName);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<ClientOfferType> getOffers2(Long userId, UserLocationType userLocation) {

        Coordinate origin = new Coordinate(userLocation.getLatitude(), userLocation.getLongitude());
        GeoFence fence = GeoBoundaries.getGeoFence(origin, config.getDouble("geo.fence.distance"));
        return getOffersByLocation(fence);
    }

    private Collection<ClientOfferType> getOffersByMerchant(String merchantName) {
        Merchant merchant = roMerchantDao.findByName(merchantName);
        if (merchant == null) {
            return Collections.emptyList();
        }
        Collection<Offer> offers = roOfferDao.listOffers(merchant);
        Collection<ClientOfferType> ots = new ArrayList<ClientOfferType>(offers.size());
        for (Offer offer : offers) {
            Gift gift = roGiftDao.findByOfferId(offer.getId());
            Kikbak kikbak = roKikbakDao.findByOfferId(offer.getId());
            ClientOfferType ot = new ClientOfferType();
            ot.setBeginDate(offer.getBeginDate().getTime());
            ot.setEndDate(offer.getEndDate().getTime());
            ot.setId(offer.getId());
            ot.setName(offer.getName());
            ot.setOfferType(offer.getOfferType());
            ot.setTosUrl(offer.getTosUrl());
            ot.setGiftDesc(gift.getDescription());
            ot.setGiftDetailedDesc(gift.getDetailedDesc());
            ot.setGiftValue(gift.getValue());
            ot.setGiftDiscountType(gift.getDiscountType());
            if (kikbak != null) {
                ot.setKikbakDesc(kikbak.getDescription());
                ot.setKikbakDetailedDesc(kikbak.getDetailedDesc());
                ot.setKikbakValue(kikbak.getValue());
            }
            ot.setOfferImageUrl(offer.getImageUrl());
            ot.setMerchantId(offer.getMerchantId());
            ot.setMerchantLogoUrl(merchant.getImageUrl());
            ot.setGiveImageUrl(gift.getImageUrl());

            ot.setMerchantName(merchant.getName());
            ot.setMerchantUrl(merchant.getUrl());

            Collection<Location> locations = roLocationDao.listByMerchant(offer.getMerchantId());
            for (Location location : locations) {
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
            ot.setHasEmployeeProgram(offer.getHasEmployeeProgram() != 0);
            ot.setMapUri(offer.getMapUri());
            ot.setAuth(offer.getAuth());

            ots.add(ot);
        }

        return ots;
    }

    private Collection<ClientOfferType> getOffersByLocation(GeoFence fence) {
        Collection<Offer> offers = roOfferDao.listValidOffersInGeoFence(fence);
        Collection<ClientOfferType> ots = new ArrayList<ClientOfferType>(offers.size());
        for (Offer offer : offers) {
            Gift gift = roGiftDao.findByOfferId(offer.getId());
            Kikbak kikbak = roKikbakDao.findByOfferId(offer.getId());
            ClientOfferType ot = new ClientOfferType();
            ot.setBeginDate(offer.getBeginDate().getTime());
            ot.setEndDate(offer.getEndDate().getTime());
            ot.setId(offer.getId());
            ot.setName(offer.getName());
            ot.setOfferType(offer.getOfferType());
            ot.setTosUrl(offer.getTosUrl());
            ot.setGiftDesc(gift.getDescription());
            ot.setGiftDetailedDesc(gift.getDetailedDesc());
            ot.setGiftValue(gift.getValue());
            ot.setGiftDiscountType(gift.getDiscountType());
            if (kikbak != null) {
                ot.setKikbakDesc(kikbak.getDescription());
                ot.setKikbakDetailedDesc(kikbak.getDetailedDesc());
                ot.setKikbakValue(kikbak.getValue());
            }
            ot.setOfferImageUrl(offer.getImageUrl());
            ot.setMerchantId(offer.getMerchantId());
            ot.setGiveImageUrl(gift.getImageUrl());

            Merchant merchant = roMerchantDao.findById(offer.getMerchantId());
            ot.setMerchantName(merchant.getName());
            ot.setMerchantUrl(merchant.getUrl());

            Collection<Location> locations = roLocationDao.listForMerchantInGeoFence(offer.getMerchantId(), fence);
            for (Location location : locations) {
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
            ot.setHasEmployeeProgram(offer.getHasEmployeeProgram() != 0);
            ot.setMapUri(offer.getMapUri());
            ot.setAuth(offer.getAuth());

            ots.add(ot);
        }

        return ots;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void persistDeviceToken(Long userId, DeviceTokenType deviceToken) {
        Devicetoken token = roDeviceTokenDao.findByUserId(userId);
        if (token == null) {
            token = new Devicetoken();
        }

        token.setToken(deviceToken.getToken());
        token.setPlatformType(deviceToken.getPlatformId());
        token.setLastUpdateTime(new Date());
        token.setUserId(userId);

        rwDeviceTokenDao.makePersistent(token);
    }

    private String generateRandomToken() {
        return new BigInteger(130, new SecureRandom()).toString(Character.MAX_RADIX);
    }
}
