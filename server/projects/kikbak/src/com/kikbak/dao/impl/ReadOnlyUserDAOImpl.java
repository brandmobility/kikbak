package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User;

@Repository
public class ReadOnlyUserDAOImpl extends ReadOnlyGenericDAOImpl<User, Long> implements ReadOnlyUserDAO {

    /**
     * List users that are eligible to get a gift (<code>:offerId</code>) from user (<code>:userId</code>).
     */
    static final String ELIGIBLE_USERS = "select user.* from user, user2friend where "
            // get all friends of the user
            + "user2friend.user_id = :userId and user2friend.facebook_friend_id = user.facebook_id AND "
            // don't include people who already got that offer from the user or who already redeemed that offer (from other friend)
            + "user.id not in (select distinct user_id from allocatedgift where offer_id = :offerId and (friend_user_id = :userId or redemption_date is not null) ) AND "
            // don't include the user
            + "user.id != :userId ;";

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public User findByFacebookId(Long facebookId) {
        return findByCriteria(Restrictions.eq("facebookId", facebookId));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Long> listEligibleForOfferFromUser(Long offerId, Long fromUserId) {
        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Long> result = session.createSQLQuery(ELIGIBLE_USERS).addScalar("id", StandardBasicTypes.LONG)
                .setLong("offerId", offerId).setLong("userId", fromUserId).list();
        return result;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public User findByManualEmail(String email) {
        return findByCriteria(Restrictions.eq("manualEmail", email).ignoreCase());
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public User findByManualPhone(String phone) {
        return findByCriteria(Restrictions.eq("manualNumber", phone));
    }
}
