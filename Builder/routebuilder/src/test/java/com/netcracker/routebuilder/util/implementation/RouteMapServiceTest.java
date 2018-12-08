package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.service.RouteMapService;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class RouteMapServiceTest {

    @Test
    public void ExecuteBuildMapTest() {
        AlgorithmParameters PARAMS = new AlgorithmParameters();
        PARAMS.setScale(20);
        PARAMS.setApiKey("AIzaSyDsx7KAWwgcWwWdvaVbjLRfWwnqrqoShN0");
        PARAMS.setMaxCountOfWaypoints(21);
        RouteMapService RouteM = new RouteMapService(PARAMS);
        int[][] field = RouteM.buildMap(new GeoCoordinates(30.351127, 59.8824512), new GeoCoordinates(30.265784, 59.93799));
        boolean checkNotNull = false;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] != 0) {
                    checkNotNull = !checkNotNull;
                    log.info("Potential value of cell:"+field[i][j]);
                    break;
                }
                if(checkNotNull) break;
            }
        }
        assertTrue(checkNotNull);

    }


}