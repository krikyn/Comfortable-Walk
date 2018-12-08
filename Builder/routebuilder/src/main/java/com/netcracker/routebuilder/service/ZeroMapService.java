package com.netcracker.routebuilder.service;

import com.netcracker.routebuilder.data.map.impl.ZeroMap;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Class to provide access to the zero potential map
 *
 * @author Kirill.Vakhrushev
 */
@Component
@RequiredArgsConstructor
public class ZeroMapService {

    private final AlgorithmParameters params;
    private ZeroMap zeroMap;

    /**
     * Zero potential map initialization
     */
    @PostConstruct
    public void init() {
        zeroMap = new ZeroMap();
        zeroMap.setField(initField(params.getScale()));
    }

    /**
     * Method for getting zero potential map
     *
     * @return potential field with zero values
     */
    public int[][] getField() {
        return zeroMap.getField();
    }
}
