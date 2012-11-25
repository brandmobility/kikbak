package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Merchant;

@Repository
public class ReadOnlyMerchantDAOImpl extends ReadOnlyGenericDAOImpl<Merchant, Long> implements ReadOnlyMerchantDAO{
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Merchant findByGraphPath(String graphPath) {
		return findByCriteria(Restrictions.eq("graphPath", graphPath));
	}

}
