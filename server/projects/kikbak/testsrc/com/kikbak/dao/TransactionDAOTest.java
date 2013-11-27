package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.dao.enums.TransactionType;
import com.kikbak.dto.Transaction;

public class TransactionDAOTest extends CreditDAOTest {
	
	@Autowired
	ReadOnlyTransactionDAO roDao;
	
	@Autowired
	ReadWriteTransactionDAO rwDao;
	
	@Test
	public void testFindById(){
		Transaction txn = roDao.findById(1L);
		assertEquals(12, txn.getOfferId());
	}
	
	@Test
	public void testListByUserId(){
		Collection<Transaction> txns = roDao.listByUserId(1L);
		assertEquals(2, txns.size());
	}
	
	@Test
	public void testListByOfferId(){
		Collection<Transaction> txns = roDao.listByOfferId(12L);
		assertEquals(1, txns.size());
	}
	
	@Test
	public void testListByKikbakId(){
		Collection<Transaction> txns = roDao.listByKikbakId(12L);
		assertEquals(3, txns.size());
	}
	
	@Test
	public void testListByMerchandId(){
		Collection<Transaction> txns = roDao.listByMerchantId(8L);
		assertEquals(2, txns.size());
	}
	
	@Test
	public void testListByLocationId(){
		Collection<Transaction> txns = roDao.listByLocationId(11L);
		assertEquals(1, txns.size());
	}

	@Test
	public void testMakePersistent(){
		Transaction txn = new Transaction();
		txn.setAmount(13.42);
		txn.setAuthorizationCode("4354");
		txn.setVerificationCode("434231");
		txn.setDate(new Date());
		txn.setCreditId(3);
		txn.setLocationId(15);
		txn.setTransactionType((short)TransactionType.Credit.ordinal());
		txn.setUserId(13L);
		rwDao.makePersistent(txn);
		
		Transaction t2 = roDao.findById(txn.getId());
		assertEquals(txn.getAuthorizationCode(), t2.getAuthorizationCode());
	}
	
	@Test
	public void testCountOfGifts(){
		Transaction txn = new Transaction();
		txn.setAmount(13.42);
		txn.setAuthorizationCode("4354");
		txn.setVerificationCode("434231");
		txn.setDate(new Date());
		txn.setCreditId(3);
		txn.setLocationId(15);
		txn.setMerchantId(1);
		txn.setTransactionType((short)TransactionType.Credit.ordinal());
		txn.setUserId(13L);
		txn.setOfferId(3);
		rwDao.makePersistent(txn);
		
		Transaction txn2 = new Transaction();
		txn2.setAmount(13.42);
		txn2.setAuthorizationCode("4354");
		txn2.setVerificationCode("434231");
		txn2.setDate(new Date());
		txn2.setCreditId(3);
		txn2.setLocationId(15);
		txn2.setMerchantId(1);
		txn2.setTransactionType((short)TransactionType.Credit.ordinal());
		txn2.setUserId(13L);
		txn2.setOfferId(4);
		rwDao.makePersistent(txn2);
		
		int count = roDao.countOfGiftsRedeemedByUserByMerchant(13L, 1L);
		assertEquals(2, count);
	}
}

