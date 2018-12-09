package com.netcracker.routebuilder.algorithm;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

@Slf4j
public class GoogleRouteBuilderTest {

    @Test
    public void ExecuteStartEndListRouteTest() {
        ArrayList<GeoCoordinates> coordinates;
        AlgorithmParameters PARAMS = new AlgorithmParameters();
        PARAMS.setScale(20);
        PARAMS.setApiKey("AIzaSyDsx7KAWwgcWwWdvaVbjLRfWwnqrqoShN0");
        PARAMS.setMaxCountOfWaypoints(21);
        GoogleRouteBuilder gBuilder = new GoogleRouteBuilder(PARAMS);
        coordinates = gBuilder.buildRoute(new GeoCoordinates(30.351127, 59.8824512), new GeoCoordinates(30.265784, 59.93799));
        assertNotNull(coordinates);

    }


    @Test
    public void ExecuteListRouteTest() {
        ArrayList<GeoCoordinates> coordinates;
        AlgorithmParameters PARAMS = new AlgorithmParameters();
        PARAMS.setScale(20);
        PARAMS.setApiKey("AIzaSyDsx7KAWwgcWwWdvaVbjLRfWwnqrqoShN0");
        PARAMS.setMaxCountOfWaypoints(21);
        GoogleRouteBuilder gBuilder = new GoogleRouteBuilder(PARAMS);
        //Add more then 21 point
        double lat = 0.01793 / 1.5;
        double longT = 0.01762 / 1.5;
        ArrayList<GeoCoordinates> list = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            list.add(new GeoCoordinates(30.351127 + i * lat, 59.8824512 + i * longT));
        }
        coordinates = gBuilder.buildRoute(list);
        assertNotNull(coordinates);
    }

    @Test
    public void ExecuteStartEndRouteTest() {
        ArrayList<GeoCoordinates> coordinates;
        AlgorithmParameters PARAMS = new AlgorithmParameters();
        PARAMS.setScale(20);
        PARAMS.setApiKey("AIzaSyDsx7KAWwgcWwWdvaVbjLRfWwnqrqoShN0");
        PARAMS.setMaxCountOfWaypoints(21);
        GoogleRouteBuilder gBuilder = new GoogleRouteBuilder(PARAMS);
        //Add more then 21 point
        double lat = 0.01793 / 1.5;
        double longT = 0.01762 / 1.5;
        ArrayList<GeoCoordinates> list = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            list.add(new GeoCoordinates(30.351127 + i * lat, 59.8824512 + i * longT));
        }
        coordinates = gBuilder.buildRoute(new GeoCoordinates(30.151127, 59.6824512), list, new GeoCoordinates(34.151127, 56.6824512));
        assertNotNull(coordinates);
    }
}