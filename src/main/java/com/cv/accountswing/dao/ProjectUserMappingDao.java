/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ProjectUserMapping;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface ProjectUserMappingDao {
    public ProjectUserMapping save(ProjectUserMapping pum);
    public ProjectUserMapping findById(Long id);
    public List search(String projectId, String userId);
    public int delete(String projectId, String id);
}
