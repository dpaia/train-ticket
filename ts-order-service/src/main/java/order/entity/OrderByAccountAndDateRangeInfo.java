package order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Request entity for querying orders by account and travel date range
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderByAccountAndDateRangeInfo {
    private String accountId;
    private Date startDate;
    private Date endDate;
}