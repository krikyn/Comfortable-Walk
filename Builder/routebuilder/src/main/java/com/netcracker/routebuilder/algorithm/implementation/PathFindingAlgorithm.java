package com.netcracker.routebuilder.algorithm.implementation;

import com.netcracker.routebuilder.data.bean.Cell;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.implementation.*;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.util.enums.RouteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class PathFindingAlgorithm {

    GoogleRouteBuilder googleRouteBuilder;
    AlgorithmParameters params;
    final private int potentialMapScale = 20; // scale = 20, значит размер каждой клетки 1 км / 20 = 50 метров

    ArrayList<GeoCoordinates> buildRoute(GeoCoordinates startPoint, GeoCoordinates endPoint, ArrayList<RouteProperties> routeProperties) {
        log.info("--Start of the algorithm--");

        if (EuclideanDist(startPoint, endPoint) < params.getMinDistBetweenStartEnd()){
            log.warn("Starting and ending point too close, give the standard Google route");
            return googleRouteBuilder.buildRoute(startPoint, endPoint);
        }

        final int scale = potentialMapScale;

        //количество клеток 1x1 км по Ox и Oy  на нашей потенциальной карте
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;

        final int numPointsX = Utils.recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = Utils.recountWithNewScale(defaultNumPointsY, scale);

        final FieldCoordinates startCell = Utils.convertGeoToFieldCoordinates(startPoint, scale);
        final FieldCoordinates endCell = Utils.convertGeoToFieldCoordinates(endPoint, scale);

        ArrayList<ArrayList<Cell>> potentialField = new ArrayList<>();

        potentialFieldInitialization(potentialField, numPointsX, numPointsY, scale);

        fillPotentialField(potentialField, scale, routeProperties);

        //TODO hash and equals for Cell

        Cell startNode = potentialField.get(startCell.getX()).get(startCell.getY());
        Cell endNode = potentialField.get(endCell.getX()).get(endCell.getY());

        PriorityQueue<Cell> nodes = new PriorityQueue<>(Comparator.comparing(Cell::getFx));

        Cell firstPoint = potentialField.get(startCell.getX()).get(startCell.getY());
        Double firstPointH = calcGlobalH(startNode, endNode, scale);
        firstPoint.updateParameters(null, 0d, firstPointH);

        nodes.add(firstPoint);

        while (!nodes.isEmpty()) {
            Cell curNode = nodes.poll();
            if (curNode.getFieldCoordinates().equals(endCell)) {
                System.out.println("Нашли");

                ArrayList<GeoCoordinates> route = routeRestoration(curNode);
                log.info("The algorithm found the best way");
                return googleRouteBuilder.buildRoute(startPoint, route, endPoint);
            }

            int x = curNode.getFieldCoordinates().getX();
            int y = curNode.getFieldCoordinates().getY();
            potentialField.get(x).get(y).setClosed(true);

            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    int newX = x + offsetX;
                    int newY = y + offsetY;

                    if (newX >= 0 && newY >= 0 && newX < numPointsX && newY < numPointsY) {
                        Cell nextCell = potentialField.get(newX).get(newY);

                        Double tentativeScore = curNode.getGx() + calcGxBetweenPoints(potentialField, curNode, nextCell);

                        if (!nextCell.isClosed() || tentativeScore < nextCell.getGx()) {
                            nextCell.updateParameters(curNode,
                                    tentativeScore, calcGlobalH(curNode, endNode, scale));
                            if (!nodes.contains(nextCell)) {
                                nodes.add(nextCell);
                            }
                        }
                    }
                }
            }
        }

        log.warn("The algorithm did not find the path, give the standard Google route");
        return googleRouteBuilder.buildRoute(startPoint, endPoint);
    }

    private static Double calcGxBetweenPoints(ArrayList<ArrayList<Cell>> potentialField, Cell from, Cell to) {
        return (double) potentialField.get(to.getFieldCoordinates().getX()).get(to.getFieldCoordinates().getY()).getValue();
    }

    private static void fillPotentialField(ArrayList<ArrayList<Cell>> potentialField, int scale, ArrayList<RouteProperties> routeProperties) {
        AbstractPotentialMap assembledMap = new PotentialMapBuilder().assemblePotentialMap(routeProperties);

        for (int i = 0; i < potentialField.size(); i++) {
            for (int j = 0; j < potentialField.get(i).size(); j++) {
                potentialField.get(i).get(j).setValue(assembledMap.get(i, j));
            }
        }
    }

    private static void potentialFieldInitialization(ArrayList<ArrayList<Cell>> potentialField, int x, int y, int scale) {
        final double lat1km = 0.00899;
        final double lon1km = 0.01793;

        double lat = lat1km / ((double) scale);
        double lon = lon1km / ((double) scale);

        double curX = 30.18035;
        double curY = 60.02781;

        for (int i = 0; i < x; i++) {
            curX = 30.18035;
            potentialField.add(new ArrayList<>());
            for (int j = 0; j < y; j++) {
                potentialField.get(i).add(new Cell(0, new FieldCoordinates(i, j), new GeoCoordinates(curX, curY)));
                curX += lon;
            }
            curY -= lat;
        }
    }

    private static Double calcGlobalH(Cell from, Cell to, int scale) {
        //DistanceMap distanceMap = new DistanceMap(scale);
        //TODO переделать
        return EuclideanDist(from, to);
    }

    private static Double ChebyshevDist(Cell from, Cell to) {
        //TODO переделать
        return (double) Math.max(Math.abs(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX()),
                Math.abs(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY()));
    }

    private static Double EuclideanDist(Cell from, Cell to) {
        //TODO переделать
        return Math.sqrt(Math.pow(from.getFieldCoordinates().getX() - to.getFieldCoordinates().getX(), 2) +
                Math.pow(from.getFieldCoordinates().getY() - to.getFieldCoordinates().getY(),2));
    }

    private static Double EuclideanDist(GeoCoordinates from, GeoCoordinates to) {
        //TODO переделать
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) +
                Math.pow(from.getY() - to.getY(),2));
    }

    private ArrayList<GeoCoordinates> routeRestoration(Cell curNode) {
        ArrayList<GeoCoordinates> route = new ArrayList<>();

        //не включаем конечную точку
        //route.add(curNode.getGeoCoordinates());

        while (curNode.getParent() != null) {
            curNode = curNode.getParent();
            route.add(curNode.getGeoCoordinates());
        }

        //не включаем начальную точку
        route.remove(route.size() - 1);

        Collections.reverse(route);

        return route;
    }

}
