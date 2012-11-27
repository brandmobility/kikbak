package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.dto.Transaction;

public class TransactionDAOTest extends KikbakDAOTest {
	
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
		txn.setKikbakId(3);
		txn.setLocationId(15);
		txn.setTransactionType((short)1);
		txn.setUserId(13L);
		rwDao.makePersistent(txn);
		
		Transaction t2 = roDao.findById(txn.getId());
		assertEquals(txn.getAuthorizationCode(), t2.getAuthorizationCode());
	}
}

