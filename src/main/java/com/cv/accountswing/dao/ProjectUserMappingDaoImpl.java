/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ProjectUserMapping;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class ProjectUserMappingDaoImpl extends AbstractDao<Long, ProjectUserMapping> implements ProjectUserMappingDao {

    @Override
    public ProjectUserMapping save(ProjectUserMapping pum) {
        persist(pum);
        return pum;
    }

    @Override
    public ProjectUserMapping findById(Long id) {
        ProjectUserMapping pum = getByKey(id);
        return pum;
    }

    @Override
    public List search(String projectId, String userId) {
        String strSql = "select o from VProjectUserMapping o where o.projectId = '" + projectId + "'";

        if (!userId.equals("-")) {
            strSql = strSql + "and o.userId = " + userId;
        }

        List listPUM = findHSQL(strSql);
        return listPUM;
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
            strSql = "delete from ProjectUserMapping o where " + strSql;
            cnt = execUpdateOrDelete(strSql);
        }

        return cnt;
    }
}
