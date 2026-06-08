package com.trainticket.service;

import com.trainticket.entity.Payment;
import edu.fudan.common.util.Response;
import org.springframework.http.HttpHeaders;

import java.time.Instant;

/**
 * @author Chenjie
 * @date 2017/4/3
 */
public interface PaymentService {

    Response pay(Payment info, HttpHeaders headers);

    Response addMoney(Payment info, HttpHeaders headers);

    Response query(HttpHeaders headers);

    Response searchByUserAndDateRange(String userId, String start, String end, int page, int size, HttpHeaders headers);

    void initPayment(Payment payment,HttpHeaders headers);

}
