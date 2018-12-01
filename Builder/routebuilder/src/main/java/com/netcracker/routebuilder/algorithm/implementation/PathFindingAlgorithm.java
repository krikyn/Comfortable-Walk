package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.data.bean.Cell;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import com.netcracker.routebuilder.util.implementation.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

@RequiredArgsConstructor
@Slf4j
@Component
public class PathFindingAlgorithm {

    final GoogleRouteBuilder googleRouteBuilder;
    final AlgorithmParameters params;
    final PotentialMapBuilder potentialMapBuilder;

    private final static double LAT_1_KM = 0.00898; //1 км в градусах широты
    private final static double LON_1_KM = 0.01793; //1 км в градусах долготы
    private final static int DEFAULT_MAP_SCALE = 1;
    //количество клеток 1x1 км по Ox и Oy  на нашей потенциальной карте
    private final static int DEFAULT_NUM_POINT_X = 21;
    private final static int DEFAULT_NUM_POINT_Y = 20;


    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates startPoint, GeoCoordinates endPoint, ArrayList<RouteProperty> routeProperties, int[][] mapWithRoute) {
        log.info("--Start of the algorithm--");
        log.info("Distance between Starting and ending point: " + EuclideanDist(startPoint, endPoint));

        if (EuclideanDist(startPoint, endPoint) < params.getMinDistBetweenStartEnd()) {
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

        //hmmmmmmmm
        Cell firstPoint = potentialField.get(startCell.getX()).get(startCell.getY());

        Double firstPointH = calcDistToDestinationCell(startNode, endNode);
        firstPoint.updateParameters(null, (double) firstPoint.getValue(), firstPointH);

        nodes.add(firstPoint);
        log.info("The route search begins");

        int iterationsNum = 0;

        while (!nodes.isEmpty()) {
            iterationsNum++;
            Cell curCell = nodes.poll();
            System.out.println(curCell.getGx() + " - " + curCell.getHx());

            if (curCell.getFieldCoordinates().equals(endNode.getFieldCoordinates())) {
                ArrayList<GeoCoordinates> route = routeRestoration(curCell, mapWithRoute);
                log.info("The algorithm found the best way");
                log.info("Iterations number: " + iterationsNum);
                return googleRouteBuilder.buildRoute(startPoint, route, endPoint);
            }

            curCell.setClosed(true);

            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    int newX = curCell.getFieldCoordinates().getX() + offsetX;
                    int newY = curCell.getFieldCoordinates().getY() + offsetY;

                    if (newX >= 0 && newY >= 0 && newX < numPointsX && newY < numPointsY) {
                        Cell nextCell = potentialField.get(newX).get(newY);

                        Double tentativeScore = curCell.getGx() + calcDistToDestinationCell(curCell, nextCell); //calcGxBetweenPoints(potentialField, curCell, nextCell);

                        if (!nextCell.isClosed() || tentativeScore < nextCell.getGx()) {
                            nextCell.updateParameters(curCell,
                                    tentativeScore, calcDistToDestinationCell(curCell, endNode));
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
                return EuclideanDist(from, to);
            case CHEBYSHEVA:
                return ChebyshevDist(from, to);
            case MANHATTAN:
                return ManhattanDistance(from, to);
            case COMPOSIT:
                return 0d;
            default:
                log.error("Wrong distance type in the parameters. Euclidean distance will be used");
                return EuclideanDist(from, to);
        }
    }

    private static Double ManhattanDistance(Cell from, Cell to) {
        return (double) Math.abs(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX()) +
                Math.abs(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY());
    }

    private static Double ChebyshevDist(Cell from, Cell to) {
        return (double) Math.max(Math.abs(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX()),
                Math.abs(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY()));
    }

    //ответ в метрах
    private static Double EuclideanDist(Cell from, Cell to) {
        return EuclideanDist(from.getGeoCoordinates(), to.getGeoCoordinates());
    }

    //ответ в метрах
    private static Double EuclideanDist(GeoCoordinates from, GeoCoordinates to) {
        return Math.sqrt(Math.pow(((from.getX() - to.getX()) / LON_1_KM) * 1000, 2) +
                Math.pow(((from.getY() - to.getY()) / LAT_1_KM) * 1000, 2));
    }

    private Integer recountWithNewScale(int originalSize) {
        return Utils.recountWithNewScale(originalSize, params.getScale());
    }

    private FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point) {
        return Utils.convertGeoToFieldCoordinates(point, params.getScale());
    }

    private ArrayList<GeoCoordinates> routeRestoration(Cell curNode, int[][] mapWithRoute) {
        ArrayList<GeoCoordinates> route = new ArrayList<>();

        //не включаем конечную точку
        //route.add(curNode.getGeoCoordinates());

        while (curNode.getParent() != null) {
            curNode = curNode.getParent();
            route.add(curNode.getGeoCoordinates());
            mapWithRoute[curNode.getFieldCoordinates().getX()][curNode.getFieldCoordinates().getY()] = 100;
            ;
        }

        //не включаем начальную точку
        if (!route.isEmpty()){
            route.remove(route.size() - 1);
        }


        Collections.reverse(route);

        return route;
    }

}
