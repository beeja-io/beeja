package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.repository.EmployeeRepository;
import com.beeja.api.employeemanagement.utils.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddingEmployeeNumber {

    @Autowired
    EmployeeRepository employeeRepository;

    @PostConstruct
    public void initAddEmpNumber(){
        AddEmpNumber();
    }

    private void AddEmpNumber() {

        List<Employee> allEmployees = employeeRepository.findAll();

        for (Employee emp : allEmployees) {
            String empId = emp.getEmployeeId();

            if (empId != null && !empId.isBlank() && emp.getEmployeeNumber() == 0) {
                try {
                    emp.setEmployeeNumber(extractEmpNumber(empId));
                    employeeRepository.save(emp);
                } catch (NumberFormatException e) {

                    log.warn(Constants.ERROR_IN_EXTRACTING_EMP_NUMBER, empId, e);
                }
            }
        }
    }

    private int extractEmpNumber(String empId) {

        String empNumber = empId.replaceAll("\\D", "");

        if (empNumber.isEmpty()) {
            throw new NumberFormatException(Constants.NO_NUMERIC_FOUND + empId);
        }

        return Integer.parseInt(empNumber);
    }

}
