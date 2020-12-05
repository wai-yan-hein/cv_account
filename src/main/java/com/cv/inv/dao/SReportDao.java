/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import java.util.Map;

/**
 *
 * @author Lenovo
 */
public interface SReportDao {

    public void generateStockBalance(String stockCode, String stockTypeCode, String brandId, String catId, String locId, String changeUnit);

    public void reportViewer(String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters);
}
