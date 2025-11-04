package fdse.microservice.service;

import edu.fudan.common.util.Response;
import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.controller.dto.StationDTO;
import fdse.microservice.controller.dto.StationUpdateDTO;
import fdse.microservice.entity.*;
import fdse.microservice.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationRepository repository;

    String success = "Success";

    private static final Logger LOGGER = LoggerFactory.getLogger(StationServiceImpl.class);

    @Override
    public StationDTO create(StationCreateDTO createDTO, HttpHeaders headers) {
        if (createDTO.getName().isEmpty()) {
            StationServiceImpl.LOGGER.error("[create][Create station error][Name not specify]");
            throw new IllegalArgumentException("Name not specify");
        }
        if (repository.findByName(createDTO.getName()) == null) {
            Station station = new Station(createDTO.getName(), createDTO.getStayTime());
            Station created = repository.save(station);
            return new StationDTO(created.getId(), created.getName());
        }
        StationServiceImpl.LOGGER.error("[create][Create station error][Already exists][StationId: {}]", createDTO.getName());
        throw new IllegalArgumentException("Already exists");
    }


    @Override
    public boolean exist(String stationName, HttpHeaders headers) {
        boolean result = repository.findByName(stationName) != null;
        return result;
    }

    @Override
    public StationDTO update(StationUpdateDTO info, HttpHeaders headers) {

        Optional<Station> op = repository.findById(info.getId());
        if (!op.isPresent()) {
            StationServiceImpl.LOGGER.error("[update][Update station error][Station not found][StationId: {}]", info.getId());
            throw new IllegalArgumentException("Station not found");
        } else {
            Station station = op.get();
            station.setName(info.getName());
            station.setStayTime(info.getStayTime());
            Station updated = repository.save(station);
            return new StationDTO(updated.getId(), updated.getName());
        }
    }

    @Override
    public void delete(String stationsId, HttpHeaders headers) {
        Optional<Station> op = repository.findById(stationsId);
        if (op.isPresent()) {
            repository.delete(op.get());
        } else {
            StationServiceImpl.LOGGER.error("[delete][Delete station error][Station not found][StationId: {}]", stationsId);
            throw new IllegalArgumentException("Station not found");
        }
    }

    @Override
    public List<StationDTO> query(HttpHeaders headers) {
        List<Station> stations = repository.findAll();
        if (!stations.isEmpty()) {
            return stations.stream().map(this::convertStation).collect(Collectors.toList());
        } else {
            StationServiceImpl.LOGGER.warn("[query][Query stations warn][Find all stations: {}]", "No content");
            return Collections.emptyList();
        }
    }

    @Override
    public String queryForId(String stationName, HttpHeaders headers) {
        Station station = repository.findByName(stationName);

        if (station != null) {
            return station.getId();
        } else {
            StationServiceImpl.LOGGER.warn("[queryForId][Find station id warn][Station not found][StationName: {}]", stationName);
            return null;
        }
    }


    @Override
    public Response queryForIdBatch(List<String> nameList, HttpHeaders headers) {
        Map<String, String> result = new HashMap<>();
        List<Station> stations = repository.findByNames(nameList);
        Map<String, String> stationMap = new HashMap<>();
        for (Station s : stations) {
            stationMap.put(s.getName(), s.getId());
        }

        for (String name : nameList) {
            result.put(name, stationMap.get(name));
        }

        if (!result.isEmpty()) {
            return new Response<>(1, success, result);
        } else {
            StationServiceImpl.LOGGER.warn("[queryForIdBatch][Find station ids warn][Stations not found]");
            return new Response<>(0, "No content according to name list", null);
        }

    }

    @Override
    public String queryById(String stationId, HttpHeaders headers) {
        Optional<Station> station = repository.findById(stationId);
        if (station.isPresent()) {
            return station.get().getName();
        } else {
            StationServiceImpl.LOGGER.error("[queryById][Find station name error][Station not found][StationId: {}]", stationId);
            return null;
        }
    }

    @Override
    public List<String> queryByIdBatch(List<String> idList, HttpHeaders headers) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : idList) {
            Optional<Station> stationOld = repository.findById(s);
            stationOld.ifPresent(station -> result.add(station.getName()));
        }

        if (!result.isEmpty()) {
            return result;
        } else {
            StationServiceImpl.LOGGER.error("[queryByIdBatch][Find station names error][Stations not found][StationIdNumber: {}]", idList.size());
            return Collections.emptyList();
        }
    }

    private StationDTO convertStation(Station station){
        return new StationDTO(station.getId(), station.getName());
    }
}
