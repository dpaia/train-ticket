package com.trainticket.controller;

import com.trainticket.entity.Payment;
import com.trainticket.service.PaymentService;
import edu.fudan.common.util.Response;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(
            value = "Service welcome / health-check",
            notes = "Returns plain text used by load-balancers or monitoring tools to verify the service is alive."
    )
    @ApiResponses(@ApiResponse(code = 200, message = "Plain text welcome message",
            response = String.class))
    @GetMapping(path = "/welcome", produces = "text/plain")
    public String home() {
        return "Welcome to [ Payment Service ] !";
    }

    @ApiOperation(
            value = "Pay for an order",
            notes = "Debits the user’s balance and persists the payment record."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Payment successfully processed",
                    response = Response.class)
    })
    @PostMapping(path = "/payment", consumes = "application/json", produces = "application/json")
    public HttpEntity pay(
            @ApiParam(value = "Payment payload", required = true) @RequestBody Payment info,
            @RequestHeader HttpHeaders headers) {
        PaymentController.LOGGER.info("[pay][Pay][PaymentId: {}]", info.getId());
        return ok(service.pay(info, headers));
    }

    @ApiOperation(
            value = "Add money to the user account",
            notes = "Credits the user’s balance."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Money added successfully",
                    response = Response.class)
    })
    @PostMapping(path = "/payment/money", consumes = "application/json", produces = "application/json")
    public HttpEntity addMoney(
            @ApiParam(value = "Top-up payload", required = true) @RequestBody Payment info,
            @RequestHeader HttpHeaders headers) {

        PaymentController.LOGGER.info("[addMoney][Add money][PaymentId: {}]", info.getId());
        return ok(service.addMoney(info, headers));
    }

    @ApiOperation(
            value = "Query payments",
            notes = "Returns a list of payment records for the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of payments",
                    response = Response.class, responseContainer = "List")
    })
    @GetMapping(path = "/payment", produces = "application/json")
    public HttpEntity query(@RequestHeader HttpHeaders headers) {
        PaymentController.LOGGER.info("[query][Query payment]");
        return ok(service.query(headers));
    }
}
