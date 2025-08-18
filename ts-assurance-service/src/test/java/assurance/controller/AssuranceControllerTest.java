package assurance.controller;

import assurance.entity.PlainAssurance;
import assurance.service.AssuranceService;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.UUID;

@RunWith(JUnit4.class)
public class AssuranceControllerTest {

    @InjectMocks
    private AssuranceController assuranceController;

    @Mock
    private AssuranceService assuranceService;
    private MockMvc mockMvc;
    private Response response = new Response();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assuranceController).build();
    }

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/welcome"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to [ Assurance Service ] !"));
    }

    @Test
    public void testGetAllAssurances() throws Exception {
        Mockito.when(assuranceService.getAllAssurances(Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurances"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetAssurancesPaginated() throws Exception {
        PageImpl<PlainAssurance> page = new PageImpl<>(
                Arrays.asList(new PlainAssurance(), new PlainAssurance()),
                PageRequest.of(0, 2),
                3
        );
        UUID userId = UUID.randomUUID();
        Mockito.when(assuranceService.getUserAssurancesPage(Mockito.eq(userId), Mockito.eq(0), Mockito.eq(2), Mockito.any(HttpHeaders.class)))
                .thenReturn(new Response<>(1, "Success", page));
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurances/userid/" + userId + "?page=0&size=2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertTrue(result.contains("\"content\""));
        Assert.assertTrue(result.contains("\"totalElements\":3"));
    }

    @Test
    public void testGetAssurances_InvalidUUID() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assuranceservice/assurances/userid/invalid-uuid-format"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        
        Response<?> response = JSONObject.parseObject(result, Response.class);
        Assert.assertEquals(Integer.valueOf(0), response.getStatus());
        Assert.assertEquals("Invalid UUID format", response.getMsg());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testGetAssurances_NullParameters() throws Exception {
        UUID userId = UUID.randomUUID();
        PageImpl<PlainAssurance> page = new PageImpl<>(
                Arrays.asList(new PlainAssurance()),
                PageRequest.of(0, 10),
                1
        );
        
        Mockito.when(assuranceService.getUserAssurancesPage(
                Mockito.eq(userId), 
                Mockito.isNull(), 
                Mockito.isNull(), 
                Mockito.any(HttpHeaders.class)))
            .thenReturn(new Response<>(1, "Success", page));
        
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assuranceservice/assurances/userid/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Assert.assertTrue(result.contains("\"number\":0"));  // page=0
        Assert.assertTrue(result.contains("\"size\":10"));   // size=10
    }

    @Test
    public void testGetAssurances_OutOfRangePage() throws Exception {
        UUID userId = UUID.randomUUID();
        PageImpl<PlainAssurance> page = new PageImpl<>(
                Arrays.asList(),  // Empty content
                PageRequest.of(999, 10),
                5  // Only 5 items total
        );
        
        Mockito.when(assuranceService.getUserAssurancesPage(
                Mockito.eq(userId),
                Mockito.eq(999),
                Mockito.eq(10),
                Mockito.any(HttpHeaders.class)))
            .thenReturn(new Response<>(1, "Success", page));
        
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assuranceservice/assurances/userid/" + userId + "?page=999&size=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Assert.assertTrue(result.contains("\"empty\":true"));  // No content
        Assert.assertTrue(result.contains("\"totalElements\":5"));
    }

    @Test
    public void testGetAssurances_NegativePage() throws Exception {
        UUID userId = UUID.randomUUID();
        PageImpl<PlainAssurance> page = new PageImpl<>(
                Arrays.asList(),
                PageRequest.of(0, 10),
                0
        );
        
        Mockito.when(assuranceService.getUserAssurancesPage(
                Mockito.eq(userId),
                Mockito.eq(-1),
                Mockito.eq(10),
                Mockito.any(HttpHeaders.class)))
            .thenReturn(new Response<>(1, "Success", page));
        
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assuranceservice/assurances/userid/" + userId + "?page=-1&size=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Assert.assertTrue(result.contains("\"number\":0"));  // Should be 0, not -1
    }

    @Test
    public void testGetAssurances_InvalidSize() throws Exception {
        UUID userId = UUID.randomUUID();
        PageImpl<PlainAssurance> page = new PageImpl<>(
                Arrays.asList(),
                PageRequest.of(0, 10),
                0
        );
        
        Mockito.when(assuranceService.getUserAssurancesPage(
                Mockito.eq(userId),
                Mockito.eq(0),
                Mockito.eq(-5),
                Mockito.any(HttpHeaders.class)))
            .thenReturn(new Response<>(1, "Success", page));
        
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assuranceservice/assurances/userid/" + userId + "?page=0&size=-5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Assert.assertTrue(result.contains("\"size\":10"));  // Should be 10, not -5
    }

    @Test
    public void testGetAllAssuranceType() throws Exception {
        Mockito.when(assuranceService.getAllAssuranceTypes(Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurances/types"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testDeleteAssurance() throws Exception {
        UUID assuranceId = UUID.randomUUID();
        Mockito.when(assuranceService.deleteById(Mockito.any(UUID.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assuranceservice/assurances/assuranceid/" + assuranceId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testDeleteAssuranceByOrderId() throws Exception {
        UUID orderId = UUID.randomUUID();
        Mockito.when(assuranceService.deleteByOrderId(Mockito.any(UUID.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assuranceservice/assurances/orderid/" + orderId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testModifyAssurance() throws Exception {
        Mockito.when(assuranceService.modify(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/assuranceservice/assurances/assurance_id/order_id/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testCreateNewAssurance() throws Exception {
        Mockito.when(assuranceService.create(Mockito.anyInt(), Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurances/1/order_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testGetAssuranceById() throws Exception {
        UUID assuranceId = UUID.randomUUID();
        Mockito.when(assuranceService.findAssuranceById(Mockito.any(UUID.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurances/assuranceid/" + assuranceId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testFindAssuranceByOrderId() throws Exception {
        UUID orderId = UUID.randomUUID();
        Mockito.when(assuranceService.findAssuranceByOrderId(Mockito.any(UUID.class), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assuranceservice/assurance/orderid/" + orderId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

}
