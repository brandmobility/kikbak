package com.kikbak.dao;

import com.kikbak.client.service.v1.ReferralCodeUniqueException;
import com.kikbak.dto.Shared;


public interface ReadWriteSharedDAO extends ReadOnlySharedDAO {

	public void saveShared(Shared shared) throws ReferralCodeUniqueException;
	public void makeTransient(Shared shared);
}
