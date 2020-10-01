/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.ProjectCOAMapping;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface ProjectCOAMappingService {
    public ProjectCOAMapping save(ProjectCOAMapping pcm);
    public ProjectCOAMapping findById(Long id);
    public List search(String projectId, String coaCode);
    public int delete(String projectId, String id);
}
