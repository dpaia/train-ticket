package fdse.microservice.service;

import edu.fudan.common.util.Response;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.controller.dto.StationDTO;
import fdse.microservice.controller.dto.StationUpdateDTO;
import fdse.microservice.entity.Station;
import fdse.microservice.repository.StationRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(JUnit4.class)
public class StationServiceImplTest {

    @InjectMocks
    private StationServiceImpl stationServiceImpl;

    @Mock
    private StationRepository repository;

    private HttpHeaders headers = new HttpHeaders();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate1() {
        StationCreateDTO station = new StationCreateDTO("name", 1);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Station created = new Station("name", 1);
        created.setId("id");
        Mockito.when(repository.save(Mockito.any(Station.class))).thenReturn(created);
        StationDTO result = stationServiceImpl.create(station, headers);
        Assert.assertNotNull(result.getId());
        Assert.assertEquals(station.getName(), result.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate2() {
        Station station = new Station();
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(station));
        StationCreateDTO stationCreateDTO = new StationCreateDTO(station.getName(), 1);

        stationServiceImpl.create(stationCreateDTO, headers);
    }

    @Test
    public void testExist1() {
        Station station = new Station();
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(station);
        Assert.assertTrue(stationServiceImpl.exist("station_name", headers));
    }

    @Test
    public void testExist2() {
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(null);
        Assert.assertFalse(stationServiceImpl.exist("station_name", headers));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate1() {
        StationUpdateDTO info = new StationUpdateDTO("id", "name", 1);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        stationServiceImpl.update(info, headers);
    }

    @Test
    public void testUpdate2() {
        StationUpdateDTO info = new StationUpdateDTO("id", "name", 1);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(new Station("old name", info.getStayTime())));
        Mockito.when(repository.save(Mockito.any(Station.class))).thenReturn(new Station("name", info.getStayTime()));
        StationDTO result = stationServiceImpl.update(info, headers);
        Assert.assertEquals("name", result.getName());
    }

    @Test
    public void testDelete1() {
        String id = "id";
        Station info = new Station();
        info.setId(id);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(info));
        Mockito.doNothing().doThrow(new RuntimeException()).when(repository).delete(Mockito.any(Station.class));
        stationServiceImpl.delete(id, headers);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelete2() {
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        stationServiceImpl.delete("any id", headers);
    }

    @Test
    public void testQuery1() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station());
        Mockito.when(repository.findAll()).thenReturn(stations);
        List<StationDTO> result = stationServiceImpl.query(headers);
        Assert.assertEquals(stations.size(), result.size());
    }

    @Test
    public void testQuery2() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());
        List<StationDTO> result = stationServiceImpl.query(headers);
        Assert.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testQueryForId1() {
        Station station = new Station();
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(station);
        String result = stationServiceImpl.queryForId("station_name", headers);
        Assert.assertEquals(station.getId(), result);
    }

    @Test
    public void testQueryForId2() {
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(null);
        String result = stationServiceImpl.queryForId("station_name", headers);
        Assert.assertNull(result);
    }

    @Test
    public void testQueryForIdBatch1() {
        List<String> nameList = new ArrayList<>();
        Response result = stationServiceImpl.queryForIdBatch(nameList, headers);
        Assert.assertEquals(new Response<>(0, "No content according to name list", null), result);
    }

    @Test
    public void testQueryForIdBatch2() {
        List<String> nameList = new ArrayList<>();
        nameList.add("station_name");
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(null);
        Response result = stationServiceImpl.queryForIdBatch(nameList, headers);
        Assert.assertEquals("Success", result.getMsg());
    }

    @Test
    public void testQueryById1() {
        Station station = new Station();
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(station));
        String result = stationServiceImpl.queryById("station_id", headers);
        Assert.assertEquals("", result);
    }

    @Test
    public void testQueryById2() {
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        String result = stationServiceImpl.queryById("station_id", headers);
        Assert.assertNull(result);
    }

    @Test
    public void testQueryByIdBatch1() {
        List<String> idList = new ArrayList<>();
        List<String> result = stationServiceImpl.queryByIdBatch(idList, headers);
        Assert.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testQueryByIdBatch2() {
        Station station = new Station("name");
        List<String> idList = new ArrayList<>();
        idList.add("station_id");
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(station));
        List<String> result = stationServiceImpl.queryByIdBatch(idList, headers);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("name", result.get(0));
    }

}
