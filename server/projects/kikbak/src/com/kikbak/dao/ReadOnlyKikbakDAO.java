package com.kikbak.dao;

import com.kikbak.dto.Kikbak;

public interface ReadOnlyKikbakDAO {

    public Kikbak findById(Long id);
    public Kikbak findByOfferId(Long id);
}
