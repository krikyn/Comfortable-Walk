package com.netcracker.routebuilder.controller;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.routebuilder.algorithm.implementation.PathFindingAlgorithm;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
public class AlgorithmController {

    private final PathFindingAlgorithm pathFindingAlgorithm;
    private final CityMapService cityMapService;

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

}
