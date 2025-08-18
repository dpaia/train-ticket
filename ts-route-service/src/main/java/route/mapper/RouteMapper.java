package route.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import route.entity.Route;

import java.util.List;

@Mapper
public interface RouteMapper {
    
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);
    
    /**
     * Converts Route entity to RouteDTO
     * @param route Route entity
     * @return RouteDTO
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "stations", target = "stations")
    @Mapping(source = "distances", target = "distances")
    @Mapping(source = "startStation", target = "startStation")
    @Mapping(source = "endStation", target = "endStation")
    RouteDTO toDto(Route route);

    /**
     * Converts list of Route entities to list of RouteDTOs
     * @param routes List of Route entities
     * @return List of RouteDTOs
     */
    List<RouteDTO> toDtoList(List<Route> routes);

    /**
     * Converts RouteDTO to Route entity
     * @param routeDto RouteDTO
     * @return Route entity
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "stations", target = "stations")
    @Mapping(source = "distances", target = "distances")
    @Mapping(source = "startStation", target = "startStation")
    @Mapping(source = "endStation", target = "endStation")
    Route toEntity(RouteDTO routeDto);
}
