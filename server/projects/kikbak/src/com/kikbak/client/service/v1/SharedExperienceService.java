package com.kikbak.client.service.v1;

import java.util.List;

import com.kikbak.jaxb.v1.share.SharedType;
import com.kikbak.jaxb.v2.share.StoryType;

public interface SharedExperienceService {

	public String registerSharing(final Long userId, SharedType experience);
	
	public void getShareStories(Long userId, Long offerId, String imageUrl, String platform, List<StoryType> stories) throws ReferralCodeUniqueException;
	
	public void addShareType(String code, String type) throws ReferralCodeUniqueException;
}
