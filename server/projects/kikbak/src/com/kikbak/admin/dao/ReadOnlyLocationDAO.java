package com.kikbak.admin.dao;

import java.util.Collection;

import com.kikbak.dto.Location;

public interface ReadOnlyLocationDAO {
	
	public Location findById(Long id);
	
	public Collection<Location> listByMerchant(Long merchantId);
	
	public Collection<Location> findByCoordinates(Double latitude, Double Longitude, Double radius);

}
