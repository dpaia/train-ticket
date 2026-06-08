package com.trainticket.controller;

import com.trainticket.entity.Payment;
import com.trainticket.service.PaymentService;
import edu.fudan.common.util.Response;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Chenjie
 * @date 2017/4/7
 */
@RestController
@RequestMapping("/api/v1/paymentservice")
public class PaymentController {

    @Autowired
    PaymentService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @GetMapping(path = "/welcome")
    public String home() {
        return "Welcome to [ Payment Service ] !";
    }

    @PostMapping(path = "/payment")
    public HttpEntity pay(@RequestBody Payment info, @RequestHeader HttpHeaders headers) {
        PaymentController.LOGGER.info("[pay][Pay][PaymentId: {}]", info.getId());
        return ok(service.pay(info, headers));
    }

    @PostMapping(path = "/payment/money")
    public HttpEntity addMoney(@RequestBody Payment info, @RequestHeader HttpHeaders headers) {
        PaymentController.LOGGER.info("[addMoney][Add money][PaymentId: {}]", info.getId());
        return ok(service.addMoney(info, headers));
    }

    @GetMapping(path = "/payment")
    public HttpEntity query(@RequestHeader HttpHeaders headers) {
        PaymentController.LOGGER.info("[query][Query payment]");
        return ok(service.query(headers));
    }

    @GetMapping(path = "/payment/search", produces = "application/json")
    public HttpEntity search(
            @ApiParam(value = "User ID for filtering payments. Required non-empty string identifying the user whose payment history to retrieve.", required = true, example = "user123") 
            @RequestParam("userId") String userId,
            @ApiParam(value = "Start date of the search range in yyyy-MM-dd format. Payments from start of this day (00:00:00 UTC) will be included.", required = true, example = "2025-01-01") 
            @RequestParam("startDate") String startDate,
            @ApiParam(value = "End date of the search range in yyyy-MM-dd format. Payments until end of this day (23:59:59 UTC) will be included. Must be on or after startDate.", required = true, example = "2025-01-31") 
            @RequestParam("endDate") String endDate,
            @ApiParam(value = "Page number for pagination (0-based index). Use 0 for first page. Must be non-negative.", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "Number of records per page. Must be between 1 and configured max page size (default 1000). Results are sorted by payment time in descending order (most recent first).", example = "10") 
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader HttpHeaders headers
    ) {
        LOGGER.info("[search][Query payments by user and date range][userId: {}, startDate: {}, endDate: {}, page: {}, size: {}]", userId, startDate, endDate, page, size);
        Response<?> resp = service.searchByUserAndDateRange(userId, startDate, endDate, page, size, headers);
        if (resp.getStatus() == 0) {
            return ResponseEntity.badRequest().body(resp);
        }
        return ok(resp);
    }

}
