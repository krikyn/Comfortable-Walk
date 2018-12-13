package com.netcracker.routebuilder.algorithm;

import com.netcracker.routebuilder.data.bean.Cell;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.enums.DistanceType;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.util.AlgorithmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Class that implements an algorithm for constructing a route with given parameters
 *
 * @author Kirill.Vakhrushev
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class PathFindingAlgorithm {

    private final GoogleRouteBuilder googleRouteBuilder;
    private final AlgorithmParameters params;
    private final PotentialMapBuilder potentialMapBuilder;

    /**
     * 1 km in degrees of latitude
     */
    private final static double LAT_1_KM = 0.00898;
    /**
     * 1 km in degrees of longitude
     */
    private final static double LON_1_KM = 0.01793;
    /**
     * the number of cells 1x1 km by Ox on potential map
     */
    private final static int DEFAULT_NUM_POINT_X = 21;
    /**
     * the number of cells 1x1 km by Oy on our potential map
     */
    private final static int DEFAULT_NUM_POINT_Y = 20;


    /**
     * Method for build route
     *
     * @param startPoint      starting route geo-location
     * @param endPoint        final route geo-location
     * @param routeProperties route properties
     * @param mapWithRoute    map on which the route will be applied
     * @return list of coordinates of points of the constructed route
     */
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates startPoint, GeoCoordinates endPoint, ArrayList<RouteProperty> routeProperties, int[][] mapWithRoute) {
        log.info("--Start of the algorithm--");
        double distBetweanAandB = calcEuclideanDist(startPoint, endPoint);
        log.info("Distance between Starting and ending point: " + distBetweanAandB);

        if (routeProperties.size() == 0) {
            log.warn("No additional route parameters, give the standard Google route");
            return googleRouteBuilder.buildRoute(startPoint, endPoint);
        }

        if (calcEuclideanDist(startPoint, endPoint) < params.getMinDistBetweenStartEnd()) {
            log.warn("Starting and ending point too close, give the standard Google route");
            return googleRouteBuilder.buildRoute(startPoint, endPoint);
        }

        final int numPointsX = recountWithNewScale(DEFAULT_NUM_POINT_X);
        final int numPointsY = recountWithNewScale(DEFAULT_NUM_POINT_Y);

        log.info("New potential map size: " + numPointsX + ", " + numPointsY);

        final FieldCoordinates startCell = convertGeoToFieldCoordinates(startPoint);
        final FieldCoordinates endCell = convertGeoToFieldCoordinates(endPoint);

        //test
        mapWithRoute[startCell.getX()][startCell.getY()] = 100;
        mapWithRoute[endCell.getX()][endCell.getY()] = 100;

        log.info("Start cell: " + startCell.getX() + ", " + startCell.getY());
        log.info("End cell: " + endCell.getX() + ", " + endCell.getY());

        ArrayList<ArrayList<Cell>> potentialField = new ArrayList<>();
        potentialFieldInitialization(potentialField, numPointsX, numPointsY);
        fillPotentialField(startPoint, endPoint, potentialField, routeProperties);

        Cell startNode = potentialField.get(startCell.getX()).get(startCell.getY());
        Cell endNode = potentialField.get(endCell.getX()).get(endCell.getY());

        PriorityQueue<Cell> nodes = new PriorityQueue<>(Comparator.comparing(Cell::getFx));

        Cell firstPoint = potentialField.get(startCell.getX()).get(startCell.getY());

        Double firstPointH = calcDistToDestinationCell(startNode, endNode);
        firstPoint.updateParameters(
                null,
                firstPoint.getValue() * params.getNormalFactorG(),
                firstPointH * params.getNormalFactorH());

        nodes.add(firstPoint);
        log.info("The route search begins");

        int iterationsNum = 0;

        while (!nodes.isEmpty()) {

            Cell curCell = nodes.poll();

            if (curCell.equals(endNode)) {
                log.info("The algorithm found the best way");
                ArrayList<GeoCoordinates> route = routeRestoration(curCell, mapWithRoute);
                log.info("Iterations number: " + iterationsNum);

                return googleRouteBuilder.buildRoute(startPoint, route, endPoint);
            }


            if (calcEuclideanDist(curCell, endNode) > distBetweanAandB * params.getMaxAllowableIncrease()) {
                nodes.remove(curCell);
                continue;
            }

            iterationsNum++;

            if (iterationsNum > params.getMaxIterationsNum()) {
                log.warn("Maximum number of iterations exceeded in the algorithm, standard Google route will be used");
                return googleRouteBuilder.buildRoute(startPoint, endPoint);
            }

            curCell.setClosed(true);

            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {

                    //пропускаем изначальную ячейку
                    if (offsetX == 0 && offsetY == 0) {
                        continue;
                    }

                    int newX = curCell.getFieldCoordinates().getX() + offsetX;
                    int newY = curCell.getFieldCoordinates().getY() + offsetY;

                    if (newX >= 0 && newY >= 0 && newX < numPointsX && newY < numPointsY) {
                        Cell nextCell = potentialField.get(newX).get(newY);

                        //this is next cell gx
                        Double tentativeScore = curCell.getValue() +
                                nextCell.getValue() * params.getNormalFactorG() +
                                calcDistToDestinationCell(curCell, nextCell) * params.getNormalFactorG();

                        if (!nextCell.isClosed() && tentativeScore < nextCell.getGx()) {
                            nextCell.updateParameters(
                                    curCell,
                                    tentativeScore,
                                    calcDistToDestinationCell(curCell, endNode) * params.getNormalFactorH());

                            if (!nodes.contains(nextCell)) {
                                nodes.add(nextCell);
                            }
                        }
                    }
                }
            }
        }

        log.warn("The algorithm did not find the path, standard Google route will be used");
        return googleRouteBuilder.buildRoute(startPoint, endPoint);
    }

    /**
     * Method for setting distance type in params
     */
    public void setDistanceType(DistanceType distanceType) {
        params.setDistanceType(distanceType);
    }

    private static Double calcGxBetweenPoints(ArrayList<ArrayList<Cell>> potentialField, Cell from, Cell to) {
        return (double) potentialField.get(to.getFieldCoordinates().getX()).get(to.getFieldCoordinates().getY()).getValue();
    }

    private void fillPotentialField(GeoCoordinates start, GeoCoordinates end, ArrayList<ArrayList<Cell>> potentialField, ArrayList<RouteProperty> routeProperties) {
        log.info("start of filling a potential map");
        int[][] assembledMap = potentialMapBuilder.assemblePotentialMap(start, end, routeProperties);

        for (int i = 0; i < potentialField.size(); i++) {
            for (int j = 0; j < potentialField.get(i).size(); j++) {
                potentialField.get(i).get(j).setValue(assembledMap[i][j]);
            }
        }
        log.info("potential map filled");
    }

    private void potentialFieldInitialization(ArrayList<ArrayList<Cell>> potentialField, int x, int y) {

        double latStep = LAT_1_KM / (double) params.getScale();
        double lonStep = LON_1_KM / (double) params.getScale();

        double curX = 30.18035;
        double curY = 60.02781;

        for (int i = 0; i < x; i++) {
            curX = 30.18035;
            potentialField.add(new ArrayList<>());
            for (int j = 0; j < y; j++) {
                potentialField.get(i).add(new Cell(0, new FieldCoordinates(i, j), new GeoCoordinates(curX, curY)));
                curX += lonStep;
            }
            curY -= latStep;
        }

        log.info("Potential map initialized");
    }

    private Double calcDistToDestinationCell(Cell from, Cell to) {
        switch (params.getDistanceType()) {
            case EUCLIDEAN:
                return calcEuclideanDist(from, to);
            case CHEBYSHEVA:
                return calcChebyshevDist(from, to);
            case MANHATTAN:
                return calcManhattanDistance(from, to);
            case COMPOSIT:
                return calcEuclideanDist(from, to);
            default:
                log.error("Wrong distance type in the parameters. Euclidean distance will be used");
                return calcEuclideanDist(from, to);
        }
    }

    private static Double calcManhattanDistance(Cell from, Cell to) {
        return (double) Math.abs(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX()) +
                Math.abs(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY());
    }

    private static Double calcChebyshevDist(Cell from, Cell to) {
        return (double) Math.max(Math.abs(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX()),
                Math.abs(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY()));
    }

    //ответ в метрах
    private static Double calcEuclideanDist(Cell from, Cell to) {
        return calcEuclideanDist(from.getGeoCoordinates(), to.getGeoCoordinates());
    }

    //ответ в метрах
    private static Double calcEuclideanDist(GeoCoordinates from, GeoCoordinates to) {
        return Math.sqrt(Math.pow(((from.getX() - to.getX()) / LON_1_KM) * 1000, 2) +
                Math.pow(((from.getY() - to.getY()) / LAT_1_KM) * 1000, 2));
    }

    private Integer recountWithNewScale(int originalSize) {
        return AlgorithmUtil.recountWithNewScale(originalSize, params.getScale());
    }

    private FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point) {
        return AlgorithmUtil.convertGeoToFieldCoordinates(point, params.getScale());
    }

    private ArrayList<GeoCoordinates> routeRestoration(Cell curNode, int[][] mapWithRoute) {
        int routePointsNum = 0;
        ArrayList<GeoCoordinates> route = new ArrayList<>();

        //не включаем конечную точку
        //route.add(curNode.getGeoCoordinates());

        while (curNode.getParent() != null) {
            curNode = curNode.getParent();
            route.add(curNode.getGeoCoordinates());
            mapWithRoute[curNode.getFieldCoordinates().getX()][curNode.getFieldCoordinates().getY()] = 100;
            routePointsNum++;
        }

        //не включаем начальную точку
        if (!route.isEmpty()) {
            route.remove(route.size() - 1);
        }

        log.info("Number of the route's points: " + routePointsNum);

        Collections.reverse(route);

        return route;
    }

}
