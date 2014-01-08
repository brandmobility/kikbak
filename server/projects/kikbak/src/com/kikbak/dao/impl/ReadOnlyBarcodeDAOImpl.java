package com.kikbak.dao.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyBarcodeDAO;
import com.kikbak.dao.enums.BarcodeStatus;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Barcode;

@Repository
public class ReadOnlyBarcodeDAOImpl extends ReadOnlyGenericDAOImpl<Barcode, Long> implements ReadOnlyBarcodeDAO {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Barcode findByCode(String code) {
        return findByCriteria(Restrictions.eq("code", code));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Barcode findByUserIdAndAllocatedGift(Long userId, Long allocatedGiftId) {
        return findByCriteria(Restrictions.and(Restrictions.eq("userId", userId),
                Restrictions.eq("allocatedGiftId", allocatedGiftId)));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<Barcode> getPendingBarcodes(Date before) {
        return listByCriteria(Restrictions.eq("status", BarcodeStatus.pending.name()),
                Restrictions.lt("redeemDate", before));
    }

}
