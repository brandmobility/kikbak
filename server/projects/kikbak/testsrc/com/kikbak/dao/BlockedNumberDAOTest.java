package com.kikbak.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;

public class BlockedNumberDAOTest extends KikbakBaseTest {
	
	@Autowired
	ReadOnlyBlockedNumberDAO roBNDao;
	
	@Test
	public void testBlockedNumber(){
		roBNDao.isBlockedNumber("3107094681");
	}

}
