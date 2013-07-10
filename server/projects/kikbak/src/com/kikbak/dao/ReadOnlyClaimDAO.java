package com.kikbak.dao;

import java.util.List;

import com.kikbak.dto.Claim;

public interface ReadOnlyClaimDAO {

    public Claim findById(Long id);
    public List<Claim> listByMerchant(Long merchandId);
    public List<Claim> listByOfferId(Long offerId);
    public List<Claim> listByUserid(Long userId);
}
