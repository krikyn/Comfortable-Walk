package com.netcracker.routebuilder.controller;

import com.netcracker.routebuilder.algorithm.implementation.PathFindingAlgorithm;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
public class AlgorithmController {

    private final PathFindingAlgorithm pathFindingAlgorithm;

    @GetMapping("/build")
    public @ResponseBody
    ArrayList<GeoCoordinates> buildRoute(@RequestParam GeoCoordinates startPoint, @RequestParam GeoCoordinates endPoint, @RequestParam ArrayList<RouteProperties> routeProperties) {
        return pathFindingAlgorithm.buildRoute(startPoint, endPoint, routeProperties);
    }

    @GetMapping("/test")
    public @ResponseBody
    void buildRoute() {
        GeoCoordinates home1 = new GeoCoordinates(30.300329, 59.973033);
        GeoCoordinates home2 = new GeoCoordinates(30.3008018, 59.971714);
        pathFindingAlgorithm.buildRoute(home1, home2, new ArrayList<>());
    }

}
