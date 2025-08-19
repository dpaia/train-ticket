package com.trainticket.service;

import com.trainticket.entity.Money;
import com.trainticket.entity.Payment;
import com.trainticket.repository.AddMoneyRepository;
import com.trainticket.repository.PaymentRepository;
import edu.fudan.common.util.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AddMoneyRepository addMoneyRepository;

    private HttpHeaders headers = new HttpHeaders();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(paymentServiceImpl, "maxPageSize", 1000);
    }

    @Test
    public void testPay1() {
        Payment info = new Payment();
        Mockito.when(paymentRepository.findByOrderId(Mockito.anyString())).thenReturn(null);
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(null);
        Response result = paymentServiceImpl.pay(info, headers);
        Assert.assertEquals(1, result.getStatus().intValue());
        Assert.assertEquals("Pay Success", result.getMsg());
        Assert.assertNull(result.getData());
    }

    @Test
    public void testPay2() {
        Payment info = new Payment();
        Mockito.when(paymentRepository.findByOrderId(Mockito.anyString())).thenReturn(info);
        Response result = paymentServiceImpl.pay(info, headers);
        Assert.assertEquals(0, result.getStatus().intValue());
        Assert.assertTrue(result.getMsg().startsWith("Pay Failed, order not found with order id"));
        Assert.assertNull(result.getData());
    }

    @Test
    public void testAddMoney() {
        Payment info = new Payment();
        Money savedMoney = new Money();
        Mockito.when(addMoneyRepository.save(Mockito.any(Money.class))).thenReturn(savedMoney);
        Response result = paymentServiceImpl.addMoney(info, headers);
        Assert.assertEquals(1, result.getStatus().intValue());
        Assert.assertEquals("Add Money Success", result.getMsg());
        Assert.assertNotNull(result.getData());
    }

    @Test
    public void testQuery1() {
        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment());
        Mockito.when(paymentRepository.findAll()).thenReturn(payments);
        Response result = paymentServiceImpl.query(headers);
        Assert.assertEquals(1, result.getStatus().intValue());
        Assert.assertEquals("Query Success", result.getMsg());
        Assert.assertEquals(payments, result.getData());
    }

    @Test
    public void testQuery2() {
        Mockito.when(paymentRepository.findAll()).thenReturn(null);
        Response result = paymentServiceImpl.query(headers);
        Assert.assertEquals(0, result.getStatus().intValue());
        Assert.assertEquals("No Content", result.getMsg());
        Assert.assertNull(result.getData());
    }

    @Test
    public void testInitPayment1() {
        Payment payment = new Payment();
        Mockito.when(paymentRepository.findById(Mockito.anyString())).thenReturn(java.util.Optional.empty());
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(null);
        paymentServiceImpl.initPayment(payment, headers);
        Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
    }

    @Test
    public void testInitPayment2() {
        Payment payment = new Payment();
        Mockito.when(paymentRepository.findById(Mockito.anyString())).thenReturn(java.util.Optional.of(payment));
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(null);
        paymentServiceImpl.initPayment(payment, headers);
        Mockito.verify(paymentRepository, Mockito.times(0)).save(Mockito.any(Payment.class));
    }

    @Test
    public void testSearchByUserAndDateRange_Success() {
        List<Payment> payments = new ArrayList<>();
        Payment payment = new Payment();
        payment.setUserId("user123");
        payments.add(payment);
        Page<Payment> pagedPayments = new PageImpl<>(payments);
        
        Mockito.when(paymentRepository.findByUserIdAndPaymentTimeBetween(
            Mockito.eq("user123"), 
            Mockito.any(Instant.class), 
            Mockito.any(Instant.class), 
            Mockito.any(Pageable.class)
        )).thenReturn(pagedPayments);
        
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "2025-01-31", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(1), result.getStatus());
        Assert.assertEquals("Query Success", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_BlankUserId() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("", "2025-01-01", "2025-01-31", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("userId must not be blank", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_NullUserId() {
        Response result = paymentServiceImpl.searchByUserAndDateRange(null, "2025-01-01", "2025-01-31", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("userId must not be blank", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_BlankStartDate() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "", "2025-01-31", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("startDate must not be blank and must be in yyyy-MM-dd format", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_InvalidStartDateFormat() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025/01/01", "2025-01-31", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("startDate must be in yyyy-MM-dd format (e.g., 2025-01-15)", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_BlankEndDate() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("endDate must not be blank and must be in yyyy-MM-dd format", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_InvalidEndDateFormat() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "01-31-2025", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("endDate must be in yyyy-MM-dd format (e.g., 2025-01-31)", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_EndBeforeStart() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-31", "2025-01-01", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("endDate must be on or after startDate", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_NegativePage() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "2025-01-31", -1, 10, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("page must be >= 0", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_ZeroSize() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "2025-01-31", 0, 0, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("size must be between 1 and 1000", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_ExceedsMaxSize() {
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "2025-01-31", 0, 1001, headers);
        Assert.assertEquals(Integer.valueOf(0), result.getStatus());
        Assert.assertEquals("size must be between 1 and 1000", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_SameDateRange() {
        List<Payment> payments = new ArrayList<>();
        Page<Payment> pagedPayments = new PageImpl<>(payments);
        
        Mockito.when(paymentRepository.findByUserIdAndPaymentTimeBetween(
            Mockito.eq("user123"), 
            Mockito.any(Instant.class), 
            Mockito.any(Instant.class), 
            Mockito.any(Pageable.class)
        )).thenReturn(pagedPayments);
        
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-15", "2025-01-15", 0, 10, headers);
        Assert.assertEquals(Integer.valueOf(1), result.getStatus());
        Assert.assertEquals("Query Success", result.getMsg());
    }

    @Test
    public void testSearchByUserAndDateRange_MaxSize() {
        List<Payment> payments = new ArrayList<>();
        Page<Payment> pagedPayments = new PageImpl<>(payments);
        
        Mockito.when(paymentRepository.findByUserIdAndPaymentTimeBetween(
            Mockito.eq("user123"), 
            Mockito.any(Instant.class), 
            Mockito.any(Instant.class), 
            Mockito.any(Pageable.class)
        )).thenReturn(pagedPayments);
        
        Response result = paymentServiceImpl.searchByUserAndDateRange("user123", "2025-01-01", "2025-01-31", 0, 1000, headers);
        Assert.assertEquals(Integer.valueOf(1), result.getStatus());
        Assert.assertEquals("Query Success", result.getMsg());
    }

}
