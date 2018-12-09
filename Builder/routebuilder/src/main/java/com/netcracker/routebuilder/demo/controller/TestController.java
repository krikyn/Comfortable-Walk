package com.netcracker.routebuilder.demo.controller;

import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.routebuilder.algorithm.PathFindingAlgorithm;
import com.netcracker.routebuilder.algorithm.PotentialMapBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.DistanceType;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.demo.visualizer.DrawMap;
import com.netcracker.routebuilder.service.RouteMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.netcracker.routebuilder.util.AlgorithmUtil.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Controller for testing path finding algorithm and potential map builder
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final PathFindingAlgorithm pathFindingAlgorithm;
    private final CityMapService cityMapService;
    private final PotentialMapBuilder potentialMapBuilder;
    private final RouteMapService routeMapService;


    @GetMapping("/distance-type-test")
    public @ResponseBody
    String distanceTypeTest(@RequestParam int iterationsNum) {

        //ArrayList<Integer> counter = new ArrayList<>();
        int[] counter = new int[DistanceType.values().length];

        for (int i = 0; i < iterationsNum; i++) {

            Double dist = Double.MAX_VALUE;
            GeoCoordinates from = new GeoCoordinates(0d, 0d);
            GeoCoordinates to = new GeoCoordinates(0d, 0d);

            while (dist > 2000) {
                from = getRandomCoordinates();
                to = getRandomCoordinates();
                dist = EuclideanDist(from, to);
            }

            for (DistanceType distanceType : DistanceType.values()) {

                int[][] mapWithRoute = initField(20);
                pathFindingAlgorithm.setDistanceType(distanceType);

                pathFindingAlgorithm.buildRoute(from, to, new ArrayList<>(), mapWithRoute);

                int pointsNum = calcPoints(mapWithRoute);
                counter[distanceType.ordinal()] += pointsNum;
            }
        }

        StringBuilder response = new StringBuilder();

        for (DistanceType distanceType : DistanceType.values()) {
            response.append(distanceType.toString()).append(": ").append(counter[distanceType.ordinal()]).append("\n");
        }

        return response.toString();
    }

    private GeoCoordinates getRandomCoordinates() {

        Random random = new Random();
        Double newY = random.nextDouble() * (60.02781 - 59.84801) + 59.84801;
        Double newX = random.nextDouble() * (30.52102 - 30.18035) + 30.18035;

        return new GeoCoordinates(newX, newY);
    }

    private static Double EuclideanDist(GeoCoordinates from, GeoCoordinates to) {
        final double LAT_1_KM = 0.00898;
        final double LON_1_KM = 0.01793;

        return Math.sqrt(Math.pow(((from.getX() - to.getX()) / LON_1_KM) * 1000, 2) +
                Math.pow(((from.getY() - to.getY()) / LAT_1_KM) * 1000, 2));
    }

    private int calcPoints(int[][] map) {
        int num = 0;
        for (int[] aMap : map) {
            for (int anAMap : aMap) {
                if (anAMap == 100) {
                    num++;
                }
            }
        }

        return num;
    }

    @GetMapping("/test")
    public @ResponseBody
    String buildRoute() {
        GeoCoordinates domitory = new GeoCoordinates(30.3011164, 59.972426);
        GeoCoordinates university = new GeoCoordinates(30.3060693, 59.957182);

        ArrayList<RouteProperty> routeProperties = new ArrayList<>();//Arrays.asList(RouteProperty.values()));
        int[][] mapWithRoute = initField(20);

        pathFindingAlgorithm.buildRoute(domitory, university, routeProperties, mapWithRoute);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(mapWithRoute);

        return "loading map image...";

    }

    @GetMapping("/route-map")
    public @ResponseBody
    String returnRouteMap() {
        GeoCoordinates domitory = new GeoCoordinates(30.3011164, 59.972426);
        GeoCoordinates university = new GeoCoordinates(30.3060693, 59.957182);
        GeoCoordinates northPoint = new GeoCoordinates(30.4277723, 59.841273);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(routeMapService.buildMap(domitory, northPoint));

        return "loading map image...";
    }

    @GetMapping("/route-map2")
    public @ResponseBody
    String returnRoute2Map() {
        GeoCoordinates domitory = new GeoCoordinates(30.3011164, 59.972426);
        GeoCoordinates university = new GeoCoordinates(30.3060693, 59.957182);
        GeoCoordinates northPoint = new GeoCoordinates(30.4277723, 59.841273);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(routeMapService.buildMap2(domitory, northPoint));

        return "loading map image...";
    }

    @GetMapping("/weather-map")
    public @ResponseBody
    String returnWeatherMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.GOOD_WEATHER);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(new GeoCoordinates(0d, 0d), new GeoCoordinates(0d, 0d), properties));

        return "loading map image...";
    }

    @GetMapping("/park-map")
    public @ResponseBody
    String returnParkMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.PARK);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(new GeoCoordinates(0d, 0d), new GeoCoordinates(0d, 0d), properties));

        return "loading map image...";
    }

    @GetMapping("/all-map")
    public @ResponseBody
    String returnallMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>(Arrays.asList(RouteProperty.values()));

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(new GeoCoordinates(0d, 0d), new GeoCoordinates(0d, 0d), properties));

        return "loading map image...";
    }

}
