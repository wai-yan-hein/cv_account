/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.SystemPropertyTemplate;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface SystemPropertyTemplateDao {
    public SystemPropertyTemplate save(SystemPropertyTemplate spt);
    public List<SystemPropertyTemplate> search(String propKey, String compType);
    public int delete(String id);
}
