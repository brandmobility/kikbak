package com.kikbak.dao;

import com.kikbak.dto.Suggest;

public interface ReadOnlySuggestDAO {

    public Suggest findById(Long id);

}
