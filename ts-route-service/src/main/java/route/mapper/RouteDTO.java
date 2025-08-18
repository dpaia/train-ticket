package route.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {
    private String id;
    
    @NotNull(message = "Stations list cannot be null")
    @NotEmpty(message = "Stations list cannot be empty")
    private List<String> stations;
    
    @NotNull(message = "Distances list cannot be null")
    @NotEmpty(message = "Distances list cannot be empty")
    private List<Integer> distances;
    
    @NotNull(message = "Start station cannot be null")
    @NotEmpty(message = "Start station cannot be empty")
    private String startStation;
    
    @NotNull(message = "End station cannot be null")
    @NotEmpty(message = "End station cannot be empty")
    private String endStation;
}
