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
