package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
import com.beeja.api.projectmanagement.repository.InvoiceRepository;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    private static Map<String, Object> orgMap;
    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void init() {
        orgMap = new HashMap<>();
        orgMap.put("id", "org123");
        orgMap.put("name", "Tech.at.core");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void testGenerateInvoiceIdentifiers_shouldGenerateCorrectInvoiceAndRemittance() {
        // Arrange
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String expectedPrefix = "INV-" + yearMonth + "-";
        when(invoiceRepository.countByInvoiceIdRegex("^" + expectedPrefix)).thenReturn(0L);


        InvoiceIdentifiersResponse response = invoiceService.generateInvoiceIdentifiers("contract123");


        String expectedInvoiceId = expectedPrefix + "01";
        String expectedRemittance = "TEC" + expectedInvoiceId;

        assertNotNull(response);
        assertEquals(expectedInvoiceId, response.getInvoiceId());
        assertEquals(expectedRemittance, response.getRemittanceReferenceNumber());
    }
}

