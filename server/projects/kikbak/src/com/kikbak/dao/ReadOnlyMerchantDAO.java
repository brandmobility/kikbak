package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Merchant;

public interface ReadOnlyMerchantDAO {
	
	public Merchant findById(Long id);

	public Merchant findByName(String name);
		
	public Collection<Merchant> listAll();

}
