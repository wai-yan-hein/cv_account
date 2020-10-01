/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.ProjectTraderMappingDao;
import com.cv.accountswing.entity.ProjectTraderMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class ProjectTraderMappingServiceImpl implements ProjectTraderMappingService {
    
    @Autowired
    private ProjectTraderMappingDao dao;
    
    @Override
    public ProjectTraderMapping save(ProjectTraderMapping ptm){
        return dao.save(ptm);
    }
    
    @Override
    public ProjectTraderMapping findById(Long id){
        return dao.findById(id);
    }
    
    @Override
    public List search(String projectId, String traderId){
        return dao.search(projectId, traderId);
    }
    
    @Override
    public int delete(String projectId, String id){
        return dao.delete(projectId, id);
    }
    
}
