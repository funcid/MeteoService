package ru.func.weathersender.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.func.weathersender.entity.Sensor;
import ru.func.weathersender.util.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author func 04.01.2020
 */
@Slf4j
@Controller
public class FindSensorController extends DatableController {

    @RequestMapping(path = "/findSensorById", method = POST)
    public ModelAndView findSensorById(@RequestParam Integer id) {
        ModelAndView modelAndView = new ModelAndView("findSensorById");
        modelAndView.addObject("id", id);

        Optional<Sensor> sensorObject = sensorRepository.findById(id);
        sensorObject.ifPresent(sensor -> modelAndView.addObject("sensor", sensor));

        log.info(
                "Попытка найти записи c id {}, статус: {}.",
                id,
                sensorObject.isPresent() ? "запись найдена" : "запись не найдена"
        );
        return modelAndView;
    }

    @RequestMapping(path = "/findSensorsByLocation", method = POST)
    public ModelAndView findSensorsByLocation(@RequestParam String location) {
        ModelAndView modelAndView = new ModelAndView("findSensorsByLocation");

        modelAndView.addObject("location", location);

        List<Sensor> reversedList = new ArrayList<>();

        if (Location.containsName(location)) {
            Location locationObject = Location.valueOf(location.toUpperCase());
            reversedList = sensorRepository.findByLocation(locationObject.getCords());
            Collections.reverse(reversedList);
        }

        modelAndView.addObject("sensors", reversedList);

        log.info(
                "Попытка найти записи из {}, статус: {}.",
                location,
                reversedList.size() == 0 ? "записи не найдены" : "записи найдены"
        );
        return modelAndView;
    }

    @RequestMapping(path = "/findSensorsByTimestamp", method = POST)
    public ModelAndView findSensorsByTimestamp(@RequestParam String timestamp) {
        ModelAndView modelAndView = new ModelAndView("findSensorsByTimestamp");
        modelAndView.addObject("timestamp", timestamp);

        List<Sensor> list = sensorRepository.findByTimestamp(timestamp);

        modelAndView.addObject("sensors", list);

        log.info(
                "Попытка найти записи полученные в {}, статус: {}.",
                timestamp,
                list.size() == 0 ? "записи не найдены" : "записи найдены"
        );
        return modelAndView;
    }
}
