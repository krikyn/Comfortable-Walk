package com.netcracker.routebuilder.scheduler;

import com.netcracker.routebuilder.service.PlacesMapService;
import com.netcracker.routebuilder.service.WeatherPotentialMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for periodic synchronization of local potential maps with maps in the database
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPotentialMaps {

    private final WeatherPotentialMapService weatherPotentialMapService;
    private final PlacesMapService placesMapService;

    @Scheduled(initialDelay = 60_000, fixedRate = 60_000)
    private void updateWeatherPotentialMap() {
        log.info("Start updating weather potential map");
        weatherPotentialMapService.updateMap();
        log.info("Weather potential map updated");
    }

    @Scheduled(cron = "0 0 1 * * MON")
    private void updatePlacesPotentialMap() {
        log.info("Start updating places potential map");
        placesMapService.updateMaps();
        log.info("Places potential map updated");
    }
}
