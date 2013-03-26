package com.kikbak.dao.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.location.Coordinate;
import com.kikbak.location.GeoFence;
import com.kikbak.location.GeoFenceCoordinateType;

@Repository
public class ReadOnlyOfferDAOImpl extends ReadOnlyGenericDAOImpl<Offer, Long> implements ReadOnlyOfferDAO {

	private static final String list_offers_in_geo_fence = "select offer.* from offer, location where offer.merchant_id=location.merchant_id " +
														" and offer.begin_date < ? and offer.end_date > ? and location.latitude > ? " +
														" and location.latitude < ? and location.longitude > ? and location.longitude < ?"; 
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> listValidOffers() {
		Date now = new Date();
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.lt("beginDate", now));
		conjunction.add(Restrictions.gt("endDate", now));
		return listByCriteria(conjunction);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> listOffers(Merchant merchant) {
		
		return listByCriteria(Restrictions.eq("merchantId", merchant.getId()));
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> listValidOffersInGeoFence(GeoFence fence) {
		Date now = new Date();
		Coordinate nw = fence.getFenceCoordinate(GeoFenceCoordinateType.NorthWest);
		Coordinate se = fence.getFenceCoordinate(GeoFenceCoordinateType.SouthEast);
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		Collection<Offer> offers = session.createSQLQuery(list_offers_in_geo_fence).addEntity(Offer.class)
																				   .setDate(0, now)
																				   .setDate(1, now)
																				   .setDouble(2, se.getLatDeg())
																				   .setDouble(3, nw.getLatDeg())
																				   .setDouble(4, se.getLongDeg())
																				   .setDouble(5, nw.getLongDeg())
																				   .list();
		return offers;
	}

}