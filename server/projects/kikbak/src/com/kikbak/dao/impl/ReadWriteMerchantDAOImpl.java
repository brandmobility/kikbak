package com.kikbak.dao.impl;

import org.springframework.stereotype.Repository;

import com.kikbak.dao.ReadWriteMerchantDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Merchant;

@Repository
public class ReadWriteMerchantDAOImpl extends GenericDAOImpl<Merchant, Long> implements ReadWriteMerchantDAO{

}
