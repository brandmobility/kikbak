package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;

public interface ReadOnlyOfferDAO {
	
	public Offer findById(Long id);
	
	public Collection<Offer> listValidOffers(Merchant merchant);
	
	public Collection<Offer> listOffers(Merchant merchant);

}
