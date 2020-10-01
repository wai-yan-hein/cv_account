/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.dao.ProjectCOAMappingDao;
import com.cv.accountswing.dao.ProjectDao;
import com.cv.accountswing.dao.ProjectTraderMappingDao;
import com.cv.accountswing.dao.ProjectUserMappingDao;
import com.cv.accountswing.dao.SeqTableDao;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Project;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectDao dao;
    @Autowired
    private SeqTableDao sqtDao;
    @Autowired
    private ProjectCOAMappingDao pcmDao;
    @Autowired
    private ProjectTraderMappingDao ptmDao;
    @Autowired
    private ProjectUserMappingDao pumDao;
    @Autowired
    private GlDao glDao;
    
    @Override
    public Project save(Project project){
        if(project.getProjectCode() == null){
            Date prjDate = project.getCreatedDate();
            LocalDate localDate = prjDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String strYear = String.valueOf(localDate.getYear());
            int seqNo = sqtDao.getSequence("PROJECT", strYear, project.getCompCode().toString());
            project.setProjectCode(seqNo + "/" + strYear);
        }
        return dao.save(project);
    }
    
    @Override
    public Project findById(Long id){
        return dao.findById(id);
    }
    
    @Override
    public List<Project> search(String projectCode, String projectName, String startDate,
            String endDate, String projectStatus, String compCode, String userId){
        return dao.search(projectCode, projectName, startDate, endDate, 
                projectStatus, compCode, userId);
    }
    
    @Override
    public int delete(String id){
        List<Gl> listGL = glDao.search("-", "-", "-", "-", "-", "-", "-", "-", 
                "-", "-", "-", "-", "-", "-", "-", "-", id);
        
        if(listGL != null){
            if(!listGL.isEmpty()){
                return -1;
            }
        }
        
        pcmDao.delete(id, "-");
        ptmDao.delete(id, "-");
        pumDao.delete(id, "-");
        return dao.delete(id);
    }
}
