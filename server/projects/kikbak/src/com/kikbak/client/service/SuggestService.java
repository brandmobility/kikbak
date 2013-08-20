package com.kikbak.client.service;

import com.kikbak.jaxb.suggest.SuggestBusinessType;

public interface SuggestService {

    void suggestBusiness(final Long userId, final SuggestBusinessType suggestBusiness) throws Exception;
}
