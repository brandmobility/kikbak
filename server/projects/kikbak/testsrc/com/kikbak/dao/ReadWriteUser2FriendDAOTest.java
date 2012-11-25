package com.kikbak.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.User2friend;
import com.kikbak.dto.User2friendId;

public class ReadWriteUser2FriendDAOTest extends KikbakBaseTest{

	@Autowired
	public ReadWriteUser2FriendDAO dao;
	
	@Test
	public void testBatchWrites(){
		
		Collection<User2friend> friends = new ArrayList<User2friend>();
		int userIdBase = 250;
		int fbIdBase = 1000;
		for( int count = 0; count< 250; count++){
			User2friend friend = new User2friend();
			User2friendId id = new User2friendId();
			id.setFacebookFriendId(fbIdBase + count);
			id.setUserId(userIdBase + count);
			friend.setId(id);
			
			friends.add(friend);
		}
	
		dao.batchInsert(friends);
	}
}
