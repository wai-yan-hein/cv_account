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
import com.cv.inv.dao.PurchaseHisDao;
import com.cv.inv.dao.PurchaseDetailDao;
import com.cv.inv.entity.PurDetailKey;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
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
 * @author Lenovo
 */
@Service
@Transactional
public class PurchaseDetatilServiceImpl implements PurchaseDetailService {

    private static final Logger logger = LoggerFactory.getLogger(RetInServiceImpl.class);
    private final String DELETE_OPTION = "INV_DELETE";
    private final String SOURCE_PROG = "ACCOUNT";

    @Autowired
    private GlDao glDao;

    @Autowired
    private PurchaseDetailDao dao;
    @Autowired
    private PurchaseHisDao purchaseHisDao;

    @Override
    public PurHisDetail save(PurHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<PurHisDetail> search(String glId) {
        return dao.search(glId);
    }

    @Override
    public void save(PurHis pur, List<PurHisDetail> listPD, List<String> delList) {
        String retInDetailId;
        try {
            if (delList != null) {
                delList.forEach(detailId -> {
                    dao.delete(detailId);
                });
            }
            purchaseHisDao.save(pur);
            String vouNo = pur.getPurInvId();
            for (PurHisDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPurDetailKey() != null) {
                        pd.setPurDetailKey(pd.getPurDetailKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPurDetailKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                    //  pd.setLocation(pur.getLocationId());
                    dao.save(pd);
                }
            }

        } catch (Exception ex) {
            logger.error("savePurchaseDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

    private void processPurchase(String sourceAccId) throws Exception {
        boolean isDeleted = false;
        String deptId = "";
        int split_id = 3;
        String vouTtlAccId = "";//"1-0039";
        String discAccId = "";//"-";
        String payAccId = "";//"1-0016";
        String taxAccId = "-";
        final String vouNo = "";
        String tranSource = "INVENTORY" + "-PUR";
        String remark = "";
        double vouTotal = 0.0;
        double discount = 0.0;
        double payment = 0.0;
        double tax = 0.0;
        Date purDate = null;
        long cusId = 1;
        String curCode = "";
        List<Gl> listGL = glDao.search("-", "-", "-", "-", "-", "-", "-", "-", "-", vouNo, "-", "-", Global.compId.toString(), tranSource, "-", "-", "-");

        boolean vTtlNeed = true;
        boolean discNeed = true;
        boolean payNeed = true;
        boolean taxNeed = true;

        if (listGL != null) {
            if (!listGL.isEmpty()) {
                for (Gl gl : listGL) {
                    if (isDeleted) {
                        glDao.delete(gl.getGlId(), DELETE_OPTION);
                    } else {
                        if (gl.getAccountId().equals(vouTtlAccId)) {
                            vTtlNeed = false;
                            if (vouTotal != 0) {
                                glDao.delete(gl.getGlId(), "Update");
                                gl.setCrAmt(vouTotal);
                                gl.setDescription(vouNo + remark);
                            } else {
                                glDao.delete(gl.getGlId(), DELETE_OPTION);
                            }
                        } else if (gl.getAccountId().equals(discAccId)) {
                            discNeed = false;
                            if (discount != 0) {
                                glDao.delete(gl.getGlId(), "Update");
                                gl.setDrAmt(discount);
                                gl.setDescription(vouNo + remark);
                            } else {
                                glDao.delete(gl.getGlId(), DELETE_OPTION);
                            }
                        } else if (gl.getAccountId().equals(payAccId)) {
                            payNeed = false;
                            if (payment != 0) {
                                glDao.delete(gl.getGlId(), "Update");
                                gl.setDrAmt(payment);
                                gl.setDescription(vouNo + remark);
                            } else {
                                glDao.delete(gl.getGlId(), DELETE_OPTION);
                            }
                        } else if (gl.getAccountId().equals(taxAccId)) {
                            taxNeed = false;
                            if (tax != 0) {
                                glDao.delete(gl.getGlId(), "Update");
                                gl.setDrAmt(tax);
                                gl.setDescription(vouNo + remark);
                            } else {
                                glDao.delete(gl.getGlId(), DELETE_OPTION);
                            }
                        }

                        gl.setGlDate(purDate);
                        gl.setTraderId(cusId);
                        gl.setFromCurId(curCode);
                    }
                }
            } else {
                listGL = new ArrayList();
            }
        } else {

            listGL = new ArrayList();
        }

        if (isDeleted) {
            vTtlNeed = false;
            discNeed = false;
            payNeed = false;
            taxNeed = false;
        }

        if (vouTotal != 0 && vTtlNeed) {
            Gl glVouTotal = new Gl();
            glVouTotal.setSourceAcId(sourceAccId);
            glVouTotal.setAccountId(vouTtlAccId);
            glVouTotal.setCompId(Global.compId);
            glVouTotal.setCrAmt(vouTotal);
            glVouTotal.setDescription(vouNo + remark);
            glVouTotal.setGlDate(purDate);
            glVouTotal.setCreatedBy(SOURCE_PROG);
            glVouTotal.setCreatedDate(Util1.getTodayDate());
            glVouTotal.setTraderId(cusId);
            glVouTotal.setTranSource(tranSource);
            glVouTotal.setVouNo(vouNo);
            glVouTotal.setReference("Purchase Voucher Total");
            glVouTotal.setSplitId(split_id);
            glVouTotal.setFromCurId(curCode);
            glVouTotal.setDeptId(deptId);
            listGL.add(glVouTotal);
        }

        if (discount != 0 && discNeed) {
            Gl glDiscount = new Gl();
            glDiscount.setSourceAcId(sourceAccId);
            glDiscount.setAccountId(discAccId);
            glDiscount.setCompId(Global.compId);
            glDiscount.setDrAmt(discount);
            glDiscount.setDescription(vouNo + remark);
            glDiscount.setGlDate(purDate);
            glDiscount.setCreatedBy(SOURCE_PROG);
            glDiscount.setCreatedDate(Util1.getTodayDate());
            glDiscount.setTraderId(cusId);
            glDiscount.setTranSource(tranSource);
            glDiscount.setVouNo(vouNo);
            glDiscount.setReference("Purchase Voucher Discount");
            //glDiscount.setSplitId(split_id);
            glDiscount.setFromCurId(curCode);
            glDiscount.setDeptId(deptId);
            listGL.add(glDiscount);
        }

        if (payment != 0 && payNeed) {
            Gl glVouPay = new Gl();
            glVouPay.setSourceAcId(sourceAccId);
            glVouPay.setAccountId(payAccId);
            glVouPay.setCompId(Global.compId);
            glVouPay.setDrAmt(payment);
            glVouPay.setDescription(vouNo + remark);
            glVouPay.setGlDate(purDate);
            glVouPay.setCreatedBy(SOURCE_PROG);
            glVouPay.setCreatedDate(Util1.getTodayDate());
            glVouPay.setTraderId(cusId);
            glVouPay.setTranSource(tranSource);
            glVouPay.setVouNo(vouNo);
            glVouPay.setReference("Purchase Voucher Payment");
            //glVouPay.setSplitId(split_id);
            glVouPay.setFromCurId(curCode);
            glVouPay.setDeptId(deptId);
            listGL.add(glVouPay);
        }

        if (tax != 0 && taxNeed) {
            Gl glVouTax = new Gl();
            glVouTax.setSourceAcId(sourceAccId);
            glVouTax.setAccountId(taxAccId);
            glVouTax.setCompId(Global.compId);
            glVouTax.setCrAmt(tax);
            glVouTax.setDescription(vouNo + remark);
            glVouTax.setGlDate(purDate);
            glVouTax.setCreatedBy(SOURCE_PROG);
            glVouTax.setCreatedDate(Util1.getTodayDate());
            glVouTax.setTraderId(cusId);
            glVouTax.setTranSource(tranSource);
            glVouTax.setVouNo(vouNo);
            glVouTax.setReference("Purchase Voucher Tax");
            //glVouTax.setSplitId(split_id);
            glVouTax.setFromCurId(curCode);
            glVouTax.setDeptId(deptId);
            listGL.add(glVouTax);
        }

        if (!listGL.isEmpty()) {
            //saveBatchGL(listGL);
        }

    }

}
