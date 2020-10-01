/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ProjectCOAMapping;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class ProjectCOAMappingDaoImpl extends AbstractDao<Long, ProjectCOAMapping> implements ProjectCOAMappingDao {

    @Override
    public ProjectCOAMapping save(ProjectCOAMapping pcm) {
        persist(pcm);
        return pcm;
    }

    @Override
    public ProjectCOAMapping findById(Long id) {
        ProjectCOAMapping pcm = getByKey(id);
        return pcm;
    }

    @Override
    public List search(String projectId, String coaCode) {
        String strSql = "select o from VProjectCOAMapping o where o.projectId = '" + projectId + "'";

        if (!coaCode.equals("-")) {
            strSql = strSql + "and o.coaCode = '" + coaCode + "'";
        }

        List listPCM = findHSQL(strSql);
        return listPCM;
    }

    @Override
    public int delete(String projectId, String id) {
        String strSql = "";

        if (!projectId.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.projectId = " + projectId;
            } else {
                strSql = strSql + " and o.projectId = " + projectId;
            }
        }

        if (!id.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.id = " + id;
            } else {
                strSql = strSql + " and o.id = " + id;
            }
        }

        int cnt = 0;
        if (!strSql.isEmpty()) {
            strSql = "delete from ProjectCOAMapping o where " + strSql;
            cnt = execUpdateOrDelete(strSql);
        }

        return cnt;
    }
}
