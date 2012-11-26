package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Shared;

public class ReadOnlySharedDAOImpl extends ReadOnlyGenericDAOImpl<Shared, Long> implements ReadOnlySharedDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Shared> listByUserId(Long userId) {
		return listByCriteria(Restrictions.eq("userId", userId));
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Shared> listByLocationId(Long locationId) {
		return listByCriteria(Restrictions.eq("locationId", locationId));
	}

}
