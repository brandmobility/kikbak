package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadWriteUser2FriendDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.User2friend;

public class ReadWriteUser2FriendDAOImpl extends GenericDAOImpl<User2friend, Long> implements
		ReadWriteUser2FriendDAO {

	private static final String bulk_insert = "insert into user2friend (user_id,facebook_friend_id) values ";
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchInsert(Collection<User2friend> friends) {
		Session session = sessionFactory.getCurrentSession();
		
		StringBuffer sb = new StringBuffer(bulk_insert);
		for( User2friend friend : friends){
			sb.append("('");
			sb.append(friend.getId().getUserId());
			sb.append("','");
			sb.append(friend.getId().getFacebookFriendId());
			sb.append("'),");
		}
		sb.setCharAt(sb.length()-1, ' ');
		System.out.println(sb.toString());
		SQLQuery query = session.createSQLQuery(sb.toString());
		query.executeUpdate();
	}

}
