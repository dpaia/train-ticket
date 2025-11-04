package fdse.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import edu.fudan.common.util.Response;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.controller.dto.StationDTO;
import fdse.microservice.controller.dto.StationUpdateDTO;
import fdse.microservice.service.StationService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class StationControllerTest {

    @InjectMocks
    private StationController stationController;

    @Mock
    private StationService stationService;
    private MockMvc mockMvc;
    private Response response = new Response();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stationController).build();
    }

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/welcome"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to [ Station Service ] !"));
    }

    @Test
    public void testQuery() throws Exception {
        Mockito.when(stationService.query(Mockito.any(HttpHeaders.class))).thenReturn(Collections.emptyList());
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals("{\"status\":1,\"msg\":\"Find all content\",\"data\":[]}", result);
    }

    @Test
    public void testCreate() throws Exception {
        StationCreateDTO station = new StationCreateDTO("name", 1);
        Mockito.when(stationService.create(Mockito.any(StationCreateDTO.class), Mockito.any(HttpHeaders.class))).thenReturn(new StationDTO("id", "name"));
        String requestJson = JSONObject.toJSONString(station);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals("{\"status\":1,\"msg\":\"Create success\",\"data\":{\"id\":\"id\",\"name\":\"name\"}}", result);
    }

    @Test
    public void testUpdate() throws Exception {
        StationUpdateDTO station = new StationUpdateDTO("id", "name", 1);
        Mockito.when(stationService.update(Mockito.any(StationUpdateDTO.class), Mockito.any(HttpHeaders.class))).thenReturn(new StationDTO("", station.getName()));
        String requestJson = JSONObject.toJSONString(station);
        String result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/stationservice/stations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/stationservice/stations/id").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testQueryForStationId() throws Exception {
        Mockito.when(stationService.queryForId(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn("id");
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/id/station_name"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals("{\"status\":1,\"msg\":\"Success\",\"data\":\"id\"}", result);
    }

    @Test
    public void testQueryForIdBatch() throws Exception {
        List<String> stationNameList = new ArrayList<>();
        Mockito.when(stationService.queryForIdBatch(Mockito.anyList(), Mockito.any(HttpHeaders.class))).thenReturn(response);
        String requestJson = JSONObject.toJSONString(stationNameList);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations/idlist").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
    }

    @Test
    public void testQueryById() throws Exception {
        Mockito.when(stationService.queryById(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn("name");
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/name/station_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals("{\"status\":1,\"msg\":\"Success\",\"data\":\"name\"}", result);
    }

    @Test
    public void testQueryForNameBatch() throws Exception {
        List<String> stationIdList = new ArrayList<>();
        Mockito.when(stationService.queryByIdBatch(Mockito.anyList(), Mockito.any(HttpHeaders.class))).thenReturn(Collections.emptyList());
        String requestJson = JSONObject.toJSONString(stationIdList);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations/namelist").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals("{\"status\":0,\"msg\":\"No stationNamelist according to stationIdList\",\"data\":[]}", result);
    }

}
