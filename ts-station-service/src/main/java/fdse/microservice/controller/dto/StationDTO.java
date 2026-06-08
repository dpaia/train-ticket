package fdse.microservice.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class StationDTO {
    private final String id;
    @NonNull
    private final String name;
}
