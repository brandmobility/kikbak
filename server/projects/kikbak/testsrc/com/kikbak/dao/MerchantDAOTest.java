package com.kikbak.dao;

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dto.Merchant;


public class MerchantDAOTest extends KikbakBaseTest{

	@Autowired
	ReadWriteMerchantDAO rwDao;
	
	@Autowired
	ReadOnlyMerchantDAO roDao;
	
	@Test
	public void testReadMerchant(){
	
		Merchant merchant = roDao.findById(1L);
		
		assertEquals("23", merchant.getDescription());
	}
	
}
