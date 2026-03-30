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
    /**
     * 作用: 执行login相关逻辑。
     * 输入: EmployeeLoginDTO employeeLoginDTO。
     * 输出: Employee。
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    /**
     * 作用: 执行save相关逻辑。
     * 输入: EmployeeDTO employeeDTO。
     * 输出: 无。
     */
    void save(EmployeeDTO employeeDTO);
    PageDTO<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);
    /**
     * 作用: 执行startClose相关逻辑。
     * 输入: Integer status, Long id。
     * 输出: 无。
     */
    void startClose(Integer status, Long id);
    /**
     * 作用: 执行update相关逻辑。
     * 输入: EmployeeDTO employeeDTO。
     * 输出: 无。
     */
    void update(EmployeeDTO employeeDTO);
}
