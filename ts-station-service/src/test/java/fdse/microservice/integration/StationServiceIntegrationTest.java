package fdse.microservice.integration;

import com.alibaba.fastjson.JSONObject;
import fdse.microservice.StationApplication;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.entity.Station;
import fdse.microservice.repository.StationRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StationApplication.class, fdse.microservice.config.StationTestConfiguration.class})
@AutoConfigureWebMvc
@org.springframework.test.context.ActiveProfiles("test")
public class StationServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StationRepository stationRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Clean up database before each test
        stationRepository.deleteAll();
    }

    @After
    public void tearDown() {
        // Clean up database after each test
        stationRepository.deleteAll();
    }

    @Test
    public void testQueryStationsExcludesInternalFields() throws Exception {
        // Setup: Create a station with internal field stayTime
        Station station = new Station("test-station", 30);
        stationRepository.save(station);

        // Execute: Query all stations via HTTP API
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Find all content", response.getString("msg"));

        // Verify: Data contains only public fields
        JSONObject data = response.getJSONArray("data").getJSONObject(0);
        Assert.assertTrue("Response should contain 'id' field", data.containsKey("id"));
        Assert.assertTrue("Response should contain 'name' field", data.containsKey("name"));
        Assert.assertEquals("test-station", data.getString("name"));

        // Critical: Verify internal field 'stayTime' is NOT exposed
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field", 
                          data.containsKey("stayTime"));
        
        // Verify only expected fields are present (no extra fields leaked)
        Assert.assertTrue("Response should contain exactly 2 fields (id, name)",
                          data.keySet().size() == 2);
    }

    @Test
    public void testCreateStationExcludesInternalFields() throws Exception {
        // Setup: Create station request with internal field
        StationCreateDTO createDTO = new StationCreateDTO("new-station", 45);
        String requestJson = JSONObject.toJSONString(createDTO);

        // Execute: Create station via HTTP API
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Create success", response.getString("msg"));

        // Verify: Data contains only public fields
        JSONObject data = response.getJSONObject("data");
        Assert.assertTrue("Response should contain 'id' field", data.containsKey("id"));
        Assert.assertTrue("Response should contain 'name' field", data.containsKey("name"));
        Assert.assertEquals("new-station", data.getString("name"));

        // Critical: Verify internal field 'stayTime' is NOT exposed
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field", 
                          data.containsKey("stayTime"));
        
        // Verify only expected fields are present
        Assert.assertTrue("Response should contain exactly 2 fields (id, name)",
                          data.keySet().size() == 2);

        // Verify: Station was actually saved with internal field in database
        // Using findAll() and filtering since findByName might not be available
        Station savedStation = stationRepository.findAll().stream()
                .filter(s -> "new-station".equals(s.getName()))
                .findFirst()
                .orElse(null);
        Assert.assertNotNull("Station should be saved in database", savedStation);
        Assert.assertTrue("Internal field should be saved in database",
                          savedStation.getStayTime() == 45);
    }

    @Test
    public void testUpdateStationExcludesInternalFields() throws Exception {
        // Setup: Create initial station
        Station station = new Station("original-station", 30);
        Station savedStation = stationRepository.save(station);

        // Setup: Update request with internal field
        JSONObject updateRequest = new JSONObject();
        updateRequest.put("id", savedStation.getId());
        updateRequest.put("name", "updated-station");
        updateRequest.put("stayTime", 60);

        // Execute: Update station via HTTP API
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/stationservice/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response contains only public fields
        Assert.assertTrue("Response should contain 'id' field", response.containsKey("id"));
        Assert.assertTrue("Response should contain 'name' field", response.containsKey("name"));
        Assert.assertEquals("updated-station", response.getString("name"));

        // Critical: Verify internal field 'stayTime' is NOT exposed
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field", 
                          response.containsKey("stayTime"));
        
        // Verify only expected fields are present
        Assert.assertTrue("Response should contain exactly 2 fields (id, name)",
                          response.keySet().size() == 2);

        // Verify: Station was actually updated with internal field in database
        Station updatedStation = stationRepository.findById(savedStation.getId()).orElse(null);
        Assert.assertNotNull("Station should exist in database", updatedStation);
        Assert.assertEquals("Name should be updated", "updated-station", updatedStation.getName());
        Assert.assertTrue("Internal field should be updated in database",
                          updatedStation.getStayTime() == 60);
    }

    @Test
    public void testQueryForStationIdDoesNotExposeInternalFields() throws Exception {
        // Setup: Create station with internal field
        Station station = new Station("search-station", 25);
        Station savedStation = stationRepository.save(station);

        // Execute: Query station ID by name
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/id/search-station"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Success", response.getString("msg"));

        // Verify: Only station ID is returned (no internal fields)
        String stationId = response.getString("data");
        Assert.assertNotNull("Station ID should be returned", stationId);
        Assert.assertEquals("Station ID should match", savedStation.getId(), stationId);

        // Verify: Response doesn't contain any internal fields
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field",
                          response.containsKey("stayTime"));
    }

    @Test
    public void testQueryByIdDoesNotExposeInternalFields() throws Exception {
        // Setup: Create station with internal field
        Station station = new Station("lookup-station", 35);
        Station savedStation = stationRepository.save(station);

        // Execute: Query station name by ID
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/name/" + savedStation.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Success", response.getString("msg"));

        // Verify: Only station name is returned (no internal fields)
        String stationName = response.getString("data");
        Assert.assertNotNull("Station name should be returned", stationName);
        Assert.assertEquals("Station name should match", "lookup-station", stationName);

        // Verify: Response doesn't contain any internal fields
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field",
                          response.containsKey("stayTime"));
    }

    @Test
    public void testQueryStationIdBatchDoesNotExposeInternalFields() throws Exception {
        // Setup: Create stations with internal fields
        Station station1 = new Station("batch-station-1", 20);
        Station station2 = new Station("batch-station-2", 25);
        stationRepository.save(station1);
        stationRepository.save(station2);

        // Setup: Request body with station names
        JSONObject requestBody = new JSONObject();
        requestBody.put("0", "batch-station-1");
        requestBody.put("1", "batch-station-2");

        // Execute: Query station IDs by names batch
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations/idlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"batch-station-1\", \"batch-station-2\"]"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Success", response.getString("msg"));

        // Verify: Response contains only station IDs mapping (no internal fields)
        JSONObject data = response.getJSONObject("data");
        Assert.assertNotNull("Data should contain station mappings", data);
        
        // Verify: Response doesn't contain any internal fields
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field",
                          response.containsKey("stayTime"));
        Assert.assertFalse("Data should NOT contain internal 'stayTime' field",
                          data.containsKey("stayTime"));
    }

    @Test
    public void testQueryStationNameBatchDoesNotExposeInternalFields() throws Exception {
        // Setup: Create stations with internal fields
        Station station1 = new Station("name-batch-1", 15);
        Station station2 = new Station("name-batch-2", 18);
        Station savedStation1 = stationRepository.save(station1);
        Station savedStation2 = stationRepository.save(station2);

        // Setup: Request body with station IDs
        String requestJson = "[\"" + savedStation1.getId() + "\", \"" + savedStation2.getId() + "\"]";

        // Execute: Query station names by IDs batch
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations/namelist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject response = JSONObject.parseObject(responseBody);

        // Verify: Response structure
        Assert.assertTrue("Status should be 1", response.getInteger("status") == 1);
        Assert.assertEquals("Success", response.getString("msg"));

        // Verify: Response contains only station names array (no internal fields)
        Assert.assertTrue("Data should be an array", response.get("data") instanceof com.alibaba.fastjson.JSONArray);
        
        // Verify: Response doesn't contain any internal fields
        Assert.assertFalse("Response should NOT contain internal 'stayTime' field",
                          response.containsKey("stayTime"));
    }

    @Test
    public void testAllPublicApiEndpointsExcludeInternalFields() throws Exception {
        // Comprehensive test to verify ALL public API endpoints exclude internal fields
        Station testStation = new Station("comprehensive-test", 99);
        Station savedStation = stationRepository.save(testStation);

        // Test 1: GET /stations (query all)
        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONObject response1 = JSONObject.parseObject(result1.getResponse().getContentAsString());
        JSONObject stationData = response1.getJSONArray("data").getJSONObject(0);
        Assert.assertFalse("GET /stations should NOT expose stayTime", stationData.containsKey("stayTime"));

        // Test 2: GET /stations/id/{name} (get ID by name)
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/id/comprehensive-test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONObject response2 = JSONObject.parseObject(result2.getResponse().getContentAsString());
        Assert.assertFalse("GET /stations/id/{name} should NOT expose stayTime", response2.containsKey("stayTime"));

        // Test 3: GET /stations/name/{id} (get name by ID)
        MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stationservice/stations/name/" + savedStation.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONObject response3 = JSONObject.parseObject(result3.getResponse().getContentAsString());
        Assert.assertFalse("GET /stations/name/{id} should NOT expose stayTime", response3.containsKey("stayTime"));

        // Test 4: POST /stations (create)
        StationCreateDTO createDTO = new StationCreateDTO("api-test-station", 77);
        String createJson = JSONObject.toJSONString(createDTO);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stationservice/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        JSONObject response4 = JSONObject.parseObject(result4.getResponse().getContentAsString());
        JSONObject createData = response4.getJSONObject("data");
        Assert.assertFalse("POST /stations should NOT expose stayTime", createData.containsKey("stayTime"));

        // Test 5: PUT /stations (update)
        JSONObject updateRequest = new JSONObject();
        updateRequest.put("id", savedStation.getId());
        updateRequest.put("name", "updated-comprehensive");
        updateRequest.put("stayTime", 88);
        MvcResult result5 = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/stationservice/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONObject response5 = JSONObject.parseObject(result5.getResponse().getContentAsString());
        Assert.assertFalse("PUT /stations should NOT expose stayTime", response5.containsKey("stayTime"));

        // Verify: All responses contain only expected public fields
        Assert.assertTrue("All API responses should contain only public fields",
                          stationData.keySet().size() == 2 &&
                          createData.keySet().size() == 2 &&
                          response5.keySet().size() == 2);
    }
}