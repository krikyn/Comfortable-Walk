package com.netcracker.datacollector.util.scheduler;

import com.netcracker.datacollector.util.WeatherCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for automatic getting updated data from weather radar
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledWeatherCollector {

    private final WeatherCollector weatherCollector;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //TODO сделать обработку с логами - не запускать без записи в таблице WeatherPotentialMap (insert into WeatherPotentialMap values(0, 1, null);)
    @Scheduled(fixedRate = 60_000)
    public void collectWeather() {
        weatherCollector.run();
        log.info("radar data updated {}", dateFormat.format(new Date()));
    }
}
