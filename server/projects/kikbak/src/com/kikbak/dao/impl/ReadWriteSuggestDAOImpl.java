package com.kikbak.dao.impl;

import org.springframework.stereotype.Repository;

import com.kikbak.dao.ReadWriteSuggestDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Suggest;

@Repository
public class ReadWriteSuggestDAOImpl extends GenericDAOImpl<Suggest, Long> implements ReadWriteSuggestDAO {

}
