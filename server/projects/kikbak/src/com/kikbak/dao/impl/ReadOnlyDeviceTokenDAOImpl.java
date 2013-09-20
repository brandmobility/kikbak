package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Devicetoken;

@Repository
public class ReadOnlyDeviceTokenDAOImpl extends ReadOnlyGenericDAOImpl<Devicetoken, Long> implements
        ReadOnlyDeviceTokenDAO {

    private static final String SQL_LIST_FRIENDS = "select devicetoken.* from devicetoken, user, user2friend where "
            + "user2friend.user_id=? and user.facebook_id = user2friend.facebook_friend_id and devicetoken.user_id=user.id";

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Devicetoken findByUserId(Long userId) {
        return findByCriteria(Restrictions.eq("userId", userId));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Devicetoken> listDeviceTokens(Collection<Long> userIds) {
        return listByCriteria(Restrictions.in("userId", userIds));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Devicetoken> listFriendsDeviceTokens(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        @SuppressWarnings("unchecked")
        Collection<Devicetoken> result = session.createSQLQuery(SQL_LIST_FRIENDS).addEntity(Devicetoken.class)
                .setLong(0, userId).list();
        return result;
    }
}
