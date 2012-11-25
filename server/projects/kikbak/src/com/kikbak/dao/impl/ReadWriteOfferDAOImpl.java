package com.kikbak.dao.impl;


import org.springframework.stereotype.Repository;

import com.kikbak.dao.ReadWriteOfferDAO;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Offer;

@Repository
public class ReadWriteOfferDAOImpl extends GenericDAOImpl<Offer, Long> implements ReadWriteOfferDAO{

}
