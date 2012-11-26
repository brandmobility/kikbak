package com.kikbak.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Friend;

public class FriendDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyFriendDAO roDao;
	
	@Autowired
	ReadWriteFriendDAO rwDao;
	
	@Test
	public void testReadFriend(){
		Friend friend = roDao.findByFacebookId(12L);
		assertEquals(friend.getLastName(), "bob");
	}
	
	@Test
	public void testWriteFriend(){
		Friend friend = new Friend();
		friend.setFacebookId(15L);
		friend.setFirstName("t");
		friend.setLastName("L");
		friend.setUsername("blah");
		rwDao.makePersistent(friend);
		
		Friend f = roDao.findByFacebookId(15L);
		assertEquals(friend.getUsername(), f.getUsername());
	}
}
