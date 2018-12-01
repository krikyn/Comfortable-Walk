package com.netcracker.routebuilder.controller;

import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.routebuilder.algorithm.implementation.GoogleRouteBuilder;
import com.netcracker.routebuilder.algorithm.implementation.PathFindingAlgorithm;
import com.netcracker.routebuilder.algorithm.implementation.PotentialMapBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.util.implementation.DrawMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;

import static com.netcracker.routebuilder.util.implementation.Utils.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.implementation.Utils.initField;

@RequiredArgsConstructor
@Controller
public class AlgorithmController {

    private final PathFindingAlgorithm pathFindingAlgorithm;
    private final CityMapService cityMapService;
    private final PotentialMapBuilder potentialMapBuilder;
    GoogleRouteBuilder googleRouteBuilder;

    /*@GetMapping("/build")
    public @ResponseBody
    ArrayList<GeoCoordinates> buildRoute(@RequestParam GeoCoordinates startPoint, @RequestParam GeoCoordinates endPoint, @RequestParam ArrayList<RouteProperty> routeProperties) {
        return pathFindingAlgorithm.buildRoute(startPoint, endPoint, routeProperties);
    }*/

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

    @GetMapping("/weather-map")
    public @ResponseBody
    String returnWeatherMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.GOOD_WEATHER);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

    @GetMapping("/park-map")
    public @ResponseBody
    String returnParkMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.PARK);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

    @GetMapping("/cafe-map")
    public @ResponseBody
    String returnCafeMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.CAFE);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

    @GetMapping("/zoo-map")
    public @ResponseBody
    String returnZooMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>();
        properties.add(RouteProperty.ZOO);

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

    @GetMapping("/all-map")
    public @ResponseBody
    String returnallMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>(Arrays.asList(RouteProperty.values()));

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

}
