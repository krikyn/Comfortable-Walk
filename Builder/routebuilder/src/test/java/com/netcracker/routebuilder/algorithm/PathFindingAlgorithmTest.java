package com.netcracker.routebuilder.algorithm;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PathFindingAlgorithmTestConfiguration.class)
public class PathFindingAlgorithmTest {

    //@Autowired
    //private PathFindingAlgorithm pathFindingAlgorithm;

    //@Test
    //public void sameFromToPoints() throws Exception {

        //pathFindingAlgorithm.googleRouteBuilder = new GoogleRouteBuilder();

//        pathFindingAlgorithm.params = new AlgorithmParameters();
//        pathFindingAlgorithm.params.setMaxAllowableIncrease(3);
//        pathFindingAlgorithm.params.setMinDistBetweenStartEnd(100);
//        pathFindingAlgorithm.params.setScale(20);
//        pathFindingAlgorithm.params.setDistanceType(DistanceType.EUCLIDEAN);
//        pathFindingAlgorithm.params.setNormalFactorH(1);
//        pathFindingAlgorithm.params.setNormalFactorG(1);

        /*GeoCoordinates geoCoordinates = new GeoCoordinates(30.18035, 60.02781);
        GeoCoordinates park1 = new GeoCoordinates(30.296605, 59.972628);
        GeoCoordinates park2 = new GeoCoordinates(30.300560, 59.973394);
        GeoCoordinates home1 = new GeoCoordinates(30.300329, 59.973033);
        GeoCoordinates home2 = new GeoCoordinates(30.3008018, 59.971714);
        ArrayList<GeoCoordinates> route = pathFindingAlgorithm.buildRoute(home1, home2, new ArrayList<>());
        for (GeoCoordinates coords : route) {
            System.out.println(coords.getY() + ", " + coords.getX());
        }*/
    //}
}