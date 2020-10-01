/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.ProjectTraderMapping;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface ProjectTraderMappingService {
    public ProjectTraderMapping save(ProjectTraderMapping ptm);
    public ProjectTraderMapping findById(Long id);
    public List search(String projectId, String traderId);
    public int delete(String projectId, String id);
}
