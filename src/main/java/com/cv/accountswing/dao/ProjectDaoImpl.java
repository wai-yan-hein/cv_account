/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Project;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class ProjectDaoImpl extends AbstractDao<Long, Project> implements ProjectDao{
    
    @Override
    public Project save(Project project){
        persist(project);
        return project;
    }
    
    @Override
    public Project findById(Long id){
        Project project = getByKey(id);
        return project;
    }
    
    @Override
    public List<Project> search(String projectCode, String projectName, String startDate,
            String endDate, String projectStatus, String compCode, String userId){
        String strFilter = "";
        
        if(!projectCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.projectCode = '" + projectCode + "'";
            }else{
                strFilter = strFilter + "o.projectCode = '" + projectCode + "'";
            }
        }
        
        if(!projectName.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.projectName like '%" + projectName + "%'";
            }else{
                strFilter = strFilter + "o.projectName like '%" + projectName + "%'";
            }
        }
        
        if(!startDate.equals("-") && !endDate.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "(o.startDate between '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "' " + 
                        " or o.endDate between '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "') ";
            }else{
                strFilter = strFilter + " and (o.startDate between '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "' " + 
                        " or o.endDate between '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "') ";
            }
        }/*else if(!startDate.endsWith("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.startDate >= '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and o.startDate >= '" + Util1.toDateStrMYSQL(startDate, "dd/MM/yyyy") + "'";
            }
        }else if(!endDate.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.startDate <= '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and o.startDate <= '" + Util1.toDateStrMYSQL(endDate, "dd/MM/yyyy") + "'";
            }
        }*/
        
        if(!projectStatus.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.projectStatus = " + projectStatus;
            }else{
                strFilter = strFilter + " and o.projectStatus = " + projectStatus;
            }
        }
        
        if(!compCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.compCode = " + compCode;
            }else{
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }
        
        if(!userId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.projectId in (select i.projectId "
                        + "from ProjectUserMapping i where i.userId = " + userId + ")";
            }else{
                strFilter = strFilter + " and o.projectId in (select i.projectId "
                        + "from ProjectUserMapping i where i.userId = " + userId + ")";
            }
        }
        
        if(strFilter.isEmpty()){
            strFilter = "select o from Project o";
        }else{
            strFilter = "select o from Project o where " + strFilter;
        }
        
        List<Project> listP = findHSQL(strFilter);
        return listP;
    }
    
    @Override
    public int delete(String id){
        String strSql = "delete from Project o where o.projectId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
