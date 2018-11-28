package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.implementation.PlacesMap;
import com.netcracker.routebuilder.util.implementation.WeatherMap;
import com.netcracker.routebuilder.util.implementation.ZeroMap;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.netcracker.routebuilder.util.implementation.Utils.combineFields;
import static com.netcracker.routebuilder.util.implementation.Utils.initField;

@RequiredArgsConstructor
@Slf4j
@Component
public class PotentialMapBuilder {

    private final AlgorithmParameters params;
    private final ZeroMap zeroMap;
    private final WeatherMap weatherMap;
    private final PlacesMap placesMap;

    public int[][] assemblePotentialMap(ArrayList<RouteProperty> includedProperties) {

        if (includedProperties.isEmpty()) {
            log.info("Route property list is empty, a zero potential map will be used");
            return zeroMap.getField();
        } else {
            int[][] field = initField(params.getScale());

            if (includedProperties.contains(RouteProperty.GOOD_WEATHER)) {
                combineFields(field, weatherMap.getField());
            }

            combineFields(field, placesMap.getField(includedProperties));

            return field;
        }
    }
}
