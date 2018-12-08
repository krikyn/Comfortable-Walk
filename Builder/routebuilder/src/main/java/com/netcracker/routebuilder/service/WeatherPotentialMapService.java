package com.netcracker.routebuilder.service;

import com.netcracker.commons.data.model.WeatherPotentialMap;
import com.netcracker.commons.service.WeatherMapService;
import com.netcracker.routebuilder.data.map.impl.WeatherMap;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Class to provide access to the current weather potential map
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherPotentialMapService {

    private WeatherMap weatherMap;
    private final WeatherMapService weatherMapService;
    private final AlgorithmParameters params;

    /**
     * Weather potential map initialization
     */
    @PostConstruct
    public void init() {
        weatherMap = new WeatherMap();
        weatherMap.setField(initField(params.getScale()));
        updateMap();
    }

    /**
     * Method for getting weather potential map
     *
     * @return weather potential map
     */
    public int[][] getField() {
        return weatherMap.getField();
    }

    /**
     * Method for recording the latest version of the weather potential map from the database
     */
    public void updateMap() {
        WeatherPotentialMap potentialMap = weatherMapService.getMap();

        if (potentialMap == null) {
            log.error("WeatherPotentialMap doesn't exist in DB");
            return;
        }

        int[][] map = potentialMap.getPotentialField(); //scale всегда 1

        if (map == null) {
            log.error("Null potential field in the WeatherPotentialMap");
            return;
        }

        for (int i = 0; i < weatherMap.getField().length; i++) {
            for (int j = 0; j < weatherMap.getField()[i].length; j++) {
                weatherMap.getField()[i][j] = map[(int) Math.round((double) i / (double) params.getScale())]
                        [(int) Math.round((double) j / (double) params.getScale())];
            }
        }
    }
}
