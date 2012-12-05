package com.kikbak.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.User2friend;


public class User2FriendDAOTest extends KikbakBaseTest{

	@Autowired
	public ReadWriteUser2FriendDAO rwDao;
	
	@Autowired 
	public ReadOnlyUser2FriendDAO roDao;
	
	@Test
	public void testBatchWrites(){
		
		Collection<User2friend> friends = new ArrayList<User2friend>();
		int userIdBase = 250;
		int fbIdBase = 1000;
		for( int count = 0; count< 250; count++){
			User2friend friend = new User2friend();
			friend.setFacebookFriendId(fbIdBase + count);
			friend.setUserId(userIdBase + count);
			
			friends.add(friend);
		}
	
		rwDao.batchInsert(friends);
	}
	
	
	@Test
	public void testFriendsToDelete(){
		
		Collection<User2friend> friends = new ArrayList<User2friend>();
		long userIdBase = 250;
		int fbIdBase = 1000;
		for( int count = 0; count< 15; count++){
			User2friend friend = new User2friend();
			friend.setFacebookFriendId(fbIdBase + count);
			friend.setUserId(userIdBase);
			
			friends.add(friend);
		}
	
		rwDao.batchInsert(friends);
				
		Collection<Long> ids = new ArrayList<Long>();
		ids.add(1001L);
		ids.add(1002L);
		
		Collection<Long> freinds = roDao.listFriendsToDelete(userIdBase, ids);
		assertEquals(13, freinds.size());
	}
	
	
	@Test
	public void testBatchDelete(){
		Collection<User2friend> friends = new ArrayList<User2friend>();
		long userIdBase = 250;
		int fbIdBase = 1000;
		Collection<Long> friendsToDelete = new ArrayList<Long>();
		for( int count = 0; count< 15; count++){
			User2friend friend = new User2friend();
			friend.setFacebookFriendId(fbIdBase + count);
			friend.setUserId(userIdBase);
			friends.add(friend);
			
			friendsToDelete.add((long) (fbIdBase + count));
		}
		
		rwDao.batchInsert(friends);
		rwDao.batchDelete(userIdBase, friendsToDelete);
		
		Collection<Long> existingFriends = roDao.listFriendsForUser(userIdBase);
		assertEquals(0, existingFriends.size());
	}
}
