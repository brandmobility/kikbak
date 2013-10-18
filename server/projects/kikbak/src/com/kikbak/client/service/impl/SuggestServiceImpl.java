package com.kikbak.client.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.v1.SuggestService;
import com.kikbak.dao.ReadWriteSuggestDAO;
import com.kikbak.dto.Suggest;
import com.kikbak.jaxb.v1.suggest.SuggestBusinessType;

@Service
public class SuggestServiceImpl implements SuggestService {

    @Autowired
    ReadWriteSuggestDAO rwSuggestDao;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void suggestBusiness(Long userId, SuggestBusinessType suggestBusiness) throws Exception {

        Suggest suggest = new Suggest();
        suggest.setBusinessName(suggestBusiness.getBusinessName());
        suggest.setWhy(suggestBusiness.getWhy());
        suggest.setImageUrl(suggestBusiness.getImageUrl());
        suggest.setUserId(userId);
        rwSuggestDao.makePersistent(suggest);

    }

}
