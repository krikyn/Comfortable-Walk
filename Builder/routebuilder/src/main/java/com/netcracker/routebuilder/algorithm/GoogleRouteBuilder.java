package com.netcracker.routebuilder.algorithm;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Builder of route via Google Direction API   <b>GOOGLE_CONTEXT</b> и <b>params</b>.
 *
 * @author Kisliakov Grigori
 * @version 1.0
 */
@Slf4j
@Component
public class GoogleRouteBuilder {
    /**
     * field of Google GeoApiContext need to build route
     */
    private final GeoApiContext GOOGLE_CONTEXT;
    /**
     * final field of algorithm's parameters
     */
    private final AlgorithmParameters params;

    /**
     * Initialization of  final fields
     *
     * @param params - object of Parameters class
     */
    public GoogleRouteBuilder(AlgorithmParameters params) {
        this.params = params;
        GOOGLE_CONTEXT = new GeoApiContext.Builder().apiKey(params.getApiKey()).build();
    }

    /**
     * Build Google's route  without intermediate points
     *
     * @param start - start position of route
     * @param end   - end position of route
     * @return return list  of route's points
     */
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end) {
        return buildDirection(convertToLatLang(start), convertToLatLang(end));
    }

    /**
     * Build Google's route
     *
     * @param points - list of necessary intermediate points of route
     * @return return list  of route's points
     */


    public ArrayList<GeoCoordinates> buildRoute(ArrayList<GeoCoordinates> points) {
        ArrayList<GeoCoordinates> listPoints = new ArrayList<>();
        int allPoints = points.size();
        int startPosition = 0;
        if (allPoints == 0) {
            log.error("Array of route points is empty.Check input Data", new ArrayIndexOutOfBoundsException());

        }
        //add start point of route
        listPoints.add(points.get(0));
        LatLng firstPoint = convertToLatLang(points.get(0));
        LatLng finish;
        //split route by parts
        while (allPoints > 0) {
            //array of intermediate points(not more maxCountOfWayPoints)
            LatLng[] latL;
            int length_point = 0;
            // if quantity of intermediate points less then maxCountOfWayPoints
            if (allPoints / params.getMaxCountOfWaypoints() < 1) {
                finish = convertToLatLang(points.get(points.size() - 1));
                latL = new LatLng[allPoints % params.getMaxCountOfWaypoints()];

            }
            // if quantity of intermediate points more or equals then maxCountOfWayPoints
            else {
                if (allPoints == 21) {
                    latL = new LatLng[params.getMaxCountOfWaypoints() - 1];

                } else {
                    latL = new LatLng[params.getMaxCountOfWaypoints()];

                }
                //add endpoint geoposition
                finish = convertToLatLang(points.get(startPosition + latL.length));
            }
            //add geopositions for clear array of  intermediate points from ArrayList
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[length_point++] = convertToLatLang(points.get(i));
            }
            //build route
            buildDirection(firstPoint, latL, finish, listPoints);
            startPosition += latL.length + 1;
            allPoints -= latL.length + 1;
            firstPoint = finish;
        }
        listPoints.add(points.get(points.size() - 1));
        return listPoints;
    }

    /**
     * Build Google's route
     *
     * @param start  - start position of route
     * @param points - list of necessary intermediate points of route
     * @param end    - end position of route
     * @return return list  of route's points
     */
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, ArrayList<GeoCoordinates> points, GeoCoordinates end) {
        points.add(0, start);
        points.add(end);
        return buildRoute(points);
    }

    /**
     * Standard Google's method of building Routes without intermediate points
     *
     * @param StartPoint - start position of route
     * @param endPoint   - end position of route
     * @return return list  of route's points
     */
    private ArrayList<GeoCoordinates> buildDirection(LatLng StartPoint, LatLng endPoint) {
        ArrayList<GeoCoordinates> listPoint = new ArrayList<>();
        listPoint.add(convertToGeoCoordinates(StartPoint));
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GOOGLE_CONTEXT)
                    .origin(StartPoint)
                    .destination(endPoint)
                    .mode(TravelMode.WALKING)
                    .await();
            if (result.routes.length == 0) {
                log.error("Array of route is empty.Check input Data", new ArrayIndexOutOfBoundsException());
            }
            //get steps with geoposition
            for (DirectionsStep step : result.routes[0].legs[0].steps) {
                decodePolyline(step.polyline, listPoint);
                listPoint.add(convertToGeoCoordinates(step.endLocation));
            }
            listPoint.add(convertToGeoCoordinates(endPoint));
        } catch (ApiException e) {
            log.error("Not valid API=key", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupt Thread", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return listPoint;
    }


    /**
     * Standard Google's method of building Routes with intermediate points
     *
     * @param startPoint - start position of route
     * @param wayPoints  - list of necessary intermediate points of route
     * @param endPoint   - end position of route
     * @param points     - list of finish route's points
     */
    private void buildDirection(LatLng startPoint, LatLng[] wayPoints,
                                LatLng endPoint, ArrayList<GeoCoordinates> points) {
        if (wayPoints.length > params.getMaxCountOfWaypoints()) {
            log.error("Quantity of waypoints more  than " + params.getMaxCountOfWaypoints(), new IndexOutOfBoundsException());

        }
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GOOGLE_CONTEXT)
                    .origin(startPoint)
                    .destination(endPoint)
                    .mode(TravelMode.WALKING)
                    .waypoints(convertToWaypoints(wayPoints))//add waypoints
                    .await();
            //get steps with geoposition
            if (result.routes.length == 0) {
                log.error("Array of route is empty.Check input Data", new ArrayIndexOutOfBoundsException());
            }
            for (DirectionsStep step : result.routes[0].legs[0].steps) {
                decodePolyline(step.polyline, points);
                points.add(convertToGeoCoordinates(step.endLocation));
            }
        } catch (ApiException e) {
            log.error("Not valid API=key", e);
        } catch (InterruptedException e) {
            log.error("Interrupt Thread", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    /**
     * Convert LatLng type to GeoCoordinates type
     *
     * @param point - point  in LatLng type
     * @return return  point  in GeoCoordinates type
     */
    private GeoCoordinates convertToGeoCoordinates(LatLng point) {
        String[] st = point.toString().split(",");
        return new GeoCoordinates(Double.valueOf(st[1]), Double.valueOf(st[0]));
    }

    /**
     * Convert Latlang array to WayPoints array
     *
     * @param wayPoints - array of  points  in LatLng type
     * @return array of  points  in Waypoint type
     */

    private DirectionsApiRequest.Waypoint[] convertToWaypoints(LatLng[] wayPoints) {
        DirectionsApiRequest.Waypoint[] lat = new DirectionsApiRequest.Waypoint[wayPoints.length];
        for (int i = 0; i < wayPoints.length; i++) {
            lat[i] = new DirectionsApiRequest.Waypoint(wayPoints[i], false);
        }
        return lat;
    }

    /**
     * Add points into list from Google's polyline(small part of route consisting of markers on map)
     *
     * @param polyline - array of  points  in LatLng type
     * @param list     -  list of  route's points
     */
    private void decodePolyline(EncodedPolyline polyline, ArrayList<GeoCoordinates> list) {
        for (LatLng lang : polyline.decodePath()) {
            list.add(convertToGeoCoordinates(lang));
        }
    }

    /**
     * Convert GeoCoordinates type to LatLng type
     *
     * @param point - point  in GeoCoordinates type
     * @return return  point  in LatLng type
     */
    private LatLng convertToLatLang(GeoCoordinates point) {
        return new LatLng(point.getY(), point.getX());
    }


}