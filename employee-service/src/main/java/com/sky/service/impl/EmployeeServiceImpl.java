package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PageDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	@Override
	public Employee login(EmployeeLoginDTO employeeLoginDTO) {
		String username = employeeLoginDTO.getUsername();
		String password = employeeLoginDTO.getPassword();

		Employee employee = employeeMapper
				.selectOne(new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, username));

		if (employee == null) {
			throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
		}

		password = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!password.equals(employee.getPassword())) {
			throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
		}

		if (employee.getStatus() == StatusConstant.DISABLE) {
			throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
		}

		return employee;
	}

	@Override
	public void save(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeDTO, employee);
		employee.setStatus(StatusConstant.ENABLE);
		employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
		employeeMapper.insert(employee);
	}

	@Override
	public PageDTO<Employee> page(EmployeePageQueryDTO employeePageQueryDTO) {
		Page<Employee> page = employeePageQueryDTO.toMpPageDefaultSortByCreateTimeDesc();
		page(page, new LambdaQueryWrapper<Employee>()
				.like(StringUtils.hasText(employeePageQueryDTO.getName()), Employee::getName, employeePageQueryDTO.getName()));
		return PageDTO.of(page, Employee.class);
	}

	@Override
	public void startClose(Integer status, Long id) {
		Employee employee = Employee.builder().id(id).status(status).build();
		employeeMapper.updateById(employee);
	}

	@Override
	public Employee getById(java.io.Serializable id) {
		Employee employee = employeeMapper.selectById(id);
		if (employee != null) {
			employee.setPassword("****");
		}
		return employee;
	}

	@Override
	public void update(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeDTO, employee);
		employeeMapper.updateById(employee);
	}

}
