package com.kikbak.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadWriteLandingHostDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.LandingHost;

public class ReadWriteLandingHostDAOImpl extends GenericDAOImpl<LandingHost, Long> implements ReadWriteLandingHostDAO {

	private static final String INCREASE_COUNT = "insert into landingHost (referer_host, count, block) values('%HOST%', 1, 0) on duplicate key update count = count + 1";
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public LandingHost increaseCount(String host) {
		if (StringUtils.isBlank(host)) {
			return null;
		}
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(INCREASE_COUNT.replace("%HOST%", host));
		query.executeUpdate();	
		return findByCriteria(Restrictions.eq("refererHost", host));
	}

}
