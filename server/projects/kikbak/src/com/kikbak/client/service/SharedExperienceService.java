package com.kikbak.client.service;

import com.kikbak.jaxb.SharedType;

public interface SharedExperienceService {

	public void registerSharing(final Long userId, SharedType experience);
	
}
