package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.implementation.ZeroMap;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
@Component
public class PotentialMapBuilder {

    final AlgorithmParameters params;
    private final CityMapService cityMapService;

    AbstractPotentialMap assemblePotentialMap(ArrayList<RouteProperties> includedProperties) {
        if (includedProperties.isEmpty()) {
            log.info("Route property list is empty, a zero potential map will be used");
            return new ZeroMap(params.getScale());
        } else {
            return new ZeroMap(params.getScale());
        }
    }
}
