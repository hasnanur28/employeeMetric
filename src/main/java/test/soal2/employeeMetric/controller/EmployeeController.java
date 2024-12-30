package test.soal2.employeeMetric.controller;

import test.soal2.employeeMetric.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/count")
    public long getCountOfEmployees() throws IOException {
        return employeeService.getCountOfEmployees();
    }

    @GetMapping("/average-salary")
    public Map<String, Object> getAverageSalary() throws IOException {
        return employeeService.getAverageSalary();
    }

    @GetMapping("/min-max-salary")
    public Map<String, Object> getMinMaxSalary() throws IOException {
        return employeeService.getMinMaxSalary();
    }

    @GetMapping("/gender-distribution")
    public Map<String, Long> getGenderDistribution() throws IOException {
        return employeeService.getGenderDistribution();
    }

    @GetMapping("/age-distribution")
    public Map<String, Object> getAgeDistribution() throws IOException {
        return employeeService.getAgeDistribution();
    }

    @GetMapping("/marital-status-distribution")
    public Map<String, Long> getMaritalStatusDistribution() throws IOException {
        return employeeService.getMaritalStatusDistribution();
    }

    @GetMapping("/designation-distribution")
    public Map<String, Long> getDesignationDistribution() throws IOException {
        return employeeService.getDesignationDistribution();
    }

    @GetMapping("/date-of-joining-histogram")
    public Map<String, Object> getDateOfJoiningHistogram() throws IOException {
        return employeeService.getDateOfJoiningHistogram();
    }

    @GetMapping("/top-interests")
    public Map<String, Long> getTopInterests() throws IOException {
        return employeeService.getTopInterests();
    }
}

