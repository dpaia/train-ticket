package com.trainticket.service;

import com.trainticket.entity.Money;
import com.trainticket.entity.Payment;
import com.trainticket.repository.AddMoneyRepository;
import com.trainticket.repository.PaymentRepository;
import edu.fudan.common.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.by;

/**
 * @author  Administrator
 * @date 2017/6/23.
 */
@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AddMoneyRepository addMoneyRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${payment.search.max-page-size:1000}")
    private int maxPageSize;

    @Override
    public Response pay(Payment info, HttpHeaders headers){

        if(paymentRepository.findByOrderId(info.getOrderId()) == null){
            Payment payment = new Payment();
            payment.setOrderId(info.getOrderId());
            payment.setPrice(info.getPrice());
            payment.setUserId(info.getUserId());
            payment.setPaymentTime(Instant.now());
            paymentRepository.save(payment);
            return new Response<>(1, "Pay Success", null);
        }else{
            PaymentServiceImpl.LOGGER.warn("[pay][Pay Failed][Order not found with order id][PaymentId: {}, OrderId: {}]",info.getId(),info.getOrderId());
            return new Response<>(0, "Pay Failed, order not found with order id" +info.getOrderId(), null);
        }
    }

    @Override
    public Response addMoney(Payment info, HttpHeaders headers){
        Money addMoney = new Money();
        addMoney.setUserId(info.getUserId());
        addMoney.setMoney(info.getPrice());
        addMoneyRepository.save(addMoney);
        return new Response<>(1,"Add Money Success", addMoney);
    }

    @Override
    public Response query(HttpHeaders headers){
        List<Payment> payments = paymentRepository.findAll();
        if(payments!= null && !payments.isEmpty()){
            PaymentServiceImpl.LOGGER.info("[query][Find all payment Success][size:{}]",payments.size());
            return new Response<>(1,"Query Success",  payments);
        }else {
            PaymentServiceImpl.LOGGER.warn("[query][Find all payment warn][{}]","No content");
            return new Response<>(0, "No Content", null);
        }
    }

    @Override
    public void initPayment(Payment payment, HttpHeaders headers){
        Optional<Payment> paymentTemp = paymentRepository.findById(payment.getId());
        if(!paymentTemp.isPresent()){
            paymentRepository.save(payment);
            PaymentServiceImpl.LOGGER.error("[initPayment][Init payment error][Payment not found][PaymentId: {}]",payment.getId());
        }else{
            PaymentServiceImpl.LOGGER.info("[initPayment][Init Payment Already Exists][PaymentId: {}]", payment.getId());
        }
    }

    /**
     * Search payments by user ID and date range with pagination.
     * 
     * @param userId the user ID to filter payments
     * @param startDate start date in yyyy-MM-dd format (inclusive, from 00:00:00 UTC)
     * @param endDate end date in yyyy-MM-dd format (inclusive, until 23:59:59 UTC)
     * @param page zero-based page number
     * @param size number of records per page (1 to maxPageSize)
     * @param headers HTTP headers
     * @return Response containing Page of Payment objects or error message
     */
    @Override
    public Response searchByUserAndDateRange(String userId, String startDate, String endDate, int page, int size, HttpHeaders headers) {
        if (userId == null || userId.trim().isEmpty()) {
            return new Response<>(0, "userId must not be blank", null);
        }
        if (startDate == null || startDate.trim().isEmpty()) {
            return new Response<>(0, "startDate must not be blank and must be in yyyy-MM-dd format", null);
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            return new Response<>(0, "endDate must not be blank and must be in yyyy-MM-dd format", null);
        }
        
        Instant start = parseDateToInstant(startDate, false);
        if (start == null) {
            return new Response<>(0, "startDate must be in yyyy-MM-dd format (e.g., 2025-01-15)", null);
        }
        
        Instant end = parseDateToInstant(endDate, true);
        if (end == null) {
            return new Response<>(0, "endDate must be in yyyy-MM-dd format (e.g., 2025-01-31)", null);
        }
        if (end.isBefore(start)) {
            return new Response<>(0, "endDate must be on or after startDate", null);
        }
        if (page < 0) {
            return new Response<>(0, "page must be >= 0", null);
        }
        if (size <= 0 || size > maxPageSize) {
            return new Response<>(0, "size must be between 1 and " + maxPageSize, null);
        }
        Pageable pageable = of(page, size, by("paymentTime").descending());
        Page<Payment> result = paymentRepository.findByUserIdAndPaymentTimeBetween(userId, start, end, pageable);
        return new Response<>(1, "Query Success", result);
    }

    private Instant parseDateToInstant(String input, boolean endOfDay) {
        if (input == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime dateTime = endOfDay
                    ? date.atTime(LocalTime.MAX)
                    : date.atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            LOGGER.warn("[parseDateToInstant][Invalid date format][input: {}, error: {}]", input, e.getMessage());
            return null;
        }
    }

}
