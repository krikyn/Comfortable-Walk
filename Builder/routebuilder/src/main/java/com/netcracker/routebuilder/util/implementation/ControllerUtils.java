package com.netcracker.routebuilder.util.implementation;

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
public class ControllerUtils {

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
     * @param response array of strings where to write a list of coordinates of points of the constructed route
     */
    public static void generateResponse(ArrayList<GeoCoordinates> route, String[] response) {
        for (int i = 0; i < route.size(); i++) {
            response[i] = route.get(i).getY() + ", " + route.get(i).getX();
        }
    }
}
