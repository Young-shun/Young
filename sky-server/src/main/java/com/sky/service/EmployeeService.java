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

    /**
     * 作用: 执行startClose相关逻辑。
     * 输入: Integer status, Long id。
     * 输出: 无。
     */
    void startClose(Integer status, Long id);

    /**
     * 作用: 执行getById相关逻辑。
     * 输入: Long id。
     * 输出: Employee。
     */
    Employee getById(Long id);

    /**
     * 作用: 执行update相关逻辑。
     * 输入: EmployeeDTO employeeDTO。
     * 输出: 无。
     */
    void update(EmployeeDTO employeeDTO);

}
