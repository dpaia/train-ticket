package food_delivery.controller;

import com.alibaba.fastjson.JSONObject;
import edu.fudan.common.util.Response;
import food_delivery.dto.FoodDeliveryStatusDTO;
import food_delivery.entity.DeliveryInfo;
import food_delivery.entity.FoodDeliveryOrder;
import food_delivery.entity.SeatInfo;
import food_delivery.entity.TripOrderInfo;
import food_delivery.service.FoodDeliveryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(JUnit4.class)
public class FoodDeliveryControllerTest {

    @InjectMocks
    private FoodDeliveryController foodDeliveryController;

    @Mock
    private FoodDeliveryService foodDeliveryService;
    private MockMvc mockMvc;
    private Response response = new Response();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(foodDeliveryController).build();
    }

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fooddeliveryservice/welcome"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to [ food delivery service ] !"));
    }

    @Test
    public void testCreateFoodDeliveryOrder() throws Exception {
        FoodDeliveryOrder foodDeliveryOrder = new FoodDeliveryOrder();
        Mockito.when(foodDeliveryService.createFoodDeliveryOrder(Mockito.any(FoodDeliveryOrder.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(foodDeliveryOrder);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fooddeliveryservice/orders").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testDeleteFoodDeliveryOrder() throws Exception {
        Mockito.when(foodDeliveryService.deleteFoodDeliveryOrder(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/fooddeliveryservice/orders/d/123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetFoodDeliveryOrderById() throws Exception {
        Mockito.when(foodDeliveryService.getFoodDeliveryOrderById(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fooddeliveryservice/orders/123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetFoodDeliveryOrderByStoreId() throws Exception {
        Mockito.when(foodDeliveryService.getFoodDeliveryOrderByStoreId(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fooddeliveryservice/orders/store/1234"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetAllFoodDeliveryOrders() throws Exception {
        Mockito.when(foodDeliveryService.getAllFoodDeliveryOrders(Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fooddeliveryservice/orders/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testUpdateTripId() throws Exception {
        String tripId = "trip ID";
        String updatedTripId = "trip ID updated";
        TripOrderInfo tripOrderInfo = new TripOrderInfo(tripId, updatedTripId);
        FoodDeliveryStatusDTO foodDeliveryStatusDTO = new FoodDeliveryStatusDTO();
        foodDeliveryStatusDTO.setTripId(updatedTripId);
        foodDeliveryStatusDTO.setOrderId(tripId);
        Response successResponse = new Response(1, "success", foodDeliveryStatusDTO);
        Mockito.when(foodDeliveryService.updateTripId(Mockito.eq(tripOrderInfo), Mockito.any(HttpHeaders.class)))
                .thenReturn(successResponse);
        String requestJson = JSONObject.toJSONString(tripOrderInfo);

        String result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/fooddeliveryservice/orders/tripid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        FoodDeliveryStatusDTO foodDeliveryStatusDtoResult = JSONObject.parseObject(
                JSONObject.parseObject(result, Response.class).getData().toString(), FoodDeliveryStatusDTO.class
        );

        Assert.assertEquals(successResponse.getStatus(), JSONObject.parseObject(result, Response.class).getStatus());
        Assert.assertEquals(successResponse.getMsg(), JSONObject.parseObject(result, Response.class).getMsg());
        Assert.assertEquals(tripId, foodDeliveryStatusDtoResult.getOrderId());
        Assert.assertEquals(updatedTripId, foodDeliveryStatusDtoResult.getTripId());
        Mockito.verify(foodDeliveryService, Mockito.times(1))
                .updateTripId(Mockito.eq(tripOrderInfo), Mockito.any(HttpHeaders.class));
    }

    @Test
    public void testUpdateSeatNumber() throws Exception {
        String orderId = "order ID";
        int updatedSeatNumber = 10;
        SeatInfo seatInfo = new SeatInfo(orderId, updatedSeatNumber);
        FoodDeliveryStatusDTO foodDeliveryStatusDTO = new FoodDeliveryStatusDTO();
        foodDeliveryStatusDTO.setSeatNo(updatedSeatNumber);
        foodDeliveryStatusDTO.setOrderId(orderId);
        Response successResponse = new Response(1, "success", foodDeliveryStatusDTO);
        Mockito.when(foodDeliveryService.updateSeatNo(Mockito.eq(seatInfo), Mockito.any(HttpHeaders.class)))
                .thenReturn(successResponse);
        String requestJson = JSONObject.toJSONString(seatInfo);

        String result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/fooddeliveryservice/orders/seatno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        FoodDeliveryStatusDTO foodDeliveryStatusDtoResult = JSONObject.parseObject(
                JSONObject.parseObject(result, Response.class).getData().toString(), FoodDeliveryStatusDTO.class
        );

        Assert.assertEquals(successResponse.getStatus(), JSONObject.parseObject(result, Response.class).getStatus());
        Assert.assertEquals(successResponse.getMsg(), JSONObject.parseObject(result, Response.class).getMsg());
        Assert.assertEquals(orderId, foodDeliveryStatusDtoResult.getOrderId());
        Assert.assertEquals(updatedSeatNumber, foodDeliveryStatusDtoResult.getSeatNo());
        Mockito.verify(foodDeliveryService, Mockito.times(1))
                .updateSeatNo(Mockito.eq(seatInfo), Mockito.any(HttpHeaders.class));
    }

    @Test
    public void testUpdateDeliveryTime() throws Exception {
        String orderId = "order ID";
        String updatedDeliveryTime = "updated delivery time";
        DeliveryInfo deliveryInfo = new DeliveryInfo(orderId, updatedDeliveryTime);
        FoodDeliveryStatusDTO foodDeliveryStatusDTO = new FoodDeliveryStatusDTO();
        foodDeliveryStatusDTO.setDeliveryTime(updatedDeliveryTime);
        foodDeliveryStatusDTO.setOrderId(orderId);
        Response successResponse = new Response(1, "success", foodDeliveryStatusDTO);
        Mockito.when(foodDeliveryService.updateDeliveryTime(Mockito.eq(deliveryInfo), Mockito.any(HttpHeaders.class)))
                .thenReturn(successResponse);
        String requestJson = JSONObject.toJSONString(deliveryInfo);

        String result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/fooddeliveryservice/orders/dtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        FoodDeliveryStatusDTO foodDeliveryStatusDtoResult = JSONObject.parseObject(
                JSONObject.parseObject(result, Response.class).getData().toString(), FoodDeliveryStatusDTO.class
        );

        Assert.assertEquals(successResponse.getStatus(), JSONObject.parseObject(result, Response.class).getStatus());
        Assert.assertEquals(successResponse.getMsg(), JSONObject.parseObject(result, Response.class).getMsg());
        Assert.assertEquals(orderId, foodDeliveryStatusDtoResult.getOrderId());
        Assert.assertEquals(updatedDeliveryTime, foodDeliveryStatusDtoResult.getDeliveryTime());
        Mockito.verify(foodDeliveryService, Mockito.times(1))
                .updateDeliveryTime(Mockito.eq(deliveryInfo), Mockito.any(HttpHeaders.class));
    }

}
