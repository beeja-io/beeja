package com.beeja.api.filemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** */
@SpringBootTest(classes = {FileManagementApplication.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TbeejaApplicationTests {

  @Test
  void contextLoads() {}
}
