package food_delivery.service;

import edu.fudan.common.entity.Food;
import edu.fudan.common.entity.TripInfo;
import edu.fudan.common.util.Response;
import food_delivery.dto.FoodDeliveryStatusDTO;
import food_delivery.entity.DeliveryInfo;
import food_delivery.entity.FoodDeliveryOrder;
import food_delivery.entity.SeatInfo;
import food_delivery.entity.TripOrderInfo;
import food_delivery.repository.FoodDeliveryOrderRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class FoodDeliveryServiceImplTest {
    @InjectMocks
    private FoodDeliveryServiceImpl foodDeliveryServiceImpl;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FoodDeliveryOrderRepository foodDeliveryOrderRepository;

    private HttpHeaders headers = new HttpHeaders();
    private HttpEntity requestEntity = new HttpEntity(headers);

    private Food food1 = new Food();
    private Food food2 = new Food();
    private FoodDeliveryOrder foodDeliveryOrder = new FoodDeliveryOrder();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        food1.setFoodName("Food name 1");
        food1.setPrice(10.00);
        food2.setFoodName("Food name 2");
        food2.setPrice(20.00);

        foodDeliveryOrder.setId("order ID");
        foodDeliveryOrder.setStationFoodStoreId("station food store ID");
        foodDeliveryOrder.setFoodList(Arrays.asList(food1, food2));
        foodDeliveryOrder.setTripId("trip ID");
        foodDeliveryOrder.setSeatNo(4);
        foodDeliveryOrder.setCreatedTime("created time");
        foodDeliveryOrder.setDeliveryTime("delivery time");
        foodDeliveryOrder.setDeliveryFee(4.2);
    }

    @Test
    public void testUpdateTripId() {
        String updatedTripId = foodDeliveryOrder.getTripId() + " updated";
        Mockito.when(foodDeliveryOrderRepository.findById(foodDeliveryOrder.getId()))
                .thenReturn(java.util.Optional.of(foodDeliveryOrder));
        FoodDeliveryOrder updatedFoodDeliveryOrder = new FoodDeliveryOrder(foodDeliveryOrder);
        updatedFoodDeliveryOrder.setTripId(updatedTripId);
        Mockito.when(foodDeliveryOrderRepository.save(updatedFoodDeliveryOrder)).thenReturn(updatedFoodDeliveryOrder);

        Response updateResponse = foodDeliveryServiceImpl.updateTripId(
                new TripOrderInfo(foodDeliveryOrder.getId(), updatedTripId),
                headers
        );

        Assert.assertEquals(
                new Response<>(1, "update tripId success", new FoodDeliveryStatusDTO(updatedFoodDeliveryOrder)),
                updateResponse
        );
    }

    @Test
    public void testUpdateSeatNumber() {
        int updatedSeatNumber = foodDeliveryOrder.getSeatNo() + 5;
        Mockito.when(foodDeliveryOrderRepository.findById(foodDeliveryOrder.getId()))
                .thenReturn(java.util.Optional.of(foodDeliveryOrder));
        FoodDeliveryOrder updatedFoodDeliveryOrder = new FoodDeliveryOrder(foodDeliveryOrder);
        updatedFoodDeliveryOrder.setSeatNo(updatedSeatNumber);
        Mockito.when(foodDeliveryOrderRepository.save(updatedFoodDeliveryOrder)).thenReturn(updatedFoodDeliveryOrder);

        Response updateResponse = foodDeliveryServiceImpl.updateSeatNo(
                new SeatInfo(foodDeliveryOrder.getId(), updatedSeatNumber),
                headers
        );

        Assert.assertEquals(
                new Response<>(1, "update seatNo success", new FoodDeliveryStatusDTO(updatedFoodDeliveryOrder)),
                updateResponse
        );
    }

    @Test
    public void testUpdateDeliveryTime() {
        String updatedDeliveryTime = foodDeliveryOrder.getDeliveryTime() + " updated";
        Mockito.when(foodDeliveryOrderRepository.findById(foodDeliveryOrder.getId()))
                .thenReturn(java.util.Optional.of(foodDeliveryOrder));
        FoodDeliveryOrder updatedFoodDeliveryOrder = new FoodDeliveryOrder(foodDeliveryOrder);
        updatedFoodDeliveryOrder.setDeliveryTime(updatedDeliveryTime);
        Mockito.when(foodDeliveryOrderRepository.save(updatedFoodDeliveryOrder)).thenReturn(updatedFoodDeliveryOrder);

        Response updateResponse = foodDeliveryServiceImpl.updateDeliveryTime(
                new DeliveryInfo(foodDeliveryOrder.getId(), updatedDeliveryTime),
                headers
        );

        Assert.assertEquals(
                new Response<>(1, "update deliveryTime success", new FoodDeliveryStatusDTO(updatedFoodDeliveryOrder)),
                updateResponse
        );
    }
}
