package com.kikbak.dao;

import com.kikbak.dto.Gift;

public interface ReadOnlyGiftDAO {

    public Gift findById(Long id);
    public Gift findByOfferId(Long offerId);
    
}
