package com.netcracker.datacollector.distancecollector;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for collecting distances
 *
 * @author prokhorovartem
 */
@RequiredArgsConstructor
@Component
public class ScheduledDistanceCollector {

    /**
     * Instance of DistanceCollector, which collects distances from Google API
     */
    private final DistanceCollector distanceCollector;

    /**
     * Variant we should use to calculate distances
     */
    private VariantsToCalculateDistances variantToCalculateDistances = VariantsToCalculateDistances.INACCURATE;

    /**
     * Scheduler, which updates distances every 24hr
     */
    @Scheduled(fixedDelay = 86400000)
    public void saveDistances() {
        if (variantToCalculateDistances == VariantsToCalculateDistances.EXPENSIVE)
            distanceCollector.saveDistancesViaExpensiveVariant();
        else distanceCollector.saveDistancesWithLinks();
    }
}
