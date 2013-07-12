package com.kikbak.dao.impl;

import java.util.Date;

import org.hibernate.classic.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadWriteBarcodeDao;
import com.kikbak.dao.generic.GenericDAOImpl;
import com.kikbak.dto.Barcode;

public class ReadWriteBarcodeDAOImpl extends GenericDAOImpl<Barcode, Long> implements ReadWriteBarcodeDao{

    private static final String find_allocated_barcode = "select * from barcode where user_id=? and allocated_gift_id=? and association_date=?";
    private static final String allocate_barcode = "update barcode set user_id=?, allocated_gift_id=?, association_date=? where " +
                    "gift_id=? and allocated_gift_id is NULL and expiration_date > now() and begin_date <= now()  order by RAND() limit 1";
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Barcode allocateBarcode(Long userId, Long giftId, Long allocatedGiftId) {

        Date now = new Date();
        Session session = sessionFactory.getCurrentSession();
        session.createSQLQuery(allocate_barcode).setLong(0, userId)
                                                .setLong(1,  allocatedGiftId)
                                                .setTimestamp(2, now)
                                                .setLong(3, giftId)
                                                .executeUpdate();
        session.flush();
        
        return (Barcode)session.createSQLQuery(find_allocated_barcode).addEntity(Barcode.class)
                                                                        .setLong(0, userId)
                                                                        .setLong(1, allocatedGiftId)
                                                                        .setTimestamp(2, now)
                                                                        .uniqueResult();
    }

}
