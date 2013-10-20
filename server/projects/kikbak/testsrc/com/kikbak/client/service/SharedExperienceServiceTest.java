package com.kikbak.client.service;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dto.Shared;
import com.kikbak.jaxb.share.SharedType;

public class SharedExperienceServiceTest extends KikbakBaseTest{

	@Autowired
	SharedExperienceService service;
	
	@Autowired
    @Qualifier("ReadOnlySharedDAO")
	ReadOnlySharedDAO roSharedDao;
	
	@Test
	public void testRegisteringSharing(){
	
		SharedType st = new SharedType();
		st.setLocationId(12L);
		st.setMerchantId(12);
		st.setOfferId(12);
		st.setType("email");
		
		service.registerSharing(12L, st);
		
		
		Collection<Shared> shares = roSharedDao.listByUserId(12L);
		assertEquals(1, shares.size());
	}
}
