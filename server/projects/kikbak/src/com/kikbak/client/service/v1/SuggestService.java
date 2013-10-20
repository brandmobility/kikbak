package com.kikbak.client.service.v1;

import com.kikbak.jaxb.v1.suggest.SuggestBusinessType;

public interface SuggestService {

    void suggestBusiness(final Long userId, final SuggestBusinessType suggestBusiness) throws Exception;
}
