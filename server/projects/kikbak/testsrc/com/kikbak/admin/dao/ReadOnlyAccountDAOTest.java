package com.kikbak.admin.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Account;

public class ReadOnlyAccountDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyAccountDAO dao;
	
	@Test
	public void testAccount(){
		Account account = dao.findByEmailAndPassword("e@e.com", "123");
		
		assertEquals(account.getPhoneNumber(), "1234");
	}
}