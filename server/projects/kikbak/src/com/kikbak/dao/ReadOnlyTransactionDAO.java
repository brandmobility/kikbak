package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Transaction;

public interface ReadOnlyTransactionDAO {

	public Transaction findById(Long id);
	public Collection<Transaction> listByUserId(Long userId);
	public Collection<Transaction> listByOfferId(Long offerId);
	public Collection<Transaction> listByKikbakId(Long kikbakId);
	public Collection<Transaction> listByMerchantId(Long merchantId);
	public Collection<Transaction> listByLocationId(long locationId);
	
}
