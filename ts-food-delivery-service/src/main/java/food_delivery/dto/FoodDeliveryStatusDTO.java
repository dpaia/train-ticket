package food_delivery.dto;

import food_delivery.entity.FoodDeliveryOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic order info.
 * All update order APIs must use and return this DTO.
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDeliveryStatusDTO {
    private String orderId;
    private String tripId;
    private int seatNo;
    private String stationFoodStoreId;
    private String createdTime;
    private List<FoodDto> foodList;
    private String deliveryTime;
    private double deliveryFee;

    public FoodDeliveryStatusDTO(FoodDeliveryOrder foodDeliveryOrder) {
        this.orderId = foodDeliveryOrder.getId();
        this.tripId = foodDeliveryOrder.getTripId();
        this.seatNo = foodDeliveryOrder.getSeatNo();
        this.stationFoodStoreId = foodDeliveryOrder.getStationFoodStoreId();
        this.createdTime = foodDeliveryOrder.getCreatedTime();
        this.foodList = foodDeliveryOrder.getFoodList()
                .stream()
                .map(f -> new FoodDto(f.getFoodName(), f.getPrice()))
                .collect(Collectors.toList());
        this.deliveryTime = foodDeliveryOrder.getDeliveryTime();
        this.deliveryFee = foodDeliveryOrder.getDeliveryFee();
    }
}
