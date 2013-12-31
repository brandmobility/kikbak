package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyZipcodeDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Location;
import com.kikbak.dto.Zipcode;

@Repository
public class ReadOnlyZipcodeDAOImpl extends ReadOnlyGenericDAOImpl<Location, Long> implements ReadOnlyZipcodeDAO {

    private static final String VALID_ZIPCODE = "select * from zipcode where merchant_id=? and zipcode=? limit 1";

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean isValidZipcode(long merchantId, String zipCode) {
        int zip;
        try {
            zip = Integer.parseInt(zipCode);
        } catch (NumberFormatException e) {
            return false;
        }

        Session session = sessionFactory.getCurrentSession();
        @SuppressWarnings("unchecked")
        Collection<Location> result = session.createSQLQuery(VALID_ZIPCODE).addEntity(Zipcode.class)
                .setLong(0, merchantId).setInteger(1, zip).list();

        return !result.isEmpty();
    }
}
