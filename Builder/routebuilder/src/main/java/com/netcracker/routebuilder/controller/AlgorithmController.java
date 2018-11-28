package com.netcracker.routebuilder.controller;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.routebuilder.algorithm.implementation.PathFindingAlgorithm;
import com.netcracker.routebuilder.algorithm.implementation.PotentialMapBuilder;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.util.implementation.DrawMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
@Controller
public class AlgorithmController {

    private final PathFindingAlgorithm pathFindingAlgorithm;
    private final CityMapService cityMapService;
    private final PotentialMapBuilder potentialMapBuilder;

    @GetMapping("/build")
    public @ResponseBody
    ArrayList<GeoCoordinates> buildRoute(@RequestParam GeoCoordinates startPoint, @RequestParam GeoCoordinates endPoint, @RequestParam ArrayList<RouteProperty> routeProperties) {
        return pathFindingAlgorithm.buildRoute(startPoint, endPoint, routeProperties);
    }

    @GetMapping("/test")
    public @ResponseBody
    void buildRoute() {
        GeoCoordinates home1 = new GeoCoordinates(30.300329, 59.973033);
        GeoCoordinates home2 = new GeoCoordinates(30.3008018, 59.971714);
        ArrayList<RouteProperty> routeProperties = new ArrayList<>();
        pathFindingAlgorithm.buildRoute(home1, home2, new ArrayList<>());
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

    @GetMapping("/all-map")
    public @ResponseBody
    String returnallMap() {

        ArrayList<RouteProperty> properties = new ArrayList<>(Arrays.asList(RouteProperty.values()));

        DrawMap drawMap = new DrawMap();
        drawMap.draw(potentialMapBuilder.assemblePotentialMap(properties));

        return "loading map image...";
    }

}
