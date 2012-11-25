package com.kikbak.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadWriteMerchantDAO;
import com.kikbak.dto.Merchant;

public class ReadWriteMerchantDAOTest extends KikbakBaseTest{

	@Autowired
	ReadWriteMerchantDAO rwDao;
	
	@Autowired
	ReadOnlyMerchantDAO roDao;
	
	@Test
	public void testWriteMerchant(){
		Merchant merchant = new Merchant();
		merchant.setName("name");
		merchant.setGraphPath("foo_path");
		merchant.setDescription("desc");
	
		rwDao.makePersistent(merchant);
		
		Merchant m2 = roDao.findByGraphPath("foo_path");
		
		assertEquals("foo_path",m2.getGraphPath());
		
	}
}
