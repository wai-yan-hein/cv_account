/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.Department;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface DepartmentService {

    public Department save(Department dept);

    public Department findById(String id);

    public List<Department> search(String code, String name, String compCode, String userCode, String parentId);

    public int delete(String code);

    public List<Department> findAll();
}
