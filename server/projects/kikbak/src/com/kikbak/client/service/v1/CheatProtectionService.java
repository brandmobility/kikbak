package com.kikbak.client.service.v1;

import java.util.Collection;

import com.kikbak.dto.Offer;

public interface CheatProtectionService {

    /**
     * Check if protection policy allows user to receive an offer.
     * 
     * @param userId
     *            user who is getting the offer
     * @param fromUserId
     *            user who is giving the offer
     * @param offer
     *            an offer thats is being shared
     * @return <code>true<code> if <b>currently</b> user can get the gift, 
     *         <code>false<code> if some protection policy forbids it
     */
    public boolean canReceiveGift(Long userId, Long fromUserId, Offer offer);

    /**
     * Get the list of users for which protection policy allows them to receive an offer.
     * 
     * @param userIds
     *            list of users to check
     * @param fromUserId
     *            user who is giving the offer
     * @param offer
     *            an offer that is being shared
     * @return a list of users that can get the gift
     */
    public Collection<Long> getUsersWhoCanReceive(Collection<Long> userIds, Long fromUserId, Offer offer);

}
