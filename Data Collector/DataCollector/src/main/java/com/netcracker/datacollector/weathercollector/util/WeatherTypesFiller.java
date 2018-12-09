package com.netcracker.datacollector.weathercollector.util;

import java.awt.*;
import java.util.Map;

/**
 * Class for adding all possible colors from the radar map to the map
 *
 * @author Kirill.Vakhrushev
 */
public class WeatherTypesFiller {

    /**
     * Method for adding all possible colors from the radar map to the map
     *
     * @param weatherTypes map to which values will be added
     */
    public void fillWeatherTypes(Map<Integer, Integer> weatherTypes) {
        weatherTypes.put(new Color(208, 208, 208).getRGB(), 0);
        weatherTypes.put(new Color(255, 255, 255).getRGB(), 1);
        weatherTypes.put(new Color(192, 255, 192).getRGB(), 2);
        weatherTypes.put(new Color(0, 255, 0).getRGB(), 3);
        weatherTypes.put(new Color(0, 191, 0).getRGB(), 4);
        weatherTypes.put(new Color(0, 127, 0).getRGB(), 5);
        weatherTypes.put(new Color(0, 255, 255).getRGB(), 6);
        weatherTypes.put(new Color(0, 150, 255).getRGB(), 7);
        weatherTypes.put(new Color(0, 0, 255).getRGB(), 8);
        weatherTypes.put(new Color(0, 0, 150).getRGB(), 9);
        weatherTypes.put(new Color(255, 192, 192).getRGB(), 10);
        weatherTypes.put(new Color(255, 0, 204).getRGB(), 11);
        weatherTypes.put(new Color(255, 0, 0).getRGB(), 12);
        weatherTypes.put(new Color(255, 255, 0).getRGB(), 13);
        weatherTypes.put(new Color(255, 180, 100).getRGB(), 14);
        weatherTypes.put(new Color(191, 66, 66).getRGB(), 15);
    }
}
