package com.kikbak.dao.impl;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User2friend;

public class ReadOnlyUser2FriendDAOImpl extends ReadOnlyGenericDAOImpl<User2friend, Long> implements ReadOnlyUser2FriendDAO{

	private static final String friends_to_delete_query = "select facebook_friend_id from user2friend where user_id=";

    private static final String list_friends = "select user.id from user, user2friend where user.facebook_id = user2friend.facebook_friend_id and user2friend.user_id = :userId";	
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Long> listFriendsForUser(Long userId) {
		String queryString = new String(friends_to_delete_query + userId);
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<Long> listFriendsToDelete(Long userId, Collection<Long> facebookIds) {
		if (facebookIds.isEmpty()) {
		  return Collections.emptyList();
		}
		String fbIds = facebookIds.toString();
		StringBuffer queryString = new StringBuffer(friends_to_delete_query + userId);
		queryString.append(" and facebook_friend_id not in(" + fbIds.substring(1, fbIds.length() -1) +")");
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString()).addScalar("facebook_friend_id", StandardBasicTypes.LONG);
		return query.list();
	}

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Collection<Long> listFriends(Long userId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(list_friends)
                .addScalar("user.id", StandardBasicTypes.LONG).setLong("userId", userId);
        return query.list();
    }
}
