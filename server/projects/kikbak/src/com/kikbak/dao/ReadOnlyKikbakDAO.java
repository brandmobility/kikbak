package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Kikbak;

public interface ReadOnlyKikbakDAO {

	public Kikbak findById(Long id);
	public Collection<Kikbak> listByUserId(Long userId);
	public Collection<Kikbak> listByMerchantId(Long merchantId);
	public Collection<Kikbak> listByOfferId(Long offerId);
}
