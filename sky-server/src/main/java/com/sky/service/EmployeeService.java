package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PageDTO;
import com.sky.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * 
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * 
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    PageDTO<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);

    void startClose(Integer status, Long id);

    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);

}
