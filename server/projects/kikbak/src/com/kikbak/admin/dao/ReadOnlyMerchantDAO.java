package com.kikbak.admin.dao;

import com.kikbak.dto.Merchant;

public interface ReadOnlyMerchantDAO {
	
	public Merchant findById(Long id);
	
	public Merchant findByGraphPath(String graphPath);

}
