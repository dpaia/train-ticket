package com.trainticket.repository;

import com.trainticket.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author fdse
 */
public interface PaymentRepository extends PagingAndSortingRepository<Payment,String> {

    Optional<Payment> findById(String id);

    Payment findByOrderId(String orderId);

    @Override
    List<Payment> findAll();

    List<Payment> findByUserId(String userId);

    Page<Payment> findByUserIdAndPaymentTimeBetween(String userId, Instant start, Instant end, Pageable pageable);
}
