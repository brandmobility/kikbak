package com.kikbak.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyAccountDAO;
import com.kikbak.dto.Account;

public class AccountDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyAccountDAO dao;
	
	@Test
	public void testAccount(){
		Account account = dao.findByEmailAndPassword("test@kikbak.com", "1234");
		
		assertEquals(account.getPhoneNumber(), "1234");
	}
}
