package com.kikbak.client.service.v1;

import com.kikbak.jaxb.v1.share.SharedType;
import com.kikbak.jaxb.v2.share.StoriesResponse;

public interface SharedExperienceService {

	public String registerSharing(final Long userId, SharedType experience);
	
	public void addShareType(String code, String type) throws ReferralCodeUniqueException;

	void getShareStories(Long userId, Long offerId, Long locationId, String imageUrl,
			String platform, String email, String phonenumber, String caption,
			String employeeId, StoriesResponse response)
			throws ReferralCodeUniqueException, RateLimitException;
}
