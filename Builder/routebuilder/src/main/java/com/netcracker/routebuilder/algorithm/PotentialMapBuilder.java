package com.netcracker.routebuilder.algorithm;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.service.PlacesMapService;
import com.netcracker.routebuilder.service.RouteMapService;
import com.netcracker.routebuilder.service.WeatherPotentialMapService;
import com.netcracker.routebuilder.service.ZeroMapService;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.netcracker.routebuilder.util.AlgorithmUtil.*;

/**
 * Class to combine all potential maps into one, taking into account the necessary features of the route
 *
 * @author Kirill.Vakhrushev
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class PotentialMapBuilder {

    private final AlgorithmParameters params;
    private final ZeroMapService zeroMapService;
    private final WeatherPotentialMapService weatherPotentialMapService;
    private final PlacesMapService placesMapService;
    private final RouteMapService routeMapService;

    /**
     * method for assemble main potential map
     *
     * @param start              start point of the route
     * @param end                end point of the route
     * @param includedProperties necessary properties of the route
     * @return assembled potential map
     */
    public int[][] assemblePotentialMap(GeoCoordinates start, GeoCoordinates end, ArrayList<RouteProperty> includedProperties) {
        if (includedProperties.isEmpty()) {
            int[][] field = initField(params.getScale());

            int[][] routeField = routeMapService.buildMap(start, end);
            fieldNormalization100(routeField);
            combineFields(field, routeField, params.getRouteFieldFactor());

            fieldNormalization100(field);
            return field;
            //log.info("Route property list is empty, a zero potential map will be used");
            //return zeroMapService.getField();
        } else {
            int[][] field = initField(params.getScale());

            if (includedProperties.contains(RouteProperty.GOOD_WEATHER)) {
                int[][] weatherField = weatherPotentialMapService.getField();
                fieldNormalization100(weatherField);
                combineFields(field, weatherField, params.getWeatherFieldFactor());
            }

            combineFields(field, placesMapService.getField(includedProperties), params.getPlacesFieldFactor());

            int[][] routeField = routeMapService.buildMap(start, end);
            fieldNormalization100(routeField);
            combineFields(field, routeField, params.getRouteFieldFactor());

            fieldNormalization100(field);
            return field;
        }
    }
}
