package com.netcracker.routebuilder.service;

import com.netcracker.commons.service.CityMapService;
import com.netcracker.routebuilder.data.map.impl.PlacesMap;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.enums.PlacesType;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;

import static com.netcracker.routebuilder.util.AlgorithmUtil.*;

/**
 * Class to provide access to the current places potential map
 *
 * @author Kirill.Vakhrushev
 */
@Component
@RequiredArgsConstructor
public class PlacesMapService {

    private PlacesMap placesMap;
    private final AlgorithmParameters params;
    private final CityMapService cityMapService;

    /**
     * Places potential map initialization
     */
    @PostConstruct
    public void init() {
        placesMap = new PlacesMap();
        placesMap.setFields(new HashMap<>());

        for (PlacesType type : PlacesType.values()) {
            placesMap.getFields().put(type.toString(), initField(params.getScale()));
        }

        updateMaps();
    }

    /**
     * Method for collecting a potential map with the desired properties
     *
     * @param includedProperties route properties
     * @return potential map with plotted objects
     */
    public int[][] getField(ArrayList<RouteProperty> includedProperties) {
        int[][] baseMap = initField(params.getScale());

        for (RouteProperty type : includedProperties) {
            if (!type.equals(RouteProperty.GOOD_WEATHER)) {
                int[][] curField = placesMap.getFields().get(type.toString());
                fieldNormalization100(curField);
                combineFields(baseMap, curField, 1);
            }
        }

        return baseMap;
    }

    /**
     * Method for recording the latest version of the places potential map from the database
     */
    public void updateMaps() {
        for (PlacesType place : PlacesType.values()) {
            placesMap.getFields().replace(place.toString(), cityMapService.loadCityMapByType("POTENTIAL_" + place.toString()).getGrid());
        }
    }
}
