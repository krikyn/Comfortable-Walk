package com.netcracker.routebuilder.util;

import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.data.bean.Path;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * Util tools for controllers
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
                routeProperties.add(RouteProperty.valueOf(arrayOfWord));
            } catch (IllegalArgumentException e) {
                log.error("Error with parsing places in the controller", e);
            }
        }
    }

    /**
     * Method for generate response
     *
     * @param route    list of coordinates of points of the constructed route
     * @param response two-dimensional array where to write a list of coordinates of points of the constructed route
     */
    public static void generateResponse(ArrayList<GeoCoordinates> route, double[][] response) {
        for (int i = 0; i < route.size(); i++) {
            response[i][0] = route.get(i).getY();
            response[i][1] = route.get(i).getX();
        }
        //Переменная для центра
        response[route.size()][0] = 61.946079;
        response[route.size()][1] = 32.318713;
    }
}
