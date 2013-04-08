package com.kikbak.dao.impl;

import org.springframework.stereotype.Repository;

import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Merchant;

@Repository
public class ReadOnlyMerchantDAOImpl extends ReadOnlyGenericDAOImpl<Merchant, Long> implements ReadOnlyMerchantDAO{
	
}
