package com.kikbak.admin.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.admin.service.InvalidRequestException;
import com.kikbak.admin.service.MerchantService;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadWriteLocationDAO;
import com.kikbak.dao.ReadWriteMerchantDAO;
import com.kikbak.dao.ReadWriteOfferDAO;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.jaxb.LocationType;
import com.kikbak.jaxb.MerchantType;
import com.kikbak.jaxb.OfferType;

@Service
public class MerchantServiceImpl implements MerchantService{
	
	@Autowired
	ReadOnlyMerchantDAO roMerchantDao;
	
	@Autowired
	ReadWriteMerchantDAO rwMerchantDao;
	
	@Autowired
	ReadOnlyLocationDAO roLocationDao;
	
	@Autowired
	ReadWriteLocationDAO rwLocationDao;
	
	@Autowired
	ReadOnlyOfferDAO roOfferDao;
	
	@Autowired
	ReadWriteOfferDAO rwOfferDao;
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<MerchantType> getMerchants(){
		Collection<Merchant> merchants = roMerchantDao.listAll();
		Collection<MerchantType> result = new ArrayList<MerchantType>();
		//get all merchants
		for(Merchant merchant : merchants){
			MerchantType mt = new MerchantType();
			mt.setDescription(merchant.getDescription());
			mt.setGraphPath(merchant.getGraphPath());
			mt.setUrl(merchant.getUrl());
			mt.setImageUrl(merchant.getImageUrl());
			mt.setName(merchant.getName());
			mt.setId(merchant.getId());
			
			result.add(mt);
			
			//get all locations for merchant
			Collection<Location> locations = roLocationDao.listByMerchant(merchant.getId());
			for(Location location : locations){
				LocationType lt = new LocationType();
				lt.setAddress1(location.getAddress1());
				lt.setAddress2(location.getAddress2());
				lt.setCity(location.getCity());
				lt.setState(location.getState());
				lt.setZipCode(location.getZipcode());
				lt.setVerificationCode(location.getVerificationCode());
				lt.setLatitude(location.getLatitude());
				lt.setLongitude(location.getLongitude());
				
				mt.getLocations().add(lt);
			}
		}
		/*
		Collection<MerchantType> result = new ArrayList<MerchantType>();
		MerchantType merchant = new MerchantType();
		merchant.setDescription("test");
		merchant.setGraphPath("http://test");
		merchant.setName("Test");
		merchant.setUrl("http://co.com");
		merchant.setImageUrl("image url");
		LocationType lt = new LocationType();
		lt.setAddress1("a1");
		lt.setAddress2("a2");
		lt.setCity("my city");
		lt.setGraphPath("");
		lt.setZipCode(12345);
		lt.setState("my state");
		lt.setLatitude(12.21);
		lt.setLongitude(32.21);
		merchant.getLocations().add(lt);
		lt = new LocationType();
		lt.setAddress1("a3");
		lt.setAddress2("a4");
		lt.setCity("that city");
		lt.setGraphPath("");
		lt.setZipCode(12345);
		lt.setState("that state");
		lt.setLatitude(12.21);
		lt.setLongitude(32.21);
		merchant.getLocations().add(lt);
		result.add(merchant);
		merchant = new MerchantType();
		merchant.setDescription("t2");
		merchant.setGraphPath("http://t2");
		merchant.setName("T2");
		merchant.setUrl("http://t.com");
		merchant.setImageUrl("image url");
		merchant.getLocations().add(lt);
		result.add(merchant);
		*/
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public MerchantType addOrUpdateMerchant(MerchantType mt) throws Exception {
		
		if( mt.getLocations().size() == 0){
			throw new InvalidRequestException("At least one location needs to be specified when adding a merchant");
		}
		
		Merchant merchant = getMerchantFromMerchantType(mt);
		merchant.setDescription(mt.getDescription());
		merchant.setGraphPath(mt.getGraphPath());
		merchant.setName(mt.getName());
		merchant.setUrl(mt.getUrl());
		merchant.setImageUrl(mt.getImageUrl());
		rwMerchantDao.makePersistent(merchant);
		
		for(LocationType lt : mt.getLocations() ){
			Location location = transformLocationTypeToLocation(lt, merchant.getId());
			
			rwLocationDao.makePersistent(location);
			lt.setId(location.getId());
		}
		
		mt.setId(merchant.getId());
		return mt;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public LocationType addOrUpdateLocation(LocationType lt) {
		Location location = transformLocationTypeToLocation(lt, lt.getMerchantId());
		
		rwLocationDao.makePersistent(location);
		lt.setId(location.getId());
		return lt;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public OfferType addOrUpdateOffer(OfferType ot) {
		Offer offer = getOfferFromOfferType(ot);
		offer.setDescription(ot.getDescription());
		offer.setMerchantId(ot.getMerchantId());
		offer.setDefaultText(ot.getDefaultText());
		offer.setGiftName(ot.getGiftName());
		offer.setGiftDescription(ot.getGiftDescription());
		offer.setGiftValue(ot.getGiftValue());
		offer.setKikbakNotificationText(ot.getKikbakNotificationText());
		offer.setKikbakName(ot.getKikbakName());
		offer.setKikbakDescription(ot.getKikbakDescription());
		offer.setKikbakValue(ot.getKikbakValue());
		offer.setKikbakNotificationText(ot.getKikbakNotificationText());
		offer.setName(ot.getName());
		offer.setBeginDate(new Date(ot.getBeginDate()));
		offer.setEndDate(new Date(ot.getEndDate()));
		
		rwOfferDao.makePersistent(offer);
		ot.setId(offer.getId());
		
		return ot;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<OfferType> getOffersByMerchant(MerchantType mt) throws Exception {
		Merchant merchant = roMerchantDao.findById(mt.getId());
		if( merchant == null){
			throw new InvalidRequestException("Invalid or no merchant specified");
		}
		
		Collection<Offer> offers = roOfferDao.listOffers(merchant);
		Collection<OfferType> ots = new ArrayList<OfferType>();
		for(Offer offer: offers){
			OfferType ot = new OfferType();
			ot.setBeginDate(offer.getBeginDate().getTime());
			ot.setDescription(offer.getDescription());
			ot.setKikbakName(offer.getKikbakName());
			ot.setKikbakDescription(offer.getKikbakDescription());
			ot.setKikbakValue(offer.getKikbakValue());
			ot.setKikbakName(offer.getKikbakName());
			ot.setKikbakNotificationText(offer.getKikbakNotificationText());
			ot.setGiftDescription(offer.getGiftDescription());
			ot.setGiftValue(offer.getGiftValue());
			ot.setGiftNotificationText(offer.getGiftNotificationText());
			ot.setEndDate(offer.getEndDate().getTime());
			ot.setId(offer.getId());
			ot.setMerchantId(offer.getMerchantId());
			ot.setName(offer.getName());
			ot.setDefaultText(offer.getDefaultText());
			
			ots.add(ot);
		}
		return ots;
	}

	
	protected Location getLocationFromLocationType(LocationType lt){
		Location location;
		if( lt.getId() == null || lt.getId() != 0){
			location = new Location();
		}
		else{
			location = roLocationDao.findById(lt.getId());
		}
		return location;
	}
	
	protected Merchant getMerchantFromMerchantType(MerchantType mt){
		Merchant merchant;
		if( mt.getId() == null || mt.getId() == 0 ){
			merchant = new Merchant();
		}
		else{
			merchant = roMerchantDao.findById(mt.getId());
		}
		return merchant;
	}
	
	protected Offer getOfferFromOfferType(OfferType ot){
		Offer offer;
		if( ot.getId() == null || ot.getId() == 0 ){
			offer = new Offer();
		}
		else{
			offer = roOfferDao.findById(ot.getId());
		}
		
		return offer;
	}
	
	protected Location transformLocationTypeToLocation(LocationType lt, Long merchantId){
		Location location = getLocationFromLocationType(lt);
		location.setAddress1(lt.getAddress1());
		location.setAddress2(lt.getAddress2());
		location.setCity(lt.getCity());
		location.setState(lt.getState());
		location.setZipcode(lt.getZipCode());
		location.setLatitude(lt.getLongitude());
		location.setLatitude(lt.getLatitude());
		location.setMerchantId(merchantId);
		location.setVerificationCode(lt.getVerificationCode());
		
		return location;
	}
}
