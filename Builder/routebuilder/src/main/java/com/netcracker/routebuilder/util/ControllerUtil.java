package com.netcracker.routebuilder.util;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.data.bean.Path;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * bean tools for controllers
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
public class ControllerUtil {

    /**
     * Method for extracting route properties (can be rewritten using the substring search algorithm)
     *
     * @param path            path
     * @param routeProperties list to extract properties to
     */
    public static void extractRouteProperties(Path path, ArrayList<RouteProperty> routeProperties) {
        String[] arrayOfWords = path.getPlaceName().toUpperCase().split(",");
        for (String arrayOfWord : arrayOfWords) {
            try {
                if (!arrayOfWord.replace(" ", "").equals("")) {
                    routeProperties.add(RouteProperty.valueOf(arrayOfWord));
                }
            } catch (IllegalArgumentException e) {
                log.error("Error with parsing places in the controller", e);
            }
        }
    }

    /**
     * Method for removing unnecessary branches in the route
     *
     * @param originalRoute original route gotten from algorithm
     * @return route
     */
    public static ArrayList<GeoCoordinates> removeUnnecessaryBranches(ArrayList<GeoCoordinates> originalRoute) {

        GeoCoordinates zeroCoord = new GeoCoordinates(0d, 0d);

        for (GeoCoordinates coord : originalRoute) {
            if (coord.equals(zeroCoord)) {
                continue;
            }
            int first = originalRoute.indexOf(coord);
            int last = originalRoute.lastIndexOf(coord);

            if (first != last) {
                for (int i = first; i < last; i++) {
                    originalRoute.set(i, zeroCoord);
                }
            }
        }

        ArrayList<GeoCoordinates> resultRoute = new ArrayList<>();
        int numDeleted = 0;

        for (GeoCoordinates coord : originalRoute) {
            if (!coord.equals(zeroCoord)) {
                resultRoute.add(coord);
            } else {
                numDeleted++;
            }
        }

        log.info("Delete unnecessary branches: " + numDeleted);
        return resultRoute;
    }

    /**
     * Method for generate response
     *
     * @param route    list of coordinates of points of the constructed route
     * @param response two-dimensional array where to write a list of coordinates of points of the constructed route
     */
    public static void generateResponse(ArrayList<GeoCoordinates> route, double[][] response) {

        double xCoordSum = 0d;
        double yCoordSum = 0d;

        for (int i = 0; i < route.size(); i++) {
            response[i][0] = route.get(i).getY();
            response[i][1] = route.get(i).getX();

            yCoordSum += response[i][0];
            xCoordSum += response[i][1];
        }

        //Переменная для центра, берем сумму всех значений по одной координате и делим на их количество,
        // получаем среднюю координату
        response[route.size()][0] = yCoordSum / route.size();
        response[route.size()][1] = xCoordSum / route.size();
    }
}
