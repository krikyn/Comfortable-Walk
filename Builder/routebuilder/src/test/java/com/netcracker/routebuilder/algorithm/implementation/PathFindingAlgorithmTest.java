package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.enums.DistanceTypes;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PathFindingAlgorithmTestConfiguration.class)
public class PathFindingAlgorithmTest {

    @Autowired
    private PathFindingAlgorithm pathFindingAlgorithm;

    @Test
    public void sameFromToPoints() throws Exception{

        pathFindingAlgorithm.googleRouteBuilder = new GoogleRouteBuilder();

        pathFindingAlgorithm.params = new AlgorithmParameters();
        pathFindingAlgorithm.params.setMaxAllowableIncrease(3);
        pathFindingAlgorithm.params.setMinDistBetweenStartEnd(100);
        pathFindingAlgorithm.params.setScale(20);
        pathFindingAlgorithm.params.setDistanceType(DistanceTypes.EUCLIDEAN);
        pathFindingAlgorithm.params.setNormalFactorH(1);
        pathFindingAlgorithm.params.setNormalFactorG(1);

        GeoCoordinates geoCoordinates = new GeoCoordinates(30.18035, 60.02781);
        System.out.println(pathFindingAlgorithm.buildRoute(geoCoordinates, geoCoordinates, new ArrayList<>()));
    }
}