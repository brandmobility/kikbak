package com.kikbak.dao;

import com.kikbak.dto.Blockednumber;


public interface ReadOnlyBlockedNumberDAO {

	public Blockednumber findById(Long id);	
	public Boolean isBlockedNumber(String number);
}
