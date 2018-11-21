package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.util.implementation.ZeroMap;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class PotentialMapBuilder {

    private int scale; // scale = 20, значит размер каждой клетки 1 км / 20 = 50 метров

    public PotentialMapBuilder() {
        scale = 20;
    }

    public PotentialMapBuilder(int scale) {
        this.scale = scale;
    }

    AbstractPotentialMap assemblePotentialMap(ArrayList<RouteProperties> includedProperties) {
        if (includedProperties.isEmpty()){
            log.info("Route property list is empty, a zero potential map will be used");
            return new ZeroMap(scale);
        } else {
            return new ZeroMap(scale);
        }
    }
}
