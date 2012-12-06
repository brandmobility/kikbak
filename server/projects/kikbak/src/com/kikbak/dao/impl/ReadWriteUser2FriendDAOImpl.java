package com.kikbak.dao.impl;

import java.math.BigInteger;
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

	private static final String batch_insert = "insert into user2friend (user_id,facebook_friend_id) values ";
	private static final String batch_delete = "delete from user2friend where user_id=";
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchInsert(final Collection<User2friend> friends) {
		Session session = sessionFactory.getCurrentSession();
		
		StringBuffer sb = new StringBuffer(batch_insert);
		for( User2friend friend : friends){
			sb.append("('");
			sb.append(friend.getUserId());
			sb.append("','");
			sb.append(friend.getFacebookFriendId());
			sb.append("'),");
		}
		sb.setCharAt(sb.length()-1, ' ');
		SQLQuery query = session.createSQLQuery(sb.toString());
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchDelete(final Long userId, final Collection<BigInteger> ids) {
		Session session = sessionFactory.getCurrentSession();
		
		StringBuffer sb = new StringBuffer(batch_delete);
		sb.append(userId);
		sb.append(" and facebook_friend_id in(");
		for(BigInteger id : ids){
			sb.append(id + ",");
		}
		
		sb.setCharAt(sb.length()-1, ' ');
		sb.append(")");
		
		SQLQuery query = session.createSQLQuery(sb.toString());
		query.executeUpdate();	
	}

}
