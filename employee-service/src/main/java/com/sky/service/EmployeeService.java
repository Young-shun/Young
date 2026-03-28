package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PageDTO;
import com.sky.entity.Employee;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService extends IService<Employee> {
    // return Employee entity for login (controller expects Employee)
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    void save(EmployeeDTO employeeDTO);
    PageDTO<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);
    void startClose(Integer status, Long id);
    void update(EmployeeDTO employeeDTO);
}
