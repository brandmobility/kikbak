package com.kikbak.admin.dao.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.admin.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;

@Repository
public class ReadOnlyOfferDAOImpl extends ReadOnlyGenericDAOImpl<Offer, Long> implements ReadOnlyOfferDAO {

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Collection<Offer> findValidOffers(Merchant merchant) {
		Date now = new Date();
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.eq("merchantId", merchant.getId()));
		conjunction.add(Restrictions.gt("beginDate", now));
		conjunction.add(Restrictions.lt("endDate", now));
		return listByCriteria(conjunction);
	}

}
