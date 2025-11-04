package fdse.microservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StationUpdateDTO {
    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private int stayTime;
}
