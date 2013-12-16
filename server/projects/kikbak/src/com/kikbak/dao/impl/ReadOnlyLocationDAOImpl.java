package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Location;
import com.kikbak.location.Coordinate;
import com.kikbak.location.GeoFence;
import com.kikbak.location.GeoFenceCoordinateType;

@Repository
public class ReadOnlyLocationDAOImpl extends ReadOnlyGenericDAOImpl<Location, Long> implements ReadOnlyLocationDAO{

    private static final String location_in_geofence = "select * from location where merchant_id=? and latitude > ? " +
                                                        " and latitude < ? and longitude > ? and longitude < ?";
    
    private static final String locations_in_area = "select *, "
            + " (3959*acos(cos(radians(:latitude))*cos(radians(latitude))*cos(radians(longitude)-radians(:longitude))+sin(radians(:latitude))*sin(radians(latitude)))) as distance"
            + " from location where merchant_id=:merchantId having distance < geofence";

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Location> listByMerchant(Long merchantId){
		
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Location> listForMerchantInGeoFence(Long merchantId, GeoFence fence) {
        Coordinate nw = fence.getFenceCoordinate(GeoFenceCoordinateType.NorthWest);
        Coordinate se = fence.getFenceCoordinate(GeoFenceCoordinateType.SouthEast);
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Location> locations = session.createSQLQuery(location_in_geofence).addEntity(Location.class)
                                                                                   .setLong(0, merchantId)
                                                                                   .setDouble(1, se.getLatDeg())
                                                                                   .setDouble(2, nw.getLatDeg())
                                                                                   .setDouble(3, se.getLongDeg())
                                                                                   .setDouble(4, nw.getLongDeg())
                                                                                   .list();
        return locations;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Location> listForMerchantInArea(Long merchantId, double latitude, double longitude) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Location> locations = session.createSQLQuery(locations_in_area).addEntity(Location.class)
                .setLong("merchantId", merchantId).setDouble("latitude", latitude).setDouble("longitude", longitude)
                .list();
        return locations;
    }    

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean hasLocationInArea(Long merchantId, double latitude, double longitude) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Location> locations = session.createSQLQuery(locations_in_area + " limit 1").addEntity(Location.class)
                .setLong("merchantId", merchantId).setDouble("latitude", latitude).setDouble("longitude", longitude)
                .list();
        return !locations.isEmpty();
    }    
    
}
