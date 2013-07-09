package com.kikbak.client.service;

import com.kikbak.jaxb.share.SharedType;

public interface SharedExperienceService {

	public void registerSharing(final Long userId, SharedType experience);
	
}
