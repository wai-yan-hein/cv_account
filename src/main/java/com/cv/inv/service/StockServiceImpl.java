/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.util.Util1;
import com.cv.inv.dao.CharacterNoDao;
import com.cv.inv.dao.StockDao;
import com.cv.inv.entity.CharacterNo;
import com.cv.inv.entity.StockType;
import com.cv.inv.entity.Stock;
import java.util.List;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Transactional
@Service
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMainFrame.class);
    @Autowired
    private StockDao dao;
    @Autowired
    private CharacterNoDao chDao;

    @Override
    public Stock save(Stock stock, StockType item, String status) {
        if (status.equals("NEW")) {
            if (stock.getStockCode().isEmpty()) {
                stock.setStockCode(getMedCode(stock.getStockName(), item));
            } else {
                Stock findById = dao.findById(stock.getStockCode());
                if (findById != null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Duplicate Code.");
                }

            }
        }

        return dao.save(stock);
    }

    @Override
    public Stock findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Stock> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Stock> findActiveStock() {
        return dao.findActiveStock();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    private String getMedCode(String stockName, StockType itemType) {
        String medCode = "";
        CharacterNo characterNo = null;

        if (!stockName.equals("")) {
            try {
                for (int i = 0; i < stockName.length(); i++) {
                    String strTmp = stockName.substring(i, i + 1).toUpperCase();
                    if (!strTmp.trim().equals("")) { //Space character detection
                        if (Util1.isNumber(strTmp)) {
                            i = stockName.length();
                            characterNo = new CharacterNo(" ", "00");
                        } else {
                            characterNo = chDao.findById(stockName);
                            if (characterNo != null) {
                                i = stockName.length();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            String strType = "";
            if (characterNo != null) {
                strType = characterNo.getStrNumber();
            }
            medCode = itemType.getItemTypeCode() + strType;
            int typeLength = itemType.getItemTypeCode().length();

            try {
                Object maxMedId = dao.getMax("select max(o.stock_code) from stock o where o.stock_code like '" + medCode + "%'");
                int ttlLength = 5;// get from system property
                int leftLength = ttlLength - medCode.length();

                if (maxMedId == null) {
                    for (int i = 0; i < leftLength - 1; i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + "1";
                } else {
                    int begin = typeLength + 2;
                    String tmpMedSerial = maxMedId.toString().substring(begin);

                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
            } catch (Exception ex) {
                LOGGER.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }

        return medCode;
    }

}
