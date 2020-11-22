/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.util.Util1;
import com.cv.inv.dao.SaleDetailDao;
import com.cv.inv.dao.SaleHisDao;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.SaleHis;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {
    
    private static final Logger logger = LoggerFactory.getLogger(SaleDetailServiceImpl.class);
    private final String SOURCE_PROG = "ACCOUNT";
    private final String tranSource = "ACCOUNT-SALE";
    private final int split_id = 2;
    
    @Autowired
    private SaleDetailDao dao;
    
    @Autowired
    private SaleHisDao hisDao;
    @Autowired
    private GlDao glDao;
    
    @Override
    public SaleDetailHis save(SaleDetailHis sdh) {
        return dao.save(sdh);
    }
    
    @Override
    public List<SaleDetailHis> search(String vouId) {
        return dao.search(vouId);
    }
    
    @Override
    public void save(SaleHis saleHis, List<SaleDetailHis> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception {
        try {
            if (vouStatus.equals("EDIT")) {
                if (deleteList != null) {
                    deleteList.forEach(detailId -> {
                        dao.delete(detailId);
                    });
                }
            }
            
            hisDao.save(saleHis);
            
            for (SaleDetailHis sdh : listSaleDetail) {
                if (sdh.getStock().getStockCode() != null) {
                    String vouNo = saleHis.getVouNo();
                    sdh.setVouNo(vouNo);
                    dao.save(sdh);
                }
            }
        } catch (Exception ex) {
            logger.error("saveSaleDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            
        }
    }
    
    private void saveInGl() {
        List<Gl> listGL = new ArrayList<>();
        String sourceAccId = "";
        String vouTtlAccId = "";
        Double vouTotal = 0.0;
        String remark = "";
        Date saleDate = null;
        Long cusId = null;
        String vouNo = "";
        String curCode = "";
        String depId = "";
        Double discount = 0.0;
        Double payment = 0.0;
        Double tax = 0.0;
        String discAccId = "";
        String payAccId = "";
        String taxAccId = "";
        if (vouTotal != 0) {
            Gl glVouTotal = new Gl();
            glVouTotal.setSourceAcId(sourceAccId);
            glVouTotal.setAccountId(vouTtlAccId);
            glVouTotal.setCompId(Global.compId);
            glVouTotal.setDrAmt(vouTotal);
            glVouTotal.setDescription(vouNo + remark);
            glVouTotal.setGlDate(saleDate);
            glVouTotal.setCreatedBy(SOURCE_PROG);
            glVouTotal.setCreatedDate(Util1.getTodayDate());
            glVouTotal.setTraderId(cusId);
            glVouTotal.setTranSource(tranSource);
            glVouTotal.setVouNo(vouNo);
            glVouTotal.setReference("Sale Voucher Total");
            glVouTotal.setSplitId(split_id);
            glVouTotal.setFromCurId(curCode);
            glVouTotal.setDeptId(depId);
            listGL.add(glVouTotal);
        }
        
        if (discount != 0) {
            Gl glDiscount = new Gl();
            glDiscount.setSourceAcId(sourceAccId);
            glDiscount.setAccountId(discAccId);
            glDiscount.setCompId(Global.compId);
            glDiscount.setCrAmt(discount);
            glDiscount.setDescription(vouNo + remark);
            glDiscount.setGlDate(saleDate);
            glDiscount.setCreatedBy(SOURCE_PROG);
            glDiscount.setCreatedDate(Util1.getTodayDate());
            glDiscount.setTraderId(cusId);
            glDiscount.setTranSource(tranSource);
            glDiscount.setVouNo(vouNo);
            glDiscount.setReference("Sale Voucher Discount");
            glDiscount.setDeptId(depId);
            glDiscount.setSplitId(split_id);
            glDiscount.setFromCurId(curCode);
            listGL.add(glDiscount);
        }
        
        if (payment != 0) {
            Gl glVouPay = new Gl();
            glVouPay.setSourceAcId(sourceAccId);
            glVouPay.setAccountId(payAccId);
            glVouPay.setCompId(Global.compId);
            glVouPay.setCrAmt(payment);
            glVouPay.setDescription(vouNo + remark);
            glVouPay.setGlDate(saleDate);
            glVouPay.setCreatedBy(SOURCE_PROG);
            glVouPay.setCreatedDate(Util1.getTodayDate());
            glVouPay.setTraderId(cusId);
            glVouPay.setTranSource(tranSource);
            glVouPay.setVouNo(vouNo);
            glVouPay.setReference("Sale Voucher Payment");
            glVouPay.setDeptId(depId);
            glVouPay.setSplitId(split_id);
            glVouPay.setFromCurId(curCode);
            listGL.add(glVouPay);
        }
        
        if (tax != 0) {
            Gl glVouTax = new Gl();
            glVouTax.setSourceAcId(sourceAccId);
            glVouTax.setAccountId(taxAccId);
            glVouTax.setCompId(Global.compId);
            glVouTax.setCrAmt(tax);
            glVouTax.setDescription(vouNo + remark);
            glVouTax.setGlDate(saleDate);
            glVouTax.setCreatedBy(SOURCE_PROG);
            glVouTax.setCreatedDate(Util1.getTodayDate());
            glVouTax.setTraderId(cusId);
            glVouTax.setTranSource(tranSource);
            glVouTax.setVouNo(vouNo);
            glVouTax.setReference("Sale Voucher Tax");
            glVouTax.setDeptId(depId);
            glVouTax.setSplitId(split_id);
            glVouTax.setFromCurId(curCode);
            listGL.add(glVouTax);
        }
        
        if (!listGL.isEmpty()) {
            listGL.forEach((gl) -> {
                try {
                    glDao.save(gl);
                } catch (Exception ex) {
                    logger.error("SaveToGl :" + ex.getMessage());
                }
            });
        }
        
    }
}
