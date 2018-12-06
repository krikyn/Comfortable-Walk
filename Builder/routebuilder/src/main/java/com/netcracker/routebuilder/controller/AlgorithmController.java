package com.netcracker.routebuilder.controller;

import com.netcracker.routebuilder.algorithm.PathFindingAlgorithm;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.data.bean.Path;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.netcracker.routebuilder.util.ControllerUtil.extractRouteProperties;
import static com.netcracker.routebuilder.util.ControllerUtil.generateResponse;
import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Controller for path finding algorithm
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class AlgorithmController {

    private final PathFindingAlgorithm pathFindingAlgorithm;

    /**
     * Method for building a route according to specified parameters
     *
     * @param path path params
     * @return array of strings with a list of coordinates of points of the constructed route
     */
    @CrossOrigin
    @PostMapping("/build")
    public double[][] build(@RequestBody Path path) {
        int[][] mapWithRoute = initField(20);
        ArrayList<RouteProperty> routeProperties = new ArrayList<>();

        if (path.getIsBestWeather()) {
            routeProperties.add(RouteProperty.GOOD_WEATHER);
        }

        extractRouteProperties(path, routeProperties);

        GeoCoordinates from = new GeoCoordinates(Double.valueOf(path.getFromPointLng()), Double.valueOf(path.getFromPointLat()));
        GeoCoordinates to = new GeoCoordinates(Double.valueOf(path.getToPointLng()), Double.valueOf(path.getToPointLat()));

        ArrayList<GeoCoordinates> route = pathFindingAlgorithm.buildRoute(from, to, routeProperties, mapWithRoute);

        double[][] response = new double[route.size() + 1][2];
        generateResponse(route, response);

        return response;
    }
}
