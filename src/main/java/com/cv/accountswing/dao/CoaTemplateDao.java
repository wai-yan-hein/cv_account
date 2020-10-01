/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.CoaTemplate;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface CoaTemplateDao {
    public void addTemplate(String coaCode, String businessType);
    public CoaTemplate save(CoaTemplate coat);
    public List<CoaTemplate> search(String businessType, String coaCode);
    public int delete(String id);
}
