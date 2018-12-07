package com.netcracker.datacollector.placecollector.scheduler;

import com.netcracker.datacollector.placecollector.service.PlaceGrabber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Class for automatic information gathering of places.
 *
 * @author Ali
 */

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ScheduledPlaceCollector {

    private final int MILLIS_PER_MINUTE = 60000;

    private final PlaceGrabber placeGrabber;

    @Scheduled(fixedDelay = MILLIS_PER_MINUTE)
    public void getPlacesFromGoogleMaps() {
        placeGrabber.collect();
        log.info("Place grabbed successfully. ", LocalDateTime.now());
    }

}
