package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyBlockedNumberDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Blockednumber;

@Repository
public class ReadOnlyBlockedNumberDAOImpl extends ReadOnlyGenericDAOImpl<Blockednumber, Long> implements ReadOnlyBlockedNumberDAO {

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Boolean isBlockedNumber(String number) {
		Blockednumber blocked =  findByCriteria((Restrictions.eq("phoneNumber", number)));
		return blocked != null;
	}

}
