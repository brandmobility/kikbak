package com.kikbak.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dto.Merchant;

public class ReadOnlyMerchantDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyMerchantDAO dao;
	
	@Test
	public void testReadMerchant(){
	
		Merchant merchant = dao.findById(1L);
		
		assertEquals("23", merchant.getDescription());
	}
}
