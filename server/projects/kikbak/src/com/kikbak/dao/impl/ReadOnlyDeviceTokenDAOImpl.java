package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Devicetoken;

@Repository
public class ReadOnlyDeviceTokenDAOImpl extends ReadOnlyGenericDAOImpl<Devicetoken, Long> implements ReadOnlyDeviceTokenDAO {

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Devicetoken findByUserId(Long userId) {
		return findByCriteria(Restrictions.eq("userId", userId));
	}

}
