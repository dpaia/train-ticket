package com.trainticket.controller;

import com.alibaba.fastjson.JSONObject;
import com.trainticket.entity.Payment;
import com.trainticket.repository.PaymentRepository;
import edu.fudan.common.util.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PaymentRepository paymentRepository;


    @AfterEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void searchSuccess() throws Exception {
        // given
        String orderId = "ORD-INTEG-1";
        String userId  = "user-integ";
        savePayment("PAY-DB-1", orderId, userId,
                Instant.parse("2025-01-01T12:34:56Z"));

        // when
        String raw = doSearch(userId, "2025-01-01", "2025-12-31", 0, 10);

        // then
        Response<?> resp = JSONObject.parseObject(raw, Response.class);
        Assertions.assertEquals(1, resp.getStatus(), "service must report success");
        Assertions.assertTrue(raw.contains(orderId),
                "response body should contain the created orderId");
    }

    @Test
    void searchFailedByRange() throws Exception {
        // given
        String orderId = "ORD-INTEG-1";
        String userId  = "user-integ";
        savePayment("PAY-DB-1", orderId, userId,
                Instant.parse("2025-01-01T12:34:56Z"));

        // when
        String raw = doSearch(userId, "2024-01-01", "2024-12-31", 0, 10);

        // then
        Response<?> resp = JSONObject.parseObject(raw, Response.class);
        Assertions.assertEquals(1, resp.getStatus(), "service must report success");
        Assertions.assertFalse(raw.contains(orderId),
                "response body should not contain the created orderId");
    }

    @Test
    void searchBlankUserId() throws Exception {
        mockMvc.perform(
                        get("/api/v1/paymentservice/payment/search")
                                .param("userId", "   ")
                                .param("startDate", "2025-01-01")
                                .param("endDate", "2025-01-31")
                                .header("X-Request-Id", "test-request")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.msg").value("userId must not be blank"));
    }

    @Test
    void searchInvalidDate() throws Exception {
        mockMvc.perform(
                        get("/api/v1/paymentservice/payment/search")
                                .param("userId", "u1")
                                .param("startDate", "invalid-date")
                                .param("endDate", "2025-01-31")
                                .header("X-Request-Id", "test-request")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.msg").value("startDate must be in yyyy-MM-dd format (e.g., 2025-01-15)"));
    }

    @Test
    void searchEndBeforeStart() throws Exception {
        mockMvc.perform(
                        get("/api/v1/paymentservice/payment/search")
                                .param("userId", "u1")
                                .param("startDate", "2025-02-01")
                                .param("endDate", "2025-01-01")
                                .header("X-Request-Id", "test-request")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.msg").value("endDate must be on or after startDate"));
    }

    /**
     * Persists a {@link Payment} instance that will later be queried.
     */
    private void savePayment(String paymentId,
                             String orderId,
                             String userId,
                             Instant paymentTime) {

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setPrice("50");
        payment.setPaymentTime(paymentTime);
        paymentRepository.save(payment);
    }

    /**
     * Invokes the search endpoint and returns the raw JSON response body.
     */
    private String doSearch(String userId,
                            String startDate,
                            String endDate,
                            int page,
                            int size) throws Exception {

        return mockMvc.perform(get("/api/v1/paymentservice/payment/search")
                        .param("userId",   userId)
                        .param("startDate", startDate)
                        .param("endDate",   endDate)
                        .param("page",      String.valueOf(page))
                        .param("size",      String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


}