package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Credit;

public interface ReadOnlyCreditDAO {

	public Credit findById(Long id);
	public Credit findByUserIdAndOfferId(Long userId, Long offerId);
	public Collection<Credit> listCreditsWithBalance(Long userId);
	public Collection<Credit> listByMerchantId(Long merchantId);
	public Collection<Credit> listByOfferId(Long offerId);
}
