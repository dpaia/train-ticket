package order.repository;

import order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author fdse
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Override
    Optional<Order> findById(String id);

    @Override
    ArrayList<Order> findAll();

    ArrayList<Order> findByAccountId(String accountId);

    ArrayList<Order> findByTravelDateAndTrainNumber(String travelDate,String trainNumber);
    
    /**
     * Find orders by account ID and travel date range.
     * Note: travelDate is stored as String in format yyyy-MM-dd, so string comparison works for date ranges.
     * @param accountId the account ID to search for
     * @param startDate the start date (inclusive) in yyyy-MM-dd format
     * @param endDate the end date (inclusive) in yyyy-MM-dd format
     * @return list of orders matching the criteria
     */
    @Query("SELECT o FROM Order o WHERE o.accountId = :accountId AND o.travelDate >= :startDate AND o.travelDate <= :endDate")
    ArrayList<Order> findByAccountIdAndTravelDateBetween(@Param("accountId") String accountId, 
                                                        @Param("startDate") String startDate, 
                                                        @Param("endDate") String endDate);

    @Override
    void deleteById(String id);
}
