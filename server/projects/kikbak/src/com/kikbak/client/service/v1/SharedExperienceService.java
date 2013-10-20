package com.kikbak.client.service.v1;

import com.kikbak.jaxb.v1.share.SharedType;

public interface SharedExperienceService {

	public String registerSharing(final Long userId, SharedType experience);
	
}
