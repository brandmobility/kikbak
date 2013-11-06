package com.kikbak.client.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kikbak.client.service.impl.types.ProtectionMethod;
import com.kikbak.client.service.v1.CheatProtectionService;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dto.Offer;
import com.kikbak.dto.Shared;

@Service
public class CheatProtectionServiceImpl implements CheatProtectionService {

    public static final String PROTECTION_NOT_AFTER_SHARED = "not_after_shared";

    @Autowired
    @Qualifier("ReadOnlySharedDAO")
    ReadOnlySharedDAO roSharedDao;

    @Autowired
    ReadOnlyUser2FriendDAO roUser2FriendDao;

    public boolean canReceiveGift(Long userId, Long fromUseId, Offer offer) {

        String protection = offer.getProtection();
        if (protection == null || protection.isEmpty())
            return true;

        if (ProtectionMethod.not_after_shared.name().equals(protection))
            return notAfterShared(userId, offer);

        if (ProtectionMethod.reciprocal.name().equals(protection))
            return reciprocal(userId, fromUseId, offer);

        return true;
    }

    public Collection<Long> getUsersWhoCanReceive(Collection<Long> userIds, Long fromUserId, Offer offer) {

        String protection = offer.getProtection();
        if (protection == null || protection.isEmpty())
            return userIds;

        ArrayList<Long> result = new ArrayList<Long>();
        for (Long userId : userIds) {
            if (canReceiveGift(userId, fromUserId, offer)) {
                result.add(userId);
            }
        }
        return result;
    }

    private boolean notAfterShared(Long userId, Offer offer) {
        Collection<Shared> result = roSharedDao.listByUserIdAndOfferId(userId, offer.getId());
        if (!result.isEmpty())
            return false;
        return true;
    }

    private boolean reciprocal(Long userId, Long fromUseId, Offer offer) {
        Collection<Shared> result = roSharedDao.listByUserIdAndOfferId(userId, offer.getId());
        if (!result.isEmpty()) {
            Collection<Long> friends = roUser2FriendDao.listFriends(userId);
            if (friends.contains(fromUseId))
                return false;
        }
        return true;
    }
}
