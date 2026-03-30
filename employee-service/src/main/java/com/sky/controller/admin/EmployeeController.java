package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PageDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    /**
     * 作用: 执行login相关逻辑。
     * 输入: @RequestBody EmployeeLoginDTO employeeLoginDTO。
     * 输出: Result<EmployeeLoginVO>。
     */
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("employee login: {}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @PostMapping("/logout")
    /**
     * 作用: 执行logout相关逻辑。
     * 输入: 无。
     * 输出: Result<String>。
     */
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    /**
     * 作用: 执行save相关逻辑。
     * 输入: @RequestBody EmployeeDTO employeeDTO。
     * 输出: Result<String>。
     */
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("employee info: {}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    /**
     * 作用: 执行page相关逻辑。
     * 输入: EmployeePageQueryDTO employeePageQueryDTO。
     * 输出: Result<PageResult>。
     */
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("employee page query: {}", employeePageQueryDTO);
        PageDTO<Employee> p = employeeService.page(employeePageQueryDTO);

        return Result.success(new PageResult(p.getTotal(), p.getList()));
    }

    @PostMapping("/status/{status}")
    /**
     * 作用: 执行startClose相关逻辑。
     * 输入: @PathVariable Integer status, Long id。
     * 输出: Result<String>。
     */
    public Result<String> startClose(@PathVariable Integer status, Long id) {
        log.info("update employee status: {}, id: {}", status, id);
        employeeService.startClose(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    /**
     * 作用: 执行getById相关逻辑。
     * 输入: @PathVariable Long id。
     * 输出: Result<Employee>。
     */
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("get employee by id: {}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping()
    /**
     * 作用: 执行update相关逻辑。
     * 输入: @RequestBody EmployeeDTO employeeDTO。
     * 输出: Result<String>。
     */
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("update employee info: {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
