package com.beeja.api.financemanagementservice.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BulkPayslipControllerTest {
  @InjectMocks BulkPayslipController bulkPayslipController;

  @Autowired MockMvc mockMvc;

  @Mock private LoanService loanService;

  @Mock private UserContext userContext;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(bulkPayslipController).build();
  }

  @Test
  public void testUploadBulkPaySlips_Success() throws Exception {
    String authorizationHeader = "Bearer token";
    BulkPayslipRequest bulkPayslipRequest = new BulkPayslipRequest();
    doNothing().when(loanService).uploadBulkPaySlips(bulkPayslipRequest, authorizationHeader);
    mockMvc
        .perform(
            post("/v1/payslips")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bulkPayslipRequest)))
        .andExpect(status().isOk());
    verify(loanService, times(1)).uploadBulkPaySlips(bulkPayslipRequest, authorizationHeader);
  }
}
