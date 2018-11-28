package com.netcracker.routebuilder.util.implementation;

import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import com.netcracker.datacollector.service.WeatherMapService;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.netcracker.routebuilder.util.implementation.Utils.initField;
import static com.netcracker.routebuilder.util.implementation.Utils.recountWithNewScale;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherMap extends AbstractPotentialMap {

    private final WeatherMapService weatherMapService;
    private final AlgorithmParameters params;
    private final static int WEATHER_MAP_SCALE = 1;

    @PostConstruct
    public void init() {
        field = initField(params.getScale());
        updateMap();
    }

    @Scheduled(initialDelay = 60_000, fixedRate = 60_000)
    private void update() {
        updateMap();
    }


    private void updateMap() {
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

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = map[(int) Math.round((double) i / (double) params.getScale())]
                        [(int) Math.round((double) j / (double) params.getScale())];
            }
        }
    }

}
