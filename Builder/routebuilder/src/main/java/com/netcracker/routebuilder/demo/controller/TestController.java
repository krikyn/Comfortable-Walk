package com.netcracker.routebuilder.demo.controller;

import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.routebuilder.algorithm.PathFindingAlgorithm;
import com.netcracker.routebuilder.algorithm.PotentialMapBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.demo.visualizer.DrawMap;
import com.netcracker.routebuilder.service.RouteMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

import static com.netcracker.routebuilder.util.AlgorithmUtil.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Controller for testing path finding algorithm and potential map builder
 * TODO: переделать!!!!
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {

    private final PathFindingAlgorithm pathFindingAlgorithm;
    private final CityMapService cityMapService;
    private final PotentialMapBuilder potentialMapBuilder;
    private final RouteMapService routeMapService;

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

    @GetMapping("/test-points")
    public @ResponseBody
    String testPoints() {
        GeoCoordinates domitory = new GeoCoordinates(30.3011164, 59.972426);
        GeoCoordinates university = new GeoCoordinates(30.3060693, 59.957182);

        ArrayList<RouteProperty> routeProperties = new ArrayList<>();//Arrays.asList(RouteProperty.values()));
        int[][] mapWithRoute = initField(20);
        FieldCoordinates dom = convertGeoToFieldCoordinates(domitory, 20);
        mapWithRoute[dom.getY()][dom.getX()] = 100;
        mapWithRoute[dom.getX()][dom.getY()] = 100;

        DrawMap drawMap = new DrawMap();
        drawMap.draw(mapWithRoute);

        return "loading map image...";

    }

    @GetMapping("/hello")
    public @ResponseBody
    int[][] returnHello() {
        CityMap map = cityMapService.loadCityMapByType("POTENTIAL_PARK");
        /*for (int i=0;i<m.length;i++){
            for (int j=0;j<m[0].length;j++){

            }
        }*/
        //System.out.println("МЯСООООООООООООО");
        return map.getGrid();
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

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.ZOO);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(domitory, university, properties));

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

    @GetMapping("/cafe-map")
    public @ResponseBody
    String returnCafeMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.CAFE);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(new GeoCoordinates(0d, 0d), new GeoCoordinates(0d, 0d), properties));

        return "loading map image...";
    }

    @GetMapping("/zoo-map")
    public @ResponseBody
    String returnZooMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.ZOO);

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
