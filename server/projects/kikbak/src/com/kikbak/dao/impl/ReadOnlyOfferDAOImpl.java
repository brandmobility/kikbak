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

	private static final String list_offers_in_geo_fence = "select * from offer where offer.begin_date <= ? and offer.end_date >= ? and merchant_id in" +
														" (select merchant_id from location where location.latitude > ? " +
														" and location.latitude < ? and location.longitude > ? and location.longitude < ?)"; 

    private static final String list_merchants_in_area = "select merchant_id from ("
            + "select merchant_id, geofence, "
            + " (3959*acos(cos(radians(:latitude))*cos(radians(latitude))*cos(radians(longitude)-radians(:longitude))+sin(radians(:latitude))*sin(radians(latitude)))) as distance"
            + " from location having distance < geofence" + " ) as tmp";

    private static final String list_offers_in_area = "select *"
            + " from offer where offer.begin_date <= :now and offer.end_date >= :now" + 
             " and merchant_id in (" + list_merchants_in_area + ")";

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
        Date now = new Date();
		return listByCriteria(Restrictions.eq("merchantId", merchant.getId()),
                Restrictions.le("beginDate", now),
                Restrictions.ge("endDate", now));
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
																				   .setTimestamp(0, now)
																				   .setTimestamp(1, now)
																				   .setDouble(2, se.getLatDeg())
																				   .setDouble(3, nw.getLatDeg())
																				   .setDouble(4, se.getLongDeg())
																				   .setDouble(5, nw.getLongDeg())
																				   .list();
		return offers;
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Offer> listValidOffersForArea(double latitude, double longitude) {
        Date now = new Date();
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Offer> offers = session.createSQLQuery(list_offers_in_area).addEntity(Offer.class)
                .setTimestamp("now", now).setDouble("latitude", latitude).setDouble("longitude", longitude).list();
        return offers;
    }

}
