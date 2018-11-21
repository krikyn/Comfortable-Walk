package com.netcracker.routebuilder.controller;

import com.netcracker.routebuilder.algorithm.implementation.PathFindingAlgorithm;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
public class AlgorithmController {

    PathFindingAlgorithm pathFindingAlgorithm;

    @GetMapping("/build")
    public @ResponseBody
    ArrayList<GeoCoordinates> buildRoute(@RequestParam GeoCoordinates startPoint, @RequestParam GeoCoordinates endPoint, @RequestParam ArrayList<RouteProperties> routeProperties) {
        return pathFindingAlgorithm.buildRoute(startPoint, endPoint, routeProperties);
    }

}
