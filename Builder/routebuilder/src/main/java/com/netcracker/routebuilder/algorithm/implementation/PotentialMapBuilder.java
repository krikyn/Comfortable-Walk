package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.util.implementation.ZeroMap;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PotentialMapBuilder {

    final int potentialMapScale = 20; // scale = 20, значит размер каждой клетки 1 км / 20 = 50 метров

    AbstractPotentialMap assemblePotentialMap(ArrayList<RouteProperties> includedProperties) {
        return new ZeroMap(potentialMapScale);
    }


}
