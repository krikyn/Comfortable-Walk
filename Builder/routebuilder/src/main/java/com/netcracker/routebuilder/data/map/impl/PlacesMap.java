package com.netcracker.routebuilder.data.map.impl;

import com.netcracker.routebuilder.data.map.AbstractPotentialMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Class describing a places potential map
 *
 * @author Kirill.Vakhrushev
 */
public class PlacesMap extends AbstractPotentialMap {

    @Getter
    @Setter
    private Map<String, int[][]> fields;

}
