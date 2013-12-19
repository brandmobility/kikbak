package com.kikbak.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.GeneralSecurityException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.oned.UPCAWriter;
import com.kikbak.KikbakBaseTest;
import com.kikbak.client.service.v1.RewardService;
import com.kikbak.client.util.CryptoUtils;
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.ReadWriteAllocatedGiftDAO;
import com.kikbak.dao.ReadWriteLocationDAO;
import com.kikbak.dao.ReadWriteMerchantDAO;
import com.kikbak.dao.ReadWriteOfferDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dao.ReadWriteUser2FriendDAO;
import com.kikbak.dao.ReadWriteUserDAO;
import com.kikbak.jaxb.v1.barcode.BarcodeResponse;
import com.kikbak.jaxb.v1.claim.ClaimType;
import com.kikbak.jaxb.v1.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.v1.redeemgift.GiftRedemptionType;

public class RewardServiceTest extends KikbakBaseTest{
	
	@Autowired
	RewardService service;
	
	@Autowired
	ReadWriteOfferDAO rwOfferDao;
	
	@Autowired
	ReadWriteMerchantDAO rwMerchantDao;
	
	@Autowired
	ReadWriteUser2FriendDAO rwU2FDao;
	
	@Autowired
	ReadWriteSharedDAO rwSharedDao;
	
	@Autowired
	ReadWriteAllocatedGiftDAO rwGiftDao;
	
	@Autowired
	ReadWriteLocationDAO rwLocationDao;
	
	@Autowired
	ReadOnlyAllocatedGiftDAO roGiftDao;
	
	@Autowired
	ReadWriteUserDAO rwUserDao;
	
//	@Test
//	public void testGetGifts(){
//		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao, rwUserDao);
//		long userId = populator.pupulateDataForGiftTest(2);
//		
//		Collection<GiftType> gifts = service.getGifts(userId);
////		assertEquals(2, gifts.size());
//	}
//	
//	@Test
//	public void testGiftsWithExistingGift(){
//		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao, rwUserDao);
//		long userId =  populator.pupulateDataForGiftTest(2);
//		
//		Gift gift = new Gift();
//		Date now = new Date();
//		gift.setExperirationDate(new Date(now.getTime() + 999999999));
//		gift.setFriendUserId(16);
//		gift.setMerchantId(1);
//		gift.setOfferId(1);
//		gift.setUserId(32);
//		gift.setValue(12.21);
//		rwGiftDao.makePersistent(gift);
//		
//		Collection<GiftType> gifts = service.getGifts(32L);
//		assertEquals(3, gifts.size());
//	}
//	
//	@Test
//	public void testGetKikbaks(){
//		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao, rwUserDao);
//		long userId = populator.pupulateDataForGiftTest(2);
//		
//		Collection<GiftType> gifts = service.getGifts(32L);
//		GiftType gift = (GiftType) gifts.toArray()[0];
//		ClientLocationType clt = (ClientLocationType) gift.getMerchant().getLocations().toArray()[0];
//		
//		GiftRedemptionType grt = new GiftRedemptionType();
//		grt.setId(gift.getId());
//		grt.setLocationId(clt.getLocationId());
//		grt.setVerificationCode("12345");
//		
//		try {
//			service.registerGiftRedemption(32L, grt);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		Gift giftDto = roGiftDao.findById(gift.getId());
//		
//		Collection<KikbakType> krt = service.getKikbaks(giftDto.getFriendUserId());
//		assertEquals(1, krt.size());
//	}
	
	@Test
	public void testRegisterGiftRedemption(){
		GiftRedemptionType grt = new GiftRedemptionType();
		grt.setId(1);
		grt.setLocationId(1);
		grt.setVerificationCode("4343");
		
		try {
			service.registerGiftRedemption(1L, grt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRegisterKikbakRedemption(){
		CreditRedemptionType krt = new CreditRedemptionType();
		krt.setLocationId(1);
		krt.setVerificationCode("4343");
		krt.setAmount(5);
		krt.setId(1);
		
		try {
			service.redeemCredit(1L, krt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testAllocateBarcode() {
	    try {
	    	BarcodeResponse code = new BarcodeResponse();
            service.getBarcode(52L, 4L, code);
            assertTrue(code.getCode() != null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
	}
	
	@Test
    public void testAllocateBarcodeOnePerUser() {
        try {
        	BarcodeResponse code = new BarcodeResponse();
        	BarcodeResponse code2 = new BarcodeResponse();
            service.getBarcode(52L, 4L, code);
            service.getBarcode(52L, 4L, code2);
            assertEquals(code.getCode(), code.getCode());
            assertEquals(code.getValidDays(), code.getValidDays());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
	
	@Test
	public void testCreateClaim() {
	    ClaimType ct = new ClaimType();
	    ct.setName("foo");
	    ct.setApt("1");
	    ct.setCity("bar");
	    ct.setCreditId(1);
	    ct.setPhoneNumber("1234");
	    ct.setState("ca");
	    ct.setStreet("oak");
	    ct.setZipcode("code");
	    
	    try {
            service.claimCredit(2L, ct);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
	}
	
	
	@Test
	public void testValidBarcode() {
	    try {
	        new UPCAWriter().encode("485963095124", BarcodeFormat.UPC_A, 150, 150);
	    }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
	}
	
	@Test
    public void testInvalidBarcode() {
        try {
            new UPCAWriter().encode("222222222222", BarcodeFormat.UPC_A, 150, 150);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            return;     
        }
        fail();
    }
	
	@Test
	public void testAllocateAndValidateBarcode() {
	    try {
	    	BarcodeResponse code = new BarcodeResponse();
	        service.getBarcode(12L, 5L, code);
            new UPCAWriter().encode(code.getCode(), BarcodeFormat.UPC_A, 150, 150);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
	}
	
	@Test
	public void testGenerateCode(){
		try {
			String code = CryptoUtils.symetricEncrypt(1L);
			System.out.println("new code: " + code);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
