package fdse.microservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StationCreateDTO {
    @NonNull
    private final String name;
    @NonNull
    private int stayTime;
}

