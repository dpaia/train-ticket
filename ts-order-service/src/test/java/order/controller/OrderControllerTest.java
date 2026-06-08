package order.controller;

import com.alibaba.fastjson.JSONObject;
import edu.fudan.common.entity.Seat;
import edu.fudan.common.util.Response;
import order.entity.Order;
import order.entity.OrderByAccountAndDateRangeInfo;
import order.entity.OrderInfo;
import order.service.OrderService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

@RunWith(JUnit4.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;
    private MockMvc mockMvc;
    private Response response = new Response();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/welcome"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to [ Order Service ] !"));
    }

    @Test
    public void testGetTicketListByDateAndTripId() throws Exception {
        Seat seatRequest = new Seat();
        Mockito.when(orderService.getSoldTickets(Mockito.any(Seat.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(seatRequest);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/tickets").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testCreateNewOrder() throws Exception {
        Order createOrder = new Order();
        Mockito.when(orderService.create(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(createOrder);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testAddCreateNewOrder() throws Exception {
        Order order = new Order();
        Mockito.when(orderService.addNewOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(order);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/admin").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testQueryOrders() throws Exception {
        OrderInfo qi = new OrderInfo();
        Mockito.when(orderService.queryOrders(Mockito.any(OrderInfo.class), Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(qi);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/query").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testQueryOrdersForRefresh() throws Exception {
        OrderInfo qi = new OrderInfo();
        Mockito.when(orderService.queryOrdersForRefresh(Mockito.any(OrderInfo.class), Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(qi);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/refresh").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }
    
    @Test
    public void testQueryOrdersByAccountAndTravelDate() throws Exception {
        // Create test request object
        OrderByAccountAndDateRangeInfo queryInfo = new OrderByAccountAndDateRangeInfo();
        queryInfo.setAccountId("user_id_1");
        queryInfo.setStartDate(new Date());
        queryInfo.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // next day
        
        // Mock service response
        Mockito.when(orderService.queryOrdersByAccountAndTravelDate(
            Mockito.anyString(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(HttpHeaders.class)))
            .thenReturn(response);
        
        // Convert request to JSON and perform POST request (not GET)
        String requestJson = JSONObject.toJSONString(queryInfo);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/byDate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Verify response
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }
    
    @Test
    public void testQueryOrdersByAccountAndTravelDateWithInvalidDateRange() throws Exception {
        // Create test request with invalid date range
        OrderByAccountAndDateRangeInfo queryInfo = new OrderByAccountAndDateRangeInfo();
        queryInfo.setAccountId("user_id_1");
        queryInfo.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // tomorrow
        queryInfo.setEndDate(new Date()); // today - invalid
        
        // Mock service to return error response
        Response errorResponse = new Response(0, "Start date must be before or equal to end date", null);
        Mockito.when(orderService.queryOrdersByAccountAndTravelDate(
            Mockito.anyString(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(HttpHeaders.class)))
            .thenReturn(errorResponse);
        
        // Perform request
        String requestJson = JSONObject.toJSONString(queryInfo);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/byDate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Verify error response
        Response actualResponse = JSONObject.parseObject(result, Response.class);
        Assert.assertEquals(new Integer(0), actualResponse.getStatus());
    }
    
    @Test
    public void testQueryOrdersByAccountAndTravelDateWithNullValues() throws Exception {
        // Create test request with null accountId
        OrderByAccountAndDateRangeInfo queryInfo = new OrderByAccountAndDateRangeInfo();
        queryInfo.setAccountId(null);
        queryInfo.setStartDate(new Date());
        queryInfo.setEndDate(new Date());
        
        // Mock service to return error response
        Response errorResponse = new Response(0, "Account ID cannot be null or empty", null);
        Mockito.when(orderService.queryOrdersByAccountAndTravelDate(
            Mockito.any(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(HttpHeaders.class)))
            .thenReturn(errorResponse);
        
        // Perform request
        String requestJson = JSONObject.toJSONString(queryInfo);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderservice/order/byDate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Verify error response
        Response actualResponse = JSONObject.parseObject(result, Response.class);
        Assert.assertEquals(new Integer(0), actualResponse.getStatus());
    }

    @Test
    public void testCalculateSoldTicket() throws Exception {
        Date travelDate = new Date();
        Mockito.when(orderService.queryAlreadySoldOrders(Mockito.any(Date.class), Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/" + travelDate.toString() + "/train_number"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetOrderPrice() throws Exception {
        Mockito.when(orderService.getOrderPrice(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/price/order_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testPayOrder() throws Exception {
        Mockito.when(orderService.payOrder(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/orderPay/order_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetOrderById() throws Exception {
        Mockito.when(orderService.getOrderById(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/order_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testModifyOrder() throws Exception {
        Mockito.when(orderService.modifyOrder(Mockito.anyString(), Mockito.anyInt(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/status/order_id/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testSecurityInfoCheck() throws Exception {
        Date checkDate = new Date();
        Mockito.when(orderService.checkSecurityAboutOrder(Mockito.any(Date.class), Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order/security/" + checkDate.toString() + "/account_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testSaveOrderInfo() throws Exception {
        Order orderInfo = new Order();
        Mockito.when(orderService.saveChanges(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(orderInfo);
        String result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orderservice/order").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testUpdateOrder() throws Exception {
        Order order = new Order();
        Mockito.when(orderService.updateOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(order);
        String result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orderservice/order/admin").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testDeleteOrder() throws Exception {
        Mockito.when(orderService.deleteOrder(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orderservice/order/order_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testFindAllOrder() throws Exception {
        Mockito.when(orderService.getAllOrders(Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderservice/order"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

}
