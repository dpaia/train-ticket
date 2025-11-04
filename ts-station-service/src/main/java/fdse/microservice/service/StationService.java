package fdse.microservice.service;

import edu.fudan.common.util.Response;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.controller.dto.StationDTO;
import fdse.microservice.controller.dto.StationUpdateDTO;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface StationService {
    //CRUD
    StationDTO create(StationCreateDTO info, HttpHeaders headers);

    boolean exist(String stationName, HttpHeaders headers);

    StationDTO update(StationUpdateDTO info, HttpHeaders headers);

    void delete(String stationsId, HttpHeaders headers);

    List<StationDTO> query(HttpHeaders headers);

    String queryForId(String stationName, HttpHeaders headers);

    Response queryForIdBatch(List<String> nameList, HttpHeaders headers);

    String queryById(String stationId, HttpHeaders headers);

    List<String> queryByIdBatch(List<String> stationIdList, HttpHeaders headers);

}
