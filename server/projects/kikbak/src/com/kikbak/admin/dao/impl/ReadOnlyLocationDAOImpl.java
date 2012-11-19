package com.kikbak.admin.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.admin.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Location;

@Repository
public class ReadOnlyLocationDAOImpl extends ReadOnlyGenericDAOImpl<Location, Long> implements ReadOnlyLocationDAO{

	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Location> findByCoordinates(Double latitude,
			Double Longitude, Double radius) {

		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Location> listByMerchant(Long merchantId){
		
		return listByCriteria(Restrictions.eq("merchantId", merchantId));
	}
}
