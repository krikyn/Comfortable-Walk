package com.netcracker.datacollector.util.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.netcracker.datacollector.util.WeatherCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledWeatherCollector {

    private final WeatherCollector weatherCollector;

    public ScheduledWeatherCollector(WeatherCollector weatherCollector){
        this.weatherCollector = weatherCollector;
    }

    private static final Logger log = LoggerFactory.getLogger(ScheduledWeatherCollector.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 60_000)
    public void collectWeather() {
        weatherCollector.run();
        log.info("radar data updated {}", dateFormat.format(new Date()));

    }
}

/*private final WeatherPotentialMapRepository weatherPotentialMapRepository;

    public Collector(WeatherPotentialMapRepository weatherPotentialMapRepository){
        this.weatherPotentialMapRepository = weatherPotentialMapRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(Collector.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 60_000)
    public void collectWeather() {
        WeatherMapService weatherMapService = new WeatherMapServiceImpl(weatherPotentialMapRepository);
        WeatherCollector weatherCollector = new WeatherCollector(weatherMapService);
        weatherCollector.run();
        log.info("radar data updated {}", dateFormat.format(new Date()));

    }*/
