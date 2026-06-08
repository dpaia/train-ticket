package fdse.microservice.init;

import fdse.microservice.controller.dto.StationCreateDTO;
import fdse.microservice.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    StationService service;

    @Override
    public void run(String... args) {
        StationCreateDTO info = new StationCreateDTO("Shang Hai", 10);
        service.create(info,null);

        info = new StationCreateDTO("Shang Hai Hong Qiao", 10);
        service.create(info,null);

        info = new StationCreateDTO("Tai Yuan", 5);
        service.create(info,null);

        info = new StationCreateDTO("Bei Jing", 10);
        service.create(info,null);

        info = new StationCreateDTO("Nan Jing", 8);
        service.create(info,null);

        info = new StationCreateDTO("Shi Jia Zhuang", 8);
        service.create(info,null);

        info = new StationCreateDTO("Xu Zhou", 7);
        service.create(info,null);

        info = new StationCreateDTO("Ji Nan", 5);
        service.create(info,null);

        info = new StationCreateDTO("Hang Zhou", 9);
        service.create(info,null);

        info = new StationCreateDTO("Jia Xing Nan", 2);
        service.create(info,null);

        info = new StationCreateDTO("Zhen Jiang", 2);
        service.create(info,null);

        info = new StationCreateDTO("Wu Xi", 3);
        service.create(info,null);

        info = new StationCreateDTO("Su Zhou", 3);
        service.create(info,null);

    }
}
