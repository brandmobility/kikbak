package com.kikbak.dao.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;

@Repository
public class ReadOnlyOfferDAOImpl extends ReadOnlyGenericDAOImpl<Offer, Long> implements ReadOnlyOfferDAO {

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> listValidOffers() {
		Date now = new Date();
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.gt("beginDate", now));
		conjunction.add(Restrictions.lt("endDate", now));
		return listByCriteria(conjunction);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> listOffers(Merchant merchant) {
		
		return listByCriteria(Restrictions.eq("merchantId", merchant.getId()));
	}

}
