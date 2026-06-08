package food_delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import edu.fudan.common.entity.Food;

@Data
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class FoodDeliveryOrder {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 36)
    private String id;

    private String stationFoodStoreId;

    @ElementCollection(targetClass = Food.class)
    private List<Food> foodList;

    private String tripId;

    private int seatNo;

    private String createdTime;

    private String deliveryTime;

    private double deliveryFee;

    public FoodDeliveryOrder(FoodDeliveryOrder foodDeliveryOrder) {
        this.id = foodDeliveryOrder.getId();
        this.stationFoodStoreId = foodDeliveryOrder.getStationFoodStoreId();
        this.foodList = foodDeliveryOrder.getFoodList().stream()
                .map(food -> {
                    Food foodCopy = new Food();
                    foodCopy.setFoodName(food.getFoodName());
                    foodCopy.setPrice(food.getPrice());
                    return foodCopy;
                }).collect(Collectors.toList());
        this.tripId = foodDeliveryOrder.getTripId();
        this.seatNo = foodDeliveryOrder.getSeatNo();
        this.createdTime = foodDeliveryOrder.getCreatedTime();
        this.deliveryTime = foodDeliveryOrder.getDeliveryTime();
        this.deliveryFee = foodDeliveryOrder.getDeliveryFee();
    }
}
