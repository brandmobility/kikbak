package com.kikbak.dao.enums;

public enum ProtectionMethod {
    /*
     * Prevent receiving after sharing: If user has shared a gift with a friend, he is ineligible to receive the same
     * gift from anyone else.
     */
    not_after_shared,

    /*
     * Prevent reciprocal giving: If user has shared a gift with a friend, he is ineligible to receive that same gift
     * from the same friend. He can still receive the gift from someone else with whom they have not shared the gift.
     */
    reciprocal,
}
