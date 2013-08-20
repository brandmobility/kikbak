package com.kikbak.dao.impl;

import org.springframework.stereotype.Repository;

import com.kikbak.dao.ReadOnlySuggestDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Suggest;

@Repository
public class ReadOnlySuggestDAOImpl extends ReadOnlyGenericDAOImpl<Suggest, Long> implements ReadOnlySuggestDAO {

}
