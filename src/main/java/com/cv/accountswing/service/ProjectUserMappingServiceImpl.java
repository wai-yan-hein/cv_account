/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.ProjectUserMappingDao;
import com.cv.accountswing.entity.ProjectUserMapping;
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
public class ProjectUserMappingServiceImpl implements ProjectUserMappingService {
    
    @Autowired
    private ProjectUserMappingDao dao;
    
    @Override
    public ProjectUserMapping save(ProjectUserMapping pum){
        return dao.save(pum);
    }
    
    @Override
    public ProjectUserMapping findById(Long id){
        return dao.findById(id);
    }
    
    @Override
    public List search(String projectId, String userId){
        return dao.search(projectId, userId);
    }
    
    @Override
    public int delete(String projectId, String id){
        return dao.delete(projectId, id);
    }
}
