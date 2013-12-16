package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Location;
import com.kikbak.location.GeoFence;

public interface ReadOnlyLocationDAO {
	
	public Location findById(Long id);
	public Collection<Location> listByMerchant(Long merchantId);
	public Collection<Location> listForMerchantInGeoFence(Long merchantId,GeoFence fence);
    public Collection<Location> listForMerchantInArea(Long merchantId, double latitude, double longitude);
    public boolean hasLocationInArea(Long merchantId, double latitude, double longitude);
    public boolean isValidZipcode(Long merchantId, String zipcode);
}
