package com.kikbak.admin.dao.impl;


import org.springframework.stereotype.Repository;

import com.kikbak.admin.dao.ReadWriteOfferDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Offer;

@Repository
public class ReadWriteOfferDAOImpl extends GenericDAOImpl<Offer, Long> implements ReadWriteOfferDAO{

}
