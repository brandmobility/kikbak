package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyAccountDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Account;

@Repository
public class ReadOnlyAccountDAOImpl extends ReadOnlyGenericDAOImpl<Account, Long> implements ReadOnlyAccountDAO{


	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Account findByEmailAndPassword(String email, String password) {
		return findByCriteria(Restrictions.and(Restrictions.eq("email", email), Restrictions.eq("password", password)));
	}

}
