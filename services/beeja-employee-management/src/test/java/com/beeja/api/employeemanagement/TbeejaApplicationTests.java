package com.beeja.api.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** */
@SpringBootTest(classes = {EmployeeManagementApplication.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
class TbeejaApplicationTests {

  @Test
  void contextLoads() {}
}
