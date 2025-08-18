package route.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRouteMapperConfiguration {
    
    /**
     * Provides RouteMapper instance as a Spring bean
     * @return RouteMapper instance
     */
    @Bean
    public RouteMapper routeMapper() {
        return Mappers.getMapper(RouteMapper.class);
    }
}
