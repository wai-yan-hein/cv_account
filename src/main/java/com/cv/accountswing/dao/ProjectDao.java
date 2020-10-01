/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Project;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface ProjectDao {
    public Project save(Project project);
    public Project findById(Long id);
    public List<Project> search(String projectCode, String projectName, String startDate,
            String endDate, String projectStatus, String compCode, String userId);
    public int delete(String id);
}
