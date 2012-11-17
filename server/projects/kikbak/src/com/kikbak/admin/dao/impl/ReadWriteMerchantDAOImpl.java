package com.kikbak.admin.dao.impl;

import org.springframework.stereotype.Repository;

import com.kikbak.admin.dao.ReadWriteMerchantDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Merchant;

@Repository
public class ReadWriteMerchantDAOImpl extends GenericDAOImpl<Merchant, Long> implements ReadWriteMerchantDAO{

}
