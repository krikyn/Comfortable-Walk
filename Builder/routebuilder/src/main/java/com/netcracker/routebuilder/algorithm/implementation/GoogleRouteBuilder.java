package com.netcracker.routebuilder.algorithm.implementation;

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
     * field of Google GeoApiContex need to build route
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
        return buildDirection(ConvertToLatLang(start), ConvertToLatLang(end));
    }

    /**
     * Build Google's route
     *
     * @param points - list of necessary intermediate points of route
     * @return return list  of route's points
     */


    public ArrayList<GeoCoordinates> buildRoute(ArrayList<GeoCoordinates> points) {
        ArrayList<GeoCoordinates> ListPoints = new ArrayList<>();
        int AllPoints = points.size();
        int startPosition = 0;
        if (AllPoints == 0) {
            log.error("Array of route points is empty.Check input Data");
            throw new ArrayIndexOutOfBoundsException();
        }
        //add start point of
        ListPoints.add(points.get(0));
        LatLng starT = ConvertToLatLang(points.get(0));
        LatLng enD;
        //devide route by parts
        while (AllPoints > 0) {
            //array of intermediate points(not more maxCountOfWayPoints)
            LatLng[] latL;
            int leng_point = 0;
            // if quantity of intermediate points less then maxCountOfWayPoints
            if (AllPoints / params.getMaxCountOfWaypoints() < 1) {
                latL = new LatLng[AllPoints % params.getMaxCountOfWaypoints()];
                enD = ConvertToLatLang(points.get(points.size() - 1));
            }
            // if quantity of intermediate points more then maxCountOfWayPoints
            else {
                latL = new LatLng[params.getMaxCountOfWaypoints()];
                //add endpoint geoposition
                enD = ConvertToLatLang(points.get(startPosition + latL.length));
            }
            //add geoposition for clear array of  intermediate points from ArrayList
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[leng_point++] = ConvertToLatLang(points.get(i));
            }
            //build route
            buildDirection(starT, latL, enD, ListPoints);
            startPosition += latL.length + 1;
            //condition of 'while'
            AllPoints -= latL.length + 1;
            starT = enD;
        }
        ListPoints.add(points.get(points.size() - 1));
        return ListPoints;
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
        ArrayList<GeoCoordinates> ListPoints = new ArrayList<>();
        int AllPoints = points.size();
        int startPosition = 0;
        if (AllPoints == 0) {
            log.error("Array of route points is empty.Check input Data");
            throw new ArrayIndexOutOfBoundsException();
        }
        //add start point of route
        ListPoints.add(start);
        LatLng starT = ConvertToLatLang(start);
        LatLng enD;
        //devide route by parts
        while (AllPoints > 0) {
            //array of intermediate points(not more maxCountOfWayPoints)
            LatLng[] latL;
            int leng_point = 0;
            // if quantity of intermediate points less then maxCountOfWayPoints
            if (AllPoints / params.getMaxCountOfWaypoints() < 1) {
                latL = new LatLng[AllPoints % params.getMaxCountOfWaypoints()];
                enD = ConvertToLatLang(end);
            }
            // if quantity of intermediate points more then maxCountOfWayPoints
            else {
                latL = new LatLng[params.getMaxCountOfWaypoints()];
                //add endpoint geoposition
                enD = ConvertToLatLang(points.get(startPosition + latL.length));
            }
            //add geoposition for clear array of  intermediate points from ArrayList
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[leng_point++] = ConvertToLatLang(points.get(i));
            }
            //build route
            buildDirection(starT, latL, enD, ListPoints);
            startPosition += latL.length + 1;
            //condition of 'while'
            AllPoints -= latL.length + 1;
            starT = ConvertToLatLang(end);
        }
        ListPoints.add(end);
        return ListPoints;
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
        listPoint.add(ConvertToGeoCoordinates(StartPoint));
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GOOGLE_CONTEXT)
                    .origin(StartPoint)
                    .destination(endPoint)
                    .mode(TravelMode.WALKING)
                    .await();
            if (result.routes.length == 0) {
                log.error("Array of route is empty.Check input Data");
                throw new ArrayIndexOutOfBoundsException();
            }
            //get steps with geoposition
            for (DirectionsStep step : result.routes[0].legs[0].steps) {
                DecodePolyine(step.polyline, listPoint);
                listPoint.add(ConvertToGeoCoordinates(step.endLocation));
            }
            listPoint.add(ConvertToGeoCoordinates(endPoint));
        } catch (ApiException e) {
            log.error("Not valid API=key");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupt Thread");
        } catch (IOException e) {
            log.error("IOException");
        }
        return listPoint;
    }


    /**
     * Standard Google's method of building Routes with intermediate points
     *
     * @param StartPoint - start position of route
     * @param waypoints  - list of necessary intermediate points of route
     * @param endPoint   - end position of route
     * @param Points     - list of finish route's points
     */
    private void buildDirection(LatLng StartPoint, LatLng[] waypoints,
                                LatLng endPoint, ArrayList<GeoCoordinates> Points) {
        if (waypoints.length > params.getMaxCountOfWaypoints()) {
            log.error("Quantity of waypoints more  than " + params.getMaxCountOfWaypoints());
            log.error("Check  buildRoute() method");
            throw new IndexOutOfBoundsException();
        }
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GOOGLE_CONTEXT)
                    .origin(StartPoint)
                    .destination(endPoint)
                    .mode(TravelMode.WALKING)
                    .waypoints(ConvertToWaypoints(waypoints))//add waypoints
                    .await();
            //get steps with geoposition
            if (result.routes.length == 0) {
                log.error("Array of route is empty.Check input Data");
                throw new ArrayIndexOutOfBoundsException();
            }
            for (DirectionsStep step : result.routes[0].legs[0].steps) {
                DecodePolyine(step.polyline, Points);
                Points.add(ConvertToGeoCoordinates(step.endLocation));
            }
        } catch (ApiException e) {
            log.error("Not valid API=key");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupt Thread");
        } catch (IOException e) {
            log.error("IOException");
        }
    }

    /**
     * Convert LatLng type to GeoCoordinates type
     *
     * @param point - point  in LatLng type
     * @return return  point  in GeoCoordinates type
     */
    private GeoCoordinates ConvertToGeoCoordinates(LatLng point) {
        String[] st = point.toString().split(",");
        return new GeoCoordinates(Double.valueOf(st[1]), Double.valueOf(st[0]));
    }

    /**
     * Convert Latlang array to WayPoints array
     *
     * @param waypoints - array of  points  in LatLng type
     * @return array of  points  in Waypoint type
     */

    private DirectionsApiRequest.Waypoint[] ConvertToWaypoints(LatLng[] waypoints) {
        DirectionsApiRequest.Waypoint[] lat = new DirectionsApiRequest.Waypoint[waypoints.length];
        for (int i = 0; i < waypoints.length; i++) {
            lat[i] = new DirectionsApiRequest.Waypoint(waypoints[i], false);
        }
        return lat;
    }

    /**
     * Add points into list from Google's polyline(small part of route consisting of markers on map)
     *
     * @param polyline - array of  points  in LatLng type
     * @param list     -  list of  route's points
     */
    private void DecodePolyine(EncodedPolyline polyline, ArrayList<GeoCoordinates> list) {
        for (LatLng lang : polyline.decodePath()) {
            list.add(ConvertToGeoCoordinates(lang));
        }
    }

    /**
     * Convert GeoCoordinates type to LatLng type
     *
     * @param point - point  in GeoCoordinates type
     * @return return  point  in LatLng type
     */
    private LatLng ConvertToLatLang(GeoCoordinates point) {
        return new LatLng(point.getY(), point.getX());
    }
}

