package com.kikbak.client.service.v1;

import com.kikbak.dao.enums.Channel;

public interface SharedExperienceService {

    class ShareInfo {
        // mandatory
        public long userId;
        public long offerId;
        public Channel channel;
        // optional
        public String caption;
        public String imageUrl;
        public Long locationId;
        public String employeeId;
        public String email;
        public String phoneNumber;
    }

    public String registerSharing(ShareInfo share) throws RateLimitException;

    public String registerSharingAndNotify(ShareInfo share) throws RateLimitException;

    public void addShareType(String code, Channel channel);
    
    public boolean validateZipCodeEligibility(long offerId, String zipCode);

}
