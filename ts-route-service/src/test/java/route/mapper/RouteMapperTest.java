package route.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import route.entity.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestRouteMapperConfiguration.class})
public class RouteMapperTest {

    @Autowired
    private RouteMapper routeMapper;

    @Test
    public void testToDtoWithValidRoute() {
        List<String> stations = Arrays.asList("Station1", "Station2", "Station3");
        List<Integer> distances = Arrays.asList(10, 20, 30);
        Route route = new Route("test-id", stations, distances, "Station1", "Station3");

        RouteDTO result = routeMapper.toDto(route);

        Assert.assertNotNull("RouteDTO should not be null", result);
        Assert.assertEquals("ID should match", "test-id", result.getId());
        Assert.assertEquals("Stations should match", stations, result.getStations());
        Assert.assertEquals("Distances should match", distances, result.getDistances());
        Assert.assertEquals("Start station should match", "Station1", result.getStartStation());
        Assert.assertEquals("End station should match", "Station3", result.getEndStation());
    }

    /**
     * Test toDto method with null Route
     */
    @Test
    public void testToDtoWithNullRoute() {
        RouteDTO result = routeMapper.toDto(null);
        Assert.assertNull("RouteDTO should be null when input is null", result);
    }

    /**
     * Test toDtoList method with valid routes
     */
    @Test
    public void testToDtoListWithValidRoutes() {
        List<String> stations1 = Arrays.asList("A", "B");
        List<Integer> distances1 = Arrays.asList(5, 10);
        Route route1 = new Route("id1", stations1, distances1, "A", "B");

        List<String> stations2 = Arrays.asList("C", "D");
        List<Integer> distances2 = Arrays.asList(15, 20);
        Route route2 = new Route("id2", stations2, distances2, "C", "D");

        List<Route> routes = Arrays.asList(route1, route2);

        List<RouteDTO> result = routeMapper.toDtoList(routes);

        Assert.assertNotNull("RouteDTO list should not be null", result);
        Assert.assertEquals("List size should match", 2, result.size());
        
        RouteDTO dto1 = result.get(0);
        Assert.assertEquals("First DTO ID should match", "id1", dto1.getId());
        Assert.assertEquals("First DTO stations should match", stations1, dto1.getStations());
        Assert.assertEquals("First DTO distances should match", distances1, dto1.getDistances());
        
        RouteDTO dto2 = result.get(1);
        Assert.assertEquals("Second DTO ID should match", "id2", dto2.getId());
        Assert.assertEquals("Second DTO stations should match", stations2, dto2.getStations());
        Assert.assertEquals("Second DTO distances should match", distances2, dto2.getDistances());
    }

    /**
     * Test toDtoList method with null list
     */
    @Test
    public void testToDtoListWithNullList() {
        List<RouteDTO> result = routeMapper.toDtoList(null);
        Assert.assertNull("RouteDTO list should be null when input is null", result);
    }

    /**
     * Test toDtoList method with empty list
     */
    @Test
    public void testToDtoListWithEmptyList() {
        List<Route> routes = new ArrayList<>();
        List<RouteDTO> result = routeMapper.toDtoList(routes);
        
        Assert.assertNotNull("RouteDTO list should not be null", result);
        Assert.assertTrue("RouteDTO list should be empty", result.isEmpty());
    }

    /**
     * Test toEntity method with valid DTO
     */
    @Test
    public void testToEntityWithValidDto() {
        List<String> stations = Arrays.asList("Station1", "Station2", "Station3");
        List<Integer> distances = Arrays.asList(10, 20, 30);
        RouteDTO routeDTO = new RouteDTO("test-id", stations, distances, "Station1", "Station3");

        Route result = routeMapper.toEntity(routeDTO);

        Assert.assertNotNull("Route should not be null", result);
        Assert.assertEquals("ID should match", "test-id", result.getId());
        Assert.assertEquals("Stations should match", stations, result.getStations());
        Assert.assertEquals("Distances should match", distances, result.getDistances());
        Assert.assertEquals("Start station should match", "Station1", result.getStartStation());
        Assert.assertEquals("End station should match", "Station3", result.getEndStation());
    }

    /**
     * Test toEntity method with null DTO
     */
    @Test
    public void testToEntityWithNullDto() {
        Route result = routeMapper.toEntity(null);
        Assert.assertNull("Route should be null when input is null", result);
    }

    /**
     * Test round-trip conversion to ensure data integrity
     */
    @Test
    public void testRoundTripConversion() {
        List<String> stations = Arrays.asList("X", "Y", "Z");
        List<Integer> distances = Arrays.asList(100, 200, 300);
        Route originalRoute = new Route("round-trip-id", stations, distances, "X", "Z");

        RouteDTO dto = routeMapper.toDto(originalRoute);
        Route convertedRoute = routeMapper.toEntity(dto);

        Assert.assertEquals("ID should be preserved", originalRoute.getId(), convertedRoute.getId());
        Assert.assertEquals("Stations should be preserved", originalRoute.getStations(), convertedRoute.getStations());
        Assert.assertEquals("Distances should be preserved", originalRoute.getDistances(),
                convertedRoute.getDistances());
        Assert.assertEquals("Start station should be preserved", originalRoute.getStartStation(),
                convertedRoute.getStartStation());
        Assert.assertEquals("End station should be preserved", originalRoute.getEndStation(),
                convertedRoute.getEndStation());
    }
}
