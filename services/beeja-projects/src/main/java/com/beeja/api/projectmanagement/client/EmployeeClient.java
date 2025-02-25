package com.beeja.api.projectmanagement.client;



import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "employee-service", url = "${client-urls.employeeService}")
public interface EmployeeClient {

    @GetMapping("/v1/users/response")
    List<EmployeeDetailsResponse>getEmployeeDetails();
}

