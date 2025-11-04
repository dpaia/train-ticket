package fdse.microservice.controller;

import edu.fudan.common.util.Response;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.controller.dto.StationDTO;
import fdse.microservice.controller.dto.StationUpdateDTO;
import fdse.microservice.entity.*;
import fdse.microservice.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/stationservice")
public class StationController {

    @Autowired
    private StationService stationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(StationController.class);

    @GetMapping(path = "/welcome")
    public String home(@RequestHeader HttpHeaders headers) {
        return "Welcome to [ Station Service ] !";
    }

    @GetMapping(value = "/stations")
    public HttpEntity query(@RequestHeader HttpHeaders headers) {
        Response response = new Response<>(1, "Find all content", stationService.query(headers));
        return ok(response);
    }

    @PostMapping(value = "/stations")
    public ResponseEntity<Response> create(@RequestBody StationCreateDTO station, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[create][Create station][name: {}]", station.getName());
        Response<StationDTO> response = new Response<>(1, "Create success", stationService.create(station, headers));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/stations")
    public HttpEntity update(@RequestBody StationUpdateDTO station, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[update][Update station][StationId: {}]", station.getId());
        return ok(stationService.update(station, headers));
    }

    @DeleteMapping(value = "/stations/{stationsId}")
    public ResponseEntity<Response> delete(@PathVariable String stationsId, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[delete][Delete station][StationId: {}]", stationsId);
        stationService.delete(stationsId, headers);
        return ok().build();
    }

    // according to station name ---> query station id
    @GetMapping(value = "/stations/id/{stationNameForId}")
    public HttpEntity queryForStationId(@PathVariable(value = "stationNameForId")
                                        String stationName, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[queryForId][Query for station id][StationName: {}]", stationName);
        String id = stationService.queryForId(stationName, headers);
        Response response;
        if(id != null)
            response = new Response<>(1, "Success", id);
        else
            response = new Response<>(0, "Not exists", stationName);
        return ok(response);
    }

    // according to station name list --->  query all station ids
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/stations/idlist")
    public HttpEntity queryForIdBatch(@RequestBody List<String> stationNameList, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[queryForIdBatch][Query stations for id batch][StationNameNumbers: {}]", stationNameList.size());
        return ok(stationService.queryForIdBatch(stationNameList, headers));
    }

    // according to station id ---> query station name
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/stations/name/{stationIdForName}")
    public HttpEntity queryById(@PathVariable(value = "stationIdForName")
                                String stationId, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[queryById][Query stations By Id][Id: {}]", stationId);
        String stationName = stationService.queryById(stationId, headers);
        Response response;
        if (stationName != null)
            response = new Response<>(1, "Success", stationName);
        else
            response = new Response<>(0, "Not exists", null);
        return ok(response);
    }

    // according to station id list  ---> query all station names
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/stations/namelist")
    public HttpEntity queryForNameBatch(@RequestBody List<String> stationIdList, @RequestHeader HttpHeaders headers) {
        StationController.LOGGER.info("[queryByIdBatch][Query stations for name batch][StationIdNumbers: {}]", stationIdList.size());
        List<String> result = stationService.queryByIdBatch(stationIdList, headers);
        Response response;
        if (!result.isEmpty())
            response = new Response<>(1, "Success", result);
        else
            response = new Response<>(0, "No stationNamelist according to stationIdList", result);
        return ok(response);
    }

}
